package com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model

data class Maintenance(
    val id: String,
    val date: String,
    val description: String,
    val workshopName: String?,
    val mileage: Int?,
    val totalValue: Double?,
    val photoUrls: List<String>? = null,
    val isPendingSync: Boolean = false,
    val vehicleId: String? = null
)


