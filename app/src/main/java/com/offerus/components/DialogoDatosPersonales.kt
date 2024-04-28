package com.offerus.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import com.offerus.ui.theme.OfferUSTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoDatosPersonales(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var sexExpanded by rememberSaveable {
        mutableStateOf(false)
    }
    val Sexo = listOf(
        "Hombre",
        "Mujer",
        "Otro")
    var mTextFieldSize by remember { mutableStateOf(Size.Zero) }

    Dialog(
        onDismissRequest = { onDismissRequest() },
    ){
        Card(
            modifier = Modifier
                //.width(400.dp)
                //.height(150.dp)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Datos personales", //stringResource("Sobre mí"),
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 40.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp),
                value = "", // TODO
                onValueChange = {  }, // TODO
                label = { Text("Nombre y apellidos") }, // TODO
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                        leadingIcon = {
                    Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.padding(0.dp).size(20.dp))
                }
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween

            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(start = 20.dp, top = 5.dp, bottom = 5.dp)
                        .width(103.dp),
                    value = "", // TODO
                    onValueChange = {  }, // TODO
                    label = { Text("Edad") }, // TODO
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
                )
                /*OutlinedTextField(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 5.dp)
                        .width(125.dp),
                        //.clickable(onClick = {
                        //    sexExpanded = !sexExpanded
                        //}),
                    readOnly = true,
                    value = "", // TODO
                    onValueChange = {  }, // TODO
                    label = { Text("Sexo") }, // TODO
                    //keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Number),
                    //keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
                )


                DropdownMenu(
                    expanded = sexExpanded,
                    onDismissRequest = { sexExpanded = false },
                    modifier =  Modifier.width(with(LocalDensity.current){mTextFieldSize.width.toDp()})
                ) {
                    Sexo.forEach { sexo ->
                        DropdownMenuItem(
                            text = { Text(sexo) },
                            onClick = {
                                sexExpanded = false
                            }
                        )
                    }
                }*/

                ExposedDropdownMenuBox(
                    expanded = sexExpanded,
                    onExpandedChange = {sexExpanded = !sexExpanded }
                )
                {
                    OutlinedTextField(
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 5.dp)
                            .width(118.dp)
                            .menuAnchor(),
                        //.clickable(onClick = {
                        //    sexExpanded = !sexExpanded
                        //}),
                        readOnly = true,
                        value = "", // TODO
                        onValueChange = {  }, // TODO
                        label = { Text("Sexo") }, // TODO
                        //keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Number),
                        //keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
                        trailingIcon = {
                            if (sexExpanded) Icon(Icons.Filled.KeyboardArrowUp, contentDescription = null, modifier = Modifier.padding(0.dp).size(20.dp))
                            else Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null, modifier = Modifier.padding(0.dp).size(20.dp))
                        }
                            //ExposedDropdownMenuDefaults.TrailingIcon(expanded = sexExpanded)},
                    )
                    ExposedDropdownMenu(
                        expanded = sexExpanded,
                        onDismissRequest = { sexExpanded = false },
                        //modifier =  Modifier.width(with(LocalDensity.current){mTextFieldSize.width.toDp()})
                    ) {
                        Sexo.forEach { sexo ->
                            DropdownMenuItem(
                                text = { Text(sexo) },
                                onClick = {
                                    sexExpanded = false
                                    //viewModel.updateIdioma(idioma, context)
                                }
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp),
                value = "", // TODO
                onValueChange = {  }, // TODO
                label = { Text("Telefono") }, // TODO
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Number),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                leadingIcon = {
                    Icon(Icons.Filled.Phone, contentDescription = null, modifier = Modifier.padding(0.dp).size(20.dp))
                }
            )
            OutlinedTextField(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp),
                value = "", // TODO
                onValueChange = {  }, // TODO
                label = { Text("Email") }, // TODO
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Email),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                leadingIcon = {
                    Icon(Icons.Filled.Email, contentDescription = null, modifier = Modifier.padding(0.dp).size(20.dp))
                }
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                //horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(onClick = {
                    onDismissRequest()
                }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = ""// stringResource(R.string.Cancelar)
                    )
                }

                Button(onClick = {
                    onConfirmation()
                }) {
                    Text(text = "")
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = ""//stringResource(R.string.Borrar)
                    )
                }
            }

        }
    }
}




@Preview(showBackground = true)
@Composable
fun previewDialogodatospersonales() {
    OfferUSTheme(content = {
        DialogoDatosPersonales(onDismissRequest = {  }) {

        }
    })
}