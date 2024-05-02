package com.offerus.navigation

// TODAS LAS PANTALLAS
sealed class AppScreens(val route: String){
    //object SplashScreen: AppScreens("splash_screen")
    object LoginScreen: AppScreens("login_screen")
    object RegisterScreen: AppScreens("register_screen")
    object MainScreen: AppScreens("main_screen")
    object UserScreen: AppScreens("user_screen")
    //object MapScreen: AppScreens("map_screen")
    object OfferDetailsScreen: AppScreens("offer_details_screen")
}