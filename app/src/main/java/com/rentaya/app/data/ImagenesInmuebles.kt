package com.rentaya.app.data

import com.rentaya.app.data.model.Property
import com.rentaya.app.data.model.PropertyType
import kotlin.math.abs

/**
 * Asignación estable de fotos locales, misma lógica que RentaGo
 * (`js/imagenes-inmuebles.js`: hash del id sobre el pool del tipo).
 *
 * APARTAMENTO / CUARTO → apto1-5.jpg
 * CASA → casa1-5.jpg
 */
object ImagenesInmuebles {

    const val PREFIJO_ASSET = "file:///android_asset/inmuebles/"

    val poolApto = listOf("apto1.jpg", "apto2.jpg", "apto3.jpg", "apto4.jpg", "apto5.jpg")
    val poolCasa = listOf("casa1.jpg", "casa2.jpg", "casa3.jpg", "casa4.jpg", "casa5.jpg")
    val poolParqueadero = listOf("parking1.jpg", "parking2.jpg", "parking3.jpg")

    /** Hash 32-bit idéntico al `hashString` de RentaGo. */
    fun hashString(str: String): Int {
        var hash = 0
        for (char in str) {
            hash = (hash shl 5) - hash + char.code
        }
        return abs(hash)
    }

    fun poolPara(tipo: PropertyType): List<String> = when (tipo) {
        PropertyType.APARTAMENTO, PropertyType.CUARTO -> poolApto
        PropertyType.CASA -> poolCasa
    }

    fun rutaAsset(nombreArchivo: String): String =
        PREFIJO_ASSET + nombreArchivo.substringAfterLast('/')

    fun obtenerImagen(id: String, tipo: PropertyType): String {
        val pool = poolPara(tipo)
        val index = hashString(id) % pool.size
        return rutaAsset(pool[index])
    }

    fun obtenerGaleria(id: String, tipo: PropertyType, cantidad: Int = 3): List<String> {
        val pool = poolPara(tipo)
        val hash = hashString(id)
        val n = minOf(cantidad, pool.size)
        return (0 until n).map { i ->
            rutaAsset(pool[(hash + i) % pool.size])
        }
    }

    /**
     * Convierte claves de semilla (`apto1.jpg`, `/img/inmuebles/apto1.jpg`, `sample`)
     * o URLs remotas / content Uri a un modelo que Coil puede cargar.
     */
    fun normalizar(valor: String, id: String = "", tipo: PropertyType = PropertyType.APARTAMENTO): String {
        val limpio = valor.trim()
        if (limpio.isBlank() || limpio == "sample") {
            return if (id.isNotBlank()) obtenerImagen(id, tipo) else rutaAsset(poolApto.first())
        }
        if (limpio.startsWith("http://") ||
            limpio.startsWith("https://") ||
            limpio.startsWith("content://") ||
            limpio.startsWith("file://")
        ) {
            return limpio
        }
        if (limpio.startsWith("/img/inmuebles/") || limpio.startsWith("inmuebles/")) {
            return rutaAsset(limpio.substringAfterLast('/'))
        }
        if (limpio.contains(".jpg") || limpio.contains(".jpeg") ||
            limpio.contains(".png") || limpio.contains(".webp")
        ) {
            return rutaAsset(limpio.substringAfterLast('/'))
        }
        return if (id.isNotBlank()) obtenerImagen(id, tipo) else limpio
    }

    fun galeriaDe(propiedad: Property): List<String> {
        val remotas = propiedad.imagenes.map { it.trim() }.filter { it.isNotBlank() }
        if (remotas.isNotEmpty()) {
            return remotas.map { normalizar(it, propiedad.id, propiedad.type) }
        }
        if (propiedad.imageRes.isNotBlank() && propiedad.imageRes != "sample") {
            return listOf(normalizar(propiedad.imageRes, propiedad.id, propiedad.type))
        }
        return obtenerGaleria(propiedad.id, propiedad.type)
    }

    fun principalDe(propiedad: Property): String =
        galeriaDe(propiedad).firstOrNull()
            ?: obtenerImagen(propiedad.id, propiedad.type)
}
