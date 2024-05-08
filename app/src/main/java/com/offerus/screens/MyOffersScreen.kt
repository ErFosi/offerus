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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
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
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
                .size(56.dp),
            shape = CircleShape,
            //backgroundColor = MaterialTheme.colors.primary
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Agregar")
        }
    }
}

@Composable
fun MisOfertas(viewModel: MainViewModel, onItemClick: () -> Unit) {
    val listaMisOfertas = viewModel.listaMisOfertas.value
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize()
        ) {
            items(listaMisOfertas.size) { index ->
                MisOfertasCard(listaMisOfertas[index], viewModel) {
                    viewModel.cambiarServicioDetalle(listaMisOfertas[index].id)
                    onItemClick()
                }
            }
        }
    }
}

@Composable
fun MisOfertasCard(servicioPeticion: ServicioPeticion, viewModel: MainViewModel, onItemClick: () -> Unit) {
    var context = LocalContext.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            modifier = Modifier
                .padding(8.dp)
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
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Botón de aceptar
        OutlinedButton(
            onClick = {

                onEditClick() },
            modifier = Modifier.size(30.dp),  //avoid the oval shape
            shape = CircleShape,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            contentPadding = PaddingValues(0.dp),  //avoid the little icon
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = Icons.Filled.Create,
                contentDescription = "Editar",
                modifier = Modifier.padding(4.dp)
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
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}


// MIS PETICIONES
@Composable
fun MisPeticiones(viewModel: MainViewModel, onItemClick: () -> Unit) {
    var listaMisPeticiones = viewModel.listaMisPeticiones.value
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize()
        ) {
            items(listaMisPeticiones.size) { index ->
                MisOfertasCard(listaMisPeticiones[index], viewModel) {
                    viewModel.cambiarServicioDetalle(listaMisPeticiones[index].id)
                    onItemClick()
                }
            }
        }
    }
}

@Composable
fun MyOfferInfo(
    servicioPeticion: ServicioPeticion,
    buttons: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = servicioPeticion.titulo,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
        )
        Text(text = servicioPeticion.precio.toString() + "€", modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp))

        Spacer(modifier = Modifier.height(8.dp))

        buttons()
    }
}






