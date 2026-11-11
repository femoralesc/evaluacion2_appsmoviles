package com.example.juntavecinal_grupox.ui.navigation

sealed class AppRoutes (val route: String) {

    data object Home : AppRoutes(route = "home_page")

    data object Reservations : AppRoutes(route = "reservations_page")

    data object Finance : AppRoutes(route = "finance_page")

    data object Profile : AppRoutes(route = "profile_page")

    data object ReservationForm : AppRoutes(route = "reservation_form_page")

    data object MovementForm : AppRoutes(route = "movement_form_page")


}
