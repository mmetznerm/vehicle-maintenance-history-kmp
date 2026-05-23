package com.mmetzner.vehiclemaintenance.feature.vehicle.data

import com.mmetzner.vehiclemaintenance.core.util.randomUuid
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.dao.VehicleDao
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.MaintenanceEntity
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.SyncStatus
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.mapper.toDomain
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.mapper.toEntity
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.mapper.toPendingEntity
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.mapper.toPhotoEntities
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.mapper.toRequestDto
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote.VehicleRemoteDataSource
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Maintenance
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Vehicle
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.repository.VehicleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class VehicleRepositoryImpl(
    private val remoteDataSource: VehicleRemoteDataSource,
    private val vehicleDao: VehicleDao
) : VehicleRepository {

    private val syncScope = CoroutineScope(Dispatchers.IO)

    override suspend fun observeVehicle(plate: String): Flow<Vehicle?> {
        return vehicleDao.observeVehicleByPlate(plate).map { relation ->
            relation?.toDomain()
        }
    }

    override suspend fun syncVehicle(plate: String): Result<Unit> {
        return try {
            val dto = remoteDataSource.getVehicleByPlate(plate)

            val vehicleEntity = dto.toEntity()
            val maintenanceEntities = dto.maintenances?.map { it.toEntity(dto.plate) } ?: emptyList()
            val photoEntities = dto.maintenances?.flatMap { it.toPhotoEntities(it.id) } ?: emptyList()

            vehicleDao.syncVehicleData(vehicleEntity, maintenanceEntities, photoEntities)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addVehicle(vehicle: Vehicle) {
        val entity = vehicle.toPendingEntity()
        vehicleDao.insertVehicle(entity)

        syncScope.launch {
            syncPendingOutbox()
        }
    }

    override suspend fun addMaintenance(vehiclePlate: String, maintenance: Maintenance) {
        val entity = MaintenanceEntity(
            id = randomUuid(),
            vehiclePlate = vehiclePlate,
            date = maintenance.date,
            description = maintenance.description,
            workshopName = maintenance.workshopName,
            mileage = maintenance.mileage,
            totalValue = maintenance.totalValue,
            syncStatus = SyncStatus.PENDING
        )
        vehicleDao.insertMaintenances(listOf(entity))

        syncScope.launch {
            syncPendingOutbox()
        }
    }

    override suspend fun syncPendingOutbox() {
        try {
            val pendingVehicles = vehicleDao.getVehiclesByStatus(SyncStatus.PENDING)
            for (entity in pendingVehicles) {
                remoteDataSource.createVehicle(entity.toRequestDto())
                vehicleDao.updateVehicleSyncStatus(entity.plate, SyncStatus.SYNCED)
            }

            val pendingMaintenances = vehicleDao.getMaintenancesByStatus(SyncStatus.PENDING)
            for (entity in pendingMaintenances) {
                remoteDataSource.createMaintenance(entity.toRequestDto())
                vehicleDao.updateMaintenanceSyncStatus(entity.id, SyncStatus.SYNCED)
            }
        } catch (_: Exception) {
            // Retry metadata belongs in a dedicated outbox model, which will be introduced later.
        }
    }
}
