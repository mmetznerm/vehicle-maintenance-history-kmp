package com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote.dto

import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Maintenance
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MaintenanceResponse(
    @SerialName("id") val id: String,
    @SerialName("vehicleId") val vehicleId: String,
    @SerialName("maintenanceDate") val maintenanceDate: String,
    @SerialName("odometer") val odometer: Int,
    @SerialName("description") val description: String,
    @SerialName("cost") val cost: Double,
    @SerialName("createdAt") val createdAt: String? = null,
    @SerialName("updatedAt") val updatedAt: String? = null
)

@Serializable
data class CreateMaintenanceRequest(
    @SerialName("maintenanceDate") val maintenanceDate: String,
    @SerialName("odometer") val odometer: Int,
    @SerialName("description") val description: String,
    @SerialName("cost") val cost: Double
)

fun MaintenanceResponse.toDomain() = Maintenance(
    id = this.id,
    vehicleId = this.vehicleId,
    date = this.maintenanceDate,
    description = this.description,
    workshopName = null,
    mileage = this.odometer,
    totalValue = this.cost,
    photoUrls = emptyList()
)
