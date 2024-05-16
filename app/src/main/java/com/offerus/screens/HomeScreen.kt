package com.offerus.screens

import android.util.Log
import androidx.compose.foundation.border
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
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.offerus.R
import com.offerus.components.CategoriasCirculos
import com.offerus.components.ReviewDialog
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
    val tabs = listOf(stringResource(id = R.string.entrantes), stringResource(id = R.string.salientes), stringResource(id = R.string.valoraciones_pendientes))

    // Superficie principal
    Surface {
        Column {

            // dialogo para realizar la review
            if (viewModel.dialogoReview.value) {
                ReviewDialog(viewModel, viewModel.usuario, viewModel.dealReview,
                    onDismissRequest = { viewModel.dialogoReview.value = false }) {
                    //enviar la review
                    viewModel.dealRate(
                        viewModel.dealReview!!
                    )
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

                2 -> {
                    ValoracionesPendientes(
                        viewModel,
                        onMakeReview = { viewModel.dialogoReview.value = true }) {
                        navController.navigate(AppScreens.OfferDetailsScreen.route)
                    }
                }
            }


        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ValoracionesPendientes(
    viewModel: MainViewModel,
    onMakeReview: () -> Unit,
    onItemClick: () -> Unit
) {
    val listaValoracionesPendientes = viewModel.listaValoracionesPendientes.value
    // pull refresh
    val refreshState = rememberPullRefreshState(
        refreshing = viewModel.isRefreshingHome.value,
        onRefresh = { viewModel.actualizarListaDeals() })
    //mostramos la lista

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(refreshState)
        ) {
            if (listaValoracionesPendientes.isEmpty()) {
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = stringResource(id = R.string.no_valoracion),
                            modifier = Modifier.padding(8.dp)
                        )
                        Text(text = stringResource(id = R.string.desliza))
                    }

                }
            }
            items(listaValoracionesPendientes.size) { index ->
                ValoracionesPendientesCard(deal = listaValoracionesPendientes[index], viewModel) {
                    if (listaValoracionesPendientes[index].estado == "aceptado") {
                        viewModel.dealReview = listaValoracionesPendientes[index]
                        onMakeReview()
                    } else {
                        viewModel.cambiarServicioDetalle(listaValoracionesPendientes[index].id_peticion)
                        onItemClick()
                    }
                }
            }
        }
        PullRefreshIndicator(
            refreshing = viewModel.isRefreshingHome.value,
            state = refreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
fun ValoracionesPendientesOfferInfo(
    deal: Deal,
    servicioPeticion: ServicioPeticion,
    viewModel: MainViewModel
) {
    Log.d("ValoracionesPendientesOfferInfo", "deal: $deal")
    Log.d("ValoracionesPendientesOfferInfo", "servicioPeticion: $servicioPeticion")
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = servicioPeticion.titulo,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(4.dp)
            )
            Text(
                text = "${servicioPeticion.precio}€",
                modifier = Modifier.padding(4.dp)
            )
            CategoriasCirculos(nombresCategorias = servicioPeticion.categorias)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.border(
                1.dp,
                MaterialTheme.colorScheme.outline,
                shape = CardDefaults.shape
            )
        ) {
            // Foto de perfil y nombre de usuario
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp)
            ) {
                val username = if (viewModel.usuario == servicioPeticion.username) {
                    deal.username_cliente
                } else {
                    deal.username_host
                }
                UserAvatar(username = username, viewModel = viewModel)
                Text(text = username)


                // estado valoracion

                if (deal.nota_host != -1) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.review_made),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                } else {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.review_made),
                        contentDescription = null
                    )
                }

            }

        }

    }
}

@Composable
fun ValoracionesPendientesCard(deal: Deal, viewModel: MainViewModel, onItemClick: () -> Unit) {
    var context = LocalContext.current
    val servicioPeticion = viewModel.listaServiciosApi.value.find { it.id == deal.id_peticion }
    if (servicioPeticion != null) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .pointerInput(deal) {
                    detectTapGestures(
                        onTap = {
                            onItemClick()
                        },
                        onLongPress = {
                            showToastOnMainThread(context, deal.estado)
                        })
                }
        ) {
            ValoracionesPendientesOfferInfo(
                deal = deal,
                servicioPeticion = servicioPeticion,
                viewModel = viewModel
            )
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Entrantes(viewModel: MainViewModel, onItemClick: () -> Unit) {
    //obtenemos la lista del viemodel
    var listaEntrantes = viewModel.listaEntrantes.value
    //mostramos la lista
    val refreshState = rememberPullRefreshState(
        refreshing = viewModel.isRefreshingHome.value,
        onRefresh = { viewModel.actualizarListaDeals() })
    //mostramos la lista

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(refreshState)
        ) {
            if (listaEntrantes.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.no_solicitudes),
                            modifier = Modifier.padding(8.dp)
                        )
                        Text(text = stringResource(id = R.string.desliza))
                    }
                }
            } else {
                items(listaEntrantes.size) { index ->
                    EntrantesCard(deal = listaEntrantes[index], viewModel) {
                        viewModel.cambiarServicioDetalle(listaEntrantes[index].id_peticion)
                        onItemClick()
                    }
                }
            }

        }
        PullRefreshIndicator(
            refreshing = viewModel.isRefreshingHome.value,
            state = refreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
fun EntrantesCard(deal: Deal, viewModel: MainViewModel, onItemClick: () -> Unit) {
    //obtener el servicio del deal
    val servicioPeticion =
        viewModel.listaPeticiones.collectAsState(listOf()).value.find { it.id == deal.id_peticion }
    if (servicioPeticion != null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
                    .clickable(onClick = onItemClick)
            ) {
                EntrantesOfferInfo(
                    deal = deal,
                    servicioPeticion = servicioPeticion,
                    viewModel = viewModel
                )
            }

        }

    }
}

