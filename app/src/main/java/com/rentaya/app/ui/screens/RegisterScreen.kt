package com.rentaya.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.rentaya.app.data.UserPreferences
import com.rentaya.app.data.repositorio.RepositorioUsuarios
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNeedsVerification: (String) -> Unit,
    onBack: () -> Unit,
    userPreferences: UserPreferences
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var acceptTerms by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Volver")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { 
                name = it
                errorMessage = ""
            },
            label = { Text("Nombre") },
            leadingIcon = { Icon(Icons.Default.Person, null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !cargando
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { 
                email = it
                errorMessage = ""
            },
            label = { Text("Correo") },
            leadingIcon = { Icon(Icons.Default.Email, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !cargando
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { 
                phone = it
                errorMessage = ""
            },
            label = { Text("Teléfono") },
            leadingIcon = { Icon(Icons.Default.Phone, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !cargando
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { 
                password = it
                errorMessage = ""
            },
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Ocultar" else "Mostrar"
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !cargando
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = acceptTerms,
                onCheckedChange = { acceptTerms = it },
                enabled = !cargando
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Acepto los Términos y Condiciones",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val correoLimpio = email.trim()
                val telefonoDigitos = phone.filter { it.isDigit() }
                val formatoCorreo = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
                when {
                    name.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank() -> {
                        errorMessage = "Por favor completa todos los campos"
                    }
                    !formatoCorreo.matches(correoLimpio) -> {
                        errorMessage = "Ingresa un correo válido (ejemplo@dominio.com)"
                    }
                    telefonoDigitos.length != 10 -> {
                        errorMessage = "El teléfono debe tener 10 dígitos (Colombia)"
                    }
                    !acceptTerms -> {
                        errorMessage = "Debes aceptar los términos y condiciones"
                    }
                    else -> {
                        cargando = true
                        errorMessage = ""
                        scope.launch {
                            val resultado = RepositorioUsuarios.registrar(
                                nombre = name.trim(),
                                correo = correoLimpio,
                                telefono = telefonoDigitos,
                                clave = password
                            )
                            cargando = false
                            resultado.fold(
                                onSuccess = { auth ->
                                    if (auth.requiereConfirmacion) {
                                        onNeedsVerification(auth.usuario.email)
                                    } else {
                                        userPreferences.login(
                                            auth.usuario.email,
                                            auth.usuario.name,
                                            auth.usuario.phone,
                                            auth.usuario.emailConfirmed
                                        )
                                        onRegisterSuccess()
                                    }
                                },
                                onFailure = { error ->
                                    errorMessage = error.message
                                        ?: "No se pudo crear la cuenta"
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
                Text("Crear Cuenta")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("¿Ya tienes cuenta? ")
            TextButton(onClick = onBack, enabled = !cargando) {
                Text("Inicia sesión")
            }
        }
    }
}
