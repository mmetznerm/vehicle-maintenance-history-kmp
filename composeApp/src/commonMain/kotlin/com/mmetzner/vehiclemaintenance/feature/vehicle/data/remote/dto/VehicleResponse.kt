package com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote.dto

import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Vehicle
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VehicleResponse(
    @SerialName("id") val id: String,
    @SerialName("plate") val plate: String,
    @SerialName("brand") val brand: String,
    @SerialName("model") val model: String,
    @SerialName("manufactureYear") val manufactureYear: Int,
    @SerialName("color") val color: String? = null,
    @SerialName("createdAt") val createdAt: String? = null,
    @SerialName("updatedAt") val updatedAt: String? = null
)

@Serializable
data class VehicleSummaryResponse(
    @SerialName("id") val id: String,
    @SerialName("plate") val plate: String,
    @SerialName("brand") val brand: String,
    @SerialName("model") val model: String,
    @SerialName("manufactureYear") val manufactureYear: Int,
    @SerialName("color") val color: String? = null
)

@Serializable
data class CreateVehicleRequest(
    @SerialName("plate") val plate: String,
    @SerialName("brand") val brand: String,
    @SerialName("model") val model: String,
    @SerialName("manufactureYear") val manufactureYear: Int,
    @SerialName("color") val color: String? = null
)

fun VehicleResponse.toDomain() = Vehicle(
    id = this.id,
    plate = this.plate,
    model = this.model,
    brand = this.brand,
    year = this.manufactureYear,
    color = this.color.orEmpty(),
    maintenances = emptyList()
)

fun VehicleSummaryResponse.toDomain() = Vehicle(
    id = this.id,
    plate = this.plate,
    model = this.model,
    brand = this.brand,
    year = this.manufactureYear,
    color = this.color.orEmpty(),
    maintenances = emptyList()
)
