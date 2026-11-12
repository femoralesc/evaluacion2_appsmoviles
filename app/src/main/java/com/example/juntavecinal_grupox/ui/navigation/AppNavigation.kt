package com.example.juntavecinal_grupox.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.juntavecinal_grupox.ui.screens.FinanceScreen
import com.example.juntavecinal_grupox.ui.screens.HomeScreen
import com.example.juntavecinal_grupox.ui.screens.MovementFormScreen
import com.example.juntavecinal_grupox.ui.screens.ProfileScreen
import com.example.juntavecinal_grupox.ui.screens.ReservationFormScreen
import com.example.juntavecinal_grupox.ui.screens.ReservationsScreen
import com.example.juntavecinal_grupox.viewmodel.MainViewModel

private data class BottomNavItem(
    val label: String,
    val route: AppRoutes,
    val icon: ImageVector
)

@Composable
fun AppNavigation(
    viewModel: MainViewModel,
    navController: NavHostController = rememberNavController()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    val navItems = listOf(
        BottomNavItem("Inicio", AppRoutes.Home, Icons.Default.Home),
        BottomNavItem("Reservas", AppRoutes.Reservations, Icons.Default.CalendarMonth),
        BottomNavItem("Finanzas", AppRoutes.Finance, Icons.Default.AccountBalanceWallet),
        BottomNavItem("Perfil", AppRoutes.Profile, Icons.Default.Person)
    )


    val showBottomBar = navItems.any { it.route.route == currentRoute }

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
                is NavigationEvent.PopBackStack -> navController.popBackStack()
                is NavigationEvent.NavigateUp -> navController.navigateUp()
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    navItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route.route,
                            onClick = {
                                viewModel.navigateTo(
                                    route = item.route,
                                    popUpTo = AppRoutes.Home
                                )
                            },
                            icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoutes.Home.route,
            modifier = Modifier.padding(innerPadding)
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
}