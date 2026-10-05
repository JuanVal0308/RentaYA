package com.rentaya.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rentaya.app.data.model.Landlord
import com.rentaya.app.data.model.Property
import com.rentaya.app.data.model.PropertyType
import com.rentaya.app.data.repositorio.RepositorioPropiedades
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishPropertyScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var neighborhood by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var bedrooms by remember { mutableStateOf("") }
    var bathrooms by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(PropertyType.APARTAMENTO) }
    var hasParking by remember { mutableStateOf(false) }
    var isFurnished by remember { mutableStateOf(false) }
    var hasGym by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var cargando by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    // Mismo id en cada intento: reintentar tras un error no duplica la publicación local.
    val idPropiedad = remember { UUID.randomUUID().toString() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publicar Inmueble") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Información básica",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = title,
                onValueChange = { 
                    title = it
                    errorMessage = ""
                },
                label = { Text("Título") },
                placeholder = { Text("Ej: Apto en El Poblado") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { 
                    description = it
                    errorMessage = ""
                },
                label = { Text("Descripción") },
                placeholder = { Text("Describe tu propiedad...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            OutlinedTextField(
                value = neighborhood,
                onValueChange = { 
                    neighborhood = it
                    errorMessage = ""
                },
                label = { Text("Barrio") },
                placeholder = { Text("Ej: El Poblado") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text(
                text = "Tipo de propiedad",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PropertyType.values().forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        label = { Text(type.displayName) }
                    )
                }
            }

            Text(
                text = "Características",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { 
                        price = it.filter { char -> char.isDigit() }
                        errorMessage = ""
                    },
                    label = { Text("Precio/mes") },
                    leadingIcon = { Text("$") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = area,
                    onValueChange = { 
                        area = it.filter { char -> char.isDigit() }
                        errorMessage = ""
                    },
                    label = { Text("Área m²") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = bedrooms,
                    onValueChange = { 
                        bedrooms = it.filter { char -> char.isDigit() }
                        errorMessage = ""
                    },
                    label = { Text("Habitaciones") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = bathrooms,
                    onValueChange = { 
                        bathrooms = it.filter { char -> char.isDigit() }
                        errorMessage = ""
                    },
                    label = { Text("Baños") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Text(
                text = "Servicios adicionales",
                style = MaterialTheme.typography.titleMedium
            )

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Checkbox(checked = hasParking, onCheckedChange = { hasParking = it })
                Text("Parqueadero")
            }

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Checkbox(checked = isFurnished, onCheckedChange = { isFurnished = it })
                Text("Amoblado")
            }

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Checkbox(checked = hasGym, onCheckedChange = { hasGym = it })
                Text("Gimnasio")
            }

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = {
                    when {
                        title.isBlank() -> errorMessage = "El título es obligatorio"
                        description.isBlank() -> errorMessage = "La descripción es obligatoria"
                        neighborhood.isBlank() -> errorMessage = "El barrio es obligatorio"
                        price.isBlank() -> errorMessage = "El precio es obligatorio"
                        bedrooms.isBlank() -> errorMessage = "Las habitaciones son obligatorias"
                        bathrooms.isBlank() -> errorMessage = "Los baños son obligatorios"
                        area.isBlank() -> errorMessage = "El área es obligatoria"
                        else -> {
                            val amenities = mutableListOf<String>()
                            if (hasParking) amenities.add("Parqueadero")
                            if (isFurnished) amenities.add("Amoblado")
                            if (hasGym) amenities.add("Gimnasio")

                            // TODO(equipo - Mariana): Validar precio > 0, área > 0, habitaciones >= 1
                            val propiedad = Property(
                                id = idPropiedad,
                                title = title.trim(),
                                description = description.trim(),
                                type = selectedType,
                                price = price.toIntOrNull() ?: 0,
                                neighborhood = neighborhood.trim(),
                                bedrooms = bedrooms.toIntOrNull() ?: 0,
                                bathrooms = bathrooms.toIntOrNull() ?: 0,
                                area = area.toIntOrNull() ?: 0,
                                amenities = amenities,
                                landlord = Landlord("Arrendador", 4.0f)
                            )
                            cargando = true
                            errorMessage = ""
                            scope.launch {
                                val resultado = RepositorioPropiedades.publicarPropiedad(propiedad)
                                cargando = false
                                resultado.fold(
                                    onSuccess = { showSuccessDialog = true },
                                    onFailure = { error ->
                                        errorMessage = error.message
                                            ?: "No se pudo publicar la propiedad"
                                    }
                                )
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !cargando
            ) {
                if (cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Publicar")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("¡Publicación exitosa!") },
            text = { Text("Tu propiedad ha sido publicada correctamente y estará visible para los arrendatarios.") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onSuccess()
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }
}
