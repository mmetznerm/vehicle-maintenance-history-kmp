package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation

import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.Vehicle
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeOfflineFirstRepository : VehicleRepository {
    // Simula a tabela do banco de dados
    val databaseFlow = MutableStateFlow<Vehicle?>(null)

    // Simula a resposta da rede
    var networkResult: Result<Unit> = Result.success(Unit)

    // Flag para sabermos se a viewmodel chamou a API
    var syncCalled = false

    override suspend fun observeVehicle(plate: String): Flow<Vehicle?> {
        return databaseFlow
    }

    override suspend fun syncVehicle(plate: String): Result<Unit> {
        syncCalled = true
        return networkResult
    }
}

