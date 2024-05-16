package com.offerus.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.offerus.ui.theme.OfferUSTheme
import com.offerus.R
import com.offerus.utils.locationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


@Composable
fun DialogoSeleccionarUbicacion(
    lat: Double,
    lon: Double,
    onDismissRequest: () -> Unit,
    onConfirmation: (LatLng) -> Unit
) {
    var abrirMapa by rememberSaveable { mutableStateOf(false) }
    var ubicacion by rememberSaveable {
        mutableStateOf(LatLng(lat, lon))
    }
    val locationUtils = locationUtils()
    val context = LocalContext.current

    val cameraPosition = CameraPosition.Builder().target(ubicacion).zoom(10f).build()
    val cameraPositionState = rememberCameraPositionState {
        position = if (lat != 0.0 && lon != 0.0) CameraPosition(LatLng(lat, lon), 15f, 0f, 0f) else CameraPosition(LatLng(0.0, 0.0), 15f, 0f, 0f)
    }

    val coroutineScope = rememberCoroutineScope()

    var permisoUbicacion by rememberSaveable {
        mutableStateOf(false)
    }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            // Permission is granted
            permisoUbicacion = true
        } else {
            // Permission is denied
            permisoUbicacion = false
        }
    }

    val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    if (ContextCompat.checkSelfPermission(
            LocalContext.current,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            LocalContext.current,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        // Permission is already granted
        permisoUbicacion = true
    } else {
        // Request for permission
        LaunchedEffect(permissions) {
            requestPermissionLauncher.launch(permissions)
        }
    }

    Dialog(
        onDismissRequest = { onDismissRequest() },
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.seleccionarUbicacion),
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 10.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                    )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 20.dp, start = 8.dp, end = 8.dp)
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                )
                if (permisoUbicacion) {
                    Button(
                        onClick = {
                            coroutineScope.launch(Dispatchers.IO) {
                                val ubi = locationUtils.getLocation(context)
                                if (ubi != null) {
                                    ubicacion = LatLng(ubi.latitude, ubi.longitude)
                                }
                            }
                        },
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 10.dp).fillMaxWidth()
                    ) {
                        Text(text = stringResource(id = R.string.seleccionarMiUbicacion), textAlign = TextAlign.Center )
                    }
                }

                Button(
                    onClick = { abrirMapa = !abrirMapa },
                    modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.ubicacionMapa), textAlign = TextAlign.Center)
                }
                AnimatedVisibility(visible = abrirMapa) {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .height(300.dp),
                    ) {
                        Text(text = stringResource(id = R.string.mapaDosDedos), modifier = Modifier.padding(10.dp))
                        GoogleMap(
                            onMapClick = { ubicacion = it },
                            cameraPositionState = cameraPositionState
                        ) {
                            if (ubicacion.latitude != 0.0 && ubicacion.longitude != 0.0) {
                                // Add a marker to the map
                                Marker(MarkerState(position = ubicacion))
                            }
                        }
                    }

                }
                //Text(text = "Ubicación seleccionada DEBUG: ${ubicacion.latitude}, ${ubicacion.longitude}")

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 8.dp, start = 8.dp, end = 8.dp)
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    //horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(onClick = {
                        onDismissRequest()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = ""// stringResource(R.string.Cancelar)
                        )
                    }

                    Button(onClick = {
                        onConfirmation(
                            ubicacion
                        )
                    }) {
                        Text(text = "")
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = ""//stringResource(R.string.Borrar)
                        )
                    }
                }

            }

        }
    }
}




@Preview(showBackground = true)
@Composable
fun previewDialogoSeleccionarUbicacion() {
    OfferUSTheme(content = {
       // DialogoSeleccionarUbicacion(onDismissRequest = {  }, {

        //})
    })
}
