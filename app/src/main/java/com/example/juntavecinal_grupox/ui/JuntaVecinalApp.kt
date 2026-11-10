package com.example.juntavecinal_grupox.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.juntavecinal_grupox.navigation.AppRoutes
import com.example.juntavecinal_grupox.ui.screens.FinanceScreen
import com.example.juntavecinal_grupox.ui.screens.HomeScreen
import com.example.juntavecinal_grupox.ui.screens.MovementFormScreen
import com.example.juntavecinal_grupox.ui.screens.ProfileScreen
import com.example.juntavecinal_grupox.ui.screens.ReservationFormScreen
import com.example.juntavecinal_grupox.ui.screens.ReservationsScreen
import com.example.juntavecinal_grupox.ui.viewmodels.MainViewModel

private data class BottomNavigationItem(
    val route: String,
    val label: String,
    val symbol: String
)

private val bottomNavigationItems = listOf(
    BottomNavigationItem(AppRoutes.Home.route, "Inicio", "⌂"),
    BottomNavigationItem(AppRoutes.Reservations.route, "Reservas", "▤"),
    BottomNavigationItem(AppRoutes.Finance.route, "Finanzas", "\$"),
    BottomNavigationItem(AppRoutes.Profile.route, "Perfil", "●")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JuntaVecinalApp(mainViewModel: MainViewModel = viewModel()) {
    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: AppRoutes.Home.route
    val showBottomBar = bottomNavigationItems.any { it.route == currentRoute }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Junta Vecinal") },
                actions = {
                    TextButton(
                        onClick = {
                            navController.navigate(AppRoutes.Profile.route) {
                                launchSingleTop = true
                            }
                        }
                    ) {
                        Text("Perfil")
                    }
                }
            )
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavigationItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Text(item.symbol) },
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
                    onOpenReservations = { navController.navigate(AppRoutes.Reservations.route) },
                    onOpenFinance = { navController.navigate(AppRoutes.Finance.route) },
                    onNewReservation = { navController.navigate(AppRoutes.ReservationForm.route) },
                    onNewMovement = { navController.navigate(AppRoutes.MovementForm.route) }
                )
            }
            composable(AppRoutes.Reservations.route) {
                ReservationsScreen(
                    uiState = uiState,
                    onNewReservation = { navController.navigate(AppRoutes.ReservationForm.route) },
                    onUpdateStatus = mainViewModel::updateReservationStatus
                )
            }
            composable(AppRoutes.Finance.route) {
                FinanceScreen(
                    uiState = uiState,
                    onNewMovement = { navController.navigate(AppRoutes.MovementForm.route) }
                )
            }
            composable(AppRoutes.Profile.route) {
                ProfileScreen(
                    activeProfile = uiState.activeProfile,
                    onSelectProfile = { profile ->
                        mainViewModel.selectProfile(profile)
                        navController.navigate(AppRoutes.Home.route) {
                            popUpTo(AppRoutes.Home.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(AppRoutes.ReservationForm.route) {
                ReservationFormScreen(
                    onBack = { navController.popBackStack() },
                    onSubmit = { date, time, purpose ->
                        mainViewModel.addReservation(date, time, purpose)
                        navController.popBackStack()
                    }
                )
            }
            composable(AppRoutes.MovementForm.route) {
                MovementFormScreen(
                    onBack = { navController.popBackStack() },
                    onSubmit = { title, category, amount, type ->
                        mainViewModel.addMovement(title, category, amount, type)
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
