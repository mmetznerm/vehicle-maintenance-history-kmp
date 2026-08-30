package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.repository.SyncRepository
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vehiclemaintenance.composeapp.generated.resources.*

class VehicleHomeViewModel(
    private val repository: VehicleRepository,
    private val syncRepository: SyncRepository
) : ViewModel() {

    private val _state = MutableStateFlow<VehicleHomeState>(VehicleHomeState.Loading)
    val state: StateFlow<VehicleHomeState> = _state.asStateFlow()

    init {
        observeVehicle()
        syncPendingChanges()
    }

    private fun observeVehicle() {
        viewModelScope.launch {
            try {
                repository.observePrimaryVehicle().collect { vehicle ->
                    _state.value = if (vehicle == null) {
                        VehicleHomeState.Empty
                    } else {
                        VehicleHomeState.Content(vehicle)
                    }
                }
            } catch (e: Exception) {
                _state.value = VehicleHomeState.Error(
                    Res.string.error_load_primary_vehicle
                )
            }
        }
    }

    fun syncPendingChanges() {
        viewModelScope.launch {
            syncRepository.syncPendingOutbox()
        }
    }
}
