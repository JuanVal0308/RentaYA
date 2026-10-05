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
import com.rentaya.app.data.model.Property
import com.rentaya.app.data.model.PropertyType
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
    
    val filteredProperties = remember(searchQuery, selectedType) {
        SampleData.properties.filter { property ->
            val matchesSearch = searchQuery.isBlank() || 
                property.neighborhood.contains(searchQuery, ignoreCase = true) ||
                property.title.contains(searchQuery, ignoreCase = true)
            val matchesType = selectedType == null || property.type == selectedType
            matchesSearch && matchesType
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "RentaYa",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

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
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Map,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "× Mapa de Medellín ×",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "${filteredProperties.size} propiedades",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Propiedades destacadas",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            filteredProperties.take(5).forEach { property ->
                PropertyCard(
                    property = property,
                    onClick = { onPropertyClick(property.id) }
                )
            }
            
            if (filteredProperties.size > 5) {
                Button(
                    onClick = onFilterResults,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ver ${filteredProperties.size - 5} más")
                }
            }
        }
    }

    if (showFilters) {
        FiltersBottomSheet(
            onDismiss = { showFilters = false },
            onApply = { 
                showFilters = false
                onFilterResults()
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
                Icon(
                    Icons.Default.Home,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(64.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
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
    onDismiss: () -> Unit,
    onApply: () -> Unit
) {
    var priceRange by remember { mutableStateOf(0f..5000000f) }
    var selectedBedrooms by remember { mutableStateOf<Int?>(null) }
    var hasParking by remember { mutableStateOf(false) }
    var isFurnished by remember { mutableStateOf(false) }
    
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
                TextButton(onClick = onDismiss) {
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

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onApply,
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
