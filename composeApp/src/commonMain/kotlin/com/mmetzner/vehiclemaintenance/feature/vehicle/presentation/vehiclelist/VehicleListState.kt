package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.vehiclelist

import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Vehicle

data class VehicleListState(
    val vehicles: List<Vehicle> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)
