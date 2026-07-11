package com.mmetzner.vehiclemaintenance.core.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.repository.VehicleRepository
import org.koin.core.context.GlobalContext

class OutboxSyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val repository = GlobalContext.get().get<VehicleRepository>()
            repository.syncPendingOutbox()
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount >= MAX_RUN_ATTEMPTS) {
                Result.failure()
            } else {
                Result.retry()
            }
        }
    }

    private companion object {
        const val MAX_RUN_ATTEMPTS = 5
    }
}
