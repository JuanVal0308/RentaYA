package com.rentaya.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rentaya.app.data.repositorio.RepositorioUsuarios
import kotlinx.coroutines.launch

@Composable
fun VerifyEmailScreen(
    correo: String,
    onIrALogin: () -> Unit,
    onAtras: () -> Unit
) {
    var cargando by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Email,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Revisa tu correo",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            if (correo.isNotBlank()) {
                "Enviamos un enlace de verificación a $correo. Ábrelo para activar tu cuenta y luego inicia sesión."
            } else {
                "Enviamos un enlace de verificación. Ábrelo para activar tu cuenta y luego inicia sesión."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (mensaje.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                mensaje,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
        if (error.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedButton(
            onClick = {
                if (correo.isBlank()) {
                    error = "No hay un correo para reenviar"
                    return@OutlinedButton
                }
                cargando = true
                error = ""
                mensaje = ""
                scope.launch {
                    val resultado = RepositorioUsuarios.reenviarConfirmacion(correo)
                    cargando = false
                    resultado.fold(
                        onSuccess = { mensaje = "Correo reenviado. Revisa también spam." },
                        onFailure = { err ->
                            error = err.message ?: "No se pudo reenviar el correo"
                        }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !cargando
        ) {
            if (cargando) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Reenviar verificación")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onIrALogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = !cargando
        ) {
            Text("Ya confirmé, iniciar sesión")
        }
        TextButton(onClick = onAtras, enabled = !cargando) {
            Text("Volver")
        }
    }
}
