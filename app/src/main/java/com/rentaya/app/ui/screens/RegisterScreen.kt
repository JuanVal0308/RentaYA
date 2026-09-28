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
import com.rentaya.app.data.SampleData
import com.rentaya.app.data.UserPreferences
import com.rentaya.app.data.model.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
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
            singleLine = true
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
            singleLine = true
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
            singleLine = true
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
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = acceptTerms,
                onCheckedChange = { acceptTerms = it }
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
                when {
                    name.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank() -> {
                        errorMessage = "Por favor completa todos los campos"
                    }
                    !acceptTerms -> {
                        errorMessage = "Debes aceptar los términos y condiciones"
                    }
                    SampleData.isEmailRegistered(email) -> {
                        errorMessage = "Este correo ya está registrado"
                    }
                    else -> {
                        val user = User(email, name, phone, password)
                        SampleData.registerUser(user)
                        scope.launch {
                            userPreferences.login(email, name, phone)
                            onRegisterSuccess()
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Crear Cuenta")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("¿Ya tienes cuenta? ")
            TextButton(onClick = onBack) {
                Text("Inicia sesión")
            }
        }
    }
}
