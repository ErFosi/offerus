package com.offerus.screens

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle
import com.offerus.components.Marcador
import com.offerus.components.TopBarSecundario
import com.offerus.components.mapa
import com.offerus.data.ServicioPeticion
import com.offerus.utils.crearContacto
import com.offerus.utils.enviarEmail
import com.offerus.utils.showToastOnMainThread
import com.offerus.viewModels.MainViewModel

@Composable
fun OfferDetails(
    navController: NavController,
    viewModel: MainViewModel
) {
    Scaffold(topBar = { TopBarSecundario(navController) }) {
        OfferDetailsContent(it, viewModel)
    }
}

@Composable
fun OfferDetailsContent(paddingValues: PaddingValues, viewModel: MainViewModel) {

    var servicioPeticion = viewModel.servicioDetalle.value ?: return

    // lista de categorias
    val categorias =
        servicioPeticion.categorias.replace("[", "").replace("]", "").split(", ").map { it.trim() }

    val favorito = rememberSaveable { mutableStateOf(false) }

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
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
                                    text = servicioPeticion.titulo,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )

                                //precio
                                Text(
                                    text = "${servicioPeticion.precio}€",
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
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

                        Row {
                            //recorrer la lista de categorias
                            for (category in categorias) {
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
                            text = servicioPeticion.descripcion,
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                        )

                        Spacer(modifier = Modifier.padding(8.dp))

                        //fecha de publicacion de la oferta
                        Text(
                            text = "Publicado el ${servicioPeticion.fecha}",
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }


                //mapa de la oferta
                var marcador = Marcador(
                    servicioPeticion.titulo,
                    servicioPeticion.latitud,
                    servicioPeticion.longitud,
                    categorias[0],
                    servicioPeticion.precio.toString() + "€"
                )
                item {
                    Card(modifier = Modifier.padding(start = 30.dp, end = 30.dp)) {
                        Column(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth()
                                .height(200.dp),
                        ) {

                            mapa(
                                permisoUbicacion = false,
                                marcadores = listOf(marcador),
                                sePuedeDesplazar = false,
                                cameraPosition = CameraPosition.fromLatLngZoom(
                                    LatLng(servicioPeticion.latitud, servicioPeticion.longitud),
                                    15f
                                ),

                                )
                        }
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
                            //info contacto
                            Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround){

                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    UserAvatar(iniciales = "AC")
                                    Text(text = servicioPeticion.username, textAlign = TextAlign.Center)
                                    RatingBar(
                                        modifier = Modifier
                                            .padding(
                                                bottom = 5.dp,
                                                top = 5.dp,
                                                start = 0.dp,
                                                end = 0.dp
                                            )
                                        //.scale(0.6F)
                                        ,
                                        value = 3.5F,
                                        style = RatingBarStyle.Fill(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.outline
                                        ),
                                        onValueChange = {},
                                        onRatingChanged = {},
                                        size = 15.dp,
                                        spaceBetween = 3.dp
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))
                                Column(verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "25 años", textAlign = TextAlign.Center)
                                    Text(text = "Hombre", textAlign = TextAlign.Center)
                                }


                            }
                            //spacer with color
                            Spacer(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .alpha(0.5f)
                                    .background(MaterialTheme.colorScheme.primary)

                            )
                            // sobre mi
                            Text(
                                text = "Sobre mi: " + LoremIpsum(20).values.first(),
                                textAlign = TextAlign.Justify,
                                modifier = Modifier.padding(8.dp)
                            )


                        }
                    }
                }
            }

            BotonesDetalles(viewModel, servicioPeticion)
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BotonesDetalles(viewModel: MainViewModel, servicioPeticion: ServicioPeticion) {

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
        ElevatedButton(onClick = {
            nuevoContacto(context)
            showToastOnMainThread(context, "Contacto añadido")
        }) {
            Icon(Icons.Filled.Call, contentDescription = "Llamar")
        }

        Spacer(modifier = Modifier.width(16.dp))

        ElevatedButton(onClick = {
            viewModel.createDeal(servicioPeticion.id)
            showToastOnMainThread(context, "Solicitud enviada")

        }) {
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



