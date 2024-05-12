package com.offerus.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.offerus.R
import com.offerus.components.Marcador
import com.offerus.components.TopBarSecundario
import com.offerus.components.mapa
import com.offerus.ui.theme.OfferUSTheme
import com.offerus.utils.isNetworkAvailable
import com.offerus.viewModels.MainViewModel

@Composable
fun MapaScreen(
    navController: NavHostController
){
    Scaffold(topBar = { TopBarSecundario(navController) }) {
            innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            MapaScreenContent()
        }
    }
}

@Composable
fun MapaScreenContent(

) {
    val context = LocalContext.current

    if (!isNetworkAvailable(context)){
        Card(
            modifier = Modifier
                .width(350.dp)
                .height(100.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(stringResource(R.string.no_internet))
                }
            }
        }
    }else {
        // Show the map only if there is internet connection

        var permisoUbicacion by rememberSaveable {
            mutableStateOf(false)
        }

        var marcadores: List<Marcador>? by rememberSaveable {
            mutableStateOf(null)
        }


        if (marcadores == null) {
            // TODO OBTENER MARCADORES ??
            /*viewModel.getMarcadores {
                if (it != null) {
                    marcadores = it
                }
            }*/
        }
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

        if (marcadores != null) {
            //mapa(marcadores!!, permisoUbicacion)
            mapa(
                marcadores = marcadores!!,
                permisoUbicacion = permisoUbicacion,
                sePuedeDesplazar = true,
                /*cameraPosition = CameraPosition.Builder()
                    .target(LatLng(0.0, 0.0)) // TODO PONER LA UBICACICON DEL USUARIO
                    .zoom(5f).build()*/
            )
        }else{
            // TODO REVISAR SI ESTO ES NECESARIO
            mapa(
                marcadores = listOf(),
                permisoUbicacion = permisoUbicacion,
                sePuedeDesplazar = true,
                /*cameraPosition = CameraPosition.Builder()
                    .target(LatLng(0.0, 0.0)) // TODO PONER LA UBICACICON DEL USUARIO
                    .zoom(5f).build()*/
            )
        }
    }

}


// make a preview of the mapascreen composable
@Preview(showBackground = true)
@Composable
fun MapaScreenPreview() {
    OfferUSTheme{
        //MapaScreen()
    }

}