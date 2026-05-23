package com.mmetzner.vehiclemaintenance.feature.auth.data

import com.mmetzner.vehiclemaintenance.core.auth.AuthTokenStore
import com.mmetzner.vehiclemaintenance.core.auth.AuthTokens
import com.mmetzner.vehiclemaintenance.feature.auth.data.remote.AuthRemoteDataSource
import com.mmetzner.vehiclemaintenance.feature.auth.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource,
    private val tokenStore: AuthTokenStore
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val response = remoteDataSource.login(email = email, password = password)
            tokenStore.saveTokens(
                AuthTokens(
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        tokenStore.clear()
    }
}
