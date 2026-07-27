package com.mmetzner.vehiclemaintenance.feature.auth.presentation.register

import org.jetbrains.compose.resources.StringResource

data class RegisterState(
    val fullName: String = "",
    val emailOrPhone: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: StringResource? = null,
    val isAuthenticated: Boolean = false
)
