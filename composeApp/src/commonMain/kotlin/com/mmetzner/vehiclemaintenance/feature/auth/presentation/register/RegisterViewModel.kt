package com.mmetzner.vehiclemaintenance.feature.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmetzner.vehiclemaintenance.core.network.toLoginErrorMessage
import com.mmetzner.vehiclemaintenance.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import vehiclemaintenance.composeapp.generated.resources.*

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun onFullNameChanged(value: String) {
        _state.update { it.copy(fullName = value, errorMessage = null) }
    }

    fun onEmailOrPhoneChanged(value: String) {
        _state.update { it.copy(emailOrPhone = value, errorMessage = null) }
    }

    fun onPasswordChanged(value: String) {
        _state.update { it.copy(password = value, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun createAccount() {
        val currentState = _state.value
        val fullName = currentState.fullName.trim()
        val emailOrPhone = currentState.emailOrPhone.trim()
        val password = currentState.password

        if (fullName.isBlank() || emailOrPhone.isBlank() || password.isBlank()) {
            _state.update { it.copy(errorMessage = Res.string.error_register_required) }
            return
        }

        if (password.length < 8) {
            _state.update { it.copy(errorMessage = Res.string.error_password_too_short) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val result = authRepository.createAccount(
                fullName = fullName,
                emailOrPhone = emailOrPhone,
                password = password
            )

            _state.update {
                if (result.isSuccess) {
                    it.copy(isLoading = false, isAuthenticated = true)
                } else {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull().toLoginErrorMessage()
                    )
                }
            }
        }
    }
}
