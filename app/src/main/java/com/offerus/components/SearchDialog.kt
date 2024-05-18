package com.offerus.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.offerus.R


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
    var internalSliderValue by if (distanciaMaxima == null) {
        remember { mutableStateOf(0.0F) }
    } else {
        remember { mutableStateOf(distanciaMaxima.toFloat()) }
    }

    var textoTitulo = if (titulo == null) {
        remember { mutableStateOf("") }
    } else {
        remember { mutableStateOf(titulo) }
    }
    var textoPrecioMaximo = if (precioMaximo == null) {
        remember { mutableStateOf("") }
    } else {
        remember { mutableStateOf(precioMaximo.toString()) }
    }
    var textoPrecioMinimo = if (precioMinimo == null) {
        remember { mutableStateOf("") }
    } else {
        remember { mutableStateOf(precioMinimo.toString()) }
    }

    val titulo = remember { mutableStateOf(titulo) }

    val all_cat=stringResource(id = R.string.all_categories)
    val textoCategoria = if (categoria == null){
        remember { mutableStateOf(all_cat) }
    } else {
        remember { mutableStateOf(categoria) }
    }


    var errorState by remember { mutableStateOf(false) }


    val transformedValue = if (internalSliderValue <= 200f) {
        internalSliderValue / 2f  // Velocidad baja en la primera mitad
    } else if (internalSliderValue <= 300f) {
        internalSliderValue / 1.5f
    } else {
        internalSliderValue// Doble velocidad en la segunda mitad
    }




    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card (
            modifier = Modifier
            //.background(Color.White, shape = RoundedCornerShape(8.dp))
        ){
            Column (
                modifier = Modifier
                    .verticalScroll(rememberScrollState())

            ) {


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
                                label = { Text(stringResource(id = R.string.title)) }
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
                                value = internalSliderValue,
                                onValueChange = { newValue ->
                                    internalSliderValue = newValue
                                },
                                valueRange = 0f..400f,
                                steps = 10000,
                                modifier = Modifier
                                    .width(190.dp)
                            )
                            Text(text = "%d Km".format(transformedValue.toInt()))

                            /*
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

                             */

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
                                label = { Text(text = "Min") },
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
                                label = { Text(text = "Max") }
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
                            internalSliderValue = 0F

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


