package com.offerus

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.offerus.navigation.MainNavigation
import com.offerus.ui.theme.OfferUSTheme
import com.offerus.utils.isNetworkAvailable
import com.offerus.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // viewModel initialize
    // viewmodel general y de autenticacion
    // private val viewmodel by viewModels<>()  //hiltviewmodel

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            OfferUSTheme(
                // Select the app theme based on the theme salected by the user (dark/light)
                darkTheme = mainViewModel.tema.collectAsState(initial = true).value
            ) {
                // Log in if there is a user saved in the datastore
                var logedIn by rememberSaveable {
                    mutableStateOf(false)
                }
                // check if there is internet conection
                if (!isNetworkAvailable(this)) {
                    // show a toast
                    val mensaje = stringResource(R.string.no_internet)
                    Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                }else {
                    logedIn = mainViewModel.obtenerUsuarioLogeado() != ""
                    if (logedIn) {
                        Log.d("login", "Usuario logeado1: $logedIn")
                        mainViewModel.loginUsuarioGuardado()
                    }
                }

                // Update the app language, to restore the previous app language in case a different
                // language has been stablished before closing the app
                mainViewModel.reloadLang(mainViewModel.idioma.collectAsState(initial = mainViewModel.idiomaActual).value, this)


                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation(logedIn, mainViewModel)
                }
            }
        }
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




