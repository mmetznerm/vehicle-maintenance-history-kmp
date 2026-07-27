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
import org.jetbrains.compose.resources.StringResource
import vehiclemaintenance.composeapp.generated.resources.*

class AddVehicleViewModel(
    private val repository: VehicleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddVehicleState())
    val state: StateFlow<AddVehicleState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<AddVehicleUiEvent>()
    val uiEvent: SharedFlow<AddVehicleUiEvent> = _uiEvent.asSharedFlow()

    fun saveVehicle(plate: String, model: String, brand: String, yearStr: String, color: String) {
        if (plate.isBlank() || model.isBlank() || brand.isBlank()) {
            _state.update { it.copy(error = Res.string.error_vehicle_required_fields) }
            return
        }

        val year = yearStr.toIntOrNull()
        if (year == null) {
            _state.update { it.copy(error = Res.string.error_invalid_year) }
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
                        error = Res.string.error_save_vehicle
                    )
                }
            }
        }
    }
}

data class AddVehicleState(
    val isSaving: Boolean = false,
    val error: StringResource? = null
)

sealed interface AddVehicleUiEvent {
    data object NavigateBack : AddVehicleUiEvent
}

