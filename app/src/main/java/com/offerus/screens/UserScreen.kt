package com.offerus.screens

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle
import com.offerus.R
import com.offerus.components.DialogoDatosPersonales
import com.offerus.components.DialogoSobreMi
import com.offerus.components.ProfilePicture
import com.offerus.ui.theme.OfferUSTheme
import java.io.File


@Composable
fun UserScreen(){
    var sobreMiExpanded by rememberSaveable {
        mutableStateOf(false)
    }
    var datosPersonalesExpanded by rememberSaveable {
        mutableStateOf(false)
    }
    var contrasenaExpanded by rememberSaveable {
        mutableStateOf(false)
    }
    var sobreMiEdit by rememberSaveable {
        mutableStateOf(false)
    }
    var datosPersonalesEdit by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
            //.padding(horizontal = 50.dp)
        )
    {
        Row(
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .height(120.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(0.dp)
                    .width(120.dp)
            )
            {
                // Profile picture
                var uri by remember { mutableStateOf<Uri?>(Uri.parse("")) }
                if (uri == Uri.parse("")){
                    if (true) { // TODO: check if there is internet connection
                        /*try {
                            viewModel.getProfilePicture { bitmap ->
                                if (bitmap != null) {
                                    uri = context.createImageFileFromBitMap(bitmap)
                                }
                            }
                        }catch (e: NotFoundException){
                            Log.d("Ajustes", "No se ha podido obtener la imagen de perfil")
                        }*/

                    }else{
                        /*Toast.makeText(
                            context,
                            R.string.no_internet_pic,
                            Toast.LENGTH_SHORT
                        ).show()*/
                    }
                    uri = "android.resource://com.offerus/drawable/baseline_adb_24".toUri()
                }

                //image to show bottom sheet
                ProfilePicture(
                    directory = File("images"),
                    uri = uri,
                    onSetUri = {
                        /*if (isNetworkAvailable(context)) context.getFileFromUri(it)?.let {
                                it1 -> viewModel.subirFotoDePerfil(it1)
                            uri = it
                        }
                        else Toast.makeText(context, R.string.no_internet_pic, Toast.LENGTH_SHORT).show()*/
                    },
                    editable = true
                )
            }
            Column(
                modifier = Modifier
                    .padding(0.dp)
                    .align(Alignment.CenterVertically),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "User Name", // TODO: get user name
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 30.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.padding(start = 30.dp)
                ) {
                    RatingBar(
                        modifier = Modifier
                            .padding(bottom = 20.dp, top = 15.dp, start = 0.dp, end = 0.dp)
                            //.scale(0.6F)
                            ,
                        value = 3.5F,
                        style = RatingBarStyle.Fill(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.outline),
                        onValueChange = {},
                        onRatingChanged = {},
                        size = 20.dp,
                        spaceBetween = 4.dp
                    )
                    Text(text = "(20)", modifier = Modifier.padding(start =5.dp , top = 13.dp))
                }
            }
        }

        // Sobre mi
        Row {
            Text(
                text = "Sobre mi", // TODO: poner stringresource
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(start = 30.dp, top = 20.dp, end = 20.dp)
                    .weight(1f)
                    .clickable(onClick = { sobreMiExpanded = !sobreMiExpanded })
            )
            IconButton(
                onClick = { sobreMiExpanded = !sobreMiExpanded },
                modifier = Modifier.padding(top = 8.dp, end = 30.dp, bottom = 0.dp)
            ) {
                Icon(
                    imageVector = if (!sobreMiExpanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Edit",
                    modifier = Modifier
                        .size(20.dp)
                        .padding(0.dp)
                )
            }
        }
        Divider(
            modifier = Modifier.padding(top = 0.dp, start = 30.dp, end = 30.dp, bottom = 10.dp),
            thickness = 0.75.dp,
            color = MaterialTheme.colorScheme.secondary
        )
        AnimatedVisibility(visible = sobreMiExpanded){
            Card(modifier = Modifier.padding(start = 30.dp, end = 30.dp)) {
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "aqui va el texto de sobre mi bhipavujsgbhdfgvuia sfvgoasdfvg yyaosidfvgygb asbfv ioalfvg ",
                        modifier = Modifier.width(270.dp)
                    ) // TODO: get user description
                    IconButton(
                        onClick = { sobreMiEdit = true },
                        modifier = Modifier.padding(top = 0.dp, end = 0.dp, bottom = 0.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.editar),
                            contentDescription = "",
                            modifier = Modifier
                                .size(20.dp)
                                .padding(0.dp)
                        )
                    }
                }
            }
        }

        // Datos personales
        Row {
            Text(
                text = "Datos personales", // TODO: poner stringresource
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(start = 30.dp, top = 20.dp, end = 20.dp)
                    .weight(1f)
            )
            IconButton(
                onClick = { datosPersonalesExpanded = !datosPersonalesExpanded },
                modifier = Modifier.padding(top = 8.dp, end = 30.dp, bottom = 0.dp)
            ) {
                Icon(
                    imageVector = if (!datosPersonalesExpanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Edit",
                    modifier = Modifier
                        .size(20.dp)
                        .padding(0.dp)
                        .clickable(onClick = { datosPersonalesExpanded = !datosPersonalesExpanded })
                )
            }
        }
        Divider(
            modifier = Modifier.padding(top = 0.dp, start = 30.dp, end = 30.dp, bottom = 10.dp),
            thickness = 0.75.dp,
            color = MaterialTheme.colorScheme.secondary
        )
        AnimatedVisibility(
            visible = datosPersonalesExpanded,
        ){

            Card(modifier = Modifier.padding(start = 30.dp, end = 30.dp)) {
                Row(
                    modifier = Modifier
                        .padding(15.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.width(270.dp)
                    ) {
                        Text(text = "Nombre y apellidos") // TODO: get user name
                        Text(text = "Telefono")
                        Text(text = "Email")
                        Text(text = "Edad")
                        Text(text = "Sexo")
                    }
                    IconButton(
                        onClick = { datosPersonalesEdit = true },
                        modifier = Modifier.padding(top = 0.dp, end = 0.dp, bottom = 0.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.editar),
                            contentDescription = "",
                            modifier = Modifier
                                .size(20.dp)
                                .padding(0.dp)
                        )
                    }
                }
            }
        }
        // Contrasena
        Row {
            Text(
                text = "Cambiar contraseña", // TODO: poner stringresource
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(start = 30.dp, top = 20.dp, end = 20.dp)
                    .weight(1f)
                    .clickable(onClick = { contrasenaExpanded = !contrasenaExpanded })
            )
            IconButton(
                onClick = { contrasenaExpanded = !contrasenaExpanded },
                modifier = Modifier.padding(top = 8.dp, end = 30.dp, bottom = 0.dp)
            ) {
                Icon(
                    imageVector = if (!contrasenaExpanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Edit",
                    modifier = Modifier
                        .size(20.dp)
                        .padding(0.dp)
                )
            }
        }
        Divider(
            modifier = Modifier.padding(top = 0.dp, start = 30.dp, end = 30.dp, bottom = 10.dp),
            thickness = 0.75.dp,
            color = MaterialTheme.colorScheme.secondary
        )
        AnimatedVisibility(visible = contrasenaExpanded){
            val keyboardController = LocalSoftwareKeyboardController.current
            Card(modifier = Modifier.padding(start = 30.dp, end = 30.dp)) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                ) {
                    OutlinedTextField(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp),
                        value = "", // TODO
                        onValueChange = {  }, // TODO
                        label = { Text("Contraseña actual") }, // TODO
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
                        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                        leadingIcon = {
                            Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier.padding(0.dp).size(20.dp))
                        }
                    )
                    OutlinedTextField(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp),
                        value = "", // TODO
                        onValueChange = {  }, // TODO
                        label = { Text("Nueva contraseña") }, // TODO
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
                        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                        leadingIcon = {
                            Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier.padding(0.dp).size(20.dp))
                        }
                    )
                    OutlinedTextField(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp),
                        value = "", // TODO
                        onValueChange = {  }, // TODO
                        label = { Text("Confirma la contraseña") }, // TODO
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
                        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                        leadingIcon = {
                            Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier.padding(0.dp).size(20.dp))
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
                            /*TODO*/
                        }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = ""// stringResource(R.string.Cancelar)
                            )
                        }

                        Button(onClick = {
                            /*TODO*/
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

        if (sobreMiEdit){
            DialogoSobreMi(
                onDismissRequest = { sobreMiEdit = false },
                onConfirmation = { sobreMiEdit = false /*TODO*/ }
            )
        }
        if (datosPersonalesEdit) {
            DialogoDatosPersonales(
                onDismissRequest = { datosPersonalesEdit = false },
                onConfirmation = { datosPersonalesEdit = false /*TODO*/ }
            )
        }

        // MAPA
        Text(
            text = "Mi ubicación", // TODO: poner stringresource
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(start = 30.dp, top = 20.dp, end = 20.dp)
                .weight(1f)
                .clickable(onClick = { contrasenaExpanded = !contrasenaExpanded })
        )
        Card(modifier = Modifier.padding(start = 30.dp, end = 30.dp)) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .height(200.dp),
            ){
                Text("Aqui va el mapa")
            }
        }

    }
}

/**
 * Method to validate the fields of the register form
 * @param oldPass: old user password
 * @param password: user password
 * @param confirmPassword: user password confirmation
 * @return true if the fields are valid, false otherwise
 */
fun validateFields(
    oldPass: String,
    password: String,
    confirmPassword: String,
): Boolean {
    return oldPass.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank() && password == confirmPassword
}


// preview composable function
@Preview(showBackground = true)
@Composable
fun PreviewElemento() {
    OfferUSTheme(content = {
        UserScreen()
    })
}
