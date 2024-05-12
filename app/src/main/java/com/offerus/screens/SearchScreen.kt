package com.offerus.screens

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import com.offerus.R
import com.offerus.components.DropdownCategorias
import com.offerus.data.Deal
import com.offerus.data.ServicioPeticion
import com.offerus.navigation.AppScreens
import com.offerus.utils.createDealListExample
import com.offerus.utils.showToastOnMainThread
import com.offerus.viewModels.MainViewModel

@Composable
fun OffersScreen(
    navController: NavController,
    mainViewModel: MainViewModel
) {

    // DIALOGAS
    //val openCreateDialog = remember { mutableStateOf(false) }
    val openFilterDialog = remember { mutableStateOf(false) }
    //val openDescriptionDialog = remember { mutableStateOf(false) }

    // PANTALLAS
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Ofertas", "Solicitudes")

    // FILTRO
    val titulo = remember { mutableStateOf("") }
    val categoria = remember { mutableStateOf("gratis,deporte,hogar,otros,entretenimiento,academico,online") }
    val distanciaMaxima = remember { mutableStateOf(100.0) }
    val precioMinimo = remember { mutableStateOf(0.0) }
    val precioMaxima = remember { mutableStateOf(0.0) }
    val ordenarPor = remember { mutableStateOf("precio_asc") }

    // LISTAS
    val listaOfertas = mainViewModel.listaOfertas
    val listaSolicitudes =  mainViewModel.listaSolicitudes

    if (!mainViewModel.cargaInicialPeticiones.value) {
        mainViewModel.cargarListasPeticiones()
    }


    when {

        openFilterDialog.value -> {
            SearchDialog(
                onDismissRequest = { openFilterDialog.value = false },
                onConfirmation = { openFilterDialog.value = false
                    mainViewModel.getRequests(titulo.value,categoria.value,distanciaMaxima.value,precioMinimo.value,precioMaxima.value,"")},
                onRefresh = {
                    titulo.value = ""
                    categoria.value = "gratis,deporte,hogar,otros,entretenimiento,academico,online"
                    distanciaMaxima.value = 0.0
                    precioMaxima.value = 0.0
                    precioMinimo.value = 0.0



                },
                onTituloChange = { titulo.value = it },
                onCategoriaChange = { categoria.value = it },
                onDistanciaChange = { distanciaMaxima.value = it },
                onPrecioMinChange = { precioMinimo.value = it },
                onPrecioMaxChange = { precioMaxima.value = it },
                categoria = categoria.value,
                distanciaMaxima = distanciaMaxima.value.toFloat(),
                precioMaximo = precioMaxima.value,
                precioMinimo = precioMinimo.value,
                titulo = titulo.value

            )

        }


    }

    Surface {

        Column {

            TabRow(selectedTabIndex) {
                // Crear una pestaña para cada elemento en la lista de pestañas
                tabs.forEachIndexed { index, title ->
                    Tab(selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) })
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            SubPageSearch(
                onOpenFilterDialog = {  openFilterDialog.value = true },
                navController = navController,
                onBuscar = { mainViewModel.getRequests(titulo.value,categoria.value,distanciaMaxima.value,precioMinimo.value,precioMaxima.value,"")},
                onBusquedaChange = { titulo.value = it}
            )

            if (selectedTabIndex == 0) {
                if (listaOfertas.value.isEmpty() && mainViewModel.cargaInicialPeticiones.value){
                    Text(
                        text = "No hay Resultados de Ofertas",
                        style =  MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(60.dp)
                    )
                } else {
                    ListaOfertas(onItemClick = {navController.navigate(AppScreens.OfferDetailsScreen.route)} ,listaOfertas.value, mainViewModel)
                }

            } else {
                if (listaSolicitudes.value.isEmpty() && mainViewModel.cargaInicialPeticiones.value){
                    Text(
                        text = "No hay Resultados de Solicitudes",
                        style =  MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(60.dp)
                    )
                } else {
                    ListaOfertas(onItemClick = {navController.navigate(AppScreens.OfferDetailsScreen.route)} ,listaSolicitudes.value, mainViewModel)
                }
            }




        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubPageSearch(
    navController: NavController,
    onOpenFilterDialog: () -> Unit,
    onBusquedaChange: (String) -> Unit,
    onBuscar: () -> Unit,
){

    var ordenMenorMayor = remember {
        mutableStateOf(true)
    }
    Column {

        Row ( modifier = Modifier
            .padding(horizontal = 35.dp, vertical = 3.dp)
        ){
            var campoBuscar by remember {
                mutableStateOf("")
            }

            TextField(value = campoBuscar,
                onValueChange = {
                    campoBuscar = it
                    onBusquedaChange(it) },
                modifier = Modifier.width(260.dp),
                shape = RoundedCornerShape(20.dp),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            onBuscar()
                            campoBuscar = ""
                        }
                    ) {
                        Icon(Icons.Filled.Search, "Buscar")
                    }

                },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent, // Establece el indicador de foco transparente
                    unfocusedIndicatorColor = Color.Transparent, // Establece el indicador sin foco transparente
                    cursorColor = Color.Black // Establece el color del cursor
                )
            )
            IconButton(
                modifier = Modifier
                    .padding(5.dp)
                    .width(50.dp),
                onClick = { ordenMenorMayor.value = !ordenMenorMayor.value }
            ) {
                if (ordenMenorMayor.value){
                    Icon(
                        painter = painterResource(id = R.drawable.descendente),
                        null,
                        modifier = Modifier.size(100.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ascendente),
                        null,
                        modifier = Modifier.size(100.dp)
                    )
                }

            }
        }
        Row (modifier = Modifier

            .padding(horizontal = 35.dp, vertical = 3.dp),


            ){


            OutlinedButton(
                modifier = Modifier

                    .width(80.dp),
                onClick = {
                    navController.navigate(AppScreens.MapScreen.route)
                }

            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_map_24),
                    null
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            OutlinedButton(
                modifier = Modifier
                    .weight(1f),
                onClick = { onOpenFilterDialog() }

            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_filter_list_24),
                    null
                )
            }

        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    onRefresh: () -> Unit,
    onTituloChange: (String) -> Unit,
    onCategoriaChange: (String) -> Unit,
    onDistanciaChange: (Double) -> Unit,
    onPrecioMinChange: (Double) -> Unit,
    onPrecioMaxChange: (Double) -> Unit,
    titulo: String,
    precioMinimo: Double,
    precioMaximo: Double,
    categoria: String,
    distanciaMaxima: Float

){
    var sliderValue by remember { mutableStateOf(distanciaMaxima.toFloat()) }
    val titulo = remember { mutableStateOf(titulo) }
    val precioMinimo = remember { mutableStateOf(precioMinimo.toInt().toString()) }
    val precioMaxima = remember { mutableStateOf(precioMaximo.toInt().toString()) }

    var errorState by remember { mutableStateOf(false) }


    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card (
            modifier = Modifier
            //.background(Color.White, shape = RoundedCornerShape(8.dp))
        ){
            Column {


                Text(
                    text = "Filtro",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .padding(horizontal = 30.dp, vertical = 10.dp)
                )
                Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
                Box(
                    modifier = Modifier
                        .padding(horizontal = 30.dp, vertical = 20.dp)
                ) {
                    Column {

                        Row(
                            modifier = Modifier.padding(vertical = 5.dp),
                        ) {
                            OutlinedTextField(
                                value = titulo.value,
                                onValueChange = {
                                    titulo.value = it
                                    onTituloChange(it)
                                },
                                modifier = Modifier
                                    .height(70.dp)
                                    .padding(vertical = 10.dp),
                                label = {Text(text = "Titulo")}
                            )

                        }

                        Row(
                            modifier = Modifier.padding(vertical = 5.dp),
                        ) {
                            DropdownCategorias(onCategoriaChange = onCategoriaChange, categoria)
                        }

                        Row(
                            modifier = Modifier.padding(vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Slider(
                                value = sliderValue,
                                onValueChange = { newValue ->
                                    sliderValue = newValue
                                    onDistanciaChange(newValue.toDouble())
                                },
                                valueRange = 0f..100f,
                                steps = 100,
                                modifier = Modifier
                                    .width(150.dp)
                            )
                            Text(text = "%.1f".format(sliderValue / 10) + " Km")

                        }
                        Row(
                            modifier = Modifier.padding(vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(

                                value = precioMinimo.value.toString(),
                                onValueChange = {

                                    if ( it.isEmpty() || it.toDoubleOrNull() != null) {
                                        precioMinimo.value = it
                                        errorState = false
                                        if (!it.isEmpty()){
                                            onPrecioMinChange(it.toDouble())
                                        }


                                    } else {
                                        errorState = true
                                    }
                                },
                                modifier = Modifier
                                    .height(70.dp)
                                    .width(65.dp),
                                label = {Text(text = "Min")},
                                singleLine = true,
                                isError = errorState
                            )
                            Text(text = " - ")
                            OutlinedTextField(
                                value = precioMaxima.value.toString(),
                                onValueChange = {
                                    if ( it.isEmpty() || it.toDoubleOrNull() != null) {
                                        precioMaxima.value = it
                                        errorState = false
                                        if (!it.isEmpty()){
                                            onPrecioMaxChange(it.toDouble())
                                        }


                                    } else {
                                        errorState = true
                                    }
                                },
                                modifier = Modifier
                                    .height(70.dp)
                                    .width(65.dp),
                                label = {Text(text = "Max")}
                            )
                            Text(text = " €")
                        }

                    }
                }
                Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    horizontalArrangement = Arrangement.Center
                ){
                    OutlinedButton(
                        modifier = Modifier
                            .padding(horizontal = 5.dp),
                        onClick = { onDismissRequest() }

                    ) {
                        Icon(
                            Icons.Default.Close,
                            null
                        )

                    }
                    OutlinedButton(
                        modifier = Modifier
                            .padding(horizontal = 5.dp),
                        onClick = { onRefresh()
                            titulo.value = ""
                            precioMaxima.value = "0"
                            precioMinimo.value = "0"
                            sliderValue = 0F

                        }

                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            null
                        )
                    }
                    OutlinedButton(
                        modifier = Modifier
                            .padding(horizontal = 5.dp),
                        onClick = { onConfirmation() }

                    ) {
                        Icon(
                            Icons.Default.Search,
                            null
                        )
                    }
                }
            }
        }
    }
}






