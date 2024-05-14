package com.offerus.components

import android.service.autofill.UserData
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import androidx.datastore.preferences.core.stringPreferencesKey
import com.offerus.R
import com.offerus.data.UsuarioData
import com.offerus.ui.theme.OfferUSTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoDatosPersonales(
    infoUsuario: UsuarioData,
    onDismissRequest: () -> Unit,
    onConfirmation: (UsuarioData) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var sexExpanded by rememberSaveable {
        mutableStateOf(false)
    }
    val Sexo = listOf(
        stringResource(id = R.string.hombre),
        stringResource(id = R.string.mujer),
        stringResource(id = R.string.otros))

    var nombreYapellido by rememberSaveable { mutableStateOf(infoUsuario.nombre_apellido) }
    var edad by rememberSaveable { mutableStateOf(infoUsuario.edad) }
    var sexo by rememberSaveable { mutableStateOf(infoUsuario.sexo) }
    var telefono by rememberSaveable { mutableStateOf(infoUsuario.telefono) }
    var email by rememberSaveable { mutableStateOf(infoUsuario.mail) }

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
                text = stringResource(R.string.datosPersonales),
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 10.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .height(2.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
            )

            OutlinedTextField(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp),
                value = nombreYapellido,
                onValueChange = { nombreYapellido = it },
                label = { Text(stringResource(id = R.string.full_name)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                        leadingIcon = {
                    Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier
                        .padding(0.dp)
                        .size(20.dp))
                }
            )

            OutlinedTextField(
                modifier = Modifier
                    .padding(start = 20.dp, top = 5.dp, bottom = 5.dp, end = 20.dp) ,
                value = edad.toString(),
                onValueChange = { if (it.length <=2) edad = it.toInt() },
                label = { Text(stringResource(R.string.age)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Number),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                leadingIcon = {
                    Icon(Icons.Filled.DateRange, contentDescription = null, modifier = Modifier
                        .padding(0.dp)
                        .size(20.dp))
                }
            )

            ExposedDropdownMenuBox(
                expanded = sexExpanded,
                onExpandedChange = {sexExpanded = !sexExpanded }
            )
            {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 5.dp)
                        .menuAnchor(),
                    //.clickable(onClick = {
                    //    sexExpanded = !sexExpanded
                    //}),
                    readOnly = true,
                    value = sexo,
                    onValueChange = { sexo = it },
                    label = { Text(stringResource(id = R.string.sex)) },
                    //keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Number),
                    //keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
                    trailingIcon = {
                        if (sexExpanded) Icon(Icons.Filled.KeyboardArrowUp, contentDescription = null, modifier = Modifier
                            .padding(0.dp)
                            .size(20.dp))
                        else Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null, modifier = Modifier
                            .padding(0.dp)
                            .size(20.dp))
                    },
                    leadingIcon = {
                        Icon(painter = painterResource(R.drawable.gender), contentDescription = null, modifier = Modifier
                            .size(20.dp))
                    }
                )
                ExposedDropdownMenu(
                    expanded = sexExpanded,
                    onDismissRequest = { sexExpanded = false },
                    //modifier =  Modifier.width(with(LocalDensity.current){mTextFieldSize.width.toDp()})
                ) {
                    Sexo.forEach { sexoit ->
                        DropdownMenuItem(
                            text = { Text(sexoit) },
                            onClick = {
                                sexExpanded = false
                                sexo = sexoit
                            }
                        )
                    }
                }
            }


            OutlinedTextField(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp),
                value = telefono,
                onValueChange = { if (it.length <=9) telefono = it },
                label = { Text(stringResource(id = R.string.phone)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Number),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                leadingIcon = {
                    Icon(Icons.Filled.Phone, contentDescription = null, modifier = Modifier
                        .padding(0.dp)
                        .size(20.dp))
                }
            )
            OutlinedTextField(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp),
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(id = R.string.email)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Email),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                leadingIcon = {
                    Icon(Icons.Filled.Email, contentDescription = null, modifier = Modifier
                        .padding(0.dp)
                        .size(20.dp))
                }
            )

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .height(2.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
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
                    onConfirmation(
                        UsuarioData(
                            nombre_apellido = nombreYapellido,
                            edad = edad,
                            sexo = sexo,
                            telefono = telefono,
                            mail = email,
                            descripcion = infoUsuario.descripcion,
                            latitud = infoUsuario.latitud,
                            longitud = infoUsuario.longitud,
                            suscripciones = infoUsuario.suscripciones,
                            username = infoUsuario.username
                        )
                    )
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
        /*DialogoDatosPersonales(onDismissRequest = {  }) {

        }*/
    })
}
