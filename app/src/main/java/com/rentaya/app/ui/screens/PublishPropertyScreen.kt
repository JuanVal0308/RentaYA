package com.rentaya.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.rentaya.app.BuildConfig
import com.rentaya.app.data.CoordenadasBarrios
import com.rentaya.app.data.UserPreferences
import com.rentaya.app.data.model.Landlord
import com.rentaya.app.data.model.Property
import com.rentaya.app.data.model.PropertyType
import com.rentaya.app.data.remoto.ClienteSupabase
import com.rentaya.app.data.repositorio.RepositorioPropiedades
import com.rentaya.app.ui.components.MapaOsm
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishPropertyScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    onVerificarCorreo: () -> Unit = {},
    userPreferences: UserPreferences? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
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
    var mensajeCarga by remember { mutableStateOf("Publicando…") }
    var fotos by remember { mutableStateOf<List<String>>(emptyList()) }
    var pinManual by remember { mutableStateOf(false) }
    var latitud by remember { mutableStateOf(CoordenadasBarrios.LAT_CENTRO) }
    var longitud by remember { mutableStateOf(CoordenadasBarrios.LNG_CENTRO) }
    var fotoCamara by remember { mutableStateOf<Uri?>(null) }
    val idPropiedad = remember { UUID.randomUUID().toString() }

    LaunchedEffect(neighborhood) {
        if (!pinManual && neighborhood.isNotBlank()) {
            val (lat, lng) = CoordenadasBarrios.de(neighborhood, idPropiedad)
            latitud = lat
            longitud = lng
        }
    }

    val selectorGaleria = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(maxItems = 8)
    ) { uris ->
        if (uris.isNotEmpty()) {
            fotos = (fotos + uris.map { it.toString() }).distinct().take(8)
            errorMessage = ""
        }
    }

    val tomadorFoto = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { ok ->
        if (ok) {
            fotoCamara?.let { uri ->
                fotos = (fotos + uri.toString()).distinct().take(8)
            }
        }
    }

    fun abrirCamara() {
        val dir = File(context.cacheDir, "fotos").apply { mkdirs() }
        val archivo = File(dir, "camara_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            archivo
        )
        fotoCamara = uri
        tomadorFoto.launch(uri)
    }

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
                text = "Fotos del inmueble",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                "Elige 1 o más fotos. Si hay red se suben a Storage; si no, se ven en esta sesión.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(fotos) { uri ->
                    Box {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Foto del inmueble",
                            modifier = Modifier
                                .size(96.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { fotos = fotos.filterNot { it == uri } },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Quitar foto",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
                item {
                    OutlinedButton(
                        onClick = {
                            selectorGaleria.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.height(96.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                            Text("Galería", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                item {
                    OutlinedButton(
                        onClick = { abrirCamara() },
                        modifier = Modifier.height(96.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null)
                            Text("Cámara", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
            if (fotos.isEmpty()) {
                Text(
                    "Aún no hay fotos. Se usará una imagen de muestra según el tipo.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

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
                    pinManual = false
                    errorMessage = ""
                },
                label = { Text("Barrio") },
                placeholder = { Text("Ej: El Poblado") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(CoordenadasBarrios.nombresSugeridos) { barrio ->
                    FilterChip(
                        selected = neighborhood.equals(barrio, ignoreCase = true),
                        onClick = {
                            neighborhood = barrio
                            pinManual = false
                        },
                        label = { Text(barrio) }
                    )
                }
            }

            Text("Ubicación en el mapa", style = MaterialTheme.typography.titleMedium)
            Text(
                "Toca el mapa para ajustar el pin. Por defecto usamos el barrio.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            MapaOsm(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                pin = latitud to longitud,
                zoom = 14.0,
                permitirToque = true,
                onToqueMapa = { lat, lng ->
                    pinManual = true
                    latitud = lat
                    longitud = lng
                }
            )
            Text(
                "Lat ${"%.5f".format(latitud)} · Lng ${"%.5f".format(longitud)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Tipo de propiedad",
                style = MaterialTheme.typography.titleMedium
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = hasParking, onCheckedChange = { hasParking = it })
                Text("Parqueadero")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isFurnished, onCheckedChange = { isFurnished = it })
                Text("Amoblado")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
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
            if (cargando) {
                Text(
                    text = mensajeCarga,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Button(
                onClick = {
                    val precio = price.toIntOrNull()
                    val areaM2 = area.toIntOrNull()
                    val habitaciones = bedrooms.toIntOrNull()
                    when {
                        !ClienteSupabase.puedePublicar() -> {
                            errorMessage = "Confirma tu correo para publicar inmuebles."
                            onVerificarCorreo()
                        }
                        title.isBlank() -> errorMessage = "El título es obligatorio"
                        description.isBlank() -> errorMessage = "La descripción es obligatoria"
                        neighborhood.isBlank() -> errorMessage = "El barrio es obligatorio"
                        price.isBlank() -> errorMessage = "El precio es obligatorio"
                        bedrooms.isBlank() -> errorMessage = "Las habitaciones son obligatorias"
                        bathrooms.isBlank() -> errorMessage = "Los baños son obligatorios"
                        area.isBlank() -> errorMessage = "El área es obligatoria"
                        precio == null || precio <= 0 ->
                            errorMessage = "El precio debe ser un número válido mayor que 0"
                        areaM2 == null || areaM2 <= 0 ->
                            errorMessage = "El área debe ser un número válido mayor que 0"
                        habitaciones == null || habitaciones < 1 ->
                            errorMessage = "Debe tener al menos 1 habitación"
                        else -> {
                            val amenities = mutableListOf<String>()
                            if (hasParking) amenities.add("Parqueadero")
                            if (isFurnished) amenities.add("Amoblado")
                            if (hasGym) amenities.add("Gimnasio")

                            cargando = true
                            errorMessage = ""
                            mensajeCarga = if (fotos.isNotEmpty()) "Subiendo fotos…" else "Publicando…"
                            scope.launch {
                                val nombre = userPreferences?.userName?.first()?.ifBlank { "Arrendador" }
                                    ?: "Arrendador"
                                val telefono = ""
                                val imagenes = RepositorioPropiedades.resolverImagenesPublicacion(
                                    context = context,
                                    idPropiedad = idPropiedad,
                                    uris = fotos
                                )
                                mensajeCarga = "Publicando inmueble…"
                                val propiedad = Property(
                                    id = idPropiedad,
                                    title = title.trim(),
                                    description = description.trim(),
                                    type = selectedType,
                                    price = precio,
                                    neighborhood = neighborhood.trim(),
                                    bedrooms = habitaciones,
                                    bathrooms = bathrooms.toIntOrNull() ?: 0,
                                    area = areaM2,
                                    amenities = amenities,
                                    landlord = Landlord(nombre, 4.0f, telefono),
                                    imageRes = imagenes.firstOrNull() ?: "sample",
                                    imagenes = imagenes,
                                    latitude = latitud,
                                    longitude = longitud
                                )
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

            if (BuildConfig.DEBUG && BuildConfig.DEBUG_OMITIR_VERIFICACION_CORREO) {
                Text(
                    "Debug: se omite la verificación de correo (solo depuración).",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
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
