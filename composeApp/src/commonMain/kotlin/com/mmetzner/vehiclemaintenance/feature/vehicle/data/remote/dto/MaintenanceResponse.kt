package com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote.dto

import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Maintenance
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MaintenanceResponse(
    @SerialName("id") val id: String,
    @SerialName("date") val date: String,
    @SerialName("description") val description: String,
    @SerialName("workshopName") val workshopName: String?,
    @SerialName("mileage") val mileage: Int?,
    @SerialName("totalValue") val totalValue: Double?,
    @SerialName("photoUrls") val photoUrls: List<String>? = null
)

fun MaintenanceResponse.toDomain() = Maintenance(
    id = this.id,
    date = this.date,
    description = this.description,
    workshopName = this.workshopName ?: "Nao informada",
    mileage = this.mileage,
    totalValue = this.totalValue,
    photoUrls = this.photoUrls ?: emptyList()
)
