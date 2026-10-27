package com.rentaya.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rentaya.app.data.SampleData
import com.rentaya.app.data.repositorio.RepositorioPropiedades
import com.rentaya.app.data.model.Property
import com.rentaya.app.data.model.PropertyType
import com.rentaya.app.ui.components.ImagenPrincipalPropiedad
import com.rentaya.app.ui.components.MapaOsm
import com.rentaya.app.ui.components.RentaYaTopBar
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onPropertyClick: (String) -> Unit,
    onFilterResults: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<PropertyType?>(null) }
    var showFilters by remember { mutableStateOf(false) }
    var precioMaximo by remember { mutableStateOf(5_000_000) }
    var soloMascotas by remember { mutableStateOf(false) }
    var propiedades by remember { mutableStateOf(RepositorioPropiedades.listarLocal()) }
    val currency = remember {
        NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
            maximumFractionDigits = 0
        }
    }
    var propiedadEnMapa by remember { mutableStateOf<Property?>(null) }
    var cargandoLista by remember { mutableStateOf(true) }
    var mapaExpandido by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        propiedades = RepositorioPropiedades.listar()
        cargandoLista = false
    }

    val filteredProperties = remember(
        searchQuery,
        selectedType,
        precioMaximo,
        soloMascotas,
        propiedades
    ) {
        propiedades.filter { property ->
            val matchesSearch = searchQuery.isBlank() ||
                property.neighborhood.contains(searchQuery, ignoreCase = true) ||
                property.title.contains(searchQuery, ignoreCase = true)
            val matchesType = selectedType == null || property.type == selectedType
            val matchesPrice = property.price <= precioMaximo
            val matchesPets = !soloMascotas || property.amenities.any {
                it.equals("Mascotas", ignoreCase = true)
            }
            matchesSearch && matchesType && matchesPrice && matchesPets
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        RentaYaTopBar(
            title = "RentaYa"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar barrio...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { showFilters = true },
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.medium
                    )
            ) {
                Icon(Icons.Default.FilterList, "Filtros", tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(PropertyType.values().toList()) { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = {
                        selectedType = if (selectedType == type) null else type
                    },
                    label = { Text(type.displayName) }
                )
            }
            item {
                FilterChip(
                    selected = precioMaximo < 5_000_000,
                    onClick = { showFilters = true },
                    label = {
                        Text(
                            if (precioMaximo < 5_000_000)
                                "Hasta ${currency.format(precioMaximo)}"
                            else
                                "Precio máx."
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.AttachMoney, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Vista de mapa y lista con interacción mejorada
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // Mapa de fondo (se expande cuando mapaExpandido = true)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                MapaOsm(
                    modifier = Modifier.fillMaxSize(),
                    propiedades = filteredProperties,
                    zoom = if (mapaExpandido) 11.8 else 11.6,
                    onMarcador = { propiedadEnMapa = it }
                )
                
                // Contador de inmuebles
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                ) {
                    Text(
                        "${filteredProperties.size} inmuebles en Medellín",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                // Botón para expandir/colapsar mapa
                FloatingActionButton(
                    onClick = { mapaExpandido = !mapaExpandido },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        if (mapaExpandido) Icons.Default.List else Icons.Default.Map,
                        if (mapaExpandido) "Ver lista" else "Expandir mapa"
                    )
                }
            }

            // Panel de lista que se desliza sobre el mapa
            if (!mapaExpandido) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.6f)
                        .align(Alignment.BottomCenter),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        // Indicador de arrastre
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                            )
                        }

                        Text(
                            text = "Propiedades destacadas",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Lista de propiedades
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (cargandoLista) {
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                            }
                            if (filteredProperties.isEmpty() && !cargandoLista) {
                                Text(
                                    "No hay propiedades que coincidan con tu búsqueda.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 24.dp)
                                )
                            }
                            filteredProperties.take(5).forEach { property ->
                                PropertyCard(
                                    property = property,
                                    onClick = { onPropertyClick(property.id) }
                                )
                            }

                            if (filteredProperties.size > 5) {
                                Button(
                                    onClick = onFilterResults,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp)
                                ) {
                                    Text("Ver ${filteredProperties.size - 5} más")
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
        }
    }

    propiedadEnMapa?.let { seleccionada ->
        ModalBottomSheet(onDismissRequest = { propiedadEnMapa = null }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(seleccionada.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${formatPrice(seleccionada.price)}/mes · ${seleccionada.neighborhood}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Button(
                    onClick = {
                        val id = seleccionada.id
                        propiedadEnMapa = null
                        onPropertyClick(id)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ver detalle")
                }
            }
        }
    }

    if (showFilters) {
        FiltersBottomSheet(
            precioMaximoInicial = precioMaximo,
            soloMascotasInicial = soloMascotas,
            onDismiss = { showFilters = false },
            onApply = { nuevoMaximo, nuevoSoloMascotas ->
                precioMaximo = nuevoMaximo
                soloMascotas = nuevoSoloMascotas
                showFilters = false
            }
        )
    }
}

@Composable
fun PropertyCard(
    property: Property,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFavorite by remember { mutableStateOf(SampleData.isFavorite(property.id)) }
    
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                ImagenPrincipalPropiedad(
                    property = property,
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = property.title
                )
                
                IconButton(
                    onClick = { 
                        SampleData.toggleFavorite(property.id)
                        isFavorite = !isFavorite
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (isFavorite) Color.Red else Color.White
                    )
                }
            }

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = property.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "${formatPrice(property.price)}/mes",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = property.neighborhood,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersBottomSheet(
    precioMaximoInicial: Int = 5_000_000,
    soloMascotasInicial: Boolean = false,
    onDismiss: () -> Unit,
    onApply: (Int, Boolean) -> Unit
) {
    var priceRange by remember {
        mutableStateOf(0f..precioMaximoInicial.toFloat().coerceIn(0f, 5_000_000f))
    }
    var selectedBedrooms by remember { mutableStateOf<Int?>(null) }
    var hasParking by remember { mutableStateOf(false) }
    var isFurnished by remember { mutableStateOf(false) }
    var soloMascotas by remember { mutableStateOf(soloMascotasInicial) }
    val precioMaximoActual = priceRange.endInclusive.toInt()
    
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = {
                    priceRange = 0f..5_000_000f
                    soloMascotas = false
                    onApply(5_000_000, false)
                }) {
                    Text("Limpiar")
                }
                Text("Filtros", style = MaterialTheme.typography.titleLarge)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, "Cerrar")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Tipo", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(PropertyType.values().toList()) { type ->
                    FilterChip(
                        selected = false,
                        onClick = { },
                        label = { Text(type.displayName) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Precio", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            AssistChip(
                onClick = { },
                label = {
                    Text("Precio máximo: ${formatPrice(precioMaximoActual)}")
                },
                leadingIcon = {
                    Icon(Icons.Default.AttachMoney, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            )
            Text(
                "${formatPrice(priceRange.start.toInt())} - ${formatPrice(priceRange.endInclusive.toInt())}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            RangeSlider(
                value = priceRange,
                onValueChange = { priceRange = it },
                valueRange = 0f..5000000f,
                steps = 20
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Habitaciones", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(1, 2, 3, 4).forEach { beds ->
                    FilterChip(
                        selected = selectedBedrooms == beds,
                        onClick = { 
                            selectedBedrooms = if (selectedBedrooms == beds) null else beds
                        },
                        label = { Text("$beds+") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Servicios", style = MaterialTheme.typography.titleMedium)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = hasParking, onCheckedChange = { hasParking = it })
                Text("Parqueadero")
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = isFurnished, onCheckedChange = { isFurnished = it })
                Text("Amoblado")
            }

            Spacer(modifier = Modifier.height(8.dp))

            FilterChip(
                selected = soloMascotas,
                onClick = { soloMascotas = !soloMascotas },
                label = { Text("Mascotas") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Pets,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onApply(precioMaximoActual, soloMascotas) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Ver resultados")
            }
        }
    }
}

fun formatPrice(price: Int): String {
    return NumberFormat.getCurrencyInstance(Locale("es", "CO"))
        .format(price)
        .replace("COP", "$")
        .replace(",00", "")
}
