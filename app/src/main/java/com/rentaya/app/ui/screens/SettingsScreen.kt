package com.rentaya.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rentaya.app.data.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    userPreferences: UserPreferences
) {
    val scope = rememberCoroutineScope()
    var darkMode by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        darkMode = userPreferences.darkMode.first()
        notificationsEnabled = userPreferences.notificationsEnabled.first()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
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
        ) {
            Text(
                text = "Apariencia",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                ListItem(
                    headlineContent = { Text("Modo oscuro") },
                    leadingContent = { Icon(Icons.Default.DarkMode, null) },
                    trailingContent = {
                        Switch(
                            checked = darkMode,
                            onCheckedChange = {
                                darkMode = it
                                scope.launch {
                                    userPreferences.setDarkMode(it)
                                }
                            }
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Notificaciones",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                ListItem(
                    headlineContent = { Text("Activar notificaciones") },
                    leadingContent = { Icon(Icons.Default.Notifications, null) },
                    trailingContent = {
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = {
                                notificationsEnabled = it
                                scope.launch {
                                    userPreferences.setNotifications(it)
                                }
                            }
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Cuenta",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                ListItem(
                    headlineContent = { Text("Acerca de") },
                    leadingContent = { Icon(Icons.Default.Info, null) },
                    trailingContent = { Icon(Icons.Default.ChevronRight, null) },
                    modifier = Modifier.clickable { /* TODO(equipo - Steve): Navegar a AboutScreen (Acerca de) */ }
                )
                HorizontalDivider()
                ListItem(
                    headlineContent = { 
                        Text(
                            "Cerrar sesión",
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    leadingContent = { Icon(Icons.Default.Logout, null, tint = MaterialTheme.colorScheme.error) },
                    modifier = Modifier.clickable { showLogoutDialog = true }
                )
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            userPreferences.logout()
                            showLogoutDialog = false
                            onLogout()
                        }
                    }
                ) {
                    Text("Cerrar sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
