package com.offerus.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import com.offerus.data.CATEGORIAS
import com.offerus.utils.locationUtils
import com.offerus.utils.obtenerCategoriasString
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
    @SerialName("categoría") val categoria: String,
    @SerialName("precio") val precio: String,
    @SerialName("categorias") val categorias: List<String>? = null
)


/**
 * Muestra un mapa con los marcadores dados, en caso de haberlos
 * @param marcadores lista de marcadores a mostrar
 * @param permisoUbicacion si se tiene permiso para acceder a la ubicación
 * @param sePuedeDesplazar si se puede desplazar el mapa
 * @param cameraPosition posición inicial de la cámara (default: centro del mundo)
 */
@Composable
fun mapa(
    marcadores: List<Marcador>,
    permisoUbicacion: Boolean,
    sePuedeDesplazar: Boolean,
    lat: Double = 43.1842,
    lon: Double = -2.4821,
    zoom: Float = 10f
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
                    Log.d("mapa", "Marcadores: $marcadores")
                    marcadoresOrdenados = ordenarMarcadoresPorDistancia(marcadores, ubicacion!!)
                    Log.d("mapa", "Marcadores ordenados: $marcadoresOrdenados")
                }
            }

        }
    }
    var settings = MapUiSettings(
        zoomControlsEnabled = true
    )
    if (!sePuedeDesplazar){
        // establecer los mapuisettings para que no se pueda mover el mapa
        settings = MapUiSettings(
            scrollGesturesEnabled = false,
            tiltGesturesEnabled = false,
            zoomGesturesEnabled = false,
            mapToolbarEnabled = false,
            myLocationButtonEnabled = false,
            zoomControlsEnabled = false,
            compassEnabled = false,
            rotationGesturesEnabled = false,
            indoorLevelPickerEnabled = false,
            scrollGesturesEnabledDuringRotateOrZoom = false
        )
    }
    val cameraPositionState = CameraPositionState (
        position = if (lat != 0.0 && lon != 0.0) CameraPosition(LatLng(lat, lon), zoom, 0f, 0f) else CameraPosition(LatLng(0.00, 0.00), zoom, 0f, 0f)
    )

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        properties = MapProperties(
            isMyLocationEnabled = permisoUbicacion,
            mapType = MapType.HYBRID
        ),
        uiSettings = settings,
        cameraPositionState = cameraPositionState,


    ){

        if (permisoUbicacion) {
            if (ubicacion != null) {

                // LOS MARCADORES ESTAN ORDENADOS POR DISTANCIA

                for ((index, marcador) in marcadoresOrdenados.withIndex()) {
                    val marker = LatLng(marcador.latitud, marcador.longitud)
                    val distancia = distanciaEntrePuntos(
                        marcador.latitud,
                        marcador.longitud,
                        ubicacion!!.latitude,
                        ubicacion!!.longitude
                    )
                    Log.d("mapa", "Marcador: ${marcador.nombre}")
                    val categoria = CATEGORIAS.find { it.nombre == marcador.categoria }
                    if (categoria!=null) {
                        categoria.let {
                            MarkerInfoWindow(
                                state = MarkerState(position = marker),
                                icon = bitmapDescriptor(context, categoria.icono, categoria.color),
                            ) {
                                Card {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp)
                                    ) {
                                        Text(
                                            text = marcador.nombre,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(
                                                vertical = 4.dp,
                                                horizontal = 8.dp
                                            )
                                        )
                                        Text(
                                            text = marcador.precio,
                                            modifier = Modifier.padding(
                                                vertical = 4.dp,
                                                horizontal = 8.dp
                                            )
                                        )
                                        Text(
                                            text = "${String.format("%.2f", distancia)} km",
                                            fontStyle = FontStyle.Italic,
                                            modifier = Modifier.padding(
                                                vertical = 4.dp,
                                                horizontal = 8.dp
                                            )
                                        )
                                        CategoriasCirculos(
                                            nombresCategorias = obtenerCategoriasString(
                                                marcador.categorias!!
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }else{
                        Marker(MarkerState(position = marker), title = marcador.nombre)
                    }

                }

            }
        }else{
            marcadores.forEach{ marcador ->
                // Crear el marcador
                Log.d("mapa", "Marcador: ${marcador.nombre}")
                val marker = LatLng(marcador.latitud, marcador.longitud)

                val categoria = CATEGORIAS.find { it.nombre == marcador.categoria }
                if (categoria!=null) {
                    categoria.let {
                        MarkerInfoWindow(
                            state = MarkerState(position = marker),
                            icon = bitmapDescriptor(context, categoria.icono, categoria.color),
                        ){
                            Card {
                                Column(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = marcador.nombre,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(
                                            vertical = 4.dp,
                                            horizontal = 8.dp
                                        )
                                    )
                                    Text(
                                        text = marcador.precio,
                                        modifier = Modifier.padding(
                                            vertical = 4.dp,
                                            horizontal = 8.dp
                                        )
                                    )
                                    CategoriasCirculos(
                                        nombresCategorias = obtenerCategoriasString(
                                            marcador.categorias!!
                                        )
                                    )
                                }
                            }
                        }
                    }
                }else{
                    Marker(MarkerState(position = marker), title = marcador.nombre)
                }

                /*when (marcador.categoria) {
                    "Gratis" -> MarkerInfoWindow(
                            state = MarkerState(position = marker),
                            icon = bitmapDescriptor(context, R.drawable.gratis, Color.GREEN),
                            title = marcador.nombre,
                            snippet = marcador.precio
                    )
                    "Deporte" -> MarkerInfoWindow(
                        state = MarkerState(position = marker),
                        icon = bitmapDescriptor(context, R.drawable.deporte, Color.YELLOW),
                        title = marcador.nombre,
                        snippet = marcador.precio
                    )
                    "Entretenimiento" -> MarkerInfoWindow(
                        state = MarkerState(position = marker),
                        icon = bitmapDescriptor(context, R.drawable.ocio, Color.RED),
                        title = marcador.nombre,
                        snippet = marcador.precio
                    )
                    "Academico" -> MarkerInfoWindow(
                        state = MarkerState(position = marker),
                        icon = bitmapDescriptor(context, R.drawable.academico, Color.BLUE),
                        title = marcador.nombre,
                        snippet = marcador.precio
                    )
                    "Hogar" -> MarkerInfoWindow(
                        state = MarkerState(position = marker),
                        icon = bitmapDescriptor(context, R.drawable.hogar, Color.GRAY),
                        title = marcador.nombre,
                        snippet = marcador.precio
                    )
                    "Online" -> MarkerInfoWindow(
                        state = MarkerState(position = marker),
                        icon = bitmapDescriptor(context, R.drawable.online, Color.DKGRAY),
                        title = marcador.nombre,
                        snippet = marcador.precio
                    )
                    "Otros" -> MarkerInfoWindow(
                        state = MarkerState(position = marker),
                        icon = bitmapDescriptor(context, R.drawable.otros, Color.MAGENTA),
                        title = marcador.nombre,
                        snippet = marcador.precio
                    )

                    else -> Marker(MarkerState(position = marker), title = marcador.nombre)*/


            }
        }
    }
}

/**
 * Ordena los marcadores por distancia a la ubicación dada
 * @param marcadores lista de marcadores a ordenar
 * @param ubicacion ubicación de referencia
 * @return lista de marcadores ordenados por distancia
 */

fun ordenarMarcadoresPorDistancia(marcadores: List<Marcador>, ubicacion: Location): List<Marcador> {
    return marcadores.sortedBy { marcador ->
        distanciaEntrePuntos(marcador.latitud, marcador.longitud, ubicacion.latitude, ubicacion.longitude)
    }
}

/**
 * Calcula la distancia en kilómetros entre dos puntos geográficos
 * @param lat1 latitud del primer punto
 * @param lon1 longitud del primer punto
 * @param lat2 latitud del segundo punto
 * @param lon2 longitud del segundo punto
 * @return distancia en kilómetros
 */
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

/**
 * Crea un BitmapDescriptor a partir de un recurso drawable
 * @param context contexto de la aplicación
 * @param resId id del recurso drawable
 * @param color color del círculo que rodea al icono
 * @return BitmapDescriptor creado
 */
fun bitmapDescriptor(
    context: Context,
    resId: Int,
    color: androidx.compose.ui.graphics.Color
): BitmapDescriptor? {
    val drawable = ContextCompat.getDrawable(context, resId) ?: return null
    val size = drawable.intrinsicWidth.coerceAtLeast(drawable.intrinsicHeight)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Dibujar el fondo circular con padding
    val padding = size / 4
    val paint = Paint().apply {
        isAntiAlias = true
        this.color = color.toArgb()
    }
    val radius = (size - 2 * padding) / 2f
    canvas.drawCircle(size / 2f, size / 2f, radius + padding, paint)

    // Dibujar el icono con padding
    drawable.setBounds(padding, padding, size - padding, size - padding)
    drawable.draw(canvas)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

