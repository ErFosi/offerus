package com.offerus.components

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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.navigation.NavController
import com.offerus.R
import com.offerus.navigation.AppScreens
import com.offerus.utils.AuthenticationException
import com.offerus.utils.UserExistsException
import com.offerus.viewModels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.ConnectException


@Composable
fun LoginBox(
    mainViewModel: MainViewModel,
    navController: NavController,
    onTabChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val coroutineScope = rememberCoroutineScope()
    val offsetX = remember { mutableStateOf(0f) } // Estado para almacenar el desplazamiento en X

    // Calculamos el ancho de la pantalla en píxeles
    val screenWidthInPx = with(LocalDensity.current) { screenWidth.dp.toPx() }
    val triggerThreshold = screenWidthInPx * 0.8f
    // Estado draggable
    val draggableState = rememberDraggableState { delta ->
        offsetX.value += delta
        offsetX.value = offsetX.value.coerceIn(0f, screenWidthInPx)
    }

    // VARIABLES

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var sex by remember { mutableStateOf("M") }


    ///////////////  METODOS PARA LOGIN Y REGISTRO ///////////////

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
                mainViewModel.setUsuarioLogueado(username, password) // guardar el nombre de usuario y contraseña en el datastore
                mostrarErrorLogin = false
            } catch (e: AuthenticationException) {
                mostrarErrorLogin = true
                sesionIniciada = false
            } catch (e: ConnectException) {
                mostrarErrorLogin = true
                sesionIniciada = false
                mostrarErrorConexion = true
            }
        }
    }
    if (sesionIniciada) {
        // TODO suscribeToFCM(context)
        navController.popBackStack()
        navController.navigate(AppScreens.MainScreen.route)
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

    val onRegister: () -> Unit = {
        coroutineScope.launch(Dispatchers.IO){
            try {
                mainViewModel.register(username, password, fullName, age.toInt(),  email, phone, sex)
                registrado = true
                mostrarErrorRegistro = false
            } catch (e: UserExistsException) {
                mostrarErrorRegistro = true
                registrado = false
            }catch (e: ConnectException) {
                mostrarErrorRegistro = true
                registrado = false
                mostrarErrorConexion = true
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
        // TODO QUE SE CAMBIE DE PANTALLA A LA DE LOGIN
    }
    if (mostrarErrorLogin){
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
        modifier = if (offsetX.value > screenWidthInPx / 2) {
            Modifier
                .height(300.dp)
        } else {
            Modifier
                .fillMaxSize()
        }
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
        Spacer(modifier = Modifier.height(2.dp).fillMaxWidth(0.9f))

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
                    onTabChange(false)
                    LoginFieldView(
                        onUsernameChange = { newText -> username = newText},
                        onPasswordChange = { newText -> password = newText}
                    )
                }
                this@Column.AnimatedVisibility(
                    visible = offsetX.value <= screenWidthInPx / 2,
                    enter = slideInHorizontally(initialOffsetX = { 1000 }),
                    exit = slideOutHorizontally(targetOffsetX = { 1000 })
                ) {
                    onTabChange(true)
                    RegisterFieldView(
                        onUsernameChange = { newText -> username = newText},
                        onPasswordChange = { newText -> password = newText},
                        onFullnameChange = { newText -> fullName = newText},
                        onEmaiChange = { newText -> email = newText},
                        onPhoneChange = { newText -> phone = newText},
                        onAgeChange = { newText -> age = newText},
                        onSexChange = { newText -> sex = newText.toString()},
                    )
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
                        onRegister()
                    }
                 },
                modifier = Modifier
                    .height(70.dp)
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
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
    onPasswordChange: (String) -> Unit,
    onFullnameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onEmaiChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSexChange: (Char) -> Unit
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
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var fullName by remember { mutableStateOf("") }
        var age by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var sex by remember { mutableStateOf("") }

        OutlinedTextField(
            value = username,
            onValueChange = { username = it
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
            onValueChange = { password = it
                onPasswordChange(it)},
            label = { Text(stringResource(R.string.password)) },
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Filled.Person, contentDescription = stringResource(R.string.password))
            },

            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it
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
            onValueChange = { age = it
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
            onValueChange = { email = it
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
            onValueChange = { phone = it
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
    onPasswordChange: (String) -> Unit
) {

    //val backgroundColor = MaterialTheme.colorScheme.secondary
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        OutlinedTextField(
            value = username,
            onValueChange = { username = it
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
            onValueChange = { password = it
                            onPasswordChange(it)},
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
