package com.mmetzner.vehiclemaintenance.feature.auth.presentation.login

import org.jetbrains.compose.resources.StringResource

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: StringResource? = null,
    val isAuthenticated: Boolean = false
)
