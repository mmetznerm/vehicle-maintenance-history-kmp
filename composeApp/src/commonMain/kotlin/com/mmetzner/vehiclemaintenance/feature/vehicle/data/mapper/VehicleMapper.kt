package com.mmetzner.vehiclemaintenance.feature.vehicle.data.mapper

import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.MaintenanceEntity
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.MaintenancePhotoEntity
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.MaintenanceWithPhotos
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.SyncStatus
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.VehicleEntity
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.VehicleWithMaintenances
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote.dto.MaintenanceResponse
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote.dto.VehicleResponse
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Maintenance
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Vehicle

fun VehicleResponse.toEntity() = VehicleEntity(
    plate = this.plate,
    model = this.model,
    brand = this.brand,
    year = this.year
)

fun MaintenanceResponse.toEntity(vehiclePlate: String) = MaintenanceEntity(
    id = this.id,
    vehiclePlate = vehiclePlate,
    date = this.date,
    description = this.description,
    workshopName = this.workshopName,
    mileage = this.mileage,
    totalValue = this.totalValue
)

fun MaintenanceResponse.toPhotoEntities(maintenanceId: String): List<MaintenancePhotoEntity> {
    return this.photoUrls?.map { url ->
        MaintenancePhotoEntity(maintenanceId = maintenanceId, url = url)
    } ?: emptyList()
}

fun VehicleWithMaintenances.toDomain() = Vehicle(
    plate = this.vehicle.plate,
    model = this.vehicle.model,
    brand = this.vehicle.brand,
    year = this.vehicle.year,
    isPendingSync = this.vehicle.syncStatus == SyncStatus.PENDING,
    maintenances = this.maintenances.map { it.toDomain() }
)

fun MaintenanceWithPhotos.toDomain() = Maintenance(
    id = this.maintenance.id,
    date = this.maintenance.date,
    description = this.maintenance.description,
    workshopName = this.maintenance.workshopName,
    mileage = this.maintenance.mileage,
    totalValue = this.maintenance.totalValue,
    isPendingSync = this.maintenance.syncStatus == SyncStatus.PENDING,
    photoUrls = this.photos.map { it.url }
)

fun Vehicle.toPendingEntity() = VehicleEntity(
    plate = this.plate,
    model = this.model,
    brand = this.brand,
    year = this.year,
    syncStatus = SyncStatus.PENDING
)

fun VehicleEntity.toRequestDto() = VehicleResponse(
    plate = this.plate,
    model = this.model,
    brand = this.brand,
    year = this.year,
    maintenances = null
)

fun MaintenanceEntity.toRequestDto() = MaintenanceResponse(
    id = this.id,
    date = this.date,
    description = this.description,
    workshopName = this.workshopName,
    mileage = this.mileage,
    totalValue = this.totalValue,
    photoUrls = emptyList()
)



