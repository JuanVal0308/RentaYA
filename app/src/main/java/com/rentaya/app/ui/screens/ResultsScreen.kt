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
import com.rentaya.app.data.SampleData
import com.rentaya.app.data.repositorio.RepositorioPropiedades

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    onPropertyClick: (String) -> Unit,
    onBack: () -> Unit
) {
    var showMapView by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Resultados (${RepositorioPropiedades.listarLocal().size})") },
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Map,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Vista de mapa",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "${RepositorioPropiedades.listarLocal().size} propiedades",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(RepositorioPropiedades.listarLocal()) { property ->
                    PropertyCard(
                        property = property,
                        onClick = { onPropertyClick(property.id) }
                    )
                }
            }
        }
    }
}
