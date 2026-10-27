package com.rentaya.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/**
 * TopAppBar unificado de RentaYa con logo en la esquina superior derecha.
 * Incluye botón de navegación opcional (atrás) para pantallas secundarias.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentaYaTopBar(
    title: String,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit) = {}
) {
    TopAppBar(
        title = { Text(title) },
        modifier = modifier,
        navigationIcon = {
            if (navigationIcon != null) {
                navigationIcon()
            } else if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, "Volver")
                }
            }
        },
        actions = {
            // Acciones adicionales
            actions()
            
            // Logo de RentaYa siempre visible en la esquina superior derecha
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = com.rentaya.app.R.drawable.ic_launcher_foreground),
                    contentDescription = "RentaYa",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    )
}
