package com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote.dto

import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Vehicle
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VehicleResponse(
    @SerialName("plate") val plate: String,
    @SerialName("model") val model: String,
    @SerialName("brand") val brand: String,
    @SerialName("year") val year: Int,
    @SerialName("maintenances") val maintenances: List<MaintenanceResponse>? = null
)

fun VehicleResponse.toDomain() = Vehicle(
    plate = this.plate,
    model = this.model,
    brand = this.brand,
    year = this.year,
    maintenances = this.maintenances?.map { it.toDomain() } ?: emptyList()
)
