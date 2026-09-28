package com.rentaya.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnboardingScreen(
    onStart: () -> Unit,
    onLogin: () -> Unit
) {
    var currentPage by remember { mutableStateOf(0) }
    val pages = listOf(
        "Encuentra tu próximo hogar en Medellín",
        "Descubre miles de opciones de arriendo",
        "Contacta directamente con los arrendadores"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.large
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🏠",
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 80.dp.value.toInt().sp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = pages[currentPage],
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(pages.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (index == currentPage) 12.dp else 8.dp)
                            .background(
                                if (index == currentPage) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            )
                    )
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    if (currentPage < pages.size - 1) {
                        currentPage++
                    } else {
                        onStart()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(if (currentPage < pages.size - 1) "Siguiente" else "Comenzar →")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onLogin) {
                Text("Ya tengo una cuenta")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
