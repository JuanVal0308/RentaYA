package com.rentaya.app.data.repositorio

import android.content.Context
import android.net.Uri
import com.rentaya.app.data.SampleData
import com.rentaya.app.data.model.Property
import com.rentaya.app.data.remoto.ClienteSupabase
import java.util.UUID

/**
 * Abstracción de propiedades: Supabase cuando hay claves en BuildConfig,
 * o SampleData + lista local mutable en modo offline.
 */
object RepositorioPropiedades {

    /** Propiedades publicadas en esta sesión (y persistibles vía Supabase). */
    private val publicadasLocal = mutableListOf<Property>()

    /** Caché de la última lista remota exitosa (para la UI síncrona). */
    @Volatile
    private var cacheRemota: List<Property>? = null

    fun usaSupabase(): Boolean = ClienteSupabase.estaConfigurado

    /**
     * Lista síncrona para la UI: preferir caché remota si existe;
     * si no, semillas + publicaciones locales.
     */
    fun listarLocal(): List<Property> {
        val remota = cacheRemota
        if (remota != null) {
            val idsRemotos = remota.map { it.id }.toSet()
            val extras = publicadasLocal.filter { it.id !in idsRemotos }
            return remota + extras
        }
        return SampleData.properties + publicadasLocal.toList()
    }

    fun obtenerPorId(id: String): Property? =
        listarLocal().find { it.id == id }

    fun obtenerPublicadasLocalmente(): List<Property> =
        publicadasLocal.toList()

    /**
     * Intenta leer de Supabase; si falla o no está configurado, usa datos locales.
     */
    suspend fun listar(): List<Property> {
        if (!ClienteSupabase.estaConfigurado) {
            return listarLocal()
        }
        val remoto = ClienteSupabase.obtenerPropiedades()
        return remoto.fold(
            onSuccess = { lista ->
                cacheRemota = lista
                listarLocal()
            },
            onFailure = { listarLocal() }
        )
    }

    /**
     * Sube fotos a Storage si hay sesión remota; si no, conserva las Uri locales
     * para mostrarlas en esta sesión (offline).
     */
    suspend fun resolverImagenesPublicacion(
        context: Context,
        idPropiedad: String,
        uris: List<String>
    ): List<String> {
        if (uris.isEmpty()) return emptyList()
        val puedeSubir = ClienteSupabase.estaConfigurado &&
            !ClienteSupabase.tokenAcceso.isNullOrBlank()
        if (!puedeSubir) {
            return uris
        }
        val resultado = mutableListOf<String>()
        uris.forEachIndexed { index, uriTexto ->
            val uri = Uri.parse(uriTexto)
            val esRemota = uriTexto.startsWith("http://") || uriTexto.startsWith("https://")
            if (esRemota) {
                resultado.add(uriTexto)
                return@forEachIndexed
            }
            val bytes = runCatching {
                context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            }.getOrNull()
            if (bytes == null || bytes.isEmpty()) {
                resultado.add(uriTexto)
                return@forEachIndexed
            }
            val tipo = context.contentResolver.getType(uri) ?: "image/jpeg"
            val ext = when {
                tipo.contains("png") -> "png"
                tipo.contains("webp") -> "webp"
                else -> "jpg"
            }
            val remoto = ClienteSupabase.subirImagenInmueble(
                bytes = bytes,
                nombreArchivo = "$idPropiedad/${index + 1}.$ext",
                contentType = tipo
            )
            resultado.add(remoto.getOrDefault(uriTexto))
        }
        return resultado
    }

    /**
     * Publica una propiedad. Siempre la agrega a la lista local mutable.
     * Si Supabase está configurado, también la inserta en la tabla `propiedades`
     * con [ClienteSupabase.idUsuarioActual] como id_propietario.
     */
    suspend fun publicarPropiedad(propiedad: Property): Result<Property> {
        val conId = if (propiedad.id.isBlank()) {
            propiedad.copy(id = UUID.randomUUID().toString())
        } else {
            propiedad
        }
        synchronized(publicadasLocal) {
            publicadasLocal.removeAll { it.id == conId.id }
            publicadasLocal.add(0, conId)
        }
        if (!ClienteSupabase.estaConfigurado) {
            return Result.success(conId)
        }
        val remoto = ClienteSupabase.insertarPropiedad(
            propiedad = conId,
            idPropietario = ClienteSupabase.idUsuarioActual
        )
        return if (remoto.isSuccess) {
            cacheRemota = null
            Result.success(conId)
        } else {
            Result.failure(remoto.exceptionOrNull() ?: Exception("Error remoto al publicar"))
        }
    }
}
