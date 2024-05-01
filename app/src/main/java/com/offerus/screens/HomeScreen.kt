package com.offerus.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.offerus.R
import com.offerus.model.database.entities.Deal
import com.offerus.navigation.AppScreens
import com.offerus.utils.createDealList
import com.offerus.utils.showToastOnMainThread

@Composable
fun HomeScreen(navController: NavController) {
    // Estado para almacenar la pestaña seleccionada
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Lista de pestañas
    val tabs = listOf("Entrantes", "Salientes")

    // Superficie principal
    Surface {
        Column {
            // TabRow para mostrar las pestañas
            TabRow(selectedTabIndex) {
                // Crear una pestaña para cada elemento en la lista de pestañas
                tabs.forEachIndexed { index, title ->
                    Tab(selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) })
                }
            }

            // Mostrar la subpantalla correspondiente a la pestaña seleccionada
            when (selectedTabIndex) {
                0 -> Entrantes() {
                    navController.navigate(AppScreens.OfferDetailsScreen.route)
                }

                1 -> Salientes() {
                    navController.navigate(AppScreens.OfferDetailsScreen.route)
                }
            }
        }
    }
}

@Composable
fun Entrantes(onItemClick: () -> Unit) {
    //obtenemos la lista del viemodel
    var listaEntrantes = createDealList()
    //mostramos la lista
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        LazyColumn {
            items(listaEntrantes.size) { index ->
                EntrantesCard(deal = listaEntrantes[index]) {
                    onItemClick()
                }
            }
        }
    }
}

@Composable
fun EntrantesCard(deal: Deal, onItemClick: () -> Unit) {
    var context = LocalContext.current
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onItemClick)
    ) {
        OfferInfo(deal = deal) { BotonesEntrantes() }
    }
}

@Composable
fun BotonesEntrantes() {
    var context = LocalContext.current
    Column {
        IconButton(onClick = {
            showToastOnMainThread(context, "Petición aceptada")
        }) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Aceptar",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        IconButton(onClick = {
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

@Composable
fun Salientes(onItemClick: () -> Unit) {
    var listaSalientes = createDealList()
    //mostramos la lista
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        LazyColumn {
            items(listaSalientes.size) { index ->
                SalientesCard(deal = listaSalientes[index]) {
                    onItemClick()
                }
            }
        }
    }
}

@Composable
fun SalientesCard(deal: Deal, onItemClick: () -> Unit) {
    var context = LocalContext.current
    var estado = listOf("Pendiente", "Aceptada", "Rechazada").random()
    Card(
        modifier = Modifier
            .padding(8.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onItemClick()
                    },
                    onLongPress = {
                        showToastOnMainThread(context, estado)
                    })
            }
    ) {
        OfferInfo(deal = deal) {
            //estado aleatorio entre pendiente, aceptada y rechazada

            IconoEstado(estado = estado)
        }
    }
}

//icono pendiente, aceptada, rechazada
@Composable
fun IconoEstado(estado: String) {
    when (estado) {
        "Pendiente" -> {

            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.pending),
                contentDescription = "Pendiente",
                modifier = Modifier.size(28.dp),
            )
        }

        "Aceptada" -> {
            Icon(
                painter = painterResource(R.drawable.logorecortado),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        "Rechazada" -> {
            Icon(
                painter = painterResource(R.drawable.logorecortado),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun OfferInfo(
    deal: Deal, accion: @Composable () -> Unit = {}
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
            Text(text = deal.usernameOfrece)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Titulo de la solicitud me jdsakfld ejkalds sdfd",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
            )
            Text(text = "30€", modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp))
        }

        accion()
    }
}






