package com.offerus.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.rememberSwipeableState
import com.offerus.R
import com.offerus.model.database.entities.Deal
import com.offerus.navigation.AppScreens
import com.offerus.utils.createDealList
import com.offerus.utils.showToastOnMainThread
import com.offerus.viewModels.MainViewModel



@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun OffersScreen(
    navController: NavController,
    mainViewModel: MainViewModel,
    myOffers: Boolean, // Si es pagina MisOfertas true, si es pagina buscar false
    //myLikes: Boolean    // Si es pagina Favoritos true, si es pagina buscar false
) {

    val openCreateDialog = remember { mutableStateOf(false) }
    val openFilterDialog = remember { mutableStateOf(false) }
    val openDescriptionDialog = remember { mutableStateOf(false) }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Ofertas", "Solicitudes")

    when {

        openFilterDialog.value -> {
            SearchDialog(
                onDismissRequest = { openFilterDialog.value = false },
                onConfirmation = { openFilterDialog.value = false },
                onRefresh = { }
            )

        }

        openCreateDialog.value -> {
            CreateDialog(
                onDismissRequest = { openCreateDialog.value = false },
                onConfirmation = { openCreateDialog.value = false },
                onDescription = { openDescriptionDialog.value = true
                                    openCreateDialog.value = false},
                selectedTab = selectedTabIndex
            )

        }

        openDescriptionDialog.value -> {
            EditDescriptionDialog(
                onDismissRequest = { openDescriptionDialog.value = false
                                        openCreateDialog.value = true},
                onConfirmation = { openDescriptionDialog.value = false
                                     openCreateDialog.value = true},
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
                myOffers = myOffers,
                navController = navController,
                onOpenCreateDialog = { openCreateDialog.value = true }
            )
            ListaOfertas(myOffers = myOffers, onItemClick = {navController.navigate(AppScreens.OfferDetailsScreen.route)} )




        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubPageSearch(
    navController: NavController,
    onOpenFilterDialog: () -> Unit,
    myOffers: Boolean,
    onOpenCreateDialog: () -> Unit

){
   
    Column {

        Row ( modifier = Modifier
            .padding(horizontal = 35.dp, vertical = 3.dp)
        ){
            var campoBuscar by remember {
                mutableStateOf("")
            }

            TextField(value = campoBuscar, onValueChange = {campoBuscar = it}, modifier = Modifier.width(260.dp), shape = RoundedCornerShape(20.dp), trailingIcon = {
                Icon(Icons.Filled.Search, "Buscar")
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
                onClick = { /*TODO*/ }
            ) {
                Icon(Icons.Outlined.Refresh, "Buscar")
            }
        }
        Row (modifier = Modifier

            .padding(horizontal = 35.dp, vertical = 3.dp),


            ){

            if ( !myOffers ){
                OutlinedButton(
                    modifier = Modifier

                        .width(80.dp),
                    onClick = { /*TODO*/ }

                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_map_24),
                        null
                    )
                }
            }

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
        ListaOfertas(myOffers = myOffers, onItemClick = {navController.navigate(AppScreens.OfferDetailsScreen.route)} )
        if ( myOffers ) {
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


    }
}

@Composable
fun OfferList(){

}

@Composable
fun SelectableButtonRow(
    modifier: Modifier = Modifier,
    button1Text: String,
    button2Text: String,
    onButton1Selected: () -> Unit,
    onButton2Selected: () -> Unit,
    subPaginaOfertasSelected: Boolean
    ) {

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = { onButton1Selected() },


            ) {
            if (subPaginaOfertasSelected) {
                Text(
                    text = button1Text,
                    fontSize = 23.sp,
                    modifier = Modifier.drawBehind {
                        val strokeWidthPx = 3.dp.toPx()
                        val verticalOffset = size.height + 5.sp.toPx()
                        drawLine(
                            color = Color.Gray,
                            strokeWidth = strokeWidthPx,
                            start = Offset(0f, verticalOffset),
                            end = Offset(size.width, verticalOffset)
                        )
                    }
                )
            } else {
                Text(button1Text, fontSize = 23.sp)
            }
        }
        TextButton(
            onClick = { onButton2Selected() },


            ) {
            if (!subPaginaOfertasSelected) {
                Text(
                    text = button2Text,
                    fontSize = 23.sp,
                    modifier = Modifier.drawBehind {
                        val strokeWidthPx = 3.dp.toPx()
                        val verticalOffset = size.height + 5.sp.toPx()
                        drawLine(
                            color = Color.Gray,
                            strokeWidth = strokeWidthPx,
                            start = Offset(0f, verticalOffset),
                            end = Offset(size.width, verticalOffset)
                        )
                    }
                )
            } else {
                Text(button2Text, fontSize = 23.sp)
            }


        }


    }

}

@Composable
fun SearchDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    onRefresh: () -> Unit

    ){
    var sliderValue by remember { mutableStateOf(0f) }

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
                                value = "", onValueChange = { }, modifier = Modifier.height(50.dp),
                                label = {Text(text = "Titulo")}
                            )

                        }
                        Row(
                            modifier = Modifier.padding(vertical = 5.dp),
                        ) {
                            DropdownCategorias()
                        }

                        Row(
                            modifier = Modifier.padding(vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Slider(
                                value = sliderValue,
                                onValueChange = { newValue ->
                                    sliderValue = newValue
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
                                value = "", onValueChange = { }, modifier = Modifier
                                    .height(50.dp)
                                    .width(65.dp),
                                placeholder = {
                                    Text("Min")
                                },
                            )
                            Text(text = " - ")
                            OutlinedTextField(
                                value = "", onValueChange = { }, modifier = Modifier
                                    .height(50.dp)
                                    .width(65.dp),
                                placeholder = {
                                    Text("Max")
                                },
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
                        onClick = { onRefresh() }

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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownCategorias(

) {
    val context = LocalContext.current
    val idiomas = arrayOf("Categoria1", "Categoria2")
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember {
        mutableStateOf("Categoria1")
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            OutlinedTextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                idiomas.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            // selectedText = item
                            expanded = false
                            selectedText = item
                        }
                    )
                }
            }
        }
    }
}
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
fun ListaOfertas(onItemClick: () -> Unit, myOffers: Boolean) {
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
                OfertasCard(deal = listaEntrantes[index], myOffers = myOffers, onItemClick = onItemClick)
            }
        }
    }
}

@Composable
fun OfertasCard(deal: Deal, onItemClick: () -> Unit, myOffers: Boolean) {
    var context = LocalContext.current
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onItemClick)
    ) {

        OfferInfo(deal = deal) {
            if ( myOffers ) {
                BotonesMisOfertas()
            } else {
                BotonesOfertas()
            }

        }
    }
}

@Composable
fun BotonesOfertas() {
    var context = LocalContext.current
    Column {
        IconButton(onClick = {  }) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
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
fun BotonesMisOfertas() {
    var context = LocalContext.current
    Column {
        IconButton(onClick = {  }) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        IconButton(onClick = {

        }) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}






