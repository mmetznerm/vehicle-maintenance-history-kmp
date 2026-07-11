package com.mmetzner.vehiclemaintenance.repository

import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Maintenance
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Vehicle
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeOfflineFirstRepository : VehicleRepository {
    val databaseFlow = MutableStateFlow<Vehicle?>(null)
    val databaseListFlow = MutableStateFlow<List<Vehicle>>(emptyList())
    var networkResult: Result<Unit> = Result.success(Unit)
    var syncCalled = false
    var syncVehiclesCalled = false
    var addedVehicle: Vehicle? = null
    var updatedVehicle: Vehicle? = null
    var deletedVehicle: Vehicle? = null
    var syncOutboxCalled = false
    var addedMaintenance: Maintenance? = null
    var addedMaintenanceVehiclePlate: String? = null
    var updatedMaintenance: Maintenance? = null
    var deletedMaintenance: Maintenance? = null
    var updatedMaintenanceVehiclePlate: String? = null
    var deletedMaintenanceVehiclePlate: String? = null

    override suspend fun observeVehicles(): Flow<List<Vehicle>> {
        return databaseListFlow
    }

    override suspend fun observeVehicle(plate: String): Flow<Vehicle?> {
        return databaseFlow
    }

    override suspend fun observeVehicleById(vehicleId: String): Flow<Vehicle?> {
        return databaseFlow
    }

    override suspend fun observePrimaryVehicle(): Flow<Vehicle?> {
        return databaseFlow
    }

    override suspend fun syncVehicle(plate: String): Result<Unit> {
        syncCalled = true
        return networkResult
    }

    override suspend fun syncVehicleById(vehicleId: String): Result<Unit> {
        syncCalled = true
        return networkResult
    }

    override suspend fun syncVehicles(): Result<Unit> {
        syncVehiclesCalled = true
        return networkResult
    }

    override suspend fun addVehicle(vehicle: Vehicle) {
        addedVehicle = vehicle
    }

    override suspend fun updateVehicle(vehicle: Vehicle) {
        updatedVehicle = vehicle
    }

    override suspend fun deleteVehicle(vehicle: Vehicle) {
        deletedVehicle = vehicle
    }

    override suspend fun addMaintenance(
        vehiclePlate: String,
        maintenance: Maintenance
    ) {
        addedMaintenanceVehiclePlate = vehiclePlate
        addedMaintenance = maintenance
    }

    override suspend fun updateMaintenance(
        vehiclePlate: String,
        fallbackVehicleId: String?,
        maintenance: Maintenance
    ) {
        updatedMaintenanceVehiclePlate = vehiclePlate
        updatedMaintenance = maintenance
    }

    override suspend fun deleteMaintenance(
        vehiclePlate: String,
        fallbackVehicleId: String?,
        maintenance: Maintenance
    ) {
        deletedMaintenanceVehiclePlate = vehiclePlate
        deletedMaintenance = maintenance
    }

    override suspend fun syncPendingOutbox() {
        syncOutboxCalled = true
    }
}




