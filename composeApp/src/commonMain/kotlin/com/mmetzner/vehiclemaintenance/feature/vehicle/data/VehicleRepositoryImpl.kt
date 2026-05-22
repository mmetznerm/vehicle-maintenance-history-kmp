package com.mmetzner.vehiclemaintenance.feature.vehicle.data

import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.dao.VehicleDao
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.mapper.toDomain
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.mapper.toEntity
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.mapper.toPhotoEntities
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote.VehicleResponse
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.Vehicle
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.repository.VehicleRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VehicleRepositoryImpl(
    private val httpClient: HttpClient,
    private val vehicleDao: VehicleDao
) : VehicleRepository {

    override suspend fun observeVehicle(plate: String): Flow<Vehicle?> {
        return vehicleDao.observeVehicleByPlate(plate).map { relation ->
            relation?.toDomain()
        }
    }

    override suspend fun syncVehicle(plate: String): Result<Unit> {
        return try {
            val response = httpClient.get("https://api.exemplo.com/v1/vehicles/$plate")

            if (response.status.isSuccess()) {
                val dto: VehicleResponse = response.body()

                val vehicleEntity = dto.toEntity()
                val maintenanceEntities = dto.maintenances?.map { it.toEntity(dto.plate) } ?: emptyList()
                val photoEntities = dto.maintenances?.flatMap { it.toPhotoEntities(it.id) } ?: emptyList()

                vehicleDao.syncVehicleData(vehicleEntity, maintenanceEntities, photoEntities)

                Result.success(Unit)
            } else {
                Result.failure(Exception("Fail on API: Code ${response.status.value}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}