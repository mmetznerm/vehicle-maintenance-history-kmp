package com.mmetzner.vehiclemaintenance.feature.vehicle.data.mapper

import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.MaintenanceEntity
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.MaintenancePhotoEntity
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.MaintenanceWithPhotos
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.VehicleEntity
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.VehicleWithMaintenances
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote.MaintenanceResponse
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote.VehicleResponse
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.Maintenance
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.Vehicle

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
    workshopName = this.workshopName
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
    maintenances = this.maintenances.map { it.toDomain() }
)

fun MaintenanceWithPhotos.toDomain() = Maintenance(
    id = this.maintenance.id,
    date = this.maintenance.date,
    description = this.maintenance.description,
    workshopName = this.maintenance.workshopName,
    photoUrls = this.photos.map { it.url }
)