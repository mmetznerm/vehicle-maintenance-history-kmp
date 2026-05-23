package com.mmetzner.vehiclemaintenance.feature.auth.domain.repository

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun logout()
}
