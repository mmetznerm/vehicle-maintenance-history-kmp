package com.mmetzner.vehiclemaintenance.feature.vehicle.data

import com.mmetzner.vehiclemaintenance.core.util.randomUuid
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.dao.VehicleDao
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.MaintenanceEntity
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.SyncStatus
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.mapper.toPendingEntity
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.sync.OutboxSyncService
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Maintenance
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.repository.MaintenanceRepository

class MaintenanceRepositoryImpl(
    private val vehicleDao: VehicleDao,
    private val outboxSyncService: OutboxSyncService
) : MaintenanceRepository {

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
        outboxSyncService.enqueueMaintenanceCreate(entity)
    }

    override suspend fun updateMaintenance(
        vehiclePlate: String,
        fallbackVehicleId: String?,
        maintenance: Maintenance
    ) {
        val entity = maintenance.toPendingEntity(vehiclePlate, fallbackVehicleId)
        vehicleDao.insertMaintenances(listOf(entity))
        outboxSyncService.enqueueMaintenanceUpdate(
            vehiclePlate = vehiclePlate,
            fallbackVehicleId = fallbackVehicleId,
            maintenance = maintenance,
            entity = entity
        )
    }

    override suspend fun deleteMaintenance(
        vehiclePlate: String,
        fallbackVehicleId: String?,
        maintenance: Maintenance
    ) {
        vehicleDao.deleteMaintenanceById(maintenance.id)
        outboxSyncService.enqueueMaintenanceDelete(
            vehiclePlate = vehiclePlate,
            fallbackVehicleId = fallbackVehicleId,
            maintenance = maintenance
        )
    }
}
