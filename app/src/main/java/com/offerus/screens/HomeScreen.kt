package com.offerus.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle
import com.offerus.R
import com.offerus.components.CategoriasCirculos
import com.offerus.data.Deal
import com.offerus.data.ServicioPeticion
import com.offerus.navigation.AppScreens
import com.offerus.utils.showToastOnMainThread
import com.offerus.viewModels.MainViewModel

@Composable
fun HomeScreen(navController: NavController, viewModel: MainViewModel) {
    // Estado para almacenar la pestaña seleccionada
    var selectedTabIndex = viewModel.selectedTabIndexHome

    // Lista de pestañas
    val tabs = listOf("Entrantes", "Salientes")

    // Superficie principal
    Surface {
        Column {

            // dialogo para realizar la review
            if (viewModel.dialogoReview.value) {
                ReviewDialog(viewModel.dealReview,
                    onDismissRequest = { viewModel.dialogoReview.value = false }) {
                    //enviar la review
                    viewModel.enviarReview()
                    viewModel.dialogoReview.value = false
                }
            }

            // TabRow para mostrar las pestañas
            TabRow(selectedTabIndex.value) {
                // Crear una pestaña para cada elemento en la lista de pestañas
                tabs.forEachIndexed { index, title ->
                    Tab(selected = selectedTabIndex.value == index,
                        onClick = { selectedTabIndex.value = index },
                        text = { Text(title) })
                }
            }

            // Mostrar la subpantalla correspondiente a la pestaña seleccionada
            when (selectedTabIndex.value) {
                0 -> Entrantes(viewModel) {
                    navController.navigate(AppScreens.OfferDetailsScreen.route)
                }

                1 -> Salientes(viewModel, onMakeReview = { viewModel.dialogoReview.value = true }) {
                    navController.navigate(AppScreens.OfferDetailsScreen.route)
                }
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun ReviewDialog(
    deal: Deal?,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {

    var valoracion = mutableStateOf(0F)

    Dialog(
        onDismissRequest = { onDismissRequest() }
    ) {
        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Valoración del servicio",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(8.dp)
                )

                //info del deal
                if (deal != null) {
                    Text(text = "usuario: ${deal.username_host}", fontWeight = FontWeight.Bold)
                    Text(text = "Id peticion: ${deal.id_peticion}")

                    // valoracion en estrellas
                    RatingBar(
                        modifier = Modifier
                            .padding(bottom = 20.dp, top = 15.dp, start = 5.dp, end = 5.dp)
                            .scale(0.75F),
                        value = valoracion.value,
                        style = RatingBarStyle.Fill(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.outline
                        ),
                        onValueChange = {
                            valoracion.value = it
                        },
                        onRatingChanged = {
                            Log.d("TAG", "onRatingChanged: $it")
                        }
                    )

                    IconButton(onClick = {
                        //actualizar la review dependiendo de si es cliente o host
                        if (deal != null) {
                            if (deal.username_host == "cuadron11") {
                                deal.nota_host = valoracion.value.toInt()
                            } else {
                                deal.nota_cliente = valoracion.value.toInt()
                            }
                        }
                        onConfirmation()
                    }, modifier = Modifier
                        .padding(8.dp)
                        .size(32.dp)) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Aceptar",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                } else {
                    Text(text = "Peticion no encontrada")
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Entrantes(viewModel: MainViewModel, onItemClick: () -> Unit) {
    //obtenemos la lista del viemodel
    var listaEntrantes = viewModel.listaEntrantes.value
    //mostramos la lista
    val refreshState = rememberPullRefreshState(refreshing = viewModel.isRefreshingHome.value, onRefresh = { viewModel.actualizarListaDeals() })
    //mostramos la lista

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize().pullRefresh(refreshState)){
            items(listaEntrantes.size) { index ->
                EntrantesCard(deal = listaEntrantes[index], viewModel) {
                    viewModel.cambiarServicioDetalle(listaEntrantes[index].id_peticion)
                    onItemClick()
                }
            }
        }
        PullRefreshIndicator(refreshing = viewModel.isRefreshingHome.value, state = refreshState, modifier = Modifier.align(Alignment.TopCenter))
    }
}

@Composable
fun EntrantesCard(deal: Deal, viewModel: MainViewModel, onItemClick: () -> Unit) {
    var context = LocalContext.current
    //obtener el servicio del deal
    val servicioPeticion = viewModel.listaPeticiones.collectAsState(listOf()).value.find { it.id == deal.id_peticion }
    if (servicioPeticion != null) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .clickable(onClick = onItemClick)
        ) {
            OfferInfo(deal = deal, servicioPeticion) { BotonesEntrantes(onAccept = {viewModel.dealAcceptDeny(deal.id, true)}) {viewModel.dealAcceptDeny(deal.id, false)} }
        }
    }
}

@Composable
fun BotonesEntrantes(onAccept: () -> Unit, onDeny: () -> Unit) {
    var context = LocalContext.current
    Column {
        IconButton(onClick = {
            onAccept()
            showToastOnMainThread(context, "Petición aceptada")
        }) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Aceptar",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        IconButton(onClick = {
            onDeny()
            showToastOnMainThread(context, "Petición rechazada")
        }) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Rechazar",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Salientes(viewModel: MainViewModel, onMakeReview: () -> Unit, onItemClick: () -> Unit) {
    val listaSalientes = viewModel.listaSalientes.value
    Log.d("listaComposable", listaSalientes.toString())
    // pull refresh
    val refreshState = rememberPullRefreshState(refreshing = viewModel.isRefreshingHome.value, onRefresh = { viewModel.actualizarListaDeals() })
    //mostramos la lista

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize().pullRefresh(refreshState)) {
            items(listaSalientes.size) { index ->
                SalientesCard(deal = listaSalientes[index], viewModel) {
                    if (listaSalientes[index].estado == "aceptado") {
                        viewModel.dealReview = listaSalientes[index]
                        onMakeReview()
                    } else {
                        viewModel.cambiarServicioDetalle(listaSalientes[index].id_peticion)
                        onItemClick()
                    }

                }
            }
        }
        PullRefreshIndicator(refreshing = viewModel.isRefreshingHome.value, state = refreshState, modifier = Modifier.align(Alignment.TopCenter))
    }

}

