package com.offerus.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.offerus.R


@Composable
fun SearchScreen(

) {
    // SubPagina seleccionada ( Ofertas / Solicitudes )
    var selectedSubscreen by remember { mutableStateOf("Ofertas") }

    Surface {
        //EditDescriptionDialog()
        SearchDialog()
        //CreateDialog()
        Column {
            SelectableButtonRow(
                button1Text = "Ofertas",
                button2Text = "Solicitudes",
                onButton1Selected = { selectedSubscreen = "Ofertas" },
                onButton2Selected = { selectedSubscreen = "Solicitudes" },
                selectedSubScreen = selectedSubscreen
            )
            Spacer(modifier = Modifier.height(20.dp))
            SubPageSearch()
        }




    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubPageSearch(){
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

            OutlinedButton(
                modifier = Modifier
                    .weight(1f),
                onClick = { /*TODO*/ }

            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_filter_list_24),
                    null
                )
            }


        }
        OfferList()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .wrapContentSize(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            FloatingActionButton(
                onClick = {  },

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
    selectedSubScreen: String
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = { onButton1Selected },


            ) {
            if (selectedSubScreen == button1Text) {
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
            onClick = { onButton2Selected },


            ) {
            if (selectedSubScreen == button2Text) {
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
fun SearchDialog(){
    var sliderValue by remember { mutableStateOf(0f) }

    Dialog(onDismissRequest = { /*TODO*/ }) {
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
                        onClick = { /*TODO*/ }

                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            null
                        )
                    }
                    OutlinedButton(
                        modifier = Modifier
                            .padding(horizontal = 5.dp),
                        onClick = { /*TODO*/ }

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
fun CreateDialog(){

    Dialog(onDismissRequest = { /*TODO*/ }) {
        Box (
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(8.dp))
        ){
            Column {


                Text(
                    text = "Crear Oferta",
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
                            Text(text = "Precio: ", fontSize = 25.sp)
                            OutlinedTextField(
                                value = "", onValueChange = { }, modifier = Modifier
                                    .height(50.dp)
                                    .width(65.dp),
                            )
                            Text(text = " €", fontSize = 25.sp)
                        }
                        Row(
                            modifier = Modifier.padding(vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Descripción: ", fontSize = 25.sp)
                            OutlinedButton(
                                modifier = Modifier
                                    .padding(horizontal = 5.dp),
                                onClick = { /*TODO*/ }

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
                        onClick = { /*TODO*/ }

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
fun EditDescriptionDialog(){

    var texto by remember {
        mutableStateOf("")
    }

    Dialog(onDismissRequest = { /*TODO*/ }) {
        Box(
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(8.dp))
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
                        onClick = { /*TODO*/ }

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






