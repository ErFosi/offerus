package com.offerus.screens

import android.content.res.Configuration
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle
import com.offerus.Idioma
import com.offerus.R
import com.offerus.components.DialogoDatosPersonales
import com.offerus.components.DialogoSeleccionarUbicacion
import com.offerus.components.DialogoSobreMi
import com.offerus.components.Marcador
import com.offerus.components.ProfilePicture
import com.offerus.components.ThemeSwitcher
import com.offerus.components.languageSwitcher
import com.offerus.components.mapa
import com.offerus.ui.theme.OfferUSTheme
import com.offerus.viewModels.MainViewModel
import java.io.File


@Composable
fun UserScreen(
    viewModel: MainViewModel
){
    var sobreMiExpanded by rememberSaveable {
        mutableStateOf(false)
    }
    var datosPersonalesExpanded by rememberSaveable {
        mutableStateOf(false)
    }
    var contrasenaExpanded by rememberSaveable {
        mutableStateOf(false)
    }
    var suscripcionesExpanded by rememberSaveable {
        mutableStateOf(false)
    }
    var sobreMiEdit by rememberSaveable {
        mutableStateOf(false)
    }
    var datosPersonalesEdit by rememberSaveable {
        mutableStateOf(false)
    }
    var ubicacionEdit by rememberSaveable {
        mutableStateOf(false)
    }

    val config = LocalConfiguration.current
    val context = LocalContext.current
    val booleanState by viewModel.tema.collectAsState(initial = true)

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
            .padding(vertical = 35.dp)
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
                        value = 3.5F, // TODO poner la valoracion del usuario
                        style = RatingBarStyle.Fill(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.outline),
                        onValueChange = {},
                        onRatingChanged = {},
                        size = 20.dp,
                        spaceBetween = 4.dp
                    )
                    Text(text = "(20)", modifier = Modifier.padding(start =5.dp , top = 13.dp))
                }
            }
            if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // si esta horizontal otra columna
                Column (
                    modifier = Modifier
                        .padding(start = 70.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier
                            .padding(bottom = 5.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        languageSwitcher(
                            idiomaSeleccionado = viewModel.idioma.collectAsState(initial = Idioma.Castellano).value,
                            onLanguageSelected =  { it: Idioma -> viewModel.updateIdioma(it, context = context) })
                        ThemeSwitcher(
                            darkTheme = booleanState,
                            onClick = {
                            if (booleanState){
                                viewModel.updateTheme(false)
                            }else{
                                viewModel.updateTheme(true)
                            } })
                    }
                    OutlinedButton(
                        onClick = {
                            /*TODO*/
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 5.dp),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_logout_24),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(text = stringResource(id = R.string.signout), modifier = Modifier.padding(start = 10.dp))
                    }
                }
            }
        }

        // Sobre mi
        Row {
            Text(
                text = stringResource(id = R.string.sobreMi), 
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
                text = stringResource(id = R.string.datosPersonales), 
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(start = 30.dp, top = 20.dp, end = 20.dp)
                    .weight(1f)
                    .clickable(onClick = { datosPersonalesExpanded = !datosPersonalesExpanded })
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
                        Row(modifier = Modifier.padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier
                                .padding(end = 5.dp)
                                .size(20.dp))
                            Text(text = "Nombre y apellidos") // TODO: get user name
                        }
                        Row(modifier = Modifier.padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Phone, contentDescription = null, modifier = Modifier
                                .padding(end = 5.dp)
                                .size(20.dp))
                            Text(text = "Telefono") // TODO: get user name
                        }
                        Row(modifier = Modifier.padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Email, contentDescription = null, modifier = Modifier
                                .padding(end = 5.dp)
                                .size(20.dp))
                            Text(text = "Email") // TODO: get user name
                        }
                        Row(modifier = Modifier.padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.DateRange, contentDescription = null, modifier = Modifier
                                .padding(end = 5.dp)
                                .size(20.dp))
                            Text(text = "Edad") // TODO: get user name
                        }
                        Row(modifier = Modifier.padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(painter = painterResource(R.drawable.gender), contentDescription = null, modifier = Modifier
                                .padding(end = 5.dp)
                                .size(20.dp))
                            Text(text = "Sexo") // TODO: get user name
                        }
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
        // Suscripciones
        Row {
            Text(
                text = stringResource(id = R.string.suscripciones),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(start = 30.dp, top = 20.dp, end = 20.dp)
                    .weight(1f)
                    .clickable(onClick = { suscripcionesExpanded = !suscripcionesExpanded })
            )
            IconButton(
                onClick = { suscripcionesExpanded = !suscripcionesExpanded },
                modifier = Modifier.padding(top = 8.dp, end = 30.dp, bottom = 0.dp)
            ) {
                Icon(
                    imageVector = if (!suscripcionesExpanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
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
        AnimatedVisibility(visible = suscripcionesExpanded){
            val keyboardController = LocalSoftwareKeyboardController.current
            var gratisChecked by rememberSaveable { mutableStateOf(false) }
            var deporteChecked by rememberSaveable { mutableStateOf(false) }
            var hogarChecked by rememberSaveable { mutableStateOf(false) }
            var otrosChecked by rememberSaveable { mutableStateOf(false) }
            var entretenimientoChecked by rememberSaveable { mutableStateOf(false) }
            var academicoChecked by rememberSaveable { mutableStateOf(false) }
            var onlineChecked by rememberSaveable { mutableStateOf(false) }

            Card(modifier = Modifier.padding(start = 30.dp, end = 30.dp)) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row (verticalAlignment = Alignment.CenterVertically){

                        Column(modifier = Modifier.width(135.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = gratisChecked, onCheckedChange = { gratisChecked = it })
                                Text(text = "Gratis")
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = deporteChecked, onCheckedChange = { deporteChecked = it })
                                Text(text = "Deporte")
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = hogarChecked, onCheckedChange = { hogarChecked = it })
                                Text(text = "Hogar")
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = otrosChecked, onCheckedChange = { otrosChecked = it})
                                Text(text = "Otros")
                            }
                        }
                        Column(modifier = Modifier.width(180.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = entretenimientoChecked, onCheckedChange = { entretenimientoChecked = it})
                                Text(text = "Entretenimiento")
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = academicoChecked, onCheckedChange = { academicoChecked = it})
                                Text(text = "Academico")
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = onlineChecked, onCheckedChange = { onlineChecked = it})
                                Text(text = "Online")
                            }
                        }
                    }
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

        // Contrasena
        Row {
            Text(
                text = stringResource(R.string.changepasswd),
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
            var oldPass by rememberSaveable { mutableStateOf("") }
            var password by rememberSaveable { mutableStateOf("") }
            var confirmPassword by rememberSaveable { mutableStateOf("") }
            Card(modifier = Modifier.padding(start = 30.dp, end = 30.dp)) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp),
                        value = oldPass,
                        onValueChange = { oldPass = it },
                        label = { Text(stringResource(id = R.string.currentpass)) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
                        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                        leadingIcon = {
                            Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier
                                .padding(0.dp)
                                .size(20.dp))
                        }
                    )
                    OutlinedTextField(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp),
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(stringResource(id = R.string.newpass)) }, 
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
                        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                        leadingIcon = {
                            Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier
                                .padding(0.dp)
                                .size(20.dp))
                        }
                    )
                    OutlinedTextField(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp),
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text(stringResource(id = R.string.confirmpass)) }, 
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
                        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                        leadingIcon = {
                            Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier
                                .padding(0.dp)
                                .size(20.dp))
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
        Row {
            Text(
                text = stringResource(id = R.string.miubicacion),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(start = 30.dp, top = 20.dp, end = 20.dp)
                    .weight(1f)
            )
            IconButton(
                onClick = { ubicacionEdit = !ubicacionEdit },
                modifier = Modifier.padding(top = 8.dp, end = 30.dp, bottom = 0.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.editar),
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
        Card(modifier = Modifier.padding(start = 30.dp, end = 30.dp)) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .height(200.dp),
            ){
                val marcador1 = Marcador(
                    // TODO poner aquí la ubicación del usuarioo
                    latitud = 43.2628005,
                    longitud = -2.9479811,
                    nombre = "Mi ubicación",
                    categoria = "null",
                    precio = "null"
                )
                val marcador2 = Marcador(
                    // TODO poner aquí la ubicación del usuarioo
                    latitud = 43.2628005,
                    longitud = -2.9479819,
                    nombre = "Mi ubicación",
                    categoria = "Gratis",
                    precio = "null"
                )
                val marcador3 = Marcador(
                    // TODO poner aquí la ubicación del usuarioo
                    latitud = 43.2628405,
                    longitud = -2.9479834,
                    nombre = "Mi ubicación",
                    categoria = "Deporte",
                    precio = "null"
                )
                mapa(
                    permisoUbicacion = false,
                    marcadores = listOf(marcador1, marcador2, marcador3),
                    sePuedeDesplazar = false,
                    cameraPosition = CameraPosition.fromLatLngZoom(LatLng(marcador1.latitud, marcador1.longitud), 15F)
                )
            }
        }
        if (ubicacionEdit) {
            DialogoSeleccionarUbicacion(
                onDismissRequest = { ubicacionEdit = false },
                onConfirmation = { ubicacionEdit = false /*TODO*/ }
            )
        }

        if (config.orientation != Configuration.ORIENTATION_LANDSCAPE) {
            // si esta vertical
            Row(
                modifier = Modifier
                    .padding(horizontal = 40.dp, vertical = 20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                languageSwitcher(
                    idiomaSeleccionado = viewModel.idioma.collectAsState(initial = Idioma.Castellano).value,
                    onLanguageSelected =  { it: Idioma -> viewModel.updateIdioma(it, context = context) })
                ThemeSwitcher(
                    darkTheme = booleanState,
                    onClick = {
                        if (booleanState){
                            viewModel.updateTheme(false)
                        }else{
                            viewModel.updateTheme(true)
                        } })
            }

            Row(
                modifier = Modifier
                    .padding(horizontal = 80.dp, vertical = 20.dp)
                    .width(350.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedButton(
                    onClick = {
                        /*TODO*/
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_logout_24),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(text = stringResource(id = R.string.signout), modifier = Modifier.padding(start = 10.dp))
                }
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
        //UserScreen()
    })
}
