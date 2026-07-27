package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.home

import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Vehicle
import org.jetbrains.compose.resources.StringResource

sealed interface VehicleHomeState {
    data object Loading : VehicleHomeState
    data object Empty : VehicleHomeState
    data class Content(val vehicle: Vehicle) : VehicleHomeState
    data class Error(val message: StringResource) : VehicleHomeState
}
