package com.mmetzner.vehiclemaintenance

import android.app.Application
import com.mmetzner.vehiclemaintenance.core.di.initKoin
import com.mmetzner.vehiclemaintenance.core.sync.OutboxSyncRequestScheduler
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext

class VehicleMaintenanceApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@VehicleMaintenanceApp)
        }

        val syncScheduler = GlobalContext.get().get<OutboxSyncRequestScheduler>()
        syncScheduler.startPeriodicSync()
        syncScheduler.requestSync()
    }
}


