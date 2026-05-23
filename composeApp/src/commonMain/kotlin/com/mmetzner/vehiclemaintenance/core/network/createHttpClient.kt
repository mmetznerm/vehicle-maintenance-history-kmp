package com.mmetzner.vehiclemaintenance.core.network

import com.mmetzner.vehiclemaintenance.core.auth.AuthTokenStore
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.HttpTimeout
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun createHttpClient(
    authTokenStore: AuthTokenStore,
    engine: HttpClientEngine? = null
): HttpClient {
    val config: HttpClientConfigBlock = {
        install(Auth) {
            bearer {
                loadTokens {
                    authTokenStore.getTokens()?.let { tokens ->
                        BearerTokens(
                            accessToken = tokens.accessToken,
                            refreshToken = tokens.refreshToken.orEmpty()
                        )
                    }
                }
                sendWithoutRequest { request ->
                    !request.url.toString().endsWith("/v1/auth/login")
                }
            }
        }

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 15_000
        }
    }

    return if (engine != null) {
        HttpClient(engine, config)
    } else {
        HttpClient(config)
    }
}

private typealias HttpClientConfigBlock = io.ktor.client.HttpClientConfig<*>.() -> Unit
