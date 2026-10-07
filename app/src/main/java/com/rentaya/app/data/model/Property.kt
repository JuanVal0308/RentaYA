package com.rentaya.app.data.model

import com.rentaya.app.data.CoordenadasBarrios
import com.rentaya.app.data.ImagenesInmuebles

data class Property(
    val id: String,
    val title: String,
    val description: String,
    val type: PropertyType,
    val price: Int,
    val neighborhood: String,
    val bedrooms: Int,
    val bathrooms: Int,
    val area: Int,
    val amenities: List<String>,
    val landlord: Landlord,
    /** Primera foto: URL https, content Uri, clave de asset (`apto1.jpg`) o `sample`. */
    val imageRes: String = "sample",
    /** Galería (URLs remotas de Storage, content Uri locales o claves de asset). */
    val imagenes: List<String> = emptyList(),
    val latitude: Double = 6.2442,
    val longitude: Double = -75.5812
) {
    fun galeria(): List<String> = ImagenesInmuebles.galeriaDe(this)

    fun imagenPrincipal(): String = ImagenesInmuebles.principalDe(this)

    fun coordenada(): Pair<Double, Double> = CoordenadasBarrios.efectiva(this)
}

enum class PropertyType(val displayName: String) {
    APARTAMENTO("Apto"),
    CASA("Casa"),
    CUARTO("Cuarto")
}

data class Landlord(
    val name: String,
    val rating: Float,
    val phone: String = ""
)
