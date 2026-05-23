package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.repository.VehicleRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VehicleSearchViewModel(
    private val repository: VehicleRepository
) : ViewModel() {

    private val _state = MutableStateFlow<VehicleSearchState>(VehicleSearchState.Idle)
    val state: StateFlow<VehicleSearchState> = _state.asStateFlow()

    private var searchJob: Job? = null

    fun searchVehicle(plate: String) {
        if (plate.isBlank()) return

        searchJob?.cancel()
        _state.value = VehicleSearchState.Loading

        searchJob = viewModelScope.launch {
            launch {
                repository.observeVehicle(plate).collect { cachedVehicle ->
                    if (cachedVehicle != null) {
                        _state.value = VehicleSearchState.Success(cachedVehicle)
                    }
                }
            }

            val syncResult = repository.syncVehicle(plate)

            if (syncResult.isFailure) {
                if (_state.value !is VehicleSearchState.Success) {
                    _state.value = VehicleSearchState.Error("Você está offline e este veículo não está no cache local.")
                }
            }
        }
    }
}


