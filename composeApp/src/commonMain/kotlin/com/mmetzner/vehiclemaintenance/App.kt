package com.mmetzner.vehiclemaintenance

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
import com.mmetzner.vehiclemaintenance.core.navigation.MaintenanceEditRoute
import com.mmetzner.vehiclemaintenance.core.navigation.RegisterRoute
import com.mmetzner.vehiclemaintenance.core.navigation.VehicleDetailsRoute
import com.mmetzner.vehiclemaintenance.core.navigation.VehicleEditRoute
import com.mmetzner.vehiclemaintenance.core.navigation.VehicleHomeRoute
import com.mmetzner.vehiclemaintenance.core.navigation.VehicleListRoute
import com.mmetzner.vehiclemaintenance.core.ui.theme.VehicleMaintenanceTheme
import com.mmetzner.vehiclemaintenance.feature.auth.domain.repository.AuthRepository
import com.mmetzner.vehiclemaintenance.feature.auth.presentation.login.LoginScreen
import com.mmetzner.vehiclemaintenance.feature.auth.presentation.login.LoginViewModel
import com.mmetzner.vehiclemaintenance.feature.auth.presentation.register.RegisterScreen
import com.mmetzner.vehiclemaintenance.feature.auth.presentation.register.RegisterViewModel
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.addmaintenance.AddMaintenanceViewModel
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.addvehicle.AddVehicleScreen
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.addvehicle.AddVehicleViewModel
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.addmaintenance.AddMaintenanceScreen
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.home.VehicleHomeScreen
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.home.VehicleHomeViewModel
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.maintenanceedit.MaintenanceEditScreen
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.maintenanceedit.MaintenanceEditViewModel
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.vehicledetails.VehicleDetailsScreen
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.vehicledetails.VehicleDetailsViewModel
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.vehicleedit.VehicleEditScreen
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.vehicleedit.VehicleEditViewModel
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.vehiclelist.VehicleListScreen
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.vehiclelist.VehicleListViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    val navController = rememberNavController()

    VehicleMaintenanceTheme {
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
                            route = VehicleListRoute,
                            navOptions = navOptions {
                                popUpTo(LoginRoute) {
                                    inclusive = true
                                }
                            }
                        )
                    },
                    onCreateAccount = {
                        navController.navigate(RegisterRoute)
                    }
                )
            }

            composable<RegisterRoute> {
                val viewModel = koinViewModel<RegisterViewModel>()

                RegisterScreen(
                    viewModel = viewModel,
                    onAuthenticated = {
                        navController.navigate(
                            route = VehicleListRoute,
                            navOptions = navOptions {
                                popUpTo(LoginRoute) {
                                    inclusive = true
                                }
                            }
                        )
                    },
                    onBackToLogin = {
                        navController.popBackStack()
                    }
                )
            }

            composable<VehicleListRoute> {
                val viewModel = koinViewModel<VehicleListViewModel>()
                val authRepository = koinInject<AuthRepository>()
                val scope = rememberCoroutineScope()

                VehicleListScreen(
                    viewModel = viewModel,
                    onRegisterVehicle = {
                        navController.navigate(AddVehicleRoute)
                    },
                    onOpenVehicle = { vehicle ->
                        vehicle.id?.let { vehicleId ->
                            navController.navigate(VehicleDetailsRoute(vehicleId))
                        }
                    },
                    onAddMaintenance = { vehicle ->
                        navController.navigate(AddMaintenanceRoute(vehicle.plate))
                    },
                    onLogout = {
                        scope.launch {
                            authRepository.logout()
                            navController.navigate(
                                route = LoginRoute,
                                navOptions = navOptions {
                                    popUpTo(VehicleListRoute) {
                                        inclusive = true
                                    }
                                }
                            )
                        }
                    }
                )
            }

            composable<VehicleDetailsRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<VehicleDetailsRoute>()
                val viewModel = koinViewModel<VehicleDetailsViewModel>()

                VehicleDetailsScreen(
                    viewModel = viewModel,
                    vehicleId = route.vehicleId,
                    onBack = {
                        navController.popBackStack()
                    },
                    onEditVehicle = { vehicle ->
                        vehicle.id?.let { vehicleId ->
                            navController.navigate(VehicleEditRoute(vehicleId))
                        }
                    },
                    onAddMaintenance = { vehicle ->
                        navController.navigate(AddMaintenanceRoute(vehicle.plate))
                    },
                    onEditMaintenance = { vehicle, maintenance ->
                        vehicle.id?.let { vehicleId ->
                            navController.navigate(
                                MaintenanceEditRoute(
                                    vehicleId = vehicleId,
                                    maintenanceId = maintenance.remoteId ?: maintenance.id
                                )
                            )
                        }
                    }
                )
            }

            composable<VehicleEditRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<VehicleEditRoute>()
                val viewModel = koinViewModel<VehicleEditViewModel>()

                VehicleEditScreen(
                    viewModel = viewModel,
                    vehicleId = route.vehicleId,
                    onBack = {
                        navController.popBackStack()
                    },
                    onDeleted = {
                        navController.navigate(
                            route = VehicleListRoute,
                            navOptions = navOptions {
                                popUpTo(VehicleListRoute) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        )
                    }
                )
            }

            composable<VehicleHomeRoute> {
                val viewModel = koinViewModel<VehicleHomeViewModel>()
                val authRepository = koinInject<AuthRepository>()
                val scope = rememberCoroutineScope()

                VehicleHomeScreen(
                    viewModel = viewModel,
                    onRegisterVehicle = {
                        navController.navigate(AddVehicleRoute)
                    },
                    onAddMaintenance = { plate ->
                        navController.navigate(AddMaintenanceRoute(plate))
                    },
                    onLogout = {
                        scope.launch {
                            authRepository.logout()
                            navController.navigate(
                                route = LoginRoute,
                                navOptions = navOptions {
                                    popUpTo(VehicleHomeRoute) {
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

            composable<MaintenanceEditRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<MaintenanceEditRoute>()
                val viewModel = koinViewModel<MaintenanceEditViewModel>()

                MaintenanceEditScreen(
                    viewModel = viewModel,
                    vehicleId = route.vehicleId,
                    maintenanceId = route.maintenanceId,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}


