package com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote

import com.mmetzner.vehiclemaintenance.core.network.ApiConfig
import com.mmetzner.vehiclemaintenance.core.network.toNetworkRequestException
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote.dto.CreateMaintenanceRequest
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote.dto.CreateVehicleRequest
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote.dto.MaintenanceResponse
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote.dto.VehicleResponse
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote.dto.VehicleSummaryResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class VehicleRemoteDataSource(
    private val httpClient: HttpClient,
    private val apiConfig: ApiConfig
) {
    suspend fun listVehicles(): List<VehicleSummaryResponse> {
        val response = httpClient.get("${apiConfig.normalizedBaseUrl}/v1/vehicles")

        if (!response.status.isSuccess()) {
            throw response.status.toNetworkRequestException("List vehicles")
        }

        return response.body()
    }

    suspend fun getVehicle(vehicleId: String): VehicleResponse {
        val response = httpClient.get("${apiConfig.normalizedBaseUrl}/v1/vehicles/$vehicleId")

        if (!response.status.isSuccess()) {
            throw response.status.toNetworkRequestException("Fetch vehicle")
        }

        return response.body()
    }

    suspend fun getVehicleByPlate(plate: String): VehicleResponse {
        val vehicleId = listVehicles()
            .firstOrNull { it.plate.equals(plate, ignoreCase = true) }
            ?.id
            ?: throw 404.toNetworkRequestException("Fetch vehicle")

        return getVehicle(vehicleId)
    }

    suspend fun createVehicle(vehicle: CreateVehicleRequest): VehicleResponse {
        val response = httpClient.post("${apiConfig.normalizedBaseUrl}/v1/vehicles") {
            contentType(ContentType.Application.Json)
            setBody(vehicle)
        }

        if (!response.status.isSuccess()) {
            throw response.status.toNetworkRequestException("Create vehicle")
        }

        return response.body()
    }

    suspend fun updateVehicle(vehicleId: String, vehicle: CreateVehicleRequest): VehicleResponse {
        val response = httpClient.put("${apiConfig.normalizedBaseUrl}/v1/vehicles/$vehicleId") {
            contentType(ContentType.Application.Json)
            setBody(vehicle)
        }

        if (!response.status.isSuccess()) {
            throw response.status.toNetworkRequestException("Update vehicle")
        }

        return response.body()
    }

    suspend fun deleteVehicle(vehicleId: String) {
        val response = httpClient.delete("${apiConfig.normalizedBaseUrl}/v1/vehicles/$vehicleId")

        if (!response.status.isSuccess()) {
            throw response.status.toNetworkRequestException("Delete vehicle")
        }
    }

    suspend fun listMaintenances(vehicleId: String): List<MaintenanceResponse> {
        val response = httpClient.get("${apiConfig.normalizedBaseUrl}/v1/vehicles/$vehicleId/maintenances")

        if (!response.status.isSuccess()) {
            throw response.status.toNetworkRequestException("List maintenances")
        }

        return response.body()
    }

    suspend fun getMaintenance(vehicleId: String, maintenanceId: String): MaintenanceResponse {
        val response = httpClient.get(
            "${apiConfig.normalizedBaseUrl}/v1/vehicles/$vehicleId/maintenances/$maintenanceId"
        )

        if (!response.status.isSuccess()) {
            throw response.status.toNetworkRequestException("Fetch maintenance")
        }

        return response.body()
    }

    suspend fun createMaintenance(
        vehicleId: String,
        maintenance: CreateMaintenanceRequest
    ): MaintenanceResponse {
        val response = httpClient.post("${apiConfig.normalizedBaseUrl}/v1/vehicles/$vehicleId/maintenances") {
            contentType(ContentType.Application.Json)
            setBody(maintenance)
        }

        if (!response.status.isSuccess()) {
            throw response.status.toNetworkRequestException("Create maintenance")
        }

        return response.body()
    }

    suspend fun updateMaintenance(
        vehicleId: String,
        maintenanceId: String,
        maintenance: CreateMaintenanceRequest
    ): MaintenanceResponse {
        val response = httpClient.put(
            "${apiConfig.normalizedBaseUrl}/v1/vehicles/$vehicleId/maintenances/$maintenanceId"
        ) {
            contentType(ContentType.Application.Json)
            setBody(maintenance)
        }

        if (!response.status.isSuccess()) {
            throw response.status.toNetworkRequestException("Update maintenance")
        }

        return response.body()
    }

    suspend fun deleteMaintenance(vehicleId: String, maintenanceId: String) {
        val response = httpClient.delete(
            "${apiConfig.normalizedBaseUrl}/v1/vehicles/$vehicleId/maintenances/$maintenanceId"
        )

        if (!response.status.isSuccess()) {
            throw response.status.toNetworkRequestException("Delete maintenance")
        }
    }

    suspend fun createMaintenanceByPlate(
        plate: String,
        maintenance: CreateMaintenanceRequest
    ): MaintenanceResponse {
        val vehicleId = listVehicles()
            .firstOrNull { it.plate.equals(plate, ignoreCase = true) }
            ?.id
            ?: throw 404.toNetworkRequestException("Create maintenance")

        return createMaintenance(vehicleId, maintenance)
    }
}
