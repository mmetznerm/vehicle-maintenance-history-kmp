package com.mmetzner.vehiclemaintenance.core.navigation

import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

@Serializable
object VehicleSearchRoute

@Serializable
object AddVehicleRoute

@Serializable
data class AddMaintenanceRoute(val plate: String)



