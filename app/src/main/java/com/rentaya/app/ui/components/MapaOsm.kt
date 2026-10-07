package com.rentaya.app.ui.components

import android.view.MotionEvent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.rentaya.app.data.CoordenadasBarrios
import com.rentaya.app.data.model.Property
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

@Composable
fun MapaOsm(
    modifier: Modifier = Modifier,
    propiedades: List<Property> = emptyList(),
    pin: Pair<Double, Double>? = null,
    zoom: Double = 12.2,
    interactivo: Boolean = true,
    permitirToque: Boolean = false,
    onMarcador: ((Property) -> Unit)? = null,
    onToqueMapa: ((Double, Double) -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(interactivo)
            zoomController.setVisibility(
                if (interactivo) CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT
                else CustomZoomButtonsController.Visibility.NEVER
            )
            isTilesScaledToDpi = true
        }
    }
    val ultimaCamara = remember { mutableStateOf("") }

    DisposableEffect(lifecycle, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            mapView.onPause()
            mapView.onDetach()
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize(),
            update = { map ->
                map.setOnTouchListener { vista, evento ->
                    if (interactivo && evento.action == MotionEvent.ACTION_DOWN) {
                        vista.parent?.requestDisallowInterceptTouchEvent(true)
                    }
                    false
                }
                map.overlays.removeAll { it is Marker || it is MapEventsOverlay }

                val puntos = mutableListOf<GeoPoint>()
                propiedades.forEach { propiedad ->
                    val (lat, lng) = propiedad.coordenada()
                    val punto = GeoPoint(lat, lng)
                    puntos.add(punto)
                    val marcador = Marker(map).apply {
                        position = punto
                        title = propiedad.title
                        snippet = propiedad.neighborhood
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        setOnMarkerClickListener { _, _ ->
                            onMarcador?.invoke(propiedad)
                            true
                        }
                    }
                    map.overlays.add(marcador)
                }

                if (pin != null) {
                    val punto = GeoPoint(pin.first, pin.second)
                    puntos.add(punto)
                    val marcador = Marker(map).apply {
                        position = punto
                        title = "Ubicación"
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    }
                    map.overlays.add(marcador)
                }

                if (permitirToque && onToqueMapa != null) {
                    map.overlays.add(
                        0,
                        MapEventsOverlay(object : MapEventsReceiver {
                            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                                onToqueMapa(p.latitude, p.longitude)
                                return true
                            }

                            override fun longPressHelper(p: GeoPoint): Boolean = false
                        })
                    )
                }

                val centro = when {
                    pin != null -> GeoPoint(pin.first, pin.second)
                    puntos.size == 1 -> puntos.first()
                    puntos.isNotEmpty() -> {
                        val lat = puntos.map { it.latitude }.average()
                        val lng = puntos.map { it.longitude }.average()
                        GeoPoint(lat, lng)
                    }
                    else -> GeoPoint(CoordenadasBarrios.LAT_CENTRO, CoordenadasBarrios.LNG_CENTRO)
                }
                val claveCamara = buildString {
                    append(zoom)
                    append('|')
                    append(pin?.first ?: "")
                    append(',')
                    append(pin?.second ?: "")
                    append('|')
                    propiedades.forEach { append(it.id).append(',') }
                }
                if (claveCamara != ultimaCamara.value) {
                    map.controller.setZoom(zoom)
                    map.controller.setCenter(centro)
                    ultimaCamara.value = claveCamara
                }
                map.invalidate()
            }
        )
        if (propiedades.isEmpty() && pin == null) {
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp)
            ) {
                Text(
                    "Sin pines para mostrar",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
