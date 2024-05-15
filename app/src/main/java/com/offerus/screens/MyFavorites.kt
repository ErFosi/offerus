package com.offerus.screens



import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.offerus.R
import com.offerus.components.TopBarSecundario
import com.offerus.data.ServicioPeticion
import com.offerus.navigation.AppScreens
import com.offerus.viewModels.MainViewModel


@Composable
fun FavoritesScreen(
    navController: NavController,
    mainViewModel: MainViewModel,
){
    Scaffold(topBar = { TopBarSecundario(navController) }) {
            innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
           MyFavoritesPage(navController = navController, mainViewModel = mainViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyFavoritesPage(
    navController: NavController,
    mainViewModel: MainViewModel,
) {

    // DIALOGAS
    val openFilterDialog = remember { mutableStateOf(false) }

    // PANTALLAS
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Ofertas", "Solicitudes")

    // FILTRO
    val titulo = remember { mutableStateOf<String?>(null) }
    val categoria = remember { mutableStateOf<String?>(null) }
    val distanciaMaxima = remember { mutableStateOf<Double?>(null) }
    val precioMinimo = remember { mutableStateOf<Double?>(null) }
    val precioMaxima = remember { mutableStateOf<Double?>(null) }
    val ordenarPor = remember { mutableStateOf("precio_asc") }

    val ordenMenorMayor by mainViewModel.ordenAscendenteFavoritas

    // LISTAS
    //mainViewModel.getMyFavorites()
    val listaOfertasFavoritas = mainViewModel.listaOfertasFavoritas
    val listaSolicitudesFavoritas = mainViewModel.listaSolicitudesFavoritas


    //PRECARGAR PETICIONES
    if (!mainViewModel.cargaInicialPeticionesFavoritas.value) {

        mainViewModel.getMyFavorites()
        mainViewModel.cargaInicialPeticionesFavoritas.value = true
    }



    when {

        openFilterDialog.value -> {
            SearchDialog(
                onDismissRequest = { openFilterDialog.value = false },
                onConfirmation = { openFilterDialog.value = false
                    //mainViewModel.getMyFavorites()
                    mainViewModel.filtrarFavoritas(titulo.value,categoria.value,distanciaMaxima.value,precioMinimo.value,precioMaxima.value)
                    },

                onRefresh = {
                    titulo.value = null
                    categoria.value = null
                    distanciaMaxima.value = null
                    precioMaxima.value = null
                    precioMinimo.value = null
                    mainViewModel.filtrarFavoritas(titulo.value,categoria.value,distanciaMaxima.value,precioMinimo.value,precioMaxima.value)
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
            Row ( modifier = Modifier
                .padding(horizontal = 35.dp, vertical = 3.dp)
            ){
                var campoBuscar by remember {
                    mutableStateOf("")
                }

                TextField(value = campoBuscar,
                    onValueChange = {
                        campoBuscar = it
                        titulo.value = it},
                    modifier = Modifier.width(260.dp),
                    shape = RoundedCornerShape(20.dp),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                mainViewModel.getMyFavorites()
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
                    onClick = { mainViewModel.ordenAscendenteFavoritas.value = !ordenMenorMayor
                        mainViewModel.ordenarServiciosFavoritos(ordenMenorMayor)
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




                Spacer(modifier = Modifier.width(6.dp))

                OutlinedButton(
                    modifier = Modifier
                        .weight(1f),
                    onClick = { openFilterDialog.value = true }

                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_filter_list_24),
                        null
                    )
                }

            }

            if (selectedTabIndex == 0) {
                if (listaOfertasFavoritas.value.isEmpty() && mainViewModel.cargaInicialPeticionesFavoritas.value){

                    Text(
                        text = "No hay Resultados de Ofertas",
                        style =  MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(60.dp)
                    )
                } else {
                    ListaOfertasFavoritas(onItemClick = {navController.navigate(AppScreens.OfferDetailsScreen.route)} ,listaOfertasFavoritas.value, mainViewModel,
                        {  mainViewModel.filtrarFavoritas(titulo.value,categoria.value,distanciaMaxima.value,precioMinimo.value,precioMaxima.value) })
                }

            } else {
                if (listaSolicitudesFavoritas.value.isEmpty() && mainViewModel.cargaInicialPeticionesFavoritas.value){
                    Text(
                        text = "No hay Resultados de Solicitudes",
                        style =  MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(60.dp)
                    )
                } else {
                    ListaOfertasFavoritas(onItemClick = {navController.navigate(AppScreens.OfferDetailsScreen.route)} ,listaSolicitudesFavoritas.value, mainViewModel,
                        {  mainViewModel.filtrarFavoritas(titulo.value,categoria.value,distanciaMaxima.value,precioMinimo.value,precioMaxima.value)})
                }
            }




        }

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ListaOfertasFavoritas(onItemClick: () -> Unit, listaPeticiones: List<ServicioPeticion>, mainViewModel: MainViewModel, onRefresh: () -> Unit) {

    val refreshState = rememberPullRefreshState(
        refreshing = mainViewModel.isRefreshingFavorites.value,
        onRefresh = {onRefresh()}
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
            .padding(8.dp)
            .pullRefresh(refreshState)
    ) {

        LazyColumn {
            items(listaPeticiones.size) { index ->
                OfertasCard(peticion = listaPeticiones[index], onItemClick = onItemClick, mainViewModel)
            }
        }


        PullRefreshIndicator(
            refreshing = mainViewModel.isRefreshingFavorites.value,
            state = refreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}
