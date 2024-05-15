package com.offerus.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.offerus.components.CategoriasCirculos
import com.offerus.components.CreateDialog
import com.offerus.components.EditDialog
import com.offerus.data.ServicioPeticion
import com.offerus.navigation.AppScreens
import com.offerus.viewModels.MainViewModel

@Composable
fun MyOffersScreen(navController: NavController, viewModel: MainViewModel) {
    // Estado para almacenar la pestaña seleccionada
    var selectedTabIndex = viewModel.selectedTabIndexMyOffers


    // gestor para el dialogo de crear oferta
    var createDialog by rememberSaveable { mutableStateOf(false) }
    var editDialog = viewModel.editDialog

    // Lista de pestañas
    val tabs = listOf("Mis Ofertas", " Mis Peticiones")

    // Superficie principal
    Box {
        Column {

            if (createDialog) {
                CreateDialog(viewModel, onDismissRequest = { createDialog = false }) {
                    createDialog = false
                }
            }

            if (editDialog.value) {
                Log.d("EditDialog", "EditDialog")
                EditDialog(viewModel, viewModel.editPeticion.value, onDismissRequest = { editDialog.value = false }) {
                    editDialog.value = false
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
                0 -> MisOfertas(viewModel) {
                    navController.navigate(AppScreens.OfferDetailsScreen.route)
                }

                1 -> MisPeticiones(viewModel) {
                    navController.navigate(AppScreens.OfferDetailsScreen.route)
                }
            }

            // Botón flotante

        }
        FloatingActionButton(
            onClick = { createDialog = true },
            elevation = FloatingActionButtonDefaults.elevation(8.dp),
            shape = CircleShape,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
                .size(56.dp),

            //backgroundColor = MaterialTheme.colors.primary
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Agregar", tint = MaterialTheme.colorScheme.tertiaryContainer)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MisOfertas(viewModel: MainViewModel, onItemClick: () -> Unit) {
    val listaMisOfertas = viewModel.listaMisOfertas.value

    val refreshState = rememberPullRefreshState(
        refreshing = viewModel.isRefreshingMyOffers.value,
        onRefresh = { viewModel.obtenerMisOfertas() })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            //columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().pullRefresh(refreshState)
        ) {
            if (listaMisOfertas.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No hay ofertas. Crea una nueva.",
                            modifier = Modifier.padding(8.dp)
                        )
                        Text(text = "Desliza hacia arriba para refrescar.")
                    }
                }
            } else {
            items(listaMisOfertas.size) { index ->
                MisOfertasCard(listaMisOfertas[index], viewModel) {
                    viewModel.servicioDetalle.value = listaMisOfertas[index]
                    viewModel.obtenerInfoUsuario(listaMisOfertas[index].username)
                    onItemClick()
                }
            }
            }
        }
        PullRefreshIndicator(
            refreshing = viewModel.isRefreshingMyOffers.value,
            state = refreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
fun MisOfertasCard(servicioPeticion: ServicioPeticion, viewModel: MainViewModel, onItemClick: () -> Unit) {
    var context = LocalContext.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 4.dp)
                .clickable(onClick = onItemClick),

            ) {
            MyOfferInfo(servicioPeticion = servicioPeticion) { BotonesMyOffers(onEditClick = {
                viewModel.editPeticion.value = servicioPeticion
                viewModel.editDialog.value = true }) {viewModel.deleteRequest(servicioPeticion.id, context)} }

        }
    }
}

@Composable
fun BotonesMyOffers(onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    var context = LocalContext.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.padding(4.dp)
    ) {
        // Botón de  editar
        OutlinedButton(
            onClick = {

                onEditClick() },
            modifier = Modifier.size(30.dp),  //avoid the oval shape
            shape = CircleShape,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiaryContainer),
            contentPadding = PaddingValues(0.dp),  //avoid the little icon
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Icon(
                imageVector = Icons.Filled.Create,
                contentDescription = "Editar",
                //modifier = Modifier.padding(4.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))
        OutlinedButton(
            onClick = { onDeleteClick() },
            modifier = Modifier.size(30.dp),  //avoid the oval shape
            shape = CircleShape,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
            contentPadding = PaddingValues(0.dp),  //avoid the little icon
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Rechazar",
                //modifier = Modifier.padding(4.dp)
            )
        }
    }
}


// MIS PETICIONES
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MisPeticiones(viewModel: MainViewModel, onItemClick: () -> Unit) {
    var listaMisPeticiones = viewModel.listaMisPeticiones.value

    val refreshState = rememberPullRefreshState(
        refreshing = viewModel.isRefreshingMyOffers.value,
        onRefresh = { viewModel.obtenerMisOfertas() })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().pullRefresh(refreshState)
        ) {
            if (listaMisPeticiones.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No hay ofertas. Crea una nueva.",
                            modifier = Modifier.padding(8.dp)
                        )
                        Text(text = "Desliza hacia arriba para refrescar.")
                    }
                }
            } else {
            items(listaMisPeticiones.size) { index ->
                MisOfertasCard(listaMisPeticiones[index], viewModel) {
                    viewModel.servicioDetalle.value = listaMisPeticiones[index]
                    viewModel.obtenerInfoUsuario(listaMisPeticiones[index].username)
                    onItemClick()
                }
            }
            }
        }
        PullRefreshIndicator(
            refreshing = viewModel.isRefreshingMyOffers.value,
            state = refreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
fun MyOfferInfo(
    servicioPeticion: ServicioPeticion,
    buttons: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = servicioPeticion.titulo,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 8.dp)
                .fillMaxWidth()
        )

        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(text = servicioPeticion.precio.toString() + "€", modifier = Modifier
                    .padding(vertical = 4.dp, horizontal = 4.dp))
                CategoriasCirculos(nombresCategorias = servicioPeticion.categorias)
            }

            buttons()
        }
    }
}






