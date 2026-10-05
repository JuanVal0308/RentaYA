package com.rentaya.app.data.repositorio

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
     * Publica una propiedad. Siempre la agrega a la lista local mutable.
     * Si Supabase está configurado, también la inserta en la tabla `propiedades`
     * con [ClienteSupabase.idUsuarioActual] como id_propietario.
     *
     * La pantalla PublishPropertyScreen debe llamar este método
     * (tarea pendiente del equipo — ver TODO(equipo - Mariana)).
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
            // Invalidar caché para forzar refresco en el próximo listar().
            cacheRemota = null
            Result.success(conId)
        } else {
            Result.failure(remoto.exceptionOrNull() ?: Exception("Error remoto al publicar"))
        }
    }
}
