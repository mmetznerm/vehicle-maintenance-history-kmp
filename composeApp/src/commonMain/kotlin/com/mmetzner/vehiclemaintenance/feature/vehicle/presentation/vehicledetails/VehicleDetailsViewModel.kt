package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.vehicledetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmetzner.vehiclemaintenance.core.network.toVehicleSearchErrorMessage
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VehicleDetailsViewModel(
    private val repository: VehicleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VehicleDetailsState())
    val state: StateFlow<VehicleDetailsState> = _state.asStateFlow()

    private var vehicleId: String? = null

    fun load(vehicleId: String) {
        if (this.vehicleId == vehicleId) return

        this.vehicleId = vehicleId
        observeVehicle(vehicleId)
        refresh()
    }

    private fun observeVehicle(vehicleId: String) {
        viewModelScope.launch {
            try {
                repository.observeVehicleById(vehicleId).collect { vehicle ->
                    _state.update {
                        it.copy(
                            vehicle = vehicle,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Could not load vehicle details."
                    )
                }
            }
        }
    }

    fun refresh() {
        val id = vehicleId ?: return

        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true, errorMessage = null) }

            val result = repository.syncVehicleById(id)

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
}
