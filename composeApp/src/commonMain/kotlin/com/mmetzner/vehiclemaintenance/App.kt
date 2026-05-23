package com.mmetzner.vehiclemaintenance

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.mmetzner.vehiclemaintenance.core.navigation.AddVehicleRoute
import com.mmetzner.vehiclemaintenance.core.navigation.AddMaintenanceRoute
import com.mmetzner.vehiclemaintenance.core.navigation.LoginRoute
import com.mmetzner.vehiclemaintenance.core.navigation.VehicleSearchRoute
import com.mmetzner.vehiclemaintenance.feature.auth.domain.repository.AuthRepository
import com.mmetzner.vehiclemaintenance.feature.auth.presentation.login.LoginScreen
import com.mmetzner.vehiclemaintenance.feature.auth.presentation.login.LoginViewModel
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.addmaintenance.AddMaintenanceViewModel
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.search.VehicleSearchViewModel
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.addvehicle.AddVehicleScreen
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.addvehicle.AddVehicleViewModel
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.addmaintenance.AddMaintenanceScreen
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.search.VehicleSearchScreen
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    val navController = rememberNavController()

    MaterialTheme {
        NavHost(
            navController = navController,
            startDestination = LoginRoute
        ) {
            composable<LoginRoute> {
                val viewModel = koinViewModel<LoginViewModel>()

                LoginScreen(
                    viewModel = viewModel,
                    onAuthenticated = {
                        navController.navigate(
                            route = VehicleSearchRoute,
                            navOptions = navOptions {
                                popUpTo(LoginRoute) {
                                    inclusive = true
                                }
                            }
                        )
                    }
                )
            }

            composable<VehicleSearchRoute> {
                val viewModel = koinViewModel<VehicleSearchViewModel>()
                val authRepository = koinInject<AuthRepository>()
                val scope = rememberCoroutineScope()

                VehicleSearchScreen(
                    viewModel = viewModel,
                    onNavigateToAddVehicle = {
                        navController.navigate(AddVehicleRoute)
                    },
                    onNavigateToAddMaintenance = { plate ->
                        navController.navigate(AddMaintenanceRoute(plate))
                    },
                    onLogout = {
                        scope.launch {
                            authRepository.logout()
                            navController.navigate(
                                route = LoginRoute,
                                navOptions = navOptions {
                                    popUpTo(VehicleSearchRoute) {
                                        inclusive = true
                                    }
                                }
                            )
                        }
                    }
                )
            }

            composable<AddVehicleRoute> {
                val viewModel = koinViewModel<AddVehicleViewModel>()

                AddVehicleScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable<AddMaintenanceRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<AddMaintenanceRoute>()
                val viewModel = koinViewModel<AddMaintenanceViewModel>()

                AddMaintenanceScreen(
                    viewModel = viewModel,
                    plate = route.plate,
                    onBack = {
                        navController.popBackStack()
                    },
                    onSuccess = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}


