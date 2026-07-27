package com.mmetzner.vehiclemaintenance.core.ui.preview

import com.mmetzner.vehiclemaintenance.feature.auth.domain.repository.AuthRepository
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Maintenance
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Vehicle
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal const val PreviewVehicleId = "preview-vehicle-id"
internal const val PreviewMaintenanceId = "preview-maintenance-id"

internal val PreviewVehicle = Vehicle(
    id = PreviewVehicleId,
    plate = "ABC1D23",
    brand = "Toyota",
    model = "Corolla",
    year = 2022,
    color = "Silver",
    maintenances = listOf(
        Maintenance(
            id = "preview-local-maintenance-id",
            remoteId = PreviewMaintenanceId,
            vehicleId = PreviewVehicleId,
            date = "2026-07-18",
            description = "Oil and filter change",
            workshopName = "Auto Center",
            mileage = 45_200,
            totalValue = 289.90
        ),
        Maintenance(
            id = "preview-second-maintenance-id",
            vehicleId = PreviewVehicleId,
            date = "2026-03-10",
            description = "Tire rotation and wheel alignment",
            workshopName = "Garage Service",
            mileage = 39_850,
            totalValue = 180.00
        )
    )
)

internal object PreviewAuthRepository : AuthRepository {
    override suspend fun hasActiveSession(): Boolean = false

    override suspend fun login(email: String, password: String): Result<Unit> =
        Result.success(Unit)

    override suspend fun createAccount(
        fullName: String,
        emailOrPhone: String,
        password: String
    ): Result<Unit> = Result.success(Unit)

    override suspend fun logout() = Unit
}

internal object PreviewVehicleRepository : VehicleRepository {
    override suspend fun observeVehicles(): Flow<List<Vehicle>> =
        flowOf(listOf(PreviewVehicle))

    override suspend fun observePrimaryVehicle(): Flow<Vehicle?> =
        flowOf(PreviewVehicle)

    override suspend fun observeVehicle(plate: String): Flow<Vehicle?> =
        flowOf(PreviewVehicle)

    override suspend fun observeVehicleById(vehicleId: String): Flow<Vehicle?> =
        flowOf(PreviewVehicle)

    override suspend fun syncVehicles(): Result<Unit> = Result.success(Unit)

    override suspend fun syncVehicle(plate: String): Result<Unit> = Result.success(Unit)

    override suspend fun syncVehicleById(vehicleId: String): Result<Unit> =
        Result.success(Unit)

    override suspend fun addVehicle(vehicle: Vehicle) = Unit

    override suspend fun updateVehicle(vehicle: Vehicle) = Unit

    override suspend fun deleteVehicle(vehicle: Vehicle) = Unit

    override suspend fun addMaintenance(
        vehiclePlate: String,
        maintenance: Maintenance
    ) = Unit

    override suspend fun updateMaintenance(
        vehiclePlate: String,
        fallbackVehicleId: String?,
        maintenance: Maintenance
    ) = Unit

    override suspend fun deleteMaintenance(
        vehiclePlate: String,
        fallbackVehicleId: String?,
        maintenance: Maintenance
    ) = Unit

    override suspend fun syncPendingOutbox() = Unit
}
