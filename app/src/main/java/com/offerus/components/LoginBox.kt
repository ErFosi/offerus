package com.offerus.components

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import com.offerus.R
import com.offerus.navigation.AppScreens
import com.offerus.services.suscribeToFCM
import com.offerus.utils.AuthenticationException
import com.offerus.utils.UserExistsException
import com.offerus.utils.locationUtils
import com.offerus.viewModels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun LoginBox(
    mainViewModel: MainViewModel,
    navController: NavController,
    onTabChange: (Boolean) -> Unit
) {
    /* COMPROBAR PERMISO DE UBICACION */
    var permisoUbicacion by rememberSaveable {
        mutableStateOf(false)
    }
    if (ContextCompat.checkSelfPermission(
            LocalContext.current,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            LocalContext.current,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        // Permission is already granted
        permisoUbicacion = true
    } else {
    }



    val context = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val coroutineScope = rememberCoroutineScope()


    // Calculamos el ancho de la pantalla en píxeles
    val screenWidthInPx = with(LocalDensity.current) { screenWidth.dp.toPx() }
    val triggerThreshold = screenWidthInPx * 0.8f
    val offsetX = remember { mutableStateOf(screenWidthInPx) } // Estado para almacenar el desplazamiento en X
    // Estado draggable
    val draggableState = rememberDraggableState { delta ->
        offsetX.value += delta
        offsetX.value = offsetX.value.coerceIn(0f, screenWidthInPx)
    }

    // VARIABLES

    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var usernameRegistro by rememberSaveable { mutableStateOf("") }
    var passwordRegistro by rememberSaveable { mutableStateOf("") }
    var confirmarpassword by rememberSaveable { mutableStateOf("") }
    var fullName by rememberSaveable { mutableStateOf("") }
    var age by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var sex by rememberSaveable { mutableStateOf("") }

    val registerEnabled by remember { derivedStateOf { usernameRegistro.isNotEmpty() && passwordRegistro.isNotEmpty() && fullName.isNotEmpty() && age.isNotEmpty() &&
                                                        email.isNotEmpty() && phone.isNotEmpty() && sex.isNotEmpty() && confirmarpassword.isNotEmpty()} }
    val loginEnabled by remember { derivedStateOf {  username.isNotEmpty() && password.isNotEmpty() } }

    ///////////////  VALIDACION DE CAMPOS DE REGISTRO ///////////////
    var invalidEmail by rememberSaveable { mutableStateOf(false) }
    var invalidPhone by rememberSaveable { mutableStateOf(false) }
    var invalidUsername by rememberSaveable { mutableStateOf(false) }
    var invalidPassword by rememberSaveable { mutableStateOf(false) }
    var invalidConfirmPassword by rememberSaveable { mutableStateOf(false) }

    fun isValidEmail(): Boolean {
        val valid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        if (!valid) {
            // Show error message
            Toast.makeText(
                context,
                R.string.email_invalido,
                Toast.LENGTH_SHORT
            ).show()
            invalidEmail = true
        }else invalidEmail = false
        return valid
    }
    fun isValidPhone(): Boolean {
        val valid = android.util.Patterns.PHONE.matcher(phone).matches()
        if (!valid) {
            // Show error message
            Toast.makeText(
                context,
                R.string.phone_invalido,
                Toast.LENGTH_SHORT
            ).show()
            invalidPhone = true
        }else invalidPhone = false
        return valid
    }
    fun isValidUsername(): Boolean {
        val usernamePattern = Regex("^[a-zA-Z0-9]*$")
        val valid = usernamePattern.matches(usernameRegistro)
        if (!valid) {
            Toast.makeText(
                context,
                R.string.username_invalido,
                Toast.LENGTH_SHORT
            ).show()
            invalidUsername = true
        } else {
            invalidUsername = false
        }
        return valid
    }
    fun isValidPassword(): Boolean {
        val valid = passwordRegistro.length >= 6
        if (!valid) {
            // Show error message
            Toast.makeText(
                context,
                R.string.password_invalido,
                Toast.LENGTH_SHORT
            ).show()
            invalidPassword = true
        }else invalidPassword = false
        return valid
    }
    fun passwordsMatch(): Boolean {
        val valid = passwordRegistro == confirmarpassword
        if (!valid) {
            // Show error message
            Toast.makeText(
                context,
                R.string.passwords_no_match,
                Toast.LENGTH_SHORT
            ).show()
            invalidConfirmPassword = true
        }else invalidConfirmPassword = false
        return valid
    }

    // comprobar todos
    fun isValidRegister(): Boolean {
        return isValidPassword() && passwordsMatch() && isValidEmail() && isValidPhone()&& isValidUsername()
    }

    /////////////////////////////////////////////////////////////////

    ///////////////  METODOS PARA LOGIN Y REGISTRO ///////////////
    var recordarUser by remember { mutableStateOf(false) }

    var sesionIniciada by remember { mutableStateOf(false) }
    var mostrarErrorLogin by remember { mutableStateOf(false) }  // VARIABLE QUE INDICA EL LOGIN INCORRECTO
    var mostrarErrorConexion by remember { mutableStateOf(false) }

    var registrado by remember { mutableStateOf(false) }
    var mostrarErrorRegistro by remember { mutableStateOf(false) } // ERROR DE REGISTRO: USUARIO DUPLICADO O NO VALIDO

    val onLogin: () -> Unit = {
        coroutineScope.launch(Dispatchers.IO){
            try {
                mainViewModel.login(username, password)
                sesionIniciada = true
                mainViewModel.usuario = username // guardar el nombre de usuario en el viewmodel
                if (recordarUser) mainViewModel.setUsuarioLogueado(username, password) // guardar el nombre de usuario y contraseña en el datastore --> solo si el usuario ha marcado la casilla de recordar
                mostrarErrorLogin = false
            } catch (e: AuthenticationException) {
                mostrarErrorLogin = true
                sesionIniciada = false
            } catch (e: Exception) {
                mostrarErrorLogin = false
                sesionIniciada = false
                mostrarErrorConexion = true
            }
        }
    }
    LaunchedEffect (sesionIniciada) {
        if (sesionIniciada){
            suscribeToFCM(context)
            mainViewModel.iniciarListas()
            mainViewModel.actualizarInfoUsuario()
            navController.popBackStack()
            navController.navigate(AppScreens.MainScreen.route)
        }

    }

    if (mostrarErrorLogin){
        Toast.makeText(
            context,
            R.string.error_login,
            Toast.LENGTH_SHORT
        ).show()
    }
    if (mostrarErrorConexion){
        Toast.makeText(
            context,
            R.string.no_internet,
            Toast.LENGTH_SHORT
        ).show()
        mostrarErrorConexion = false
    }
    var ubicacion by rememberSaveable {
        mutableStateOf(LatLng(0.0, 0.0))
    }
    val locationUtils = locationUtils()
    val onRegister: () -> Unit = {
        coroutineScope.launch(Dispatchers.IO){
            try {
                if (permisoUbicacion){
                    val ubi = locationUtils.getLocation(context)
                    if (ubi != null) {
                        ubicacion = LatLng(ubi.latitude, ubi.longitude)
                    }
                }
                mainViewModel.register(usernameRegistro, passwordRegistro, fullName, age.toInt(),  email, phone, sex, ubicacion.latitude, ubicacion.longitude)
                registrado = true
                mostrarErrorRegistro = false
            } catch (e: UserExistsException) {
                mostrarErrorRegistro = true
                registrado = false
            }catch (e: Exception) {
                mostrarErrorRegistro = false
                registrado = false
                mostrarErrorConexion = true
                Log.e("ERROR", e.toString())
            }
        }
    }
    if (registrado) {
        registrado = false
        // Show success message
        Toast.makeText(
            context,
            R.string.RegistroExitoso,
            Toast.LENGTH_SHORT
        ).show()
        // cambiar a la pantalla de login
        offsetX.value = screenWidthInPx
        // vaciar todas las variables
        usernameRegistro = ""
        passwordRegistro = ""
        confirmarpassword = ""
        fullName = ""
        age = ""
        email = ""
        phone = ""
        sex = ""
    }
    if (mostrarErrorRegistro){
        // Show error message
        Toast.makeText(
            context,
            R.string.usuarioyaexiste,
            Toast.LENGTH_SHORT
        ).show()
    }
    if (mostrarErrorConexion){
        Toast.makeText(
            context,
            R.string.no_internet,
            Toast.LENGTH_SHORT
        ).show()
        mostrarErrorConexion = false
    }

    /////////////////////////////////////////////////////////////



    Column(
        modifier = Modifier.fillMaxSize()
        /*if (offsetX.value > screenWidthInPx / 2) {
            Modifier
                .height(300.dp)
        } else {
            Modifier
                .fillMaxSize()
        }*/
            //.fillMaxSize()
    ) {
        // OptionSelector fijo en la parte superior
        OptionSelector(
            selectedOption = if (offsetX.value > screenWidthInPx / 2) 1 else 2,
            onOptionSelected = { option ->
                coroutineScope.launch {
                    offsetX.value = if (option == 1) screenWidthInPx else 0f
                }
            }
        )
        Spacer(modifier = Modifier
            .height(2.dp)
            .fillMaxWidth(0.9f))

        Column(
            modifier = Modifier
                .weight(1f)
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                    onDragStopped = {
                        coroutineScope.launch {
                            if (offsetX.value > triggerThreshold && offsetX.value < screenWidthInPx - triggerThreshold) {
                                // Si el usuario desplaza menos del umbral en cualquier dirección,
                                // reestablecer a la posición más cercana
                                if (offsetX.value < screenWidthInPx / 2) {
                                    offsetX.value = 0f
                                } else {
                                    offsetX.value = screenWidthInPx
                                }
                            }
                        }
                    }
                )
        ) {
            Box(
                modifier = Modifier
                    .weight(0.75f)
                    .padding(8.dp)
                    .clipToBounds()
            ) {
                this@Column.AnimatedVisibility(
                    visible = offsetX.value > screenWidthInPx / 2,
                    enter = slideInHorizontally(initialOffsetX = { -1000 }),
                    exit = slideOutHorizontally(targetOffsetX = { -1000 })
                ) {
                    //onTabChange(false)
                    LoginFieldView(
                        onUsernameChange = { newText -> username = newText},
                        username = username,
                        onPasswordChange = { newText -> password = newText},
                        password = password,
                        mostrarErrorLogin = mostrarErrorLogin,
                    )
                }
                this@Column.AnimatedVisibility(
                    visible = offsetX.value <= screenWidthInPx / 2,
                    enter = slideInHorizontally(initialOffsetX = { 1000 }),
                    exit = slideOutHorizontally(targetOffsetX = { 1000 })
                ) {

                    RegisterFieldView(
                        onUsernameChange = { newText -> usernameRegistro = newText},
                        username = usernameRegistro,
                        onPasswordChange = { newText -> passwordRegistro = newText},
                        password = passwordRegistro,
                        onConfirmPasswordChange = { newText -> confirmarpassword = newText},
                        confirmPassword = confirmarpassword,
                        onFullnameChange = { newText -> fullName = newText},
                        fullName = fullName,
                        onEmaiChange = { newText -> email = newText},
                        email = email,
                        onPhoneChange = { newText -> phone = newText},
                        phone = phone,
                        onAgeChange = { newText -> age = newText},
                        age = age,
                        onSexChange = { newText -> sex = newText.toString()},
                        mostrarErrorRegistro = mostrarErrorRegistro,
                        invalidEmail = invalidEmail,
                        invalidPhone = invalidPhone,
                        invalidPassword = invalidPassword,
                        invalidConfirmPassword = invalidConfirmPassword
                    )
                }
            }
            if (offsetX.value > screenWidthInPx / 2) {
                Row( modifier = Modifier
                    .height(25.dp)
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, bottom = 5.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(stringResource(id = R.string.recordarUsuario))
                    Switch(checked = recordarUser, onCheckedChange = { recordarUser = !recordarUser} , modifier = Modifier.padding(start= 8.dp))
                }
            }

            Button(
                onClick = {
                    if (offsetX.value > screenWidthInPx / 2) {
                        //  LOGIN
                        mostrarErrorLogin = false
                        onLogin()
                    } else {
                        // REGISTER
                        mostrarErrorRegistro = false
                        if (isValidRegister()){
                            onRegister()
                        }else{ }

                    }
                 },
                modifier = Modifier
                    .height(70.dp)
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(24.dp),
                enabled = if (offsetX.value > screenWidthInPx / 2) loginEnabled else registerEnabled
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (offsetX.value > screenWidthInPx / 2) onTabChange(false) else onTabChange(true)
                    Text(
                        if (offsetX.value > screenWidthInPx / 2) "Log In" else "Register",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )

                    Icon(
                        imageVector = Icons.Filled.ExitToApp,
                        contentDescription = "Enviar",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }



        }
    }
}

@Composable
fun OptionSelector(selectedOption: Int, onOptionSelected: (Int) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        TextButton(
            onClick = { onOptionSelected(1) },
            shape = RectangleShape
        ) {
            Text(
                "Login",
                /*style = TextStyle(
                    //color = MaterialTheme.colorScheme.primary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium
                ),*/
                style = MaterialTheme.typography.titleLarge,
                textDecoration = if (selectedOption == 1) TextDecoration.Underline else TextDecoration.None
            )
        }
        TextButton(
            onClick = { onOptionSelected(2) },
            shape = RectangleShape
        ) {
            Text(
                "Register",
                style = MaterialTheme.typography.titleLarge,
                textDecoration = if (selectedOption == 2) TextDecoration.Underline else TextDecoration.None
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterFieldView(
    onUsernameChange: (String) -> Unit,
    username: String,
    onPasswordChange: (String) -> Unit,
    password: String,
    onConfirmPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onFullnameChange: (String) -> Unit,
    fullName: String,
    onAgeChange: (String) -> Unit,
    age: String,
    onEmaiChange: (String) -> Unit,
    email: String,
    onPhoneChange: (String) -> Unit,
    phone: String,
    onSexChange: (Char) -> Unit,
    mostrarErrorRegistro: Boolean,
    invalidEmail: Boolean,
    invalidPhone: Boolean,
    invalidPassword: Boolean,
    invalidConfirmPassword: Boolean
) {
    //val backgroundColor = MaterialTheme.colorScheme.secondary

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = username,
            isError = mostrarErrorRegistro,
            onValueChange = {
                            onUsernameChange(it)},
            label = { Text(stringResource(R.string.username)) },
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Filled.Person, contentDescription = stringResource(R.string.username))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = password,
            isError = invalidPassword or invalidConfirmPassword,
            onValueChange = {
                onPasswordChange(it)},
            label = { Text(stringResource(R.string.password)) },
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Filled.Lock, contentDescription = stringResource(R.string.password))
            },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = confirmPassword,
            isError = invalidConfirmPassword or invalidPassword,
            onValueChange = {
                onConfirmPasswordChange(it)},
            label = { Text(stringResource(R.string.confirmpass)) },
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Filled.Lock, contentDescription = stringResource(R.string.password))
            },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = fullName,
            onValueChange = {
                            onFullnameChange(it)},
            label = { Text(stringResource(R.string.full_name)) },
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Filled.AccountCircle, contentDescription = stringResource(R.string.full_name))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        OutlinedTextField(
            value = age,
            onValueChange = {
                            onAgeChange(it)},
            label = { Text(stringResource(R.string.age)) },
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Filled.DateRange, contentDescription = stringResource(R.string.age))
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Email TextField
        OutlinedTextField(
            value = email,
            isError = invalidEmail,
            onValueChange = {
                            onEmaiChange(it)},
            label = { Text(stringResource(R.string.email)) },
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Filled.Email, contentDescription = stringResource(R.string.email))
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Phone TextField
        OutlinedTextField(
            value = phone,
            isError = invalidPhone,
            onValueChange = {
                            onPhoneChange(it)},
            label = { Text(stringResource(R.string.phone)) },
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Filled.Phone, contentDescription = stringResource(R.string.phone))
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Phone
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Sex dropdown
        SexDropdown(
            onSexChange
        )


    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginFieldView(
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    mostrarErrorLogin: Boolean,
    username: String,
    password: String
) {

    //val backgroundColor = MaterialTheme.colorScheme.secondary
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(
            value = username,
            isError = mostrarErrorLogin,
            onValueChange = {
                onUsernameChange(it)
            },
            label = { Text(stringResource(R.string.username)) },
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Filled.Person, contentDescription = stringResource(R.string.username))
            },

            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        OutlinedTextField(
            value = password,
            isError = mostrarErrorLogin,
            onValueChange = {
                            onPasswordChange(it)
                },
            label = { Text(stringResource(R.string.password)) },
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Filled.Lock, contentDescription = stringResource(R.string.password))
            },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
