package com.rentaya.app.data

import com.rentaya.app.data.model.Property

/**
 * Coordenadas realistas por barrio del Valle de Aburrá.
 * El centro genérico 6.2442, -75.5812 se usa solo como fallback
 * (y para detectar semillas antiguas que aún no se actualizaron).
 */
object CoordenadasBarrios {

    const val LAT_CENTRO = 6.2442
    const val LNG_CENTRO = -75.5812

    val porBarrio: Map<String, Pair<Double, Double>> = mapOf(
        "el poblado" to (6.2088 to -75.5648),
        "laureles" to (6.2458 to -75.5936),
        "laureles norte" to (6.2534 to -75.5898),
        "envigado" to (6.1719 to -75.5830),
        "belén" to (6.2322 to -75.5988),
        "belen" to (6.2322 to -75.5988),
        "sabaneta" to (6.1516 to -75.6167),
        "buenos aires" to (6.2468 to -75.5542),
        "estadio" to (6.2569 to -75.5901),
        "universidad" to (6.2682 to -75.5684),
        "itagüí" to (6.1724 to -75.6111),
        "itagui" to (6.1724 to -75.6111),
        "la américa" to (6.2516 to -75.6078),
        "la america" to (6.2516 to -75.6078),
        "robledo" to (6.2808 to -75.5914),
        "castilla" to (6.2910 to -75.5736),
        "bello" to (6.3380 to -75.5578),
        "calasanz" to (6.2562 to -75.6126),
        "san antonio de prado" to (6.1848 to -75.6562),
        "conquistadores" to (6.2396 to -75.5848),
        "manrique" to (6.2732 to -75.5476),
        "guayabal" to (6.2106 to -75.5864),
        "aranjuez" to (6.2768 to -75.5602)
    )

    val nombresSugeridos: List<String> = listOf(
        "El Poblado", "Laureles", "Envigado", "Belén", "Sabaneta",
        "Buenos Aires", "Estadio", "Robledo", "Itagüí", "Bello",
        "Castilla", "Manrique", "Guayabal", "Calasanz", "La América", "Aranjuez"
    )

    fun de(barrio: String, id: String = ""): Pair<Double, Double> {
        val clave = barrio.trim().lowercase()
        val base = porBarrio[clave] ?: (LAT_CENTRO to LNG_CENTRO)
        if (id.isBlank()) return base
        val h = ImagenesInmuebles.hashString(id)
        val dLat = ((h % 17) - 8) * 0.00035
        val dLng = (((h / 17) % 17) - 8) * 0.00035
        return (base.first + dLat) to (base.second + dLng)
    }

    fun esCentroGenerico(lat: Double, lng: Double): Boolean =
        lat == LAT_CENTRO && lng == LNG_CENTRO

    /**
     * Si la fila aún tiene el pin genérico de Medellín centro,
     * se recoloca al barrio (útil antes de correr el UPDATE de semillas).
     */
    fun efectiva(propiedad: Property): Pair<Double, Double> {
        return if (esCentroGenerico(propiedad.latitude, propiedad.longitude)) {
            de(propiedad.neighborhood, propiedad.id)
        } else {
            propiedad.latitude to propiedad.longitude
        }
    }
}
