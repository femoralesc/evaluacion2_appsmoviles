package com.example.juntavecinal_grupox.navigation

sealed class AppRoutes (val route: String) {

    data object Home : AppRoutes(route = "home_page")

    data object Profile : AppRoutes(route = "profile_page")

    data object Settings : AppRoutes(route = "settings_page")


}