package com.mmetzner.vehiclemaintenance

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mmetzner.vehiclemaintenance.core.navigation.VehicleDetailsRoute
import com.mmetzner.vehiclemaintenance.core.navigation.VehicleSearchRoute

@Composable
@Preview
fun App() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = VehicleSearchRoute
    ) {
        composable<VehicleSearchRoute> {

        }

        composable<VehicleDetailsRoute> { backStackEntry ->
            val route: VehicleDetailsRoute = backStackEntry.toRoute()
        }
    }
}