@Composable
fun ListaOfertas(onItemClick: () -> Unit, listaPeticiones: List<ServicioPeticion>, mainViewModel: MainViewModel) {
    //obtenemos la lista del viemodel
    var listaEntrantes = createDealListExample()
    //mostramos la lista
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        LazyColumn {
            items(listaPeticiones.size) { index ->
                OfertasCard(peticion = listaPeticiones[index], onItemClick = onItemClick, mainViewModel)
            }
        }
    }
}

@Composable
fun OfertasCard(peticion: ServicioPeticion, onItemClick: () -> Unit, mainViewModel: MainViewModel) {
    var context = LocalContext.current
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = {
                mainViewModel.cambiarServicioDetalle(peticion.id)
                onItemClick()
            })

    ) {

        PeticionInfo(peticion = peticion)



    }
}
@Composable
fun PeticionInfo(
    peticion: ServicioPeticion
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
            //UserAvatar(username = "oier")
            Text(text = peticion.username)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = peticion.titulo,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
            )
            Text(text = peticion.precio.toString()+" €", modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp))
        }

    }
}



// BASURERO TEMPORAL

/*
@Composable
fun CreateOferRequestFloatingButton(
    onOpenCreateDialog: () -> Unit
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .wrapContentSize(Alignment.BottomEnd)
            .padding(16.dp)
    ) {
        FloatingActionButton(
            onClick = { onOpenCreateDialog() },

            modifier = Modifier
                .padding(16.dp)
                .size(56.dp),
            shape = CircleShape
        ) {
            Icon(Icons.Filled.Add, "Floating action button.")
        }
    }
}
@Composable
fun CreateDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    onDescription: () -> Unit,
    selectedTab: Int

){

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card (
            modifier = Modifier
            //  .background(Color.White, shape = RoundedCornerShape(8.dp))
        ){
            Column {

                if ( selectedTab == 0 ) {
                    Text(
                        text = "Crear Oferta",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier
                            .padding(horizontal = 30.dp, vertical = 10.dp)
                    )
                } else {
                    Text(
                        text = "Crear Solicitud",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier
                            .padding(horizontal = 30.dp, vertical = 10.dp)
                    )
                }

                Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
                Box(
                    modifier = Modifier
                        .padding(horizontal = 30.dp, vertical = 20.dp)
                ) {
                    Column {

                        Row(
                            modifier = Modifier.padding(vertical = 5.dp),
                        ) {
                            OutlinedTextField(
                                value = "", onValueChange = { }, modifier = Modifier.height(50.dp),
                                label = {Text(text = "Titulo")}
                            )
                        }
                        Row(
                            modifier = Modifier.padding(vertical = 5.dp),
                        ) {
                            DropdownCategorias()
                        }

                        if ( selectedTab == 0 ) {
                            Row(
                                modifier = Modifier.padding(vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Precio: ", fontSize = 25.sp)
                                OutlinedTextField(
                                    value = "", onValueChange = { }, modifier = Modifier
                                        .height(50.dp)
                                        .width(65.dp),
                                )
                                Text(text = " €", fontSize = 25.sp)
                            }
                        }

                        Row(
                            modifier = Modifier.padding(vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Descripción: ", fontSize = 25.sp)

                            OutlinedButton(
                                modifier = Modifier
                                    .padding(horizontal = 5.dp),
                                onClick = { onDescription() }

                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_edit_square_24),
                                    null
                                )

                            }
                        }



                    }
                }
                Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    horizontalArrangement = Arrangement.Center
                ){
                    OutlinedButton(
                        modifier = Modifier
                            .padding(horizontal = 5.dp),
                        onClick = { onDismissRequest() }

                    ) {
                        Icon(
                            Icons.Default.Close,
                            null
                        )

                    }
                    OutlinedButton(
                        modifier = Modifier
                            .padding(horizontal = 5.dp),
                        onClick = { onConfirmation() }

                    ) {

                        Icon(
                            Icons.Default.Add,
                            null
                        )
                        Text(text = "Crear")
                    }

                }
            }
        }
    }
}

@Composable
fun EditDescriptionDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
){

    var texto by remember {
        mutableStateOf("")
    }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
            //.background(Color.White, shape = RoundedCornerShape(8.dp))
        ) {

            Column {
                Text(
                    text = "Editar Descripción",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .padding(horizontal = 30.dp, vertical = 10.dp)
                )

                Box(
                    modifier = Modifier
                        .padding(horizontal = 30.dp, vertical = 20.dp)
                ) {
                    OutlinedTextField(
                        value = texto, onValueChange = { texto = it},
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .height(250.dp)
                            .fillMaxWidth()
                    )
                }
                Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    horizontalArrangement = Arrangement.Center
                ){

                    OutlinedButton(
                        modifier = Modifier
                            .padding(horizontal = 5.dp),
                        onClick = { onDismissRequest() }

                    ) {
                        Icon(
                            Icons.Default.Close,
                            null
                        )

                    }
                    OutlinedButton(
                        modifier = Modifier
                            .padding(horizontal = 5.dp),
                        onClick = { onConfirmation() }

                    ) {
                        Icon(
                            Icons.Default.Done,
                            null
                        )

                    }

                }
            }


        }
    }
}


*/

