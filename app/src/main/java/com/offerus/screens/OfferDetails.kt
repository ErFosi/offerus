package com.offerus.screens

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.offerus.utils.crearContacto
import com.offerus.utils.enviarEmail

@Composable
fun OfferDetails() {

    // lista de categorias
    var categories = listOf("Deportes", "Gratis")
    var favorito = rememberSaveable { mutableStateOf(false) }

    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LazyColumn(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
                    .weight(1f),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Servicio Profesional de Desarrollo Web de paginas de deportes",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )

                                //precio
                                Text(text = "10€/h", modifier = Modifier.padding(bottom = 8.dp))
                            }
                            //icono de favorito
                            IconButton(onClick = {
                                favorito.value = !favorito.value
                                Log.d("favoritos", favorito.toString())
                            }) {
                                if (favorito.value) {
                                    Icon(
                                        Icons.Filled.Favorite,
                                        contentDescription = "Favorito",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(36.dp)
                                    )
                                } else {
                                    Icon(
                                        Icons.Outlined.FavoriteBorder,
                                        contentDescription = "Favorito",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }

                        }
                        //titulo de la oferta en negrita

                        Row {
                            //recorrer la lista de categorias
                            for (category in categories) {
                                Card(modifier = Modifier.padding(4.dp)) {
                                    Text(
                                        text = category,
                                        modifier = Modifier.padding(
                                            top = 4.dp,
                                            start = 8.dp,
                                            end = 8.dp,
                                            bottom = 4.dp
                                        )
                                    )
                                }
                            }
                        }

                        //descripcion de la oferta
                        Text(
                            text = LoremIpsum(50).values.first(),
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                        )

                        Spacer(modifier = Modifier.padding(8.dp))

                        //fecha de publicacion de la oferta
                        Text(
                            text = "Publicado el 12/12/2021",
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }


                //mapa de la oferta
                item {
                    Card(modifier = Modifier.padding(16.dp)) {
                        //mapa
                        Text(text = "Mapa de la ofertas", modifier = Modifier.padding(56.dp))
                    }
                }

                // informacion de contacto
                item {
                    Card(modifier = Modifier.padding(16.dp)) {


                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                UserAvatar(iniciales = "AC")
                                Text(text = "Adrian Cuadron", textAlign = TextAlign.Center)
                                Text(text = "* * * * * (20)", textAlign = TextAlign.Center)

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(text = "25 años", textAlign = TextAlign.Center)
                                Text(text = "Hombre", textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Sobre mi: " + LoremIpsum(20).values.first(),
                                    textAlign = TextAlign.Justify
                                )

                            }

                        }
                    }
                }
            }

            BotonesDetalles()
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BotonesDetalles() {
    val context = LocalContext.current

    val permissions = arrayOf(
        Manifest.permission.WRITE_CONTACTS,
        Manifest.permission.READ_CONTACTS,
    )
    val permissionState = rememberMultiplePermissionsState(
        permissions = permissions.toList()

    )
    LaunchedEffect(true) {
        permissionState.launchMultiplePermissionRequest()
    }
    //botones de accion
    Row(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        //boton de llamar
        ElevatedButton(onClick = { nuevoContacto(context) }) {
            Icon(Icons.Filled.Call, contentDescription = "Llamar")
        }

        Spacer(modifier = Modifier.width(16.dp))

        ElevatedButton(onClick = { }) {
            Icon(
                Icons.Filled.AddCircle, contentDescription = "Solicitar", modifier = Modifier
                    .padding(vertical = 2.dp, horizontal = 8.dp)
                    .size(36.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        ElevatedButton(onClick = {
            enviarEmail(
                context,
                "ejemplo@gmail.com",
                "Servicio de Offerus",
                "Hola, me gustaria tener mas información acerca de tu servicio."
            )
        }) {
            Icon(Icons.Filled.Email, contentDescription = "Email")
        }
    }
}


fun nuevoContacto(context: Context) {
    //var contactos = obtenerContactos(context.contentResolver)
    //Log.d("contactos", contactos.toString())
    crearContacto(context.contentResolver, "Prueba Test", "123456789")
}



