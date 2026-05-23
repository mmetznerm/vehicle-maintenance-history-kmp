package com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote

import com.mmetzner.vehiclemaintenance.core.network.ApiConfig
import com.mmetzner.vehiclemaintenance.core.network.toNetworkRequestException
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote.dto.MaintenanceResponse
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote.dto.VehicleResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class VehicleRemoteDataSource(
    private val httpClient: HttpClient,
    private val apiConfig: ApiConfig
) {
    suspend fun getVehicleByPlate(plate: String): VehicleResponse {
        val response = httpClient.get("${apiConfig.normalizedBaseUrl}/v1/vehicles/$plate")

        if (!response.status.isSuccess()) {
            throw response.status.toNetworkRequestException("Fetch vehicle")
        }

        return response.body()
    }

    suspend fun createVehicle(vehicle: VehicleResponse) {
        val response = httpClient.post("${apiConfig.normalizedBaseUrl}/v1/vehicles") {
            contentType(ContentType.Application.Json)
            setBody(vehicle)
        }

        if (!response.status.isSuccess()) {
            throw response.status.toNetworkRequestException("Create vehicle")
        }
    }

    suspend fun createMaintenance(maintenance: MaintenanceResponse) {
        val response = httpClient.post("${apiConfig.normalizedBaseUrl}/v1/maintenances") {
            contentType(ContentType.Application.Json)
            setBody(maintenance)
        }

        if (!response.status.isSuccess()) {
            throw response.status.toNetworkRequestException("Create maintenance")
        }
    }
}
