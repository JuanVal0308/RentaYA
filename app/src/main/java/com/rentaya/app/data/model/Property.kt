package com.rentaya.app.data.model

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
    val imageRes: String = "sample",
    val latitude: Double = 6.2442,
    val longitude: Double = -75.5812
)

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
