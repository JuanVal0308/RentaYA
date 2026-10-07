package com.rentaya.app.data.remoto

import com.rentaya.app.BuildConfig
import com.rentaya.app.data.model.CorreoNoConfirmadoException
import com.rentaya.app.data.model.Landlord
import com.rentaya.app.data.model.Property
import com.rentaya.app.data.model.PropertyType
import com.rentaya.app.data.model.ResultadoAuth
import com.rentaya.app.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Cliente HTTP ligero contra la API REST, Auth y Storage de Supabase.
 * Si [estaConfigurado] es false, los repositorios usan solo datos locales.
 *
 * Tras login/registro guarda [tokenAcceso] e [idUsuarioActual] para que
 * los INSERT respeten las políticas RLS (rol authenticated).
 */
object ClienteSupabase {

    const val CUBETA_INMUEBLES = "inmuebles"

    val estaConfigurado: Boolean
        get() = BuildConfig.SUPABASE_URL.isNotBlank() &&
            BuildConfig.SUPABASE_ANON_KEY.isNotBlank()

    @Volatile
    var tokenAcceso: String? = null
        private set

    @Volatile
    var idUsuarioActual: String? = null
        private set

    @Volatile
    var correoConfirmado: Boolean = true
        private set

    @Volatile
    var correoPendienteConfirmacion: String? = null
        private set

