package com.offerus.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.offerus.ui.theme.OfferUSTheme


@Composable
fun DialogoSeleccionarUbicacion(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {
    var latLng by rememberSaveable { mutableStateOf(LatLng(0.0,0.0)) }
    var abrirMapa by rememberSaveable { mutableStateOf(false) }
    Dialog(
        onDismissRequest = { onDismissRequest() },
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                //.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Selecciona tu ubicación",

                    )
                Button(
                    onClick = /*TODO*/ {}
                ) {
                    Text(text = "Elegir mi ubicación actual")
                }

                Button(
                    onClick = { abrirMapa = !abrirMapa }
                ) {
                    Text(text = "Elegir mi ubicación en el mapa")
                }
                AnimatedVisibility(visible = abrirMapa) {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .height(200.dp),
                    ) {
                        GoogleMap(
                            onMapClick = { latLng = it },
                        ) {
                            if (latLng.latitude != 0.0 && latLng.longitude != 0.0) {
                                // Add a marker to the map
                                Marker(MarkerState(position = latLng))
                            }
                        }
                    }

                }

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
                        onConfirmation()
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
        DialogoSeleccionarUbicacion(onDismissRequest = {  }) {

        }
    })
}
