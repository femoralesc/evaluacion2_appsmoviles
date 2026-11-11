package com.example.juntavecinal_grupox.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.juntavecinal_grupox.ui.screens.FinanceScreen
import com.example.juntavecinal_grupox.ui.screens.HomeScreen
import com.example.juntavecinal_grupox.ui.screens.MovementFormScreen
import com.example.juntavecinal_grupox.ui.screens.ProfileScreen
import com.example.juntavecinal_grupox.ui.screens.ReservationFormScreen
import com.example.juntavecinal_grupox.ui.screens.ReservationsScreen
import com.example.juntavecinal_grupox.viewmodel.MainViewModel

@Composable
fun AppNavigation(
    viewModel: MainViewModel,
    navController: NavHostController = rememberNavController()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.NavigateTo -> {
                    navController.navigate(event.route.route) {
                        event.popUptoRoute?.let { popRoute ->
                            popUpTo(popRoute.route) {
                                inclusive = event.inclusive
                            }
                        }
                        launchSingleTop = event.singleTop
                    }
                }
                is NavigationEvent.PopBackStack -> {
                    navController.popBackStack()
                }
                is NavigationEvent.NavigateUp -> {
                    navController.navigateUp()
                }
            }
        }
    }


    NavHost(
        navController = navController,
        startDestination = AppRoutes.Home.route
    ) {
        composable(AppRoutes.Home.route) {
            HomeScreen(
                uiState = uiState,
                onOpenReservations = { viewModel.navigateTo(AppRoutes.Reservations) },
                onOpenFinance = { viewModel.navigateTo(AppRoutes.Finance) },
                onNewReservation = { viewModel.navigateTo(AppRoutes.ReservationForm) },
                onNewMovement = { viewModel.navigateTo(AppRoutes.MovementForm) }
            )
        }

        composable(AppRoutes.Reservations.route) {
            ReservationsScreen(
                uiState = uiState,
                onPayment = { id, amount -> viewModel.registerPayment(id, amount) },
                onNewReservation = { viewModel.navigateTo(AppRoutes.ReservationForm) },
                onUpdateStatus = { id, status -> viewModel.updateReservationStatus(id, status) }
            )
        }

        composable(AppRoutes.Finance.route) {
            FinanceScreen(
                uiState = uiState,
                onNewMovement = { viewModel.navigateTo(AppRoutes.MovementForm) }
            )
        }

        composable(AppRoutes.Profile.route) {
            ProfileScreen(
                activeProfile = uiState.activeProfile,
                onSelectProfile = { profile -> viewModel.selectProfile(profile) }
            )
        }

        composable(AppRoutes.ReservationForm.route) {
            ReservationFormScreen(
                reservations = uiState.reservations,
                onBack = { viewModel.navigateBack() },
                onSubmit = { date, time, purpose ->
                    viewModel.addReservation(date, time, purpose)
                }
            )
        }

        composable(AppRoutes.MovementForm.route) {
            MovementFormScreen(
                onBack = { viewModel.navigateBack() },
                onSubmit = { title, category, amount, type ->
                    viewModel.addMovement(title, category, amount, type)
                }
            )
        }
    }
}