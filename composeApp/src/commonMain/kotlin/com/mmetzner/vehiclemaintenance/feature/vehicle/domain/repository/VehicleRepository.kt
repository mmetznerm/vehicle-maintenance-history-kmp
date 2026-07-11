package com.mmetzner.vehiclemaintenance.feature.vehicle.domain.repository

import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Maintenance
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Vehicle
import kotlinx.coroutines.flow.Flow

interface VehicleRepository {
    suspend fun observeVehicles(): Flow<List<Vehicle>>
    suspend fun observePrimaryVehicle(): Flow<Vehicle?>
    suspend fun observeVehicle(plate: String): Flow<Vehicle?>
    suspend fun observeVehicleById(vehicleId: String): Flow<Vehicle?>
    suspend fun syncVehicles(): Result<Unit>
    suspend fun syncVehicle(plate: String): Result<Unit>
    suspend fun syncVehicleById(vehicleId: String): Result<Unit>
    suspend fun addVehicle(vehicle: Vehicle)
    suspend fun addMaintenance(vehiclePlate: String, maintenance: Maintenance)
    suspend fun syncPendingOutbox()
}


