package com.offerus.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.offerus.R
import com.offerus.data.CATEGORIAS
import com.offerus.utils.obtenerCategoriasString
import com.offerus.utils.obtenerFechaHoy
import com.offerus.utils.showToastOnMainThread
import com.offerus.viewModels.MainViewModel

@Composable
fun CreateDialog(
    viewModel: MainViewModel,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    val context = LocalContext.current
    var esPeticion by remember {
        mutableStateOf(false)
    }

    var texto by remember {
        mutableStateOf("")
    }

    var precio by remember {
        mutableStateOf("0")
    }

    var categoria by remember {
        mutableStateOf("")
    }
    var selectedCategories by remember { mutableStateOf(emptyList<String>()) }

    var desc by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(modifier = Modifier.padding(vertical = 64.dp)) {
            LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                item {
                    Text(
                        text = stringResource(id = R.string.nuevo_servicio),
                        modifier = Modifier
                            .padding(10.dp),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                    )

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                    )

                    // Contenido del dialogo
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 30.dp, vertical = 10.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {

                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // TIPO DE SERVICIO con switch
                                Box(
                                    modifier = Modifier
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.outline,
                                            shape = OutlinedTextFieldDefaults.shape
                                        )
                                        .padding(4.dp)

                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(
                                            vertical = 0.dp,
                                            horizontal = 2.dp
                                        )
                                    ) {

                                        Switch(
                                            checked = esPeticion,
                                            onCheckedChange = { esPeticion = it },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = MaterialTheme.colorScheme.secondary,
                                                checkedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                                                uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                                                uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                                            )
                                        )
                                        Text(
                                            text = if (esPeticion) stringResource(id = R.string.peticion) else stringResource(id = R.string.oferta),
                                            modifier = Modifier.padding(start = 8.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // PRECIO
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    OutlinedTextField(
                                        value = precio,
                                        onValueChange = { newValue ->
                                            val regex =
                                                Regex("""^\d{0,8}(\.\d{0,2})?$""") // Expresión regular para validar el formato
                                            if (regex.matches(newValue)) {
                                                precio = newValue
                                            }
                                        },
                                        label = { Text(text = stringResource(id = R.string.precio) +  "(€)") }
                                    )

                                }

                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // TITULO
                            Column(
                                modifier = Modifier.padding(vertical = 2.dp),
                            ) {
                                OutlinedTextField(
                                    value = texto, onValueChange = {
                                        if (it.length <= 100) {
                                            texto = it
                                        }
                                    },
                                    label = { Text(text = stringResource(id = R.string.titulo)) }
                                )
                                Text(
                                    text = "${100 - texto.length}/100",
                                    color = if (100 - texto.length <= 0) Color.Red else Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // DESCRIPCION
                            Column {
                                OutlinedTextField(
                                    value = desc,
                                    onValueChange = {
                                        if (it.length <= 400) {
                                            desc = it
                                        } else {

                                        }
                                    },
                                    label = { Text(text = stringResource(id = R.string.descripcion)) },

                                    )
                                Text(
                                    text = "${400 - desc.length}/400",
                                    color = if (400 - desc.length <= 0) Color.Red else Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // CATEGORIAS

                            Text(
                                text = stringResource(id = R.string.categorias),
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Column(modifier = Modifier.fillMaxWidth()) {
                                CATEGORIAS.chunked(2).forEach { categoriesRow ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        categoriesRow.forEach { category ->
                                            Card(
                                                modifier = Modifier
                                                    .padding(2.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = category.color.copy(
                                                        alpha = 0.15f
                                                    )
                                                ),

                                                ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(4.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = ImageVector.vectorResource(
                                                            category.icono
                                                        ),
                                                        contentDescription = null,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(2.dp))
                                                    Text(
                                                        text = stringResource(id = category.nombreMostrar),
                                                        fontSize = 14.sp,

                                                        )
                                                    Checkbox(
                                                        checked = selectedCategories.contains(
                                                            category.nombre
                                                        ),
                                                        onCheckedChange = { isChecked ->
                                                            selectedCategories = if (isChecked) {
                                                                selectedCategories + category.nombre
                                                            } else {
                                                                selectedCategories - category.nombre
                                                            }

                                                            Log.d(
                                                                "CATEGORIAS",
                                                                obtenerCategoriasString(
                                                                    selectedCategories
                                                                )
                                                            )
                                                        },
                                                        colors = CheckboxDefaults.colors(
                                                            checkedColor = category.color.copy(alpha = 0.45f),
                                                            uncheckedColor = category.color.copy(
                                                                alpha = 0.25f
                                                            )
                                                        ),
                                                        modifier = Modifier
                                                            .padding(0.dp)
                                                            .size(32.dp),
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }


                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                    )

                    // BOTONES
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp, bottom = 5.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
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
                        Button(
                            modifier = Modifier
                                .padding(horizontal = 5.dp),
                            onClick = {
                                if (texto.isEmpty() || selectedCategories.isEmpty() || desc.isEmpty() || precio.isEmpty()) {
                                    showToastOnMainThread(context, "Rellena todos los campos")
                                } else {
                                    viewModel.createRequest(
                                        texto,
                                        desc,
                                        esPeticion,
                                        precio.toDouble(),
                                        obtenerFechaHoy(),
                                        0.0,
                                        0.0,
                                        obtenerCategoriasString(selectedCategories),
                                        context
                                    )
                                    onConfirmation()
                                }

                            }

                        ) {

                            Icon(
                                Icons.Default.Add,
                                null
                            )
                        }

                    }
                }
            }
        }
    }
}