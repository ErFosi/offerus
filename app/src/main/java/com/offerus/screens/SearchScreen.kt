package com.offerus.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.offerus.R
import com.offerus.components.CategoriasCirculos
import com.offerus.components.DropdownCategorias
import com.offerus.data.ServicioPeticion
import com.offerus.navigation.AppScreens
import com.offerus.viewModels.MainViewModel

@OptIn(ExperimentalMaterialApi::class)
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
    val tabs = listOf( stringResource(id = R.string.offers), stringResource(id = R.string.requests))

    // FILTRO
    val titulo = remember { mutableStateOf<String?>(null) }
    val categoria = remember { mutableStateOf<String?>(null) }
    val distanciaMaxima = remember { mutableStateOf<Double?>(null) }
    val precioMinimo = remember { mutableStateOf<Double?>(null) }
    val precioMaxima = remember { mutableStateOf<Double?>(null) }
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
                    titulo.value = null
                    categoria.value = null
                    distanciaMaxima.value = null
                    precioMaxima.value = null
                    precioMinimo.value = null
                    mainViewModel.getRequests(titulo.value,categoria.value,distanciaMaxima.value,precioMinimo.value,precioMaxima.value,"")



                },
                onTituloChange = { titulo.value = it },
                onCategoriaChange = { categoria.value = it },
                onDistanciaChange = { distanciaMaxima.value = it },
                onPrecioMinChange = { precioMinimo.value = it },
                onPrecioMaxChange = { precioMaxima.value = it },
                categoria = categoria.value,
                distanciaMaxima = distanciaMaxima.value?.toFloat(),
                precioMaximo = precioMaxima.value,
                precioMinimo = precioMinimo.value,
                titulo = titulo.value

            )

        }


    }

    Surface {



        Column{



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
                    onOpenFilterDialog = { openFilterDialog.value = true },
                    navController = navController,
                    onBuscar = {
                        mainViewModel.getRequests(
                            titulo.value,
                            categoria.value,
                            distanciaMaxima.value,
                            precioMinimo.value,
                            precioMaxima.value,
                            ""
                        )
                    },
                    onBusquedaChange = { titulo.value = it },
                    setSelectedTab = { mainViewModel.selectedTabIndex = selectedTabIndex },
                    mainViewModel = mainViewModel
                )

                if (selectedTabIndex == 0) {
                    if (listaOfertas.value.isEmpty() && mainViewModel.cargaInicialPeticiones.value) {
                        Text(
                            text = stringResource(id = R.string.no_offers_found),
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(60.dp)
                        )
                    } else {
                        ListaOfertas(onItemClick = { navController.navigate(AppScreens.OfferDetailsScreen.route) },
                            listaOfertas.value,
                            mainViewModel,
                            { mainViewModel.getRequests(titulo.value,categoria.value,distanciaMaxima.value,precioMinimo.value,precioMaxima.value,"") }
                            )
                    }

                } else {
                    if (listaSolicitudes.value.isEmpty() && mainViewModel.cargaInicialPeticiones.value) {
                        Text(
                            text = stringResource(id = R.string.no_requests_found),
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(60.dp)
                        )
                    } else {
                        ListaOfertas(onItemClick = { navController.navigate(AppScreens.OfferDetailsScreen.route) },
                            listaSolicitudes.value,
                            mainViewModel,
                            { mainViewModel.getRequests(titulo.value,categoria.value,distanciaMaxima.value,precioMinimo.value,precioMaxima.value,"") }
                        )
                    }
                }





        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubPageSearch(
    navController: NavController,
    mainViewModel: MainViewModel,
    setSelectedTab: () -> Unit,
    onOpenFilterDialog: () -> Unit,
    onBusquedaChange: (String) -> Unit,
    onBuscar: () -> Unit,
){

    val ordenMenorMayor by mainViewModel.ordenAscendenteSearch
    Column {

        Row ( modifier = Modifier
            .padding(horizontal = 35.dp, vertical = 3.dp).fillMaxWidth()
        ){
            var campoBuscar by remember {
                mutableStateOf("")
            }

            TextField(value = campoBuscar,
                onValueChange = {
                    campoBuscar = it
                    onBusquedaChange(it) },
                modifier = /*Modifier.width(260.dp)*/Modifier.weight(0.83f),
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
                    .width(30.dp),
                onClick = { mainViewModel.ordenAscendenteSearch.value = !ordenMenorMayor
                            mainViewModel.ordenarServicios(ordenMenorMayor)
                }
            ) {
                if (ordenMenorMayor){
                    Icon(
                        painter = painterResource(id = R.drawable.descendente),
                        null,
                        modifier = Modifier.size(50.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ascendente),
                        null,
                        modifier = Modifier.size(50.dp)
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
                    setSelectedTab()
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
    titulo: String?,
    precioMinimo: Double?,
    precioMaximo: Double?,
    categoria: String?,
    distanciaMaxima: Float?

){

    val context = LocalContext.current
    var sliderValue by if (distanciaMaxima == null) {
        remember { mutableStateOf(0.0F)}
    } else {
        remember { mutableStateOf(distanciaMaxima.toFloat()) }
    }

    var textoTitulo = if (titulo == null) {
        remember { mutableStateOf("")}
    } else {
        remember { mutableStateOf(titulo) }
    }
    var textoPrecioMaximo = if (precioMaximo == null) {
        remember { mutableStateOf("")}
    } else {
        remember { mutableStateOf(precioMaximo.toString()) }
    }
    var textoPrecioMinimo = if (precioMinimo == null) {
        remember { mutableStateOf("")}
    } else {
        remember { mutableStateOf(precioMinimo.toString()) }
    }

    val titulo = remember { mutableStateOf(titulo) }


    val textoCategoria = if (categoria == null){
        remember { mutableStateOf("Todas las Categorias") }
    } else {
        remember { mutableStateOf(categoria) }
    }


    var errorState by remember { mutableStateOf(false) }




    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card (
            modifier = Modifier
            //.background(Color.White, shape = RoundedCornerShape(8.dp))
        ){
            Column {


                Text(
                    text = stringResource(id = R.string.filter),
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
                                    value = textoTitulo.value,
                                    onValueChange = {
                                        textoTitulo.value = it
                                        onTituloChange(it)
                                    },
                                    modifier = Modifier
                                        .height(70.dp),
                                    label = {Text(stringResource(id = R.string.title))}
                                )


                        }

                        Row(
                            modifier = Modifier.padding(vertical = 5.dp),
                        ) {
                            DropdownCategorias(onCategoriaChange = onCategoriaChange, textoCategoria.value)
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
                                valueRange = 0f..500f,
                                steps = 1000,
                                modifier = Modifier
                                    .width(200.dp)
                            )
                            Text(text = "%.1f".format(sliderValue ) + " Km")

                        }
                        Row(
                            modifier = Modifier.padding(vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            OutlinedTextField(

                                value = textoPrecioMinimo.value,
                                onValueChange = {
                                    textoPrecioMinimo.value = it
                                },
                                modifier = Modifier
                                    .height(70.dp)
                                    .width(65.dp)
                                    .onFocusChanged {
                                        if (textoPrecioMinimo.value.toDoubleOrNull() != null) {
                                            if (textoPrecioMaximo.value.toDoubleOrNull() != null) {
                                                if (textoPrecioMinimo.value.toDouble() < textoPrecioMaximo.value.toDouble()) {
                                                    onPrecioMaxChange(textoPrecioMaximo.value.toDouble())
                                                } else {
                                                    Toast
                                                        .makeText(
                                                            context,
                                                            "El precio minimo debe ser menor al precio maximo",
                                                            Toast.LENGTH_SHORT
                                                        )
                                                        .show()
                                                    textoPrecioMinimo.value = ""
                                                }
                                            } else {
                                                onPrecioMinChange(textoPrecioMinimo.value.toDouble())
                                            }
                                        } else {
                                            textoPrecioMinimo.value = ""
                                        }

                                    }


                                ,
                                label = {Text(text = "Min")},
                                singleLine = true,
                                isError = errorState
                            )
                            Text(text = " - ")
                            OutlinedTextField(
                                value = textoPrecioMaximo.value,
                                onValueChange = {
                                    textoPrecioMaximo.value = it
                                },
                                modifier = Modifier
                                    .height(70.dp)
                                    .width(65.dp)
                                    .onFocusChanged {
                                        if (textoPrecioMaximo.value.toDoubleOrNull() != null) {
                                            if (textoPrecioMinimo.value.toDoubleOrNull() != null) {
                                                if (textoPrecioMinimo.value.toDouble() < textoPrecioMaximo.value.toDouble()) {
                                                    onPrecioMaxChange(textoPrecioMaximo.value.toDouble())
                                                } else {
                                                    Toast
                                                        .makeText(
                                                            context,
                                                            "El precio minimo debe ser menor al precio maximo",
                                                            Toast.LENGTH_SHORT
                                                        )
                                                        .show()
                                                    textoPrecioMaximo.value = ""
                                                }
                                            } else {
                                                onPrecioMaxChange(textoPrecioMaximo.value.toDouble())
                                            }

                                        } else {
                                            textoPrecioMaximo.value = ""
                                        }
                                    }
                                ,
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
                            textoPrecioMaximo.value = ""
                            textoPrecioMaximo.value = ""
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






@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ListaOfertas(onItemClick: () -> Unit, listaPeticiones: List<ServicioPeticion>, mainViewModel: MainViewModel, onRefresh: () -> Unit) {

    val refreshState = rememberPullRefreshState(
        refreshing = mainViewModel.isRefreshingSearch.value,
        onRefresh = {onRefresh()}
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
            .padding(8.dp)
            .pullRefresh(refreshState)
    ) {

        LazyColumn (modifier = Modifier.fillMaxSize()) {
            items(listaPeticiones.size) { index ->
                OfertasCard(peticion = listaPeticiones[index], onItemClick = onItemClick, mainViewModel)
            }
        }


        PullRefreshIndicator(
            refreshing = mainViewModel.isRefreshingSearch.value,
            state = refreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
fun OfertasCard(peticion: ServicioPeticion, onItemClick: () -> Unit, mainViewModel: MainViewModel) {
    var context = LocalContext.current
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = {

                mainViewModel.cambiarServicioDetalle(peticion)
                mainViewModel.obtenerInfoUsuario(peticion.username)
                onItemClick()
            })

    ) {

        PeticionInfo(peticion = peticion, mainViewModel = mainViewModel)



    }
}
@Composable
fun PeticionInfo(
    peticion: ServicioPeticion,
    mainViewModel: MainViewModel
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // foto de perfil del usuario
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(4.dp)
                .width(70.dp)

        ) {
            UserAvatar(username = peticion.username, mainViewModel)
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

        Column {
            CategoriasCirculos(nombresCategorias = peticion.categorias)
        }

    }
}

