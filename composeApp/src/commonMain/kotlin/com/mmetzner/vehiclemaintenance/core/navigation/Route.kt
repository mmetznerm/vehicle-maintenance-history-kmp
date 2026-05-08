package com.mmetzner.vehiclemaintenance.core.navigation

import kotlinx.serialization.Serializable

@Serializable
object VehicleSearchRoute

@Serializable
data class VehicleDetailsRoute(val plate: String)