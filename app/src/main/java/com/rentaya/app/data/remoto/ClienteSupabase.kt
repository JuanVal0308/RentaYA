package com.rentaya.app.data.remoto

import com.rentaya.app.BuildConfig
import com.rentaya.app.data.model.Landlord
import com.rentaya.app.data.model.Property
import com.rentaya.app.data.model.PropertyType
import com.rentaya.app.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Cliente HTTP ligero contra la API REST y Auth de Supabase.
 * Si [estaConfigurado] es false, los repositorios usan solo datos locales.
 */
object ClienteSupabase {

    val estaConfigurado: Boolean
        get() = BuildConfig.SUPABASE_URL.isNotBlank() &&
            BuildConfig.SUPABASE_ANON_KEY.isNotBlank()

    private val clienteHttp: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    private val jsonMedia = "application/json".toMediaType()

    private fun urlBase(): String = BuildConfig.SUPABASE_URL.trimEnd('/')

    private fun encabezadosAuth(tokenAcceso: String? = null): Map<String, String> {
        val mapa = mutableMapOf(
            "apikey" to BuildConfig.SUPABASE_ANON_KEY,
            "Content-Type" to "application/json"
        )
        val bearer = tokenAcceso ?: BuildConfig.SUPABASE_ANON_KEY
        mapa["Authorization"] = "Bearer $bearer"
        return mapa
    }

