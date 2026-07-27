package com.mmetzner.vehiclemaintenance.feature.auth.data

import com.mmetzner.vehiclemaintenance.core.auth.AuthTokenStore
import com.mmetzner.vehiclemaintenance.core.auth.AuthTokens
import com.mmetzner.vehiclemaintenance.feature.auth.data.remote.AuthRemoteDataSource
import com.mmetzner.vehiclemaintenance.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.CancellationException

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource,
    private val tokenStore: AuthTokenStore
) : AuthRepository {

    override suspend fun hasActiveSession(): Boolean {
        return tokenStore.getTokens() != null
    }

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

    override suspend fun createAccount(
        fullName: String,
        emailOrPhone: String,
        password: String
    ): Result<Unit> {
        return try {
            val response = remoteDataSource.createAccount(
                fullName = fullName,
                emailOrPhone = emailOrPhone,
                password = password
            )
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
        val refreshToken = tokenStore.getTokens()?.refreshToken

        try {
            if (!refreshToken.isNullOrBlank()) {
                remoteDataSource.logout(refreshToken)
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: Exception) {
            // Local sign-out must still succeed when the server is unavailable.
        } finally {
            tokenStore.clear()
        }
    }
}
