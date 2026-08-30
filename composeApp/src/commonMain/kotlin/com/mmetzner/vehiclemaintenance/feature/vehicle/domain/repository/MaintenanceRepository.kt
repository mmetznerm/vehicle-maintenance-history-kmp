package com.mmetzner.vehiclemaintenance.feature.vehicle.domain.repository

import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Maintenance

interface MaintenanceRepository {
    suspend fun addMaintenance(vehiclePlate: String, maintenance: Maintenance)
    suspend fun updateMaintenance(
        vehiclePlate: String,
        fallbackVehicleId: String?,
        maintenance: Maintenance
    )
    suspend fun deleteMaintenance(
        vehiclePlate: String,
        fallbackVehicleId: String?,
        maintenance: Maintenance
    )
}
