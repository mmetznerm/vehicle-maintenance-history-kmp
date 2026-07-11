package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.addmaintenance

sealed class AddMaintenanceEvent {
    data class UpdateDate(val value: String) : AddMaintenanceEvent()
    data class UpdateMileage(val value: String) : AddMaintenanceEvent()
    data class UpdateDescription(val value: String) : AddMaintenanceEvent()
    data class UpdateValue(val value: String) : AddMaintenanceEvent()
    data class SetPlate(val plate: String) : AddMaintenanceEvent()
    object Save : AddMaintenanceEvent()
}
