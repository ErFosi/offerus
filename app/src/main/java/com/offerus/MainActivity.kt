package com.offerus

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.offerus.navigation.MainNavigation
import com.offerus.services.suscribeToFCM
import com.offerus.ui.theme.OfferUSTheme
import com.offerus.utils.BiometricAuthManager
import com.offerus.utils.DeviceBiometricsSupport
import com.offerus.utils.isNetworkAvailable
import com.offerus.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // viewModel initialize
    // viewmodel general y de autenticacion
    // private val viewmodel by viewModels<>()  //hiltviewmodel

    private val mainViewModel by viewModels<MainViewModel>()
    private lateinit var biometricAuthManager: BiometricAuthManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        setContent {
            OfferUSTheme(
                // Select the app theme based on the theme salected by the user (dark/light)
                darkTheme = mainViewModel.tema.collectAsState(initial = true).value
            ) {
                /* Request location permision */
                var permisoUbicacion by rememberSaveable {
                    mutableStateOf(false)
                }
                // pedir permisos de ubicacion para hacer el registro
                val requestPermissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                    ) {
                        // Permission is granted
                        permisoUbicacion = true
                    } else {
                        // Permission is denied
                        permisoUbicacion = false
                    }
                }

                val permissions = arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
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
                    // Request for permission
                    LaunchedEffect(permissions) {
                        requestPermissionLauncher.launch(permissions)
                    }
                }



                // Log in if there is a user saved in the datastore

                var huella by rememberSaveable {
                    mutableStateOf(false)
                }
                var logedIn by rememberSaveable {
                    mutableStateOf(false)
                }
                // check if there is internet conection
                if (!isNetworkAvailable(this)) {
                    // show a toast
                    val mensaje = stringResource(R.string.no_internet)
                    Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()

                }else {
                    // comprobar si hay un usuario guardado en el datastore para hacer el login automatico
                    logedIn = mainViewModel.obtenerUsuarioLogeado() != ""
                    val context = this
                    Log.d("login", "Usuario logeado: $logedIn y huella: $huella")
                    if (logedIn && !huella) {
                        //si hay un usuario guardado en el datastore, intentar hacer login automatico con huella
                        if (!this::biometricAuthManager.isInitialized) {
                            biometricAuthManager = BiometricAuthManager(
                                context = this,
                                onAuthenticationSucceeded = {
                                    mainViewModel.loginUsuarioGuardado()
                                    suscribeToFCM(context)
                                    huella = true
                                }
                            )
                        }
                        var huellaSupport=biometricAuthManager.checkBiometricSupport() //comprobar si el dispositivo soporta huella
                        if(huellaSupport== DeviceBiometricsSupport.SUPPORTED && !huella) {
                            //si el dispositivo soporta huella, hacer el logina automatico si la huella es correcta
                            biometricAuthManager.submitBiometricAuthorization()
                        }else{
                            //si el dispositivo no soporta huella, hacer el login automatico sin huella
                            LaunchedEffect(true) {
                                //mainViewModel.haEntrado=true
                                Log.d("login", "Usuario logeado1: $logedIn")
                                mainViewModel.loginUsuarioGuardado()
                                suscribeToFCM(context)
                                huella = true
                            }
                        }

                    }
                }

                // Update the app language, to restore the previous app language in case a different
                // language has been stablished before closing the app
                mainViewModel.reloadLang(mainViewModel.idioma.collectAsState(initial = mainViewModel.idiomaActual).value, this)


                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation( huella, mainViewModel)
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()

        // Verificar si el usuario ha iniciado sesión
        val loggedIn = mainViewModel.obtenerUsuarioLogeado() != ""


        // Si el usuario no ha iniciado sesión, reiniciar la aplicación
        if (!loggedIn) {
            restartApp()
        }
        else{
            mainViewModel.loginUsuarioGuardado()
        }
    }
    private fun restartApp() {
        // Crea un Intent para reiniciar la aplicación
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)

        // Finaliza esta actividad
        finish()
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    OfferUSTheme {
        Greeting("Android")
    }
}




