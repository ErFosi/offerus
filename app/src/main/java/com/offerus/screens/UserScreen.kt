package com.offerus.screens

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
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
import com.offerus.components.TopBarSecundario
import com.offerus.components.createImageFileFromBitMap
import com.offerus.components.getBipMapFromUri
import com.offerus.components.languageSwitcher
import com.offerus.components.mapa
import com.offerus.data.CATEGORIAS
import com.offerus.navigation.AppScreens
import com.offerus.services.suscribeToFCM
import com.offerus.ui.theme.OfferUSTheme
import com.offerus.utils.ContraseñaNoCoincideException
import com.offerus.utils.isNetworkAvailable
import com.offerus.utils.obtenerCategorias
import com.offerus.utils.obtenerCategoriasString
import com.offerus.viewModels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.net.URI


@Composable
fun UserScreen(
    viewModel: MainViewModel,
    navController: NavHostController
){
    Scaffold(topBar = { TopBarSecundario(navController) }) {
            innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            UserScreenContent(viewModel, navController)
        }
    }
}


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun UserScreenContent(
    viewModel: MainViewModel,
    navController: NavHostController,
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
    val coroutineScope = rememberCoroutineScope()
    val infoUsuario = viewModel.infoUsuario.value
    var valoracion by remember { mutableStateOf<Pair<Int, Double>?>(Pair(0, 0.0)) }
    LaunchedEffect(key1 = infoUsuario.username) {
        val result = viewModel.valoracionMedia(infoUsuario.username)
        valoracion = result
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
            .padding(vertical = 20.dp)
        )
    {
        Row(
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .height(125.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
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
                    if (isNetworkAvailable(context)) {
                        coroutineScope.launch(Dispatchers.IO) {
                            val bitmap = viewModel.getUserProfile(viewModel.usuario)
                            uri = context.createImageFileFromBitMap(bitmap, infoUsuario.username)
                            Log.d("uri", uri.toString())
                        }
                    }else{
                        Toast.makeText(
                            context,
                            R.string.no_internet_pic,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    //uri = "android.resource://com.offerus/drawable/baseline_adb_24".toUri()
                    uri = "file:///storage/emulated/0/Android/data/com.offerus/cache/JPEG_${infoUsuario.username}_.jpg".toUri()
                }
                if (uri.toString() != "" || true) {
                    //image to show bottom sheet
                    ProfilePicture(
                        directory = File("images"),
                        uri = uri,
                        onSetUri = {
                            if (isNetworkAvailable(context)) context.getBipMapFromUri(it)
                                ?.let { it1 ->
                                    context.createImageFileFromBitMap(it1, infoUsuario.username)
                                    viewModel.uploadUserProfile(it1)
                                    uri = it
                                }
                            else Toast.makeText(
                                context,
                                R.string.no_internet_pic,
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        editable = true
                    )
                }/*else{
                    Box(
                        modifier = Modifier
                            .size(120.dp) // Ajusta el tamaño del círculo según tus necesidades
                            .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape),
                    ) {
                        Text(
                            text = infoUsuario.username.first().toString(),
                            color = Color.White,
                            fontSize = 50.sp,
                            modifier = Modifier.align(Alignment.Center),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }*/
            }
            Column(
                modifier = Modifier
                    .padding(0.dp)
                    .align(Alignment.CenterVertically),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = viewModel.usuario,
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
                        value = valoracion!!.second.toFloat(),
                        style = RatingBarStyle.Fill(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.outline),
                        onValueChange = {},
                        onRatingChanged = {},
                        size = 20.dp,
                        spaceBetween = 4.dp
                    )
                    Text(text = "(${valoracion!!.first.toString()})", modifier = Modifier.padding(start =5.dp , top = 13.dp))
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
                        horizontalArrangement = Arrangement.spacedBy(30.dp)
                    ) {
                        languageSwitcher(
                            idiomaSeleccionado = viewModel.idioma.collectAsState(initial = Idioma.Castellano).value,
                            onLanguageSelected =  { it: Idioma -> viewModel.updateIdioma(it, context = context) },
                            size =  50.dp
                        )
                        ThemeSwitcher(
                            darkTheme = booleanState,
                            onClick = {
                            if (booleanState){
                                viewModel.updateTheme(false)
                            }else{
                                viewModel.updateTheme(true)
                            } },
                            size = 50.dp
                        )
                    }
                    OutlinedButton(
                        onClick = {
                            viewModel.logout()
                            while (navController.popBackStack()){navController.popBackStack()}
                            navController.navigate(AppScreens.LoginScreen.route)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 10.dp, bottom = 5.dp),
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
                    .clickable(onClick = {
                        sobreMiExpanded = !sobreMiExpanded
                        contrasenaExpanded = false
                        datosPersonalesExpanded = false
                        suscripcionesExpanded = false
                    })
            )
            IconButton(
                onClick = {
                            sobreMiExpanded = !sobreMiExpanded
                            contrasenaExpanded = false
                            datosPersonalesExpanded = false
                            suscripcionesExpanded = false
                          },
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
                        text = infoUsuario.descripcion,
                        modifier = Modifier.width(270.dp)
                    )
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
                    .clickable(onClick = {
                        datosPersonalesExpanded = !datosPersonalesExpanded
                        contrasenaExpanded = false
                        sobreMiExpanded = false
                        suscripcionesExpanded = false
                    })
            )
            IconButton(
                onClick = {
                    datosPersonalesExpanded = !datosPersonalesExpanded
                    contrasenaExpanded = false
                    sobreMiExpanded = false
                    suscripcionesExpanded = false
                          },
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
                            Text(text = infoUsuario.nombre_apellido)
                        }
                        Row(modifier = Modifier.padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Phone, contentDescription = null, modifier = Modifier
                                .padding(end = 5.dp)
                                .size(20.dp))
                            Text(text = infoUsuario.telefono)
                        }
                        Row(modifier = Modifier.padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Email, contentDescription = null, modifier = Modifier
                                .padding(end = 5.dp)
                                .size(20.dp))
                            Text(text = infoUsuario.mail)
                        }
                        Row(modifier = Modifier.padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.DateRange, contentDescription = null, modifier = Modifier
                                .padding(end = 5.dp)
                                .size(20.dp))
                            Text(text = infoUsuario.edad.toString())
                        }
                        Row(modifier = Modifier.padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(painter = painterResource(R.drawable.gender), contentDescription = null, modifier = Modifier
                                .padding(end = 5.dp)
                                .size(20.dp))
                            Text(text = infoUsuario.sexo)
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
                    .clickable(onClick = {
                        suscripcionesExpanded = !suscripcionesExpanded
                        contrasenaExpanded = false
                        datosPersonalesExpanded = false
                        sobreMiExpanded = false
                    })
            )
            IconButton(
                onClick = {
                    suscripcionesExpanded = !suscripcionesExpanded
                    contrasenaExpanded = false
                    datosPersonalesExpanded = false
                    sobreMiExpanded = false
                          },
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
            var selectedCategories by remember { mutableStateOf(obtenerCategorias(infoUsuario.suscripciones)) }
            /*val keyboardController = LocalSoftwareKeyboardController.current
            var gratisChecked by rememberSaveable { mutableStateOf(false) }
            var deporteChecked by rememberSaveable { mutableStateOf(false) }
            var hogarChecked by rememberSaveable { mutableStateOf(false) }
            var otrosChecked by rememberSaveable { mutableStateOf(false) }
            var entretenimientoChecked by rememberSaveable { mutableStateOf(false) }
            var academicoChecked by rememberSaveable { mutableStateOf(false) }
            var onlineChecked by rememberSaveable { mutableStateOf(false) }*/

            Card(modifier = Modifier.padding(start = 30.dp, end = 30.dp)) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row (verticalAlignment = Alignment.CenterVertically){

                        /*Column(modifier = Modifier.width(135.dp)) {
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
                        }*/
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
                                                    imageVector = ImageVector.vectorResource(category.icono),
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        //horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(onClick = {
                            suscripcionesExpanded = false
                        }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = ""// stringResource(R.string.Cancelar)
                            )
                        }

                        Button(onClick = {
                            viewModel.updateUserData(
                                fullName = infoUsuario.nombre_apellido,
                                age = infoUsuario.edad,
                                email = infoUsuario.mail,
                                phone = infoUsuario.telefono,
                                lat = infoUsuario.latitud,
                                lon = infoUsuario.longitud,
                                descr = infoUsuario.descripcion,
                                sex = infoUsuario.sexo,
                                suscriptions = obtenerCategoriasString(selectedCategories)
                            )
                            suscribeToFCM(context) // Actualizar las suscripciones
                            Toast.makeText(context, R.string.datos_actualizados, Toast.LENGTH_SHORT).show()
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
                    .clickable(onClick = {
                        contrasenaExpanded = !contrasenaExpanded
                        suscripcionesExpanded = false
                        datosPersonalesExpanded = false
                        sobreMiExpanded = false
                    })
            )
            IconButton(
                onClick = { contrasenaExpanded = !contrasenaExpanded
                    suscripcionesExpanded = false
                    datosPersonalesExpanded = false
                    sobreMiExpanded = false},
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
                            oldPass = ""
                            password = ""
                            confirmPassword = ""
                            contrasenaExpanded = false
                        }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = ""// stringResource(R.string.Cancelar)
                            )
                        }
                        var toast by rememberSaveable {
                            mutableStateOf(-1)
                        }
                        Button(onClick = {
                            if (validateFields(oldPass, password, confirmPassword)){
                                coroutineScope.launch(Dispatchers.IO) {
                                    try {
                                        viewModel.modifyPassword(oldPass, password)
                                        viewModel.setUsuarioLogueado(infoUsuario.username, password)
                                        oldPass = ""
                                        password = ""
                                        confirmPassword = ""
                                        contrasenaExpanded = false
                                        toast = 0
                                    } catch (e: ContraseñaNoCoincideException) {
                                        toast = 1

                                    } catch (e: Exception) {
                                        toast = 2
                                    }
                                }

                            }else{
                                Toast.makeText(context, R.string.invalid_fields, Toast.LENGTH_SHORT).show()
                            }

                        }) {
                            Text(text = "")
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = ""//stringResource(R.string.Borrar)
                            )
                        }
                        when (toast) {
                            0 -> {  Toast.makeText(context, R.string.datos_actualizados, Toast.LENGTH_SHORT).show()
                                toast = -1}
                            1 -> {  Toast.makeText(context, R.string.contrasena_incorrecta, Toast.LENGTH_SHORT).show()
                                toast = -1}
                            2 -> { Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show()
                                toast = -1}
                        }
                    }
                }
            }
        }

        if (sobreMiEdit){
            DialogoSobreMi(
                sobreMiOld = infoUsuario.descripcion,
                onDismissRequest = { sobreMiEdit = false },
                onConfirmation = {
                    sobreMiEdit = false
                    //sobreMiExpanded = false
                    viewModel.updateUserData(
                        fullName = infoUsuario.nombre_apellido,
                        age = infoUsuario.edad,
                        email = infoUsuario.mail,
                        phone = infoUsuario.telefono,
                        sex = infoUsuario.sexo,
                        lat = infoUsuario.latitud,
                        lon = infoUsuario.longitud,
                        descr = it,
                        suscriptions = infoUsuario.suscripciones
                    )
                    //viewModel.actualizarInfoUsuario()
                    Toast.makeText(context, R.string.datos_actualizados, Toast.LENGTH_SHORT).show()
                }
            )
        }
        if (datosPersonalesEdit) {
            DialogoDatosPersonales(
                infoUsuario = infoUsuario,
                onDismissRequest = { datosPersonalesEdit = false },
                onConfirmation = {
                    datosPersonalesEdit = false
                    //datosPersonalesExpanded = false
                    viewModel.updateUserData(
                        fullName = it.nombre_apellido,
                        age = it.edad,
                        email = it.mail,
                        phone = it.telefono,
                        sex = it.sexo,
                        lat = infoUsuario.latitud,
                        lon = infoUsuario.longitud,
                        descr = infoUsuario.descripcion,
                        suscriptions = infoUsuario.suscripciones
                    )
                    //viewModel.actualizarInfoUsuario()
                    Toast.makeText(context, R.string.datos_actualizados, Toast.LENGTH_SHORT).show()
                }
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
                    latitud = infoUsuario.latitud,
                    longitud = infoUsuario.longitud,
                    nombre = stringResource(id = R.string.miubicacion),
                    categoria = "null",
                    precio = "null"
                )

                /*val marcador2 = Marcador(
                    latitud = 43.2628005,
                    longitud = -2.9479819,
                    nombre = "Mi ubicación",
                    categoria = "Gratis",
                    precio = "null"
                )
                val marcador3 = Marcador(
                    latitud = 43.2628405,
                    longitud = -2.9479834,
                    nombre = "Mi ubicación",
                    categoria = "Deporte",
                    precio = "null"
                )*/
                Log.d("MAPA", "lat: ${infoUsuario.latitud} lon: ${infoUsuario.longitud}")
                if (infoUsuario.latitud == 0.0 && infoUsuario.longitud == 0.0) {
                    Text(text = stringResource(id = R.string.no_ubicacion), modifier = Modifier.padding(10.dp), fontStyle = FontStyle.Italic)
                }
                mapa(
                    permisoUbicacion = false,
                    marcadores = listOf(marcador1),
                    sePuedeDesplazar = false,
                    lat = infoUsuario.latitud,
                    lon = infoUsuario.longitud,
                )
            }
        }
        if (ubicacionEdit) {
            DialogoSeleccionarUbicacion(
                lat= infoUsuario.latitud,
                lon= infoUsuario.longitud,
                onDismissRequest = { ubicacionEdit = false },
                onConfirmation = {

                    viewModel.updateUserData(
                        fullName = infoUsuario.nombre_apellido,
                        age = infoUsuario.edad,
                        email = infoUsuario.mail,
                        phone = infoUsuario.telefono,
                        sex = infoUsuario.sexo,
                        lat = it.latitude,
                        lon = it.longitude,
                        descr = infoUsuario.descripcion,
                        suscriptions = infoUsuario.suscripciones
                    )
                    //viewModel.actualizarInfoUsuario()
                    suscribeToFCM(context)
                    ubicacionEdit = false
                    Toast.makeText(context, R.string.ubicacion_actualizada, Toast.LENGTH_SHORT).show()
                }
            )
        }

        if (config.orientation != Configuration.ORIENTATION_LANDSCAPE) {
            // si esta vertical
            Row(
                modifier = Modifier
                    .padding(horizontal = 40.dp, vertical = 20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                languageSwitcher(
                    idiomaSeleccionado = viewModel.idioma.collectAsState(initial = Idioma.Castellano).value,
                    onLanguageSelected =  { it: Idioma -> viewModel.updateIdioma(it, context = context) },
                    size = 50.dp
                )
                Spacer(modifier = Modifier.padding(30.dp))
                ThemeSwitcher(
                    darkTheme = booleanState,
                    onClick = {
                        if (booleanState){
                            viewModel.updateTheme(false)
                        }else{
                            viewModel.updateTheme(true)
                        } },
                    size = 50.dp
                )
            }

            Row(
                modifier = Modifier
                    .padding(start = 80.dp, end = 80.dp, bottom = 20.dp)
                    .width(350.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.logout()
                        while (navController.popBackStack()){navController.popBackStack()}
                        navController.navigate(AppScreens.LoginScreen.route)
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
    return oldPass.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank() && password == confirmPassword && password.length >= 6 && oldPass.length >= 6
}


// preview composable function
@Preview(showBackground = true)
@Composable
fun PreviewElemento() {
    OfferUSTheme(content = {
        //UserScreen()
    })
}
