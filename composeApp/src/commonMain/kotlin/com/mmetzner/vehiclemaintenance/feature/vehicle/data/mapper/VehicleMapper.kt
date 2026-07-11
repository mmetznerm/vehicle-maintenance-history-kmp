package com.mmetzner.vehiclemaintenance.feature.vehicle.data.mapper

import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.MaintenanceEntity
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.MaintenancePhotoEntity
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.MaintenanceWithPhotos
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.SyncStatus
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.VehicleEntity
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.VehicleWithMaintenances
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote.dto.CreateMaintenanceRequest
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote.dto.CreateVehicleRequest
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote.dto.MaintenanceResponse
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote.dto.VehicleResponse
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Maintenance
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Vehicle

fun VehicleResponse.toEntity() = VehicleEntity(
    plate = this.plate,
    id = this.id,
    model = this.model,
    brand = this.brand,
    year = this.manufactureYear,
    color = this.color.orEmpty()
)

fun MaintenanceResponse.toEntity(vehiclePlate: String) = MaintenanceEntity(
    id = this.id,
    vehicleId = this.vehicleId,
    vehiclePlate = vehiclePlate,
    date = this.maintenanceDate,
    description = this.description,
    workshopName = null,
    mileage = this.odometer,
    totalValue = this.cost
)

fun MaintenanceResponse.toPhotoEntities(maintenanceId: String): List<MaintenancePhotoEntity> {
    return emptyList()
}

fun VehicleWithMaintenances.toDomain() = Vehicle(
    plate = this.vehicle.plate,
    model = this.vehicle.model,
    brand = this.vehicle.brand,
    year = this.vehicle.year,
    isPendingSync = this.vehicle.syncStatus == SyncStatus.PENDING,
    maintenances = this.maintenances.map { it.toDomain() },
    id = this.vehicle.id,
    color = this.vehicle.color
)

fun MaintenanceWithPhotos.toDomain() = Maintenance(
    id = this.maintenance.id,
    date = this.maintenance.date,
    description = this.maintenance.description,
    workshopName = this.maintenance.workshopName,
    mileage = this.maintenance.mileage,
    totalValue = this.maintenance.totalValue,
    isPendingSync = this.maintenance.syncStatus == SyncStatus.PENDING,
    photoUrls = this.photos.map { it.url },
    vehicleId = this.maintenance.vehicleId
)

fun Vehicle.toPendingEntity() = VehicleEntity(
    plate = this.plate,
    id = this.id,
    model = this.model,
    brand = this.brand,
    year = this.year,
    color = this.color,
    syncStatus = SyncStatus.PENDING
)

fun VehicleEntity.toRequestDto() = CreateVehicleRequest(
    plate = this.plate,
    model = this.model,
    brand = this.brand,
    manufactureYear = this.year,
    color = this.color.ifBlank { null }
)

fun MaintenanceEntity.toRequestDto() = CreateMaintenanceRequest(
    maintenanceDate = this.date,
    odometer = this.mileage ?: 0,
    description = this.description,
    cost = this.totalValue ?: 0.0
)



