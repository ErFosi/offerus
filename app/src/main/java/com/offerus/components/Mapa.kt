package com.offerus.components

import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.offerus.utils.locationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Serializable
data class Marcador(
    @SerialName("nombre") val nombre: String,
    @SerialName("latitud") val latitud: Double,
    @SerialName("longitud") val longitud: Double,
)

@Composable
fun mapa(
    //viewModel: SeriesViewModel
    marcadores: List<Marcador>,
    permisoUbicacion: Boolean
){
    val context = LocalContext.current
    var ubicacion: Location? by rememberSaveable { mutableStateOf(null) }
    var marcadoresOrdenados by rememberSaveable { mutableStateOf(listOf<Marcador>()) }

    if (permisoUbicacion) {
        val location = locationUtils()
        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            coroutineScope.launch(Dispatchers.IO) {
                ubicacion = location.getLocation(context)
                if (ubicacion != null) {
                    Log.d("mapa", "Ubicación: ${ubicacion!!.latitude}, ${ubicacion!!.longitude}")
                    marcadoresOrdenados = ordenarMarcadoresPorDistancia(marcadores, ubicacion!!)
                    Log.d("mapa", "Marcadores ordenados: $marcadoresOrdenados")
                }
            }

        }
    }

    var colorLinea = Color.Black
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        properties = MapProperties(
            isMyLocationEnabled = permisoUbicacion,
            mapType = MapType.HYBRID
        )

    ){
        marcadores.forEach{ marcador ->
            // Crear el marcador
            val marker = LatLng(marcador.latitud, marcador.longitud)
            Marker(MarkerState(position = marker), title = marcador.nombre)
        }
        if (permisoUbicacion) {
            if (ubicacion != null) {
                Log.d("mapa", "Dibujando líneas")
                Log.d("mapa", "Marcadores ordenados: $marcadoresOrdenados")
                for ((index, marcador) in marcadoresOrdenados.withIndex()) {
                    val colorLinea = if (index == 0) Color.Red else Color.Black
                    val marker = LatLng(marcador.latitud, marcador.longitud)

                    Polyline(
                        points = listOf(
                            marker,
                            LatLng(ubicacion!!.latitude, ubicacion!!.longitude)
                        ),
                        color = colorLinea
                    )
                }
            }
        }

    }
}
fun ordenarMarcadoresPorDistancia(marcadores: List<Marcador>, ubicacion: Location): List<Marcador> {
    return marcadores.sortedBy { marcador ->
        distanciaEntrePuntos(marcador.latitud, marcador.longitud, ubicacion.latitude, ubicacion.longitude)
    }
}

fun distanciaEntrePuntos(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371.0 // Radio de la Tierra en kilómetros

    val lat1Rad = Math.toRadians(lat1)
    val lon1Rad = Math.toRadians(lon1)
    val lat2Rad = Math.toRadians(lat2)
    val lon2Rad = Math.toRadians(lon2)

    val dLat = lat2Rad - lat1Rad
    val dLon = lon2Rad - lon1Rad

    val a = sin(dLat / 2).pow(2) + cos(lat1Rad) * cos(lat2Rad) * sin(dLon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return R * c
}

// preview
@Composable
@Preview
fun mapaPreview(){
    //mapa()
}
