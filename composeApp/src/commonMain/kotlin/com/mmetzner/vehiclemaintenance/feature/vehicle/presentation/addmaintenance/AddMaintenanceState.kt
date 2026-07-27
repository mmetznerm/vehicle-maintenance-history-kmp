package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.addmaintenance

import org.jetbrains.compose.resources.StringResource

data class AddMaintenanceState(
    val vehiclePlate: String = "",
    val date: String = "",
    val mileage: String = "",
    val description: String = "",
    val totalValue: String = "",
    val isSaving: Boolean = false,
    val error: StringResource? = null,
    val success: Boolean = false
)
