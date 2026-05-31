package com.mmetzner.vehiclemaintenance.core.network

import io.ktor.client.engine.HttpClientEngine

interface HttpClientEngineFactory {
    fun create(): HttpClientEngine?
}

data class AppEnvironment(
    val useMockData: Boolean = false
)
