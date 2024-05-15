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
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle
import com.offerus.components.CategoriasCard
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

    var servicioPeticion = viewModel.servicioDetalle.value
    val hostData = viewModel.infoUsuarioDetalle.value

    if (servicioPeticion == null || hostData == null) {
        Text(text = "Cargando...")
    } else {


        val coroutineScope = rememberCoroutineScope()
        var valoracion by remember { mutableStateOf<Pair<Int, Double>?>(null) }


        //comprar si la tenemos pendiente
        var listaSalientes = viewModel.listaSalientes.value
        Log.d("listaSalientes", listaSalientes.toString())
        var pendiente = listaSalientes.filter { it.id_peticion == servicioPeticion.id }
            .any { it.estado == "pendiente" }
        Log.d("pendiente", pendiente.toString())
        // lista de categorias
        val categorias =
            servicioPeticion.categorias.replace("[", "").replace("]", "").split(", ")
                .map { it.trim() }

        val favorito =
            rememberSaveable { mutableStateOf(viewModel.esPeticionFavorita(servicioPeticion.id)) }

        LaunchedEffect(key1 = servicioPeticion.id) {
            val result = viewModel.valoracionMedia(hostData.username)
            valoracion = result
        }

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

                                    if (favorito.value) {
                                        viewModel.deleteFavorite(servicioPeticion.id)
                                    } else {
                                        viewModel.addFavorite(servicioPeticion.id, servicioPeticion)
                                    }
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

                            //CATEGORIAS
                            Row {
                                //recorrer la lista de categorias
                                CategoriasCard(nombresCategorias = servicioPeticion.categorias)
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
                                    lat = servicioPeticion.latitud,
                                    lon = servicioPeticion.longitud
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
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {

                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        UserAvatar(
                                            username = hostData.username,
                                            viewModel = viewModel
                                        )
                                        Text(text = hostData.username, textAlign = TextAlign.Center)
                                        if (valoracion != null) {

                                            Row(verticalAlignment = Alignment.CenterVertically) {


                                                RatingBar(
                                                    modifier = Modifier
                                                        .padding(
                                                            bottom = 5.dp,
                                                            top = 5.dp,
                                                            start = 0.dp,
                                                            end = 0.dp
                                                        ),
                                                    value = valoracion!!.second.toFloat(),
                                                    style = RatingBarStyle.Fill(
                                                        MaterialTheme.colorScheme.primary,
                                                        MaterialTheme.colorScheme.outline
                                                    ),
                                                    onValueChange = {},
                                                    onRatingChanged = {},
                                                    size = 15.dp,
                                                    spaceBetween = 2.dp
                                                )
                                                Text(
                                                    text = "(${valoracion!!.first})",
                                                    modifier = Modifier.padding(start =5.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "${hostData.edad} años",
                                            textAlign = TextAlign.Center
                                        )
                                        //sexo
                                        if (hostData.sexo == "M") {
                                            Text(text = "Hombre", textAlign = TextAlign.Center)
                                        } else if (hostData.sexo == "F") {
                                            Text(text = "Mujer", textAlign = TextAlign.Center)
                                        } else {
                                            Text(text = "Otro", textAlign = TextAlign.Center)
                                        }
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
                                    text = "Sobre mi: " + hostData.descripcion,
                                    textAlign = TextAlign.Justify,
                                    modifier = Modifier.padding(8.dp)
                                )


                            }
                        }
                    }
                }
                if (servicioPeticion.username != viewModel.usuario) {
                    if (pendiente) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "Solicitud pendiente", modifier = Modifier.padding(8.dp))
                        }
                    } else {
                        BotonesDetalles(viewModel, servicioPeticion)
                    }
                }
            }
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
                Icons.AutoMirrored.Filled.Send,
                contentDescription = "Solicitar",
                modifier = Modifier
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



