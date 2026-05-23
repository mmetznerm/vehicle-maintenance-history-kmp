package com.mmetzner.vehiclemaintenance.core.di

import com.mmetzner.vehiclemaintenance.core.network.ApiConfig
import com.mmetzner.vehiclemaintenance.core.network.createHttpClient
import org.koin.dsl.module

val networkModule = module {
    single { ApiConfig() }
    single { createHttpClient() }
}