    suspend fun iniciarSesion(correo: String, clave: String): Result<User> =
        withContext(Dispatchers.IO) {
            if (!estaConfigurado) {
                return@withContext Result.failure(IllegalStateException("Supabase no configurado"))
            }
            try {
                val cuerpo = JSONObject()
                    .put("email", correo)
                    .put("password", clave)
                    .toString()
                val peticion = Request.Builder()
                    .url("${urlBase()}/auth/v1/token?grant_type=password")
                    .apply { encabezadosAuth().forEach { (k, v) -> addHeader(k, v) } }
                    .post(cuerpo.toRequestBody(jsonMedia))
                    .build()
                clienteHttp.newCall(peticion).execute().use { respuesta ->
                    val texto = respuesta.body?.string().orEmpty()
                    if (!respuesta.isSuccessful) {
                        return@withContext Result.failure(
                            Exception(extraerMensajeError(texto, "No se pudo iniciar sesión"))
                        )
                    }
                    val json = JSONObject(texto)
                    val usuarioAuth = json.optJSONObject("user")
                        ?: return@withContext Result.failure(Exception("Respuesta de auth incompleta"))
                    val correoUsuario = usuarioAuth.optString("email", correo)
                    val meta = usuarioAuth.optJSONObject("user_metadata")
                    val nombre = meta?.optString("nombre")
                        ?.takeIf { it.isNotBlank() }
                        ?: correoUsuario.substringBefore("@")
                    val telefono = meta?.optString("telefono").orEmpty()
                    Result.success(User(correoUsuario, nombre, telefono, clave))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun registrar(
        nombre: String,
        correo: String,
        telefono: String,
        clave: String
    ): Result<User> = withContext(Dispatchers.IO) {
        if (!estaConfigurado) {
            return@withContext Result.failure(IllegalStateException("Supabase no configurado"))
        }
        try {
            val meta = JSONObject()
                .put("nombre", nombre)
                .put("telefono", telefono)
            val cuerpo = JSONObject()
                .put("email", correo)
                .put("password", clave)
                .put("data", meta)
                .toString()
            val peticion = Request.Builder()
                .url("${urlBase()}/auth/v1/signup")
                .apply { encabezadosAuth().forEach { (k, v) -> addHeader(k, v) } }
                .post(cuerpo.toRequestBody(jsonMedia))
                .build()
            clienteHttp.newCall(peticion).execute().use { respuesta ->
                val texto = respuesta.body?.string().orEmpty()
                if (!respuesta.isSuccessful) {
                    return@withContext Result.failure(
                        Exception(extraerMensajeError(texto, "No se pudo registrar"))
                    )
                }
                val json = JSONObject(texto)
                val usuarioAuth = json.optJSONObject("user")
                val idUsuario = usuarioAuth?.optString("id").orEmpty()
                if (idUsuario.isNotBlank()) {
                    crearPerfil(idUsuario, nombre, correo, telefono)
                }
                Result.success(User(correo, nombre, telefono, clave))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun crearPerfil(
        idUsuario: String,
        nombre: String,
        correo: String,
        telefono: String
    ) {
        val cuerpo = JSONObject()
            .put("id", idUsuario)
            .put("nombre", nombre)
            .put("correo", correo)
            .put("telefono", telefono)
            .toString()
        val peticion = Request.Builder()
            .url("${urlBase()}/rest/v1/perfiles")
            .apply {
                encabezadosAuth().forEach { (k, v) -> addHeader(k, v) }
                addHeader("Prefer", "resolution=merge-duplicates,return=minimal")
            }
            .post(cuerpo.toRequestBody(jsonMedia))
            .build()
        try {
            clienteHttp.newCall(peticion).execute().close()
        } catch (_: Exception) {
            // El registro de auth ya ocurrió; el perfil es complementario.
        }
    }

    suspend fun obtenerPropiedades(): Result<List<Property>> = withContext(Dispatchers.IO) {
        if (!estaConfigurado) {
            return@withContext Result.failure(IllegalStateException("Supabase no configurado"))
        }
        try {
            val peticion = Request.Builder()
                .url("${urlBase()}/rest/v1/propiedades?select=*&order=creado_en.desc")
                .apply {
                    encabezadosAuth().forEach { (k, v) -> addHeader(k, v) }
                    addHeader("Accept", "application/json")
                }
                .get()
                .build()
            clienteHttp.newCall(peticion).execute().use { respuesta ->
                val texto = respuesta.body?.string().orEmpty()
                if (!respuesta.isSuccessful) {
                    return@withContext Result.failure(
                        Exception(extraerMensajeError(texto, "Error al listar propiedades"))
                    )
                }
                val arreglo = JSONArray(texto)
                val lista = mutableListOf<Property>()
                for (i in 0 until arreglo.length()) {
                    lista.add(mapearPropiedad(arreglo.getJSONObject(i)))
                }
                Result.success(lista)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun insertarPropiedad(
        propiedad: Property,
        idPropietario: String? = null
    ): Result<Property> = withContext(Dispatchers.IO) {
        if (!estaConfigurado) {
            return@withContext Result.failure(IllegalStateException("Supabase no configurado"))
        }
        try {
            val amenidades = JSONArray()
            propiedad.amenities.forEach { amenidades.put(it) }
            val cuerpoJson = JSONObject()
                .put("id", propiedad.id)
                .put("titulo", propiedad.title)
                .put("descripcion", propiedad.description)
                .put("tipo", propiedad.type.name)
                .put("precio", propiedad.price)
                .put("barrio", propiedad.neighborhood)
                .put("habitaciones", propiedad.bedrooms)
                .put("banos", propiedad.bathrooms)
                .put("area", propiedad.area)
                .put("amenidades", amenidades)
                .put("arrendador_nombre", propiedad.landlord.name)
                .put("arrendador_calificacion", propiedad.landlord.rating.toDouble())
                .put("arrendador_telefono", propiedad.landlord.phone)
                .put("imagen", propiedad.imageRes)
                .put("latitud", propiedad.latitude)
                .put("longitud", propiedad.longitude)
            if (!idPropietario.isNullOrBlank()) {
                cuerpoJson.put("id_propietario", idPropietario)
            }
            val peticion = Request.Builder()
                .url("${urlBase()}/rest/v1/propiedades")
                .apply {
                    encabezadosAuth().forEach { (k, v) -> addHeader(k, v) }
                    addHeader("Prefer", "return=representation")
                }
                .post(cuerpoJson.toString().toRequestBody(jsonMedia))
                .build()
            clienteHttp.newCall(peticion).execute().use { respuesta ->
                val texto = respuesta.body?.string().orEmpty()
                if (!respuesta.isSuccessful) {
                    return@withContext Result.failure(
                        Exception(extraerMensajeError(texto, "Error al publicar propiedad"))
                    )
                }
                Result.success(propiedad)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapearPropiedad(json: JSONObject): Property {
        val tipoTexto = json.optString("tipo", PropertyType.APARTAMENTO.name)
        val tipo = runCatching { PropertyType.valueOf(tipoTexto) }
            .getOrDefault(PropertyType.APARTAMENTO)
        val amenidadesJson = json.optJSONArray("amenidades") ?: JSONArray()
        val amenidades = mutableListOf<String>()
        for (i in 0 until amenidadesJson.length()) {
            amenidades.add(amenidadesJson.getString(i))
        }
        return Property(
            id = json.optString("id"),
            title = json.optString("titulo"),
            description = json.optString("descripcion"),
            type = tipo,
            price = json.optInt("precio"),
            neighborhood = json.optString("barrio"),
            bedrooms = json.optInt("habitaciones"),
            bathrooms = json.optInt("banos"),
            area = json.optInt("area"),
            amenities = amenidades,
            landlord = Landlord(
                name = json.optString("arrendador_nombre", "Arrendador"),
                rating = json.optDouble("arrendador_calificacion", 4.0).toFloat(),
                phone = json.optString("arrendador_telefono", "")
            ),
            imageRes = json.optString("imagen", "sample"),
            latitude = json.optDouble("latitud", 6.2442),
            longitude = json.optDouble("longitud", -75.5812)
        )
    }

    private fun extraerMensajeError(cuerpo: String, respaldo: String): String {
        return try {
            val json = JSONObject(cuerpo)
            json.optString("msg")
                .ifBlank { json.optString("error_description") }
                .ifBlank { json.optString("message") }
                .ifBlank { respaldo }
        } catch (_: Exception) {
            respaldo
        }
    }
}
