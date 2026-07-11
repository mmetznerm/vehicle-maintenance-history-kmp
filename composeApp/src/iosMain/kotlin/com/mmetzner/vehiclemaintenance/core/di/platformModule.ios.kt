package com.mmetzner.vehiclemaintenance.core.di

import com.mmetzner.vehiclemaintenance.core.auth.AuthTokenStore
import com.mmetzner.vehiclemaintenance.core.auth.IosAuthTokenStore
import com.mmetzner.vehiclemaintenance.core.database.DatabaseBuilder
import com.mmetzner.vehiclemaintenance.core.sync.NoOpOutboxSyncRequestScheduler
import com.mmetzner.vehiclemaintenance.core.sync.OutboxSyncRequestScheduler
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<AuthTokenStore> { IosAuthTokenStore() }
    single { DatabaseBuilder() }
    single<OutboxSyncRequestScheduler> { NoOpOutboxSyncRequestScheduler() }
}
