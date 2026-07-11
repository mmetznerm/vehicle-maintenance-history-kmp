package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.vehicledetails

import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Vehicle

data class VehicleDetailsState(
    val vehicle: Vehicle? = null,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)
