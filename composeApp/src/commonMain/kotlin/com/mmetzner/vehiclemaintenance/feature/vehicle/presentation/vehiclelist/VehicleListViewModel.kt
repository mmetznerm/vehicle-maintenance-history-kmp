package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.vehiclelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmetzner.vehiclemaintenance.core.network.toVehicleSearchErrorMessage
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VehicleListViewModel(
    private val repository: VehicleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VehicleListState())
    val state: StateFlow<VehicleListState> = _state.asStateFlow()

    init {
        observeVehicles()
        refreshVehicles()
        syncPendingChanges()
    }

    private fun observeVehicles() {
        viewModelScope.launch {
            try {
                repository.observeVehicles().collect { vehicles ->
                    _state.update {
                        it.copy(
                            vehicles = vehicles,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Could not load your vehicles."
                    )
                }
            }
        }
    }

    fun refreshVehicles() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true, errorMessage = null) }

            val result = repository.syncVehicles()

            _state.update {
                if (result.isSuccess) {
                    it.copy(isRefreshing = false)
                } else {
                    it.copy(
                        isRefreshing = false,
                        isLoading = false,
                        errorMessage = result.exceptionOrNull().toVehicleSearchErrorMessage()
                    )
                }
            }
        }
    }

    fun syncPendingChanges() {
        viewModelScope.launch {
            repository.syncPendingOutbox()
        }
    }
}
