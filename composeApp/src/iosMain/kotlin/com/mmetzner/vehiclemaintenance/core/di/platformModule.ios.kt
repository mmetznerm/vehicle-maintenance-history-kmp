package com.mmetzner.vehiclemaintenance.core.di

import com.mmetzner.vehiclemaintenance.core.auth.AuthTokenStore
import com.mmetzner.vehiclemaintenance.core.auth.IosAuthTokenStore
import com.mmetzner.vehiclemaintenance.core.database.DatabaseBuilder
import com.mmetzner.vehiclemaintenance.core.network.AppEnvironment
import com.mmetzner.vehiclemaintenance.core.network.HttpClientEngineFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<AuthTokenStore> { IosAuthTokenStore() }
    single { DatabaseBuilder() }
    single { AppEnvironment(useMockData = false) }
    single<HttpClientEngineFactory> {
        object : HttpClientEngineFactory {
            override fun create() = null
        }
    }
}
