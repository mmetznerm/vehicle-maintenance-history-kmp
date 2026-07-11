package com.mmetzner.vehiclemaintenance.core.navigation

import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

@Serializable
object RegisterRoute

@Serializable
object VehicleHomeRoute

@Serializable
object VehicleListRoute

@Serializable
object AddVehicleRoute

@Serializable
data class AddMaintenanceRoute(val plate: String)