    private val clienteHttp: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(45, TimeUnit.SECONDS)
            .build()
    }

    private val jsonMedia = "application/json".toMediaType()

    private fun urlBase(): String = BuildConfig.SUPABASE_URL.trimEnd('/')

    fun cerrarSesionRemota() {
        tokenAcceso = null
        idUsuarioActual = null
        correoConfirmado = true
        correoPendienteConfirmacion = null
    }

    fun puedePublicar(): Boolean {
        if (!estaConfigurado) return true
        if (BuildConfig.DEBUG_OMITIR_VERIFICACION_CORREO) return true
        return correoConfirmado
    }

    private fun encabezadosAuth(tokenAccesoOverride: String? = null): Map<String, String> {
        val mapa = mutableMapOf(
            "apikey" to BuildConfig.SUPABASE_ANON_KEY,
            "Content-Type" to "application/json"
        )
        val bearer = tokenAccesoOverride
            ?: tokenAcceso
            ?: BuildConfig.SUPABASE_ANON_KEY
        mapa["Authorization"] = "Bearer $bearer"
        return mapa
    }

    private fun guardarSesionDesdeRespuesta(json: JSONObject) {
        val token = json.optString("access_token").takeIf { it.isNotBlank() }
        if (token != null) {
            tokenAcceso = token
        }
        val usuarioAuth = json.optJSONObject("user") ?: json
        val id = usuarioAuth.optString("id").orEmpty()
        if (id.isNotBlank()) {
            idUsuarioActual = id
        }
        correoConfirmado = estaConfirmado(usuarioAuth)
        val correo = usuarioAuth.optString("email").orEmpty()
        correoPendienteConfirmacion = if (!correoConfirmado && correo.isNotBlank()) correo else null
    }

    private fun estaConfirmado(usuarioAuth: JSONObject): Boolean {
        val marca = usuarioAuth.optString("email_confirmed_at")
            .ifBlank { usuarioAuth.optString("confirmed_at") }
        return marca.isNotBlank() && marca != "null"
    }

    private fun usuarioDesdeAuthJson(json: JSONObject, correoFallback: String, clave: String): User {
        val usuarioAuth = json.optJSONObject("user") ?: json
        val correoUsuario = usuarioAuth.optString("email", correoFallback).ifBlank { correoFallback }
        val meta = usuarioAuth.optJSONObject("user_metadata")
        val nombre = meta?.optString("nombre")
            ?.takeIf { it.isNotBlank() }
            ?: correoUsuario.substringBefore("@")
        val telefono = meta?.optString("telefono").orEmpty()
        return User(
            email = correoUsuario,
            name = nombre,
            phone = telefono,
            password = clave,
            emailConfirmed = estaConfirmado(usuarioAuth)
        )
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
                        if (esErrorCorreoNoConfirmado(texto)) {
                            correoPendienteConfirmacion = correo
                            correoConfirmado = false
                            return@withContext Result.failure(CorreoNoConfirmadoException(correo))
                        }
                        return@withContext Result.failure(
                            Exception(extraerMensajeError(texto, "No se pudo iniciar sesión"))
                        )
                    }
                    val json = JSONObject(texto)
                    guardarSesionDesdeRespuesta(json)
                    val usuario = usuarioDesdeAuthJson(json, correo, clave)
                    if (!usuario.emailConfirmed && !BuildConfig.DEBUG_OMITIR_VERIFICACION_CORREO) {
                        return@withContext Result.failure(CorreoNoConfirmadoException(correo))
                    }
                    Result.success(usuario)
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
    ): Result<ResultadoAuth> = withContext(Dispatchers.IO) {
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
                guardarSesionDesdeRespuesta(json)
                val usuario = usuarioDesdeAuthJson(json, correo, clave)
                val haySesion = !tokenAcceso.isNullOrBlank()
                val requiereConfirmacion = !usuario.emailConfirmed || !haySesion
                if (requiereConfirmacion) {
                    correoPendienteConfirmacion = correo
                    correoConfirmado = false
                    // Sin correo confirmado no dejamos sesión usable en release.
                    if (!BuildConfig.DEBUG_OMITIR_VERIFICACION_CORREO) {
                        tokenAcceso = null
                    }
                } else {
                    val idUsuario = idUsuarioActual.orEmpty()
                    if (idUsuario.isNotBlank()) {
                        crearPerfil(idUsuario, nombre, correo, telefono)
                    }
                }
                Result.success(
                    ResultadoAuth(
                        usuario = usuario.copy(emailConfirmed = !requiereConfirmacion),
                        requiereConfirmacion = requiereConfirmacion
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun reenviarConfirmacion(correo: String): Result<Unit> = withContext(Dispatchers.IO) {
        if (!estaConfigurado) {
            return@withContext Result.failure(IllegalStateException("Supabase no configurado"))
        }
        try {
            val cuerpo = JSONObject()
                .put("type", "signup")
                .put("email", correo)
                .toString()
            val peticion = Request.Builder()
                .url("${urlBase()}/auth/v1/resend")
                .apply { encabezadosAuth().forEach { (k, v) -> addHeader(k, v) } }
                .post(cuerpo.toRequestBody(jsonMedia))
                .build()
            clienteHttp.newCall(peticion).execute().use { respuesta ->
                val texto = respuesta.body?.string().orEmpty()
                if (!respuesta.isSuccessful) {
                    return@withContext Result.failure(
                        Exception(extraerMensajeError(texto, "No se pudo reenviar el correo"))
                    )
                }
                Result.success(Unit)
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
                encabezadosAuth(tokenAcceso).forEach { (k, v) -> addHeader(k, v) }
                addHeader("Prefer", "resolution=merge-duplicates,return=minimal")
            }
            .post(cuerpo.toRequestBody(jsonMedia))
            .build()
        try {
            clienteHttp.newCall(peticion).execute().use { respuesta ->
                if (!respuesta.isSuccessful) {
                    val err = respuesta.body?.string().orEmpty()
                    android.util.Log.w(
                        "ClienteSupabase",
                        "No se pudo crear perfil: HTTP ${respuesta.code} $err"
                    )
                }
            }
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
        idPropietario: String? = idUsuarioActual
    ): Result<Property> = withContext(Dispatchers.IO) {
        if (!estaConfigurado) {
            return@withContext Result.failure(IllegalStateException("Supabase no configurado"))
        }
        try {
            val cuerpoConGaleria = cuerpoInsertar(propiedad, idPropietario, incluirGaleria = true)
            val primerIntento = postPropiedad(cuerpoConGaleria)
            if (primerIntento.isSuccess) {
                return@withContext Result.success(propiedad)
            }
            val error = primerIntento.exceptionOrNull()?.message.orEmpty()
            val faltaColumnaGaleria = error.contains("imagenes", ignoreCase = true) &&
                (error.contains("column", ignoreCase = true) || error.contains("schema", ignoreCase = true))
            if (faltaColumnaGaleria) {
                val reintento = postPropiedad(cuerpoInsertar(propiedad, idPropietario, incluirGaleria = false))
                return@withContext if (reintento.isSuccess) {
                    Result.success(propiedad)
                } else {
                    reintento.map { propiedad }
                }
            }
            primerIntento.map { propiedad }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun cuerpoInsertar(
        propiedad: Property,
        idPropietario: String?,
        incluirGaleria: Boolean
    ): JSONObject {
        val amenidades = JSONArray()
        propiedad.amenities.forEach { amenidades.put(it) }
        val imagenPrincipal = propiedad.imagenes.firstOrNull()
            ?.takeIf { it.isNotBlank() }
            ?: propiedad.imageRes
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
            .put("imagen", imagenPrincipal)
            .put("latitud", propiedad.latitude)
            .put("longitud", propiedad.longitude)
        if (incluirGaleria) {
            val galeria = JSONArray()
            propiedad.imagenes.forEach { galeria.put(it) }
            cuerpoJson.put("imagenes", galeria)
        }
        if (!idPropietario.isNullOrBlank()) {
            cuerpoJson.put("id_propietario", idPropietario)
        }
        return cuerpoJson
    }

    private fun postPropiedad(cuerpoJson: JSONObject): Result<Unit> {
        val peticion = Request.Builder()
            .url("${urlBase()}/rest/v1/propiedades")
            .apply {
                encabezadosAuth(tokenAcceso).forEach { (k, v) -> addHeader(k, v) }
                addHeader("Prefer", "return=representation")
            }
            .post(cuerpoJson.toString().toRequestBody(jsonMedia))
            .build()
        clienteHttp.newCall(peticion).execute().use { respuesta ->
            val texto = respuesta.body?.string().orEmpty()
            if (!respuesta.isSuccessful) {
                return Result.failure(
                    Exception(extraerMensajeError(texto, "Error al publicar propiedad"))
                )
            }
            return Result.success(Unit)
        }
    }

    /**
     * Sube una foto al bucket público `inmuebles` (API Storage, multipart).
     * Ruta: `{idUsuario}/{nombreArchivo}`. Devuelve la URL pública.
     */
    suspend fun subirImagenInmueble(
        bytes: ByteArray,
        nombreArchivo: String,
        contentType: String = "image/jpeg"
    ): Result<String> = withContext(Dispatchers.IO) {
        if (!estaConfigurado) {
            return@withContext Result.failure(IllegalStateException("Supabase no configurado"))
        }
        val idUsuario = idUsuarioActual
        if (idUsuario.isNullOrBlank() || tokenAcceso.isNullOrBlank()) {
            return@withContext Result.failure(
                Exception("Debes iniciar sesión para subir fotos")
            )
        }
        try {
            val seguro = nombreArchivo
                .replace("\\", "/")
                .split("/")
                .filter { it.isNotBlank() && it != ".." }
                .joinToString("/")
            val ruta = "$idUsuario/$seguro"
            val tipo = contentType.ifBlank { "image/jpeg" }.toMediaType()
            val multipart = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    seguro.substringAfterLast('/'),
                    bytes.toRequestBody(tipo)
                )
                .build()
            val peticion = Request.Builder()
                .url("${urlBase()}/storage/v1/object/$CUBETA_INMUEBLES/$ruta")
                .addHeader("apikey", BuildConfig.SUPABASE_ANON_KEY)
                .addHeader("Authorization", "Bearer $tokenAcceso")
                .addHeader("x-upsert", "true")
                .post(multipart)
                .build()
            clienteHttp.newCall(peticion).execute().use { respuesta ->
                val texto = respuesta.body?.string().orEmpty()
                if (!respuesta.isSuccessful) {
                    return@withContext Result.failure(
                        Exception(extraerMensajeError(texto, "No se pudo subir la foto"))
                    )
                }
                val urlPublica = "${urlBase()}/storage/v1/object/public/$CUBETA_INMUEBLES/$ruta"
                Result.success(urlPublica)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarPropiedad(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        if (!estaConfigurado) {
            return@withContext Result.failure(IllegalStateException("Supabase no configurado"))
        }
        try {
            val peticion = Request.Builder()
                .url("${urlBase()}/rest/v1/propiedades?id=eq.$id")
                .apply {
                    encabezadosAuth(tokenAcceso).forEach { (k, v) -> addHeader(k, v) }
                }
                .delete()
                .build()
            clienteHttp.newCall(peticion).execute().use { respuesta ->
                if (!respuesta.isSuccessful) {
                    val texto = respuesta.body?.string().orEmpty()
                    return@withContext Result.failure(
                        Exception(extraerMensajeError(texto, "No se pudo eliminar la propiedad"))
                    )
                }
                Result.success(Unit)
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
        val imagenesJson = json.optJSONArray("imagenes")
        val imagenes = mutableListOf<String>()
        if (imagenesJson != null) {
            for (i in 0 until imagenesJson.length()) {
                imagenesJson.optString(i).takeIf { it.isNotBlank() }?.let { imagenes.add(it) }
            }
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
            imagenes = imagenes,
            latitude = json.optDouble("latitud", 6.2442),
            longitude = json.optDouble("longitud", -75.5812)
        )
    }

    private fun esErrorCorreoNoConfirmado(cuerpo: String): Boolean {
        val bajo = cuerpo.lowercase()
        return bajo.contains("email_not_confirmed") ||
            bajo.contains("email not confirmed") ||
            bajo.contains("correo no confirmado")
    }

    private fun extraerMensajeError(cuerpo: String, respaldo: String): String {
        return try {
            val json = JSONObject(cuerpo)
            json.optString("msg")
                .ifBlank { json.optString("error_description") }
                .ifBlank { json.optString("message") }
                .ifBlank { json.optString("error") }
                .ifBlank { respaldo }
        } catch (_: Exception) {
            respaldo
        }
    }
}
