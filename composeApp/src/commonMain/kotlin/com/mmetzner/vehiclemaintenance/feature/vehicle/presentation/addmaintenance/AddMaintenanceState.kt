package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.addmaintenance

data class AddMaintenanceState(
    val vehiclePlate: String = "",
    val serviceType: String = "",
    val date: String = "",
    val mileage: String = "",
    val description: String = "",
    val replacedParts: String = "",
    val notes: String = "",
    val workshopName: String = "",
    val totalValue: String = "",
    val isSaving: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)
