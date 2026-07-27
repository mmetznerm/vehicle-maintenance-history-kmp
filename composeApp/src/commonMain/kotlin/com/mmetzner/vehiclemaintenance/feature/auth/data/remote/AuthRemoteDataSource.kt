package com.mmetzner.vehiclemaintenance.feature.auth.data.remote

import com.mmetzner.vehiclemaintenance.core.network.ApiConfig
import com.mmetzner.vehiclemaintenance.core.network.toNetworkRequestException
import com.mmetzner.vehiclemaintenance.feature.auth.data.remote.dto.CreateAccountRequest
import com.mmetzner.vehiclemaintenance.feature.auth.data.remote.dto.LoginRequest
import com.mmetzner.vehiclemaintenance.feature.auth.data.remote.dto.LoginResponse
import com.mmetzner.vehiclemaintenance.feature.auth.data.remote.dto.LogoutRequest
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
            setBody(LoginRequest(emailOrPhone = email, password = password))
        }

        if (!response.status.isSuccess()) {
            throw response.status.toNetworkRequestException("Login")
        }

        return response.body()
    }

    suspend fun createAccount(
        fullName: String,
        emailOrPhone: String,
        password: String
    ): LoginResponse {
        val response = httpClient.post("${apiConfig.normalizedBaseUrl}/v1/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(
                CreateAccountRequest(
                    fullName = fullName,
                    emailOrPhone = emailOrPhone,
                    password = password
                )
            )
        }

        if (!response.status.isSuccess()) {
            throw response.status.toNetworkRequestException("Create account")
        }

        return response.body()
    }

    suspend fun logout(refreshToken: String) {
        val response = httpClient.post("${apiConfig.normalizedBaseUrl}/v1/auth/logout") {
            contentType(ContentType.Application.Json)
            setBody(LogoutRequest(refreshToken = refreshToken))
        }

        if (!response.status.isSuccess()) {
            throw response.status.toNetworkRequestException("Logout")
        }
    }
}
