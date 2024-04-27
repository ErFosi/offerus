package com.offerus.navigation

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.offerus.screens.MainScreen
import com.offerus.screens.UserScreen

@Composable
fun MainNavigation() {
    //hay que añadir el viewmodel como parametro
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppScreens.MainScreen.route){
        composable(AppScreens.MainScreen.route){
            MainScreen(navController)
        }
        composable(AppScreens.UserScreen.route){
            UserScreen(navController)
        }
    }
}
