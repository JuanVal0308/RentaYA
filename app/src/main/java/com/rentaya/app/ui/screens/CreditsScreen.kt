package com.rentaya.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

private val tecnologiasUsadas = listOf(
    "Kotlin" to "Lenguaje de programación de la app",
    "Jetpack Compose" to "Interfaz declarativa con Material 3",
    "Navigation Compose" to "Navegación entre pantallas",
    "DataStore" to "Sesión y preferencias guardadas en el dispositivo",
    "Supabase" to "Autenticación y base de datos en la nube",
    "OkHttp" to "Conexión HTTP con Supabase"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditsScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Créditos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "RentaYa",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Aplicación Móvil - Entrega 3",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Universidad Pontificia Bolivariana",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Equipo de Desarrollo",
                style = MaterialTheme.typography.titleMedium
            )

            TeamMemberCard(
                name = "Juan Pablo Martinez Romero",
                role = "Desarrollador"
            )

            // TODO(equipo - Steve): Reemplazar "Integrante 2" con tu nombre completo y rol
            TeamMemberCard(
                name = "Integrante 2",
                role = "Desarrollador"
            )

            TeamMemberCard(
                name = "Mariana Osorio",
                role = "Ingeniera"
            )

            Text(
                text = "Tecnologías usadas",
                style = MaterialTheme.typography.titleMedium
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    tecnologiasUsadas.forEachIndexed { index, (nombre, descripcion) ->
                        if (index > 0) HorizontalDivider()
                        ListItem(
                            headlineContent = { Text(nombre) },
                            supportingContent = { Text(descripcion) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Versión 1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "© 2026 RentaYa",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TeamMemberCard(
    name: String,
    role: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.first().uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = role,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
