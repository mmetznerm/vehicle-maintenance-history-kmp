package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.addmaintenance

data class AddMaintenanceState(
    val vehiclePlate: String = "",
    val date: String = "",
    val mileage: String = "",
    val description: String = "",
    val totalValue: String = "",
    val isSaving: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)
