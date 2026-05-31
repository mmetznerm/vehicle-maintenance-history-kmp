package com.mmetzner.vehiclemaintenance.core.di

import com.mmetzner.vehiclemaintenance.core.auth.AndroidAuthTokenStore
import com.mmetzner.vehiclemaintenance.core.auth.AuthTokenStore
import com.mmetzner.vehiclemaintenance.core.database.DatabaseBuilder
import com.mmetzner.vehiclemaintenance.core.network.AppEnvironment
import com.mmetzner.vehiclemaintenance.core.network.HttpClientEngineFactory
import com.mmetzner.vehiclemaintenance.core.network.MockHttpClientEngineFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<AuthTokenStore> { AndroidAuthTokenStore(androidContext()) }
    single { DatabaseBuilder(androidContext()) }
    single { AppEnvironment(useMockData = com.mmetzner.vehiclemaintenance.BuildConfig.IS_MOCK) }
    single<HttpClientEngineFactory> {
        MockHttpClientEngineFactory(
            context = androidContext(),
            useMockData = com.mmetzner.vehiclemaintenance.BuildConfig.IS_MOCK
        )
    }
}
