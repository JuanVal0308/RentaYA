package com.rentaya.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rentaya.app.data.SampleData
import com.rentaya.app.data.repositorio.RepositorioPropiedades

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onPropertyClick: (String) -> Unit
) {
    val favoriteIds by SampleData.favoritesFlow.collectAsState()
    val favoriteProperties = remember(favoriteIds) {
        RepositorioPropiedades.listarLocal().filter { it.id in favoriteIds }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    if (favoriteProperties.isEmpty()) {
                        "Favoritos"
                    } else {
                        "${favoriteProperties.size} favoritos"
                    }
                )
            }
        )

        if (favoriteProperties.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "No tienes favoritos aún",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Guarda propiedades para verlas aquí",
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
                items(favoriteProperties) { property ->
                    PropertyCard(
                        property = property,
                        onClick = { onPropertyClick(property.id) }
                    )
                }
            }
        }
    }
}
