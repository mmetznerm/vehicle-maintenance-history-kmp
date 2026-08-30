package com.mmetzner.vehiclemaintenance.feature.vehicle.data

import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.dao.VehicleDao
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.mapper.toDomain
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.mapper.toEntity
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.mapper.toPendingEntity
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.mapper.toPhotoEntities
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote.VehicleRemoteDataSource
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.sync.OutboxSyncService
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Vehicle
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VehicleRepositoryImpl(
    private val remoteDataSource: VehicleRemoteDataSource,
    private val vehicleDao: VehicleDao,
    private val outboxSyncService: OutboxSyncService
) : VehicleRepository {

    override suspend fun observeVehicles(): Flow<List<Vehicle>> {
        return vehicleDao.observeVehicles().map { relations ->
            relations.map { it.toDomain() }
        }
    }

    override suspend fun observePrimaryVehicle(): Flow<Vehicle?> {
        return vehicleDao.observePrimaryVehicle().map { relation ->
            relation?.toDomain()
        }
    }

    override suspend fun observeVehicle(plate: String): Flow<Vehicle?> {
        return vehicleDao.observeVehicleByPlate(plate).map { relation ->
            relation?.toDomain()
        }
    }

    override suspend fun observeVehicleById(vehicleId: String): Flow<Vehicle?> {
        return vehicleDao.observeVehicleById(vehicleId).map { relation ->
            relation?.toDomain()
        }
    }

    override suspend fun syncVehicles(): Result<Unit> {
        return try {
            val vehicles = remoteDataSource.listVehicles()

            for (vehicle in vehicles) {
                val maintenances = remoteDataSource.listMaintenances(vehicle.id)

                vehicleDao.syncVehicleData(
                    vehicle = vehicle.toEntity(),
                    maintenances = maintenances.map { it.toEntity(vehicle.plate) },
                    photos = maintenances.flatMap { it.toPhotoEntities(it.id) }
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncVehicle(plate: String): Result<Unit> {
        return try {
            val dto = remoteDataSource.getVehicleByPlate(plate)
            val maintenances = remoteDataSource.listMaintenances(dto.id)

            val vehicleEntity = dto.toEntity()
            val maintenanceEntities = maintenances.map { it.toEntity(dto.plate) }
            val photoEntities = maintenances.flatMap { it.toPhotoEntities(it.id) }

            vehicleDao.syncVehicleData(vehicleEntity, maintenanceEntities, photoEntities)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncVehicleById(vehicleId: String): Result<Unit> {
        return try {
            val dto = remoteDataSource.getVehicle(vehicleId)
            val maintenances = remoteDataSource.listMaintenances(vehicleId)

            val vehicleEntity = dto.toEntity()
            val maintenanceEntities = maintenances.map { it.toEntity(dto.plate) }
            val photoEntities = maintenances.flatMap { it.toPhotoEntities(it.id) }

            vehicleDao.syncVehicleData(vehicleEntity, maintenanceEntities, photoEntities)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addVehicle(vehicle: Vehicle) {
        val entity = vehicle.toPendingEntity()
        vehicleDao.insertVehicle(entity)
        outboxSyncService.enqueueVehicleCreate(entity)
    }

    override suspend fun updateVehicle(vehicle: Vehicle) {
        val entity = vehicle.toPendingEntity()
        vehicleDao.insertVehicle(entity)
        outboxSyncService.enqueueVehicleUpdate(vehicle, entity)
    }

    override suspend fun deleteVehicle(vehicle: Vehicle) {
        vehicleDao.deleteVehicleByPlate(vehicle.plate)
        outboxSyncService.enqueueVehicleDelete(vehicle)
    }
}
