package com.offerus.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.offerus.screens.FavoritesScreen
import com.offerus.screens.Login
import com.offerus.screens.MainScreen
import com.offerus.screens.MapaScreen
import com.offerus.screens.OfferDetails
import com.offerus.screens.UserScreen
import com.offerus.viewModels.MainViewModel

@Composable
fun MainNavigation(
    logedIn: Boolean,
    mainViewModel: MainViewModel
) {
    //hay que añadir el viewmodel como parametro
    val navController = rememberNavController()

    var startDestination = AppScreens.MainScreen.route
    if (!logedIn){
        startDestination = AppScreens.LoginScreen.route
    }


    NavHost(navController = navController, startDestination = startDestination){
        composable(AppScreens.MainScreen.route){
            MainScreen(navController, mainViewModel )
        }
        composable(AppScreens.UserScreen.route){
            UserScreen(viewModel = mainViewModel, navController = navController)
        }
        composable(AppScreens.LoginScreen.route){
            Login(
                mainViewModel = mainViewModel,
                navController = navController
            )
        }
        composable(AppScreens.OfferDetailsScreen.route){
            OfferDetails(navController, mainViewModel)
        }
        composable(AppScreens.MapScreen.route){
            // Contenido de la pestaña Map
            MapaScreen(navController)
        }
        composable(AppScreens.Favorites.route){
            FavoritesScreen(mainViewModel = mainViewModel, navController = navController)
        }
    }
}