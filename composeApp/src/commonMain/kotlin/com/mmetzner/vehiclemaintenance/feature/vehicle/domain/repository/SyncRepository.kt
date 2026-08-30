package com.mmetzner.vehiclemaintenance.feature.vehicle.domain.repository

interface SyncRepository {
    suspend fun syncPendingOutbox()
}
