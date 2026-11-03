package com.example.juntavecinal_grupox.navigation

sealed class NavigationEvent {


    data class NavigateTo(

        val route: AppRoutes,
        val popUptoRoute: AppRoutes? = null,
        val inclusive: Boolean = false,
        val singleTop: Boolean = false

    ) : NavigationEvent()


    object PopBackStack : NavigationEvent()

    object NavigateUp: NavigationEvent()



}