@Composable
fun SalientesCard(deal: Deal, viewModel: MainViewModel, onItemClick: () -> Unit) {
    var context = LocalContext.current
    val servicioPeticion = viewModel.listaServiciosApi.value.find { it.id == deal.id_peticion }
    if (servicioPeticion != null) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            onItemClick()
                        },
                        onLongPress = {
                            showToastOnMainThread(context, deal.estado)
                        })
                }
        ) {
            OfferInfo(deal = deal, servicioPeticion) {
                //estado aleatorio entre pendiente, aceptada y rechazada
                IconoEstado(estado = deal.estado)
            }
        }
    }
}

//icono pendiente, aceptada, rechazada
@Composable
fun IconoEstado(estado: String) {
    when (estado) {
        "pendiente" -> {

            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.pending),
                contentDescription = "pendiente",
                modifier = Modifier
                    .padding(11.dp)
                    .size(32.dp),
            )
        }

        "aceptado" -> {
            Icon(
                painter = painterResource(R.drawable.logorecortado),
                contentDescription = null,
                modifier = Modifier
                    .padding(12.dp)
                    .size(28.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        "rechazado" -> {
            Icon(
                painter = painterResource(R.drawable.logorecortado),
                contentDescription = null,
                modifier = Modifier
                    .padding(12.dp)
                    .size(28.dp),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun OfferInfo(
    deal: Deal, servicioPeticion: ServicioPeticion, accion: @Composable () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // foto de perfil del usuario
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(4.dp)
        ) {
            UserAvatar(iniciales = "AC")
            if (deal.username_host == servicioPeticion.username) {
                Text(text = deal.username_cliente)
            } else {
                Text(text = deal.username_host)
            }

        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = servicioPeticion.titulo,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
            )
            Text(text = "${servicioPeticion.precio}€", modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp))
            CategoriasCirculos(nombresCategorias = servicioPeticion.categorias)
        }

        accion()
    }
}






