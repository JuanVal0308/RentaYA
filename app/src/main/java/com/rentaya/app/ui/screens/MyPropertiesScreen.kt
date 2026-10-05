package com.rentaya.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rentaya.app.data.repositorio.RepositorioPropiedades

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPropertiesScreen(
    onBack: () -> Unit,
    onPropertyClick: (String) -> Unit,
    onPublish: () -> Unit
) {
    // Publicaciones hechas en esta sesión de la app (ver RepositorioPropiedades).
    val misPublicaciones = remember { RepositorioPropiedades.obtenerPublicadasLocalmente() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis publicaciones") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (misPublicaciones.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Aún no has publicado inmuebles",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Publica tu primera propiedad para verla aquí",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onPublish) {
                        Text("Publicar inmueble")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(misPublicaciones) { property ->
                    PropertyCard(
                        property = property,
                        onClick = { onPropertyClick(property.id) }
                    )
                }
            }
        }
    }
}
