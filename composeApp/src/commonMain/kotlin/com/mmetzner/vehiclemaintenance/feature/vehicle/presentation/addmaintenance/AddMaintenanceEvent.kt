package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.addmaintenance

sealed class AddMaintenanceEvent {
    data class UpdateServiceType(val value: String) : AddMaintenanceEvent()
    data class UpdateDate(val value: String) : AddMaintenanceEvent()
    data class UpdateMileage(val value: String) : AddMaintenanceEvent()
    data class UpdateDescription(val value: String) : AddMaintenanceEvent()
    data class UpdateReplacedParts(val value: String) : AddMaintenanceEvent()
    data class UpdateNotes(val value: String) : AddMaintenanceEvent()
    data class UpdateWorkshop(val value: String) : AddMaintenanceEvent()
    data class UpdateValue(val value: String) : AddMaintenanceEvent()
    data class SetPlate(val plate: String) : AddMaintenanceEvent()
    object Save : AddMaintenanceEvent()
}
