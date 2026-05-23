package com.mmetzner.vehiclemaintenance.feature.auth.data.remote

import com.mmetzner.vehiclemaintenance.core.network.ApiConfig
import com.mmetzner.vehiclemaintenance.feature.auth.data.remote.dto.LoginRequest
import com.mmetzner.vehiclemaintenance.feature.auth.data.remote.dto.LoginResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class AuthRemoteDataSource(
    private val httpClient: HttpClient,
    private val apiConfig: ApiConfig
) {
    suspend fun login(email: String, password: String): LoginResponse {
        val response = httpClient.post("${apiConfig.normalizedBaseUrl}/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email = email, password = password))
        }

        if (!response.status.isSuccess()) {
            throw AuthRequestException("Login failed. Code: ${response.status.value}")
        }

        return response.body()
    }
}

class AuthRequestException(message: String) : Exception(message)