@Composable
fun EntrantesOfferInfo(
    viewModel: MainViewModel,
    deal: Deal,
    servicioPeticion: ServicioPeticion
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = servicioPeticion.titulo,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(4.dp)
            )
            Text(
                text = "${servicioPeticion.precio}€",
                modifier = Modifier.padding(4.dp)
            )
            CategoriasCirculos(nombresCategorias = servicioPeticion.categorias)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.border(
                1.dp,
                MaterialTheme.colorScheme.outline,
                shape = CardDefaults.shape
            )
        ) {
            // Foto de perfil y nombre de usuario
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                val username = if (viewModel.usuario == servicioPeticion.username) {
                    deal.username_cliente
                } else {
                    deal.username_host
                }
                UserAvatar(username = username, viewModel = viewModel)
                Text(text = username)
            }
            BotonesEntrantes(
                onAccept = { viewModel.dealAcceptDeny(deal.id, true) },
                onDeny = { viewModel.dealAcceptDeny(deal.id, false) }
            )
        }

    }
}


@Composable
fun BotonesEntrantes(onAccept: () -> Unit, onDeny: () -> Unit) {
    var context = LocalContext.current
    Row {
        IconButton(onClick = {
            onAccept()
            showToastOnMainThread(context, "Petición aceptada")
        }) {
            Icon(
                painterResource(id = R.drawable.logorecortado),
                contentDescription = "Aceptar",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }
        IconButton(onClick = {
            onDeny()
            showToastOnMainThread(context, "Petición rechazada")
        }) {
            Icon(
                painterResource(id = R.drawable.logorecortado),
                contentDescription = "Rechazar",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(28.dp)
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
    val refreshState = rememberPullRefreshState(
        refreshing = viewModel.isRefreshingHome.value,
        onRefresh = { viewModel.actualizarListaDeals() })
    //mostramos la lista

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(refreshState)
        ) {
            if (listaSalientes.isEmpty()) {
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = stringResource(id = R.string.no_solicitudes),
                            modifier = Modifier.padding(8.dp)
                        )
                        Text(text = stringResource(id = R.string.desliza))
                    }

                }
            }
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
        PullRefreshIndicator(
            refreshing = viewModel.isRefreshingHome.value,
            state = refreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
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
                .pointerInput(deal) {
                    detectTapGestures(
                        onTap = {
                            onItemClick()
                        },
                        onLongPress = {
                            showToastOnMainThread(context, deal.estado)
                        })
                }
        ) {
            OfferInfo(deal = deal, servicioPeticion = servicioPeticion, viewModel = viewModel) {
                //estado aleatorio entre pendiente, aceptada y rechazada
                IconoEstado(deal)
            }
        }
    }
}

//icono pendiente, aceptada, rechazada
@Composable
fun IconoEstado(deal: Deal) {
    val estado = deal.estado
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
            Column {
                Icon(
                    painter = painterResource(R.drawable.logorecortado),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(12.dp)
                        .size(28.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

            }

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
    viewModel: MainViewModel,
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

            if (viewModel.usuario == servicioPeticion.username) { // si yo soy el creador del servicio, muestro la foto del cliente
                UserAvatar(username = deal.username_cliente, viewModel = viewModel)
                Text(text = deal.username_cliente)
            } else {
                UserAvatar(username = deal.username_host, viewModel = viewModel)
                Text(text = deal.username_host)
            }
            if (deal.estado == "aceptado") {
                if (deal.nota_cliente != -1) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.review_made),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }else {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.review_made),
                        contentDescription = null
                    )
                }
            }

        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = servicioPeticion.titulo,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
            )
            Text(
                text = "${servicioPeticion.precio}€",
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
            )
            CategoriasCirculos(nombresCategorias = servicioPeticion.categorias)
        }

        accion()
    }
}






