package com.offerus.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.offerus.R
import com.offerus.components.ProfilePicture
import com.offerus.components.createImageFileFromBitMap
import com.offerus.navigation.AppScreens
import com.offerus.navigation.BottomBarRoute
import com.offerus.navigation.SECTIONS
import com.offerus.utils.isNetworkAvailable
import com.offerus.viewModels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.net.URI

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun MainScreen(
    navControllerMain: NavHostController,
    mainViewModel: MainViewModel
){

    //navControler para el BOTTOM BAR
    val navController = rememberNavController()
    val context = LocalContext.current

    val windowSizeClass = calculateWindowSizeClass(context as Activity)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedDestination = navBackStackEntry?.destination?.route ?: BottomBarRoute.HOME


    val enableBottomNavigation by derivedStateOf { windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact }
    val enableNavigationRail by derivedStateOf { windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact }

    val onNavigateToSection = { route: String ->
        navController.navigate((route)){
            popUpTo(BottomBarRoute.HOME)
            launchSingleTop = true
        }
    }
    Scaffold(
        topBar = { ToolBar(
            mainViewModel.usuario,
            viewModel = mainViewModel,
            onFavoritesClick = { navControllerMain.navigate(route = AppScreens.Favorites.route) },
            onUserClick = {navControllerMain.navigate(route = AppScreens.UserScreen.route) }
        )
        },
        bottomBar = { if (enableBottomNavigation){
            AppBottomBar(selectedDestination, onNavigateToSection)
        } }
    ) {
            innerPadding ->
        Row(
            modifier = Modifier
                .padding(innerPadding),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (enableNavigationRail){
                OfferusNavigationRail(selectedDestination, onNavigateToSection)
            }
            NavHost(navController, startDestination = BottomBarRoute.HOME) {
                composable(BottomBarRoute.HOME) {
                    // Contenido de la pestaña Home
                    HomeScreen(navControllerMain, mainViewModel)
                }
                composable(BottomBarRoute.SEARCH) {
                    // Contenido de la pestaña Team
                    OffersScreen(mainViewModel = mainViewModel, navController = navControllerMain)
                }
                composable(BottomBarRoute.MYOFFERS) {
                    // Contenido de la pestaña Table
                    MyOffersScreen(navControllerMain, mainViewModel)
                }

            }
        }
    }
}

@Composable
fun AppBottomBar(currentRoute: String?, onNavigate: (String) -> Unit) {
    NavigationBar {
        SECTIONS.forEach { destinations ->
            when (destinations.route) {
                BottomBarRoute.HOME -> {
                    NavigationBarItem(
                        icon = { Icon(painter = painterResource(destinations.selectedIcon), contentDescription = null, modifier = Modifier.size(32.dp)) },
                        selected = currentRoute == destinations.route,
                        onClick = { onNavigate(destinations.route) },
                        colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primaryContainer)
                    )
                }
                BottomBarRoute.SEARCH -> {
                    NavigationBarItem(
                        icon = { Icon(ImageVector.vectorResource(id = destinations.selectedIcon), contentDescription = null, modifier = Modifier.size(24.dp)) },
                        selected = currentRoute == destinations.route,
                        onClick = { onNavigate(destinations.route) },
                        colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primaryContainer)
                    )
                }
                BottomBarRoute.MYOFFERS -> {
                    NavigationBarItem(
                        icon = { Icon(ImageVector.vectorResource(id = destinations.selectedIcon), contentDescription = null, modifier = Modifier.size(24.dp)) },
                        selected = currentRoute == destinations.route,
                        onClick = { onNavigate(destinations.route) },
                        colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primaryContainer)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolBar(
    username: String,
    onUserClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    viewModel: MainViewModel
) {
    var context = LocalContext.current
    if(username!=null) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.tertiaryContainer,
            ),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,

                    ) {

                    // Columna a la izquierda con el logo de OFFERUS
                    Column(
                        modifier = Modifier
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        Icon(painter = painterResource(id = R.drawable.offerus_letras_sinbg), contentDescription = null, modifier = Modifier.size(132.dp))

                    }

                    Spacer(modifier = Modifier.weight(1f))

                    //BOTON LISTA DESEOS
                    IconButton(onClick = { onFavoritesClick() }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.favorite_filled),
                            contentDescription = null
                        )
                    }

                    //User profile
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable { onUserClick() }) {
                        UserAvatar(username, viewModel)
                        Text(text = username, fontSize = 12.sp, lineHeight = 12.sp)
                    }
                }
            }

        )
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun UserAvatar(username: String, viewModel: MainViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var uri by remember { mutableStateOf<Uri>(Uri.parse("file:///storage/emulated/0/Android/data/com.offerus/cache/JPEG_${username}_.jpg")) }

    val file = File(URI.create(uri.toString()))
    if (!file.exists()){
        uri = "file:///storage/emulated/0/Android/data/com.offerus/cache/JPEG_default_.jpg".toUri()
    }
    if (!file.exists() || true){
        if (isNetworkAvailable(context)) {
            coroutineScope.launch(Dispatchers.IO) {
                val bitmap = viewModel.getUserProfile(username)
                uri = context.createImageFileFromBitMap(bitmap, username)
                Log.d("USERNAME FOTO AVATAR", username)
                //Log.d("uri", uri.toString())
            }
        }
    }
    Box(modifier = Modifier.size(30.dp)){
        ProfilePicture(
            directory = File("images"),
            uri = uri,
            onSetUri = {},
            editable = false
        )
    }


    /*Box(
        modifier = Modifier
            .size(30.dp) // Ajusta el tamaño del círculo según tus necesidades
            .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape),
    ) {
        Text(
            text = iniciales,
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.Center),
            fontWeight = FontWeight.Bold
        )
    }*/
}

@Composable
fun OfferusNavigationRail(currentRoute: String, onNavigate: (String) -> Unit) {
    NavigationRail() {
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
            SECTIONS.forEach { destinations ->
                when (destinations.route) {
                    BottomBarRoute.HOME -> {
                        NavigationRailItem(
                            modifier = Modifier.weight(0.25f),
                            icon = { Icon(painter = painterResource(destinations.selectedIcon), contentDescription = null, modifier = Modifier.size(32.dp)) },
                            selected = currentRoute == destinations.route,
                            onClick = { onNavigate(destinations.route) }
                        )
                    }
                    BottomBarRoute.SEARCH -> {
                        NavigationRailItem(
                            modifier = Modifier.weight(0.25f),
                            icon = { Icon(ImageVector.vectorResource(id = destinations.selectedIcon), contentDescription = null) },
                            selected = currentRoute == destinations.route,
                            onClick = { onNavigate(destinations.route) }
                        )
                    }
                    BottomBarRoute.MYOFFERS -> {
                        NavigationRailItem(
                            modifier = Modifier.weight(0.25f),
                            icon = { Icon(ImageVector.vectorResource(id = destinations.selectedIcon), contentDescription = null) },
                            selected = currentRoute == destinations.route,
                            onClick = { onNavigate(destinations.route) }
                        )
                    }
                }
            }

        }

    }
}