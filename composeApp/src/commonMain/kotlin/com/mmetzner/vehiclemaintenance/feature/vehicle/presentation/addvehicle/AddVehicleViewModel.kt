package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.addvehicle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Vehicle
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddVehicleViewModel(
    private val repository: VehicleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddVehicleState())
    val state: StateFlow<AddVehicleState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<AddVehicleUiEvent>()
    val uiEvent: SharedFlow<AddVehicleUiEvent> = _uiEvent.asSharedFlow()

    fun saveVehicle(plate: String, model: String, brand: String, yearStr: String, color: String) {
        if (plate.isBlank() || model.isBlank() || brand.isBlank()) {
            _state.update { it.copy(error = "Placa, marca e modelo são obrigatórios.") }
            return
        }

        val year = yearStr.toIntOrNull()
        if (year == null) {
            _state.update { it.copy(error = "Informe um ano válido.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }

            try {
                val newVehicle = Vehicle(
                    plate = plate.uppercase().trim(),
                    model = model.trim(),
                    brand = brand.trim(),
                    year = year,
                    maintenances = emptyList(),
                    color = color.trim()
                )

                repository.addVehicle(newVehicle)

                _state.update { it.copy(isSaving = false) }
                _uiEvent.emit(AddVehicleUiEvent.NavigateBack)
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSaving = false,
                        error = e.message ?: "Não foi possível salvar o veículo."
                    )
                }
            }
        }
    }
}

data class AddVehicleState(
    val isSaving: Boolean = false,
    val error: String? = null
)

sealed interface AddVehicleUiEvent {
    data object NavigateBack : AddVehicleUiEvent
}


