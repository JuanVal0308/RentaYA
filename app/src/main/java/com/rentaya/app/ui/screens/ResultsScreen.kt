package com.rentaya.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rentaya.app.data.model.Property
import com.rentaya.app.data.repositorio.RepositorioPropiedades
import com.rentaya.app.ui.components.MapaOsm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    onPropertyClick: (String) -> Unit,
    onBack: () -> Unit
) {
    var showMapView by remember { mutableStateOf(false) }
    val propiedades = remember { RepositorioPropiedades.listarLocal() }
    var propiedadEnMapa by remember { mutableStateOf<Property?>(null) }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Resultados (${propiedades.size})") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Volver")
                }
            },
            actions = {
                IconButton(onClick = { showMapView = !showMapView }) {
                    Icon(
                        if (showMapView) Icons.Default.List else Icons.Default.Map,
                        if (showMapView) "Ver lista" else "Ver mapa"
                    )
                }
            }
        )

        if (showMapView) {
            Box(modifier = Modifier.fillMaxSize()) {
                MapaOsm(
                    modifier = Modifier.fillMaxSize(),
                    propiedades = propiedades,
                    zoom = 11.6,
                    onMarcador = { propiedadEnMapa = it }
                )
            }
        } else if (propiedades.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No hay resultados para mostrar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(propiedades) { property ->
                    PropertyCard(
                        property = property,
                        onClick = { onPropertyClick(property.id) }
                    )
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
                    .padding(bottom = 28.dp)
            ) {
                Text(seleccionada.title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "${formatPrice(seleccionada.price)}/mes · ${seleccionada.neighborhood}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
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
}
