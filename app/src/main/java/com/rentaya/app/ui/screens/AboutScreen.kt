package com.rentaya.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rentaya.app.BuildConfig

private val aboutTechnologies = listOf(
    "Kotlin" to "Lenguaje principal de desarrollo",
    "Jetpack Compose" to "Interfaz de usuario declarativa",
    "Material 3" to "Componentes y sistema visual",
    "Navigation Compose" to "Navegación entre pantallas",
    "DataStore" to "Preferencias y sesión local",
    "Supabase" to "Autenticación y datos remotos"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Acerca de") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "RentaYa",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Versión ${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "RentaYa conecta a personas que buscan vivienda con propietarios que ofrecen inmuebles en Medellín y sus alrededores.",
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "Proyecto académico de la Universidad Pontificia Bolivariana.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tecnologías utilizadas",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column {
                    aboutTechnologies.forEachIndexed { index, (name, description) ->
                        if (index > 0) HorizontalDivider()
                        ListItem(
                            headlineContent = { Text(name) },
                            supportingContent = { Text(description) }
                        )
                    }
                }
            }
        }
    }
}
