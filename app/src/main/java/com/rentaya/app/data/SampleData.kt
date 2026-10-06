package com.rentaya.app.data

import com.rentaya.app.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object SampleData {

    private val registeredUsers = mutableMapOf<String, User>()

    private val favorites = MutableStateFlow<Set<String>>(emptySet())

    /** Favoritos en memoria; RentaYaApp guarda cada cambio en DataStore. */
    val favoritesFlow: StateFlow<Set<String>> = favorites.asStateFlow()

    private val messages = mutableListOf<Message>()
    
    fun registerUser(user: User) {
        registeredUsers[user.email] = user
    }
    
    fun validateLogin(email: String, password: String): User? {
        val user = registeredUsers[email]
        return if (user != null && user.password == password) user else null
    }
    
    fun isEmailRegistered(email: String): Boolean = registeredUsers.containsKey(email)
    
    fun toggleFavorite(propertyId: String) {
        favorites.update { ids ->
            if (propertyId in ids) ids - propertyId else ids + propertyId
        }
    }

    fun isFavorite(propertyId: String): Boolean = propertyId in favorites.value

    fun getFavorites(): Set<String> = favorites.value

    private var favoritesLoaded = false

    /**
     * Suma a los favoritos en memoria los que estaban guardados en DataStore.
     * Solo la primera vez: después la memoria manda y no se pisan cambios recientes.
     */
    fun loadFavorites(ids: Set<String>) {
        if (favoritesLoaded) return
        favoritesLoaded = true
        favorites.update { it + ids }
    }

    fun getMessages(): List<Message> = messages.toList()
    
    fun addMessage(message: Message) {
        messages.add(message)
    }
    
    val properties = listOf(
        Property(
            id = "1",
            title = "Apto en El Poblado",
            description = "Hermoso apartamento en el sector más exclusivo de Medellín. Cerca a parques, centros comerciales y restaurantes. Excelente iluminación natural.",
            type = PropertyType.APARTAMENTO,
            price = 2100000,
            neighborhood = "El Poblado",
            bedrooms = 2,
            bathrooms = 2,
            area = 65,
            amenities = listOf("Parqueadero", "Gimnasio"),
            landlord = Landlord("Carlos Pérez", 4.5f, "300 123 4567")
        ),
        Property(
            id = "2",
            title = "Casa en Laureles",
            description = "Casa espaciosa en barrio tradicional. Ideal para familias. Tiene patio y zona BBQ.",
            type = PropertyType.CASA,
            price = 1800000,
            neighborhood = "Laureles",
            bedrooms = 3,
            bathrooms = 2,
            area = 120,
            amenities = listOf("Parqueadero", "Amoblado"),
            landlord = Landlord("Ana Gómez", 4.8f, "301 234 5678")
        ),
        Property(
            id = "3",
            title = "Cuarto en Envigado",
            description = "Habitación cómoda en casa compartida. Baño privado. Servicios incluidos.",
            type = PropertyType.CUARTO,
            price = 650000,
            neighborhood = "Envigado",
            bedrooms = 1,
            bathrooms = 1,
            area = 20,
            amenities = listOf("Amoblado"),
            landlord = Landlord("Luis Rodríguez", 4.2f, "302 345 6789")
        ),
        Property(
            id = "4",
            title = "Apto en Sabaneta",
            description = "Apartamento moderno con excelente ubicación. Cerca al metro y zona comercial.",
            type = PropertyType.APARTAMENTO,
            price = 1500000,
            neighborhood = "Sabaneta",
            bedrooms = 3,
            bathrooms = 2,
            area = 80,
            amenities = listOf("Parqueadero"),
            landlord = Landlord("María López", 4.6f, "303 456 7890")
        ),
        Property(
            id = "5",
            title = "Apto en Belén",
            description = "Apartamento acogedor en conjunto cerrado con zonas verdes y juegos infantiles.",
            type = PropertyType.APARTAMENTO,
            price = 1200000,
            neighborhood = "Belén",
            bedrooms = 2,
            bathrooms = 1,
            area = 55,
            amenities = listOf("Gimnasio"),
            landlord = Landlord("Jorge Martínez", 4.3f, "304 567 8901")
        ),
        Property(
            id = "6",
            title = "Casa en Buenos Aires",
            description = "Casa tradicional remodelada. Excelente ubicación cerca a universidades.",
            type = PropertyType.CASA,
            price = 1900000,
            neighborhood = "Buenos Aires",
            bedrooms = 4,
            bathrooms = 3,
            area = 150,
            amenities = listOf("Parqueadero", "Amoblado"),
            landlord = Landlord("Sandra Díaz", 4.7f, "305 678 9012")
        ),
        Property(
            id = "7",
            title = "Apto en Estadio",
            description = "Apartamento con vista panorámica de la ciudad. Cerca al estadio Atanasio Girardot.",
            type = PropertyType.APARTAMENTO,
            price = 1600000,
            neighborhood = "Estadio",
            bedrooms = 2,
            bathrooms = 2,
            area = 70,
            amenities = listOf("Gimnasio"),
            landlord = Landlord("Pedro Ramírez", 4.4f, "306 789 0123")
        ),
        Property(
            id = "8",
            title = "Cuarto en Universidad",
            description = "Habitación para estudiantes. Muy cerca a las principales universidades de Medellín.",
            type = PropertyType.CUARTO,
            price = 550000,
            neighborhood = "Universidad",
            bedrooms = 1,
            bathrooms = 1,
            area = 18,
            amenities = listOf("Amoblado"),
            landlord = Landlord("Laura Gómez", 4.1f, "307 890 1234")
        ),
        Property(
            id = "9",
            title = "Apto en Itagüí",
            description = "Apartamento económico y bien ubicado. Cerca a centros comerciales y transporte.",
            type = PropertyType.APARTAMENTO,
            price = 1100000,
            neighborhood = "Itagüí",
            bedrooms = 2,
            bathrooms = 1,
            area = 50,
            amenities = listOf(),
            landlord = Landlord("Ricardo Silva", 4.0f, "308 901 2345")
        ),
        Property(
            id = "10",
            title = "Casa en La América",
            description = "Casa amplia con garaje para dos vehículos. Barrio tranquilo y seguro.",
            type = PropertyType.CASA,
            price = 2000000,
            neighborhood = "La América",
            bedrooms = 3,
            bathrooms = 2,
            area = 110,
            amenities = listOf("Parqueadero"),
            landlord = Landlord("Claudia Moreno", 4.5f, "309 012 3456")
        ),
        Property(
            id = "11",
            title = "Apto en Robledo",
            description = "Apartamento bien iluminado en conjunto residencial con piscina y zonas sociales.",
            type = PropertyType.APARTAMENTO,
            price = 1400000,
            neighborhood = "Robledo",
            bedrooms = 2,
            bathrooms = 2,
            area = 60,
            amenities = listOf("Gimnasio"),
            landlord = Landlord("Fernando Castro", 4.6f, "310 123 4567")
        ),
        Property(
            id = "12",
            title = "Cuarto en Laureles",
            description = "Habitación en sector premium. Baño privado y servicios incluidos.",
            type = PropertyType.CUARTO,
            price = 800000,
            neighborhood = "Laureles",
            bedrooms = 1,
            bathrooms = 1,
            area = 22,
            amenities = listOf("Amoblado", "Gimnasio"),
            landlord = Landlord("Patricia Ruiz", 4.7f, "311 234 5678")
        ),
        Property(
            id = "13",
            title = "Apto en Castilla",
            description = "Apartamento funcional cerca al metro. Ideal para personas que trabajan en el centro.",
            type = PropertyType.APARTAMENTO,
            price = 1300000,
            neighborhood = "Castilla",
            bedrooms = 2,
            bathrooms = 1,
            area = 58,
            amenities = listOf(),
            landlord = Landlord("Andrés Vargas", 4.2f, "312 345 6789")
        ),
        Property(
            id = "14",
            title = "Casa en Envigado Centro",
            description = "Casa colonial restaurada en el centro de Envigado. Cerca a parques y restaurantes.",
            type = PropertyType.CASA,
            price = 2200000,
            neighborhood = "Envigado",
            bedrooms = 4,
            bathrooms = 3,
            area = 140,
            amenities = listOf("Parqueadero", "Amoblado"),
            landlord = Landlord("Beatriz Ortiz", 4.9f, "313 456 7890")
        ),
        Property(
            id = "15",
            title = "Apto en Bello",
            description = "Apartamento nuevo en conjunto cerrado. Excelente transporte público.",
            type = PropertyType.APARTAMENTO,
            price = 1050000,
            neighborhood = "Bello",
            bedrooms = 2,
            bathrooms = 1,
            area = 52,
            amenities = listOf("Parqueadero"),
            landlord = Landlord("Gustavo Herrera", 4.3f, "314 567 8901")
        ),
        Property(
            id = "16",
            title = "Apto en Calasanz",
            description = "Apartamento con balcón y hermosa vista. Conjunto con portería 24 horas.",
            type = PropertyType.APARTAMENTO,
            price = 1350000,
            neighborhood = "Calasanz",
            bedrooms = 3,
            bathrooms = 2,
            area = 68,
            amenities = listOf("Gimnasio"),
            landlord = Landlord("Mónica Jiménez", 4.4f, "315 678 9012")
        ),
        Property(
            id = "17",
            title = "Casa en San Antonio de Prado",
            description = "Casa campestre cerca de la ciudad. Ideal para familias que buscan tranquilidad.",
            type = PropertyType.CASA,
            price = 1700000,
            neighborhood = "San Antonio de Prado",
            bedrooms = 3,
            bathrooms = 2,
            area = 130,
            amenities = listOf("Parqueadero"),
            landlord = Landlord("Alberto Mejía", 4.5f, "316 789 0123")
        ),
        Property(
            id = "18",
            title = "Cuarto en El Poblado",
            description = "Habitación en apartamento compartido en El Poblado. Zona exclusiva.",
            type = PropertyType.CUARTO,
            price = 950000,
            neighborhood = "El Poblado",
            bedrooms = 1,
            bathrooms = 1,
            area = 25,
            amenities = listOf("Amoblado", "Gimnasio", "Parqueadero"),
            landlord = Landlord("Diana Torres", 4.8f, "317 890 1234")
        ),
        Property(
            id = "19",
            title = "Apto en Conquistadores",
            description = "Apartamento moderno con acabados de lujo. Zona de alta valorización.",
            type = PropertyType.APARTAMENTO,
            price = 2500000,
            neighborhood = "Conquistadores",
            bedrooms = 2,
            bathrooms = 2,
            area = 75,
            amenities = listOf("Parqueadero", "Gimnasio"),
            landlord = Landlord("Roberto Sánchez", 4.7f, "318 901 2345")
        ),
        Property(
            id = "20",
            title = "Apto en Manrique",
            description = "Apartamento económico y seguro. Cerca a colegios y supermercados.",
            type = PropertyType.APARTAMENTO,
            price = 950000,
            neighborhood = "Manrique",
            bedrooms = 2,
            bathrooms = 1,
            area = 48,
            amenities = listOf(),
            landlord = Landlord("Lucía Ramírez", 4.1f, "319 012 3456")
        ),
        Property(
            id = "mariana-31",
            title = "Apto en Laureles Norte",
            description = "Apartamento luminoso a dos cuadras de la avenida Nutibara. Cerca a cafés, supermercados y ciclorruta.",
            type = PropertyType.APARTAMENTO,
            price = 1450000,
            neighborhood = "Laureles Norte",
            bedrooms = 2,
            bathrooms = 2,
            area = 62,
            amenities = listOf("Gimnasio"),
            landlord = Landlord("Camila Restrepo", 4.4f, "320 111 2233")
        ),
        Property(
            id = "mariana-32",
            title = "Apto con balcón en Estadio",
            description = "Apartamento remodelado a pocos minutos de la estación Estadio del metro. Balcón con vista a la unidad deportiva.",
            type = PropertyType.APARTAMENTO,
            price = 1750000,
            neighborhood = "Estadio",
            bedrooms = 3,
            bathrooms = 2,
            area = 78,
            amenities = listOf("Parqueadero", "Amoblado"),
            landlord = Landlord("Julián Cardona", 4.6f, "321 222 3344")
        ),
        Property(
            id = "mariana-33",
            title = "Apto en Guayabal",
            description = "Apartamento práctico en unidad cerrada, cerca al aeropuerto Olaya Herrera y a la avenida Guayabal.",
            type = PropertyType.APARTAMENTO,
            price = 1150000,
            neighborhood = "Guayabal",
            bedrooms = 2,
            bathrooms = 1,
            area = 54,
            amenities = listOf("Parqueadero"),
            landlord = Landlord("Natalia Zapata", 4.3f, "322 333 4455")
        ),
        Property(
            id = "mariana-34",
            title = "Cuarto económico en Buenos Aires",
            description = "Habitación amoblada en casa de familia, a pocas cuadras del tranvía de Ayacucho. Servicios e internet incluidos.",
            type = PropertyType.CUARTO,
            price = 580000,
            neighborhood = "Buenos Aires",
            bedrooms = 1,
            bathrooms = 1,
            area = 16,
            amenities = listOf("Amoblado"),
            landlord = Landlord("Gloria Henao", 4.5f, "323 444 5566")
        ),
        Property(
            id = "mariana-35",
            title = "Cuarto económico en Castilla",
            description = "Habitación independiente con baño compartido, cerca a la estación Tricentenario del metro. Ideal para estudiantes.",
            type = PropertyType.CUARTO,
            price = 560000,
            neighborhood = "Castilla",
            bedrooms = 1,
            bathrooms = 1,
            area = 14,
            amenities = listOf(),
            landlord = Landlord("Óscar Muñoz", 4.2f, "324 555 6677")
        ),
        Property(
            id = "steve-21",
            title = "Apto familiar en Aranjuez",
            description = "Apartamento iluminado cerca al Parque de Aranjuez, con fácil acceso a rutas de transporte y comercio local.",
            type = PropertyType.APARTAMENTO,
            price = 1250000,
            neighborhood = "Aranjuez",
            bedrooms = 2,
            bathrooms = 1,
            area = 55,
            amenities = listOf("Parqueadero"),
            landlord = Landlord("Daniela Vélez", 4.3f, "325 101 2020")
        ),
        Property(
            id = "steve-22",
            title = "Apto renovado en Manrique",
            description = "Apartamento recién renovado cerca a la estación Gardel del Metroplús, ideal para una pareja o familia pequeña.",
            type = PropertyType.APARTAMENTO,
            price = 980000,
            neighborhood = "Manrique",
            bedrooms = 2,
            bathrooms = 1,
            area = 50,
            amenities = listOf("Amoblado"),
            landlord = Landlord("Mateo Londoño", 4.1f, "325 202 3030")
        ),
        Property(
            id = "steve-23",
            title = "Apto en unidad cerrada de Robledo",
            description = "Apartamento en unidad residencial con zonas verdes, portería permanente y acceso cercano a universidades.",
            type = PropertyType.APARTAMENTO,
            price = 1380000,
            neighborhood = "Robledo",
            bedrooms = 3,
            bathrooms = 2,
            area = 67,
            amenities = listOf("Parqueadero", "Gimnasio"),
            landlord = Landlord("Valentina Ríos", 4.6f, "325 303 4040")
        ),
        Property(
            id = "steve-24",
            title = "Casa amplia en Bello",
            description = "Casa de dos niveles en un sector residencial de Bello, con patio y espacios cómodos para toda la familia.",
            type = PropertyType.CASA,
            price = 1650000,
            neighborhood = "Bello",
            bedrooms = 4,
            bathrooms = 2,
            area = 118,
            amenities = listOf("Parqueadero"),
            landlord = Landlord("Santiago Mejía", 4.5f, "325 404 5050")
        ),
        Property(
            id = "steve-25",
            title = "Casa moderna en Itagüí",
            description = "Casa remodelada cerca al parque principal de Itagüí, con terraza, cocina integral y buenas rutas de acceso.",
            type = PropertyType.CASA,
            price = 2050000,
            neighborhood = "Itagüí",
            bedrooms = 3,
            bathrooms = 3,
            area = 125,
            amenities = listOf("Parqueadero", "Amoblado"),
            landlord = Landlord("Paula Castaño", 4.7f, "325 505 6060")
        )
    )
}
