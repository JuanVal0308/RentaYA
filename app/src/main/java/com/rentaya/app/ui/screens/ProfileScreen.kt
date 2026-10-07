package com.rentaya.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.rentaya.app.BuildConfig
import com.rentaya.app.data.UserPreferences
import com.rentaya.app.data.remoto.ClienteSupabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onSettings: () -> Unit,
    onCredits: () -> Unit,
    onPublish: () -> Unit,
    onMyProperties: () -> Unit,
    onVerificarCorreo: (String) -> Unit = {},
    userPreferences: UserPreferences
) {
    val scope = rememberCoroutineScope()
    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var correoConfirmado by remember { mutableStateOf(true) }
    var avisoVerificacion by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userName = userPreferences.userName.first()
        userEmail = userPreferences.userEmail.first()
        correoConfirmado = userPreferences.emailConfirmed.first() &&
            (ClienteSupabase.puedePublicar() || !ClienteSupabase.estaConfigurado)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Perfil",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.firstOrNull()?.uppercase() ?: "U",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = userName,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = userEmail,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (!correoConfirmado && !BuildConfig.DEBUG_OMITIR_VERIFICACION_CORREO) {
                Spacer(modifier = Modifier.height(12.dp))
                AssistChip(
                    onClick = { onVerificarCorreo(userEmail) },
                    label = { Text("Correo sin confirmar") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                ListItem(
                    headlineContent = { Text("Publicar inmueble") },
                    leadingContent = { Icon(Icons.Default.Add, null) },
                    trailingContent = { Icon(Icons.Default.ChevronRight, null) },
                    modifier = Modifier.clickable {
                        if (!ClienteSupabase.puedePublicar()) {
                            avisoVerificacion = true
                        } else {
                            onPublish()
                        }
                    }
                )
                HorizontalDivider()
                ListItem(
                    headlineContent = { Text("Mis publicaciones") },
                    leadingContent = { Icon(Icons.Default.Home, null) },
                    trailingContent = { Icon(Icons.Default.ChevronRight, null) },
                    modifier = Modifier.clickable(onClick = onMyProperties)
                )
                HorizontalDivider()
                ListItem(
                    headlineContent = { Text("Configuración") },
                    leadingContent = { Icon(Icons.Default.Settings, null) },
                    trailingContent = { Icon(Icons.Default.ChevronRight, null) },
                    modifier = Modifier.clickable(onClick = onSettings)
                )
                HorizontalDivider()
                ListItem(
                    headlineContent = { Text("Créditos") },
                    leadingContent = { Icon(Icons.Default.Info, null) },
                    trailingContent = { Icon(Icons.Default.ChevronRight, null) },
                    modifier = Modifier.clickable(onClick = onCredits)
                )
            }
        }
    }

    if (avisoVerificacion) {
        AlertDialog(
            onDismissRequest = { avisoVerificacion = false },
            title = { Text("Confirma tu correo") },
            text = { Text("Para publicar un inmueble debes verificar tu correo. Revisa la bandeja de entrada o reenvía el enlace.") },
            confirmButton = {
                Button(onClick = {
                    avisoVerificacion = false
                    onVerificarCorreo(userEmail)
                }) {
                    Text("Revisar correo")
                }
            },
            dismissButton = {
                TextButton(onClick = { avisoVerificacion = false }) {
                    Text("Ahora no")
                }
            }
        )
    }
}
