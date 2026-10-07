package com.rentaya.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.rentaya.app.data.ImagenesInmuebles
import com.rentaya.app.data.model.Property
import com.rentaya.app.data.model.PropertyType

@Composable
fun ImagenPropiedad(
    modelo: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    id: String = "",
    tipo: PropertyType = PropertyType.APARTAMENTO,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current
    val data = ImagenesInmuebles.normalizar(modelo, id, tipo)
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(context)
            .data(data)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        loading = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    strokeWidth = 2.dp
                )
            }
        },
        error = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Home,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                )
            }
        }
    )
}

@Composable
fun ImagenPrincipalPropiedad(
    property: Property,
    modifier: Modifier = Modifier,
    contentDescription: String? = property.title
) {
    ImagenPropiedad(
        modelo = property.imagenPrincipal(),
        modifier = modifier,
        contentDescription = contentDescription,
        id = property.id,
        tipo = property.type
    )
}
