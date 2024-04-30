package com.offerus.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.offerus.R
import com.offerus.navigation.AppScreens
import com.offerus.navigation.BottomBarRoute
import com.offerus.navigation.SECTIONS

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun MainScreen(navControllerMain: NavHostController){

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
        topBar = { ToolBar(){navControllerMain.navigate(route = AppScreens.UserScreen.route)} },
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
                    HomeScreen()
                }
                composable(BottomBarRoute.SEARCH) {
                    // Contenido de la pestaña Team
                    SearchScreen()
                }
                composable(BottomBarRoute.MYOFFERS) {
                    // Contenido de la pestaña Table
                    MyOffersScreen()
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
                        icon = { Icon(painter = painterResource(destinations.selectedIcon), contentDescription = null, modifier = Modifier.size(28.dp)) },
                        selected = currentRoute == destinations.route,
                        onClick = { onNavigate(destinations.route) }
                    )
                }
                BottomBarRoute.SEARCH -> {
                    NavigationBarItem(
                        icon = { Icon(ImageVector.vectorResource(id = destinations.selectedIcon), contentDescription = null, modifier = Modifier.size(24.dp)) },
                        selected = currentRoute == destinations.route,
                        onClick = { onNavigate(destinations.route) }
                    )
                }
                BottomBarRoute.MYOFFERS -> {
                    NavigationBarItem(
                        icon = { Icon(ImageVector.vectorResource(id = destinations.selectedIcon), contentDescription = null, modifier = Modifier.size(24.dp)) },
                        selected = currentRoute == destinations.route,
                        onClick = { onNavigate(destinations.route) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolBar(onUserClick: () -> Unit) {
    var context = LocalContext.current
    //var username = viewModel.username.value
    var username = "cuadron11"
    if(username!=null) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,

                ) {

                    // Columna a la izquierda con "Zapete" en grande y "Fantasy" abajo
                    Column(
                        modifier = Modifier
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        Text(
                            "OFFERUS",
                            fontSize = 20.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontStyle = FontStyle.Italic
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    //BOTON LISTA DESEOS
                    IconButton(onClick = { Log.d("boton", "lista deseos")}) {
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
                        UserAvatar("AC")
                        Text(text = username, fontSize = 12.sp, lineHeight = 12.sp)
                    }
                    Spacer(
                        modifier = Modifier

                            .height(32.dp)

                            .width(2.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                    )



                }
            }

        )
    }
}

@Composable
fun UserAvatar(iniciales: String) {
    Box(
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
    }
}

@Composable
fun MyOffersScreen() {
    Surface() {
        Text(text = "MY OFFERS SCREEN")
    }
}



@Composable
fun HomeScreen() {
    Surface {
        Text(text = "HOME SCREEN")
    }
}

@Composable
fun OfferusNavigationRail(currentRoute: String, onNavigate: (String) -> Unit) {
    NavigationRail {
        SECTIONS.forEach { destinations ->

            when (destinations.route) {
                BottomBarRoute.HOME -> {
                    NavigationRailItem(
                        icon = { Icon(painter = painterResource(destinations.selectedIcon), contentDescription = null, modifier = Modifier.size(28.dp)) },
                        selected = currentRoute == destinations.route,
                        onClick = { onNavigate(destinations.route) }
                    )
                }
                BottomBarRoute.SEARCH -> {
                    NavigationRailItem(
                        icon = { Icon(ImageVector.vectorResource(id = destinations.selectedIcon), contentDescription = null, modifier = Modifier.size(24.dp)) },
                        selected = currentRoute == destinations.route,
                        onClick = { onNavigate(destinations.route) }
                    )
                }
                BottomBarRoute.MYOFFERS -> {
                    NavigationRailItem(
                        icon = { Icon(ImageVector.vectorResource(id = destinations.selectedIcon), contentDescription = null, modifier = Modifier.size(24.dp)) },
                        selected = currentRoute == destinations.route,
                        onClick = { onNavigate(destinations.route) }
                    )
                }
            }
        }
    }
}