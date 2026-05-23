package com.mmetzner.vehiclemaintenance.feature.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmetzner.vehiclemaintenance.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onEmailChanged(value: String) {
        _state.update { it.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChanged(value: String) {
        _state.update { it.copy(password = value, errorMessage = null) }
    }

    fun login() {
        val currentState = _state.value
        val email = currentState.email.trim()
        val password = currentState.password

        if (email.isBlank() || password.isBlank()) {
            _state.update { it.copy(errorMessage = "Email and password are required.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val result = authRepository.login(email = email, password = password)

            _state.update {
                if (result.isSuccess) {
                    it.copy(isLoading = false, isAuthenticated = true)
                } else {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Could not sign in. Check your credentials and try again."
                    )
                }
            }
        }
    }
}
