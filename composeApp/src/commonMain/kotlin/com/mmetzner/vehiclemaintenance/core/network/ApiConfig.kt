package com.mmetzner.vehiclemaintenance.core.network

data class ApiConfig(
    val baseUrl: String = defaultApiBaseUrl()
) {
    val normalizedBaseUrl: String = baseUrl.trimEnd('/')
}

expect fun defaultApiBaseUrl(): String
