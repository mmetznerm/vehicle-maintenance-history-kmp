package com.mmetzner.vehiclemaintenance.feature.vehicle.domain.repository

import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.Vehicle
import kotlinx.coroutines.flow.Flow

interface VehicleRepository {
    suspend fun observeVehicle(plate: String): Flow<Vehicle?>

    suspend fun syncVehicle(plate: String): Result<Unit>
}