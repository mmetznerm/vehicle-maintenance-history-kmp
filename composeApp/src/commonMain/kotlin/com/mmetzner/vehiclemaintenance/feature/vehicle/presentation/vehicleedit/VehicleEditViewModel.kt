package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.vehicleedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmetzner.vehiclemaintenance.core.network.toVehicleSearchErrorMessage
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Vehicle
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VehicleEditViewModel(
    private val repository: VehicleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VehicleEditState())
    val state: StateFlow<VehicleEditState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<VehicleEditUiEvent>()
    val uiEvent: SharedFlow<VehicleEditUiEvent> = _uiEvent.asSharedFlow()

    private var loadedVehicleId: String? = null
    private var formInitialized = false

    fun load(vehicleId: String) {
        if (loadedVehicleId == vehicleId) return

        loadedVehicleId = vehicleId
        formInitialized = false
        _state.value = VehicleEditState(isLoading = true)

        observeVehicle(vehicleId)
        refresh(vehicleId)
    }

    private fun observeVehicle(vehicleId: String) {
        viewModelScope.launch {
            try {
                repository.observeVehicleById(vehicleId).collect { vehicle ->
                    val shouldInitialize = vehicle != null && !formInitialized
                    if (shouldInitialize) {
                        formInitialized = true
                    }

                    _state.update { current ->
                        current.copy(
                            vehicle = vehicle,
                            isLoading = false,
                            errorMessage = null,
                            plate = if (shouldInitialize) vehicle?.plate.orEmpty() else current.plate,
                            brand = if (shouldInitialize) vehicle?.brand.orEmpty() else current.brand,
                            model = if (shouldInitialize) vehicle?.model.orEmpty() else current.model,
                            year = if (shouldInitialize) vehicle?.year?.toString().orEmpty() else current.year,
                            color = if (shouldInitialize) vehicle?.color.orEmpty() else current.color
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Could not load vehicle."
                    )
                }
            }
        }
    }

    private fun refresh(vehicleId: String) {
        viewModelScope.launch {
            val result = repository.syncVehicleById(vehicleId)
            if (result.isFailure) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull().toVehicleSearchErrorMessage()
                    )
                }
            }
        }
    }

    fun onPlateChanged(value: String) {
        _state.update { it.copy(plate = value.uppercase().trim()) }
    }

    fun onBrandChanged(value: String) {
        _state.update { it.copy(brand = value) }
    }

    fun onModelChanged(value: String) {
        _state.update { it.copy(model = value) }
    }

    fun onYearChanged(value: String) {
        _state.update { it.copy(year = value.filter(Char::isDigit).take(4)) }
    }

    fun onColorChanged(value: String) {
        _state.update { it.copy(color = value) }
    }

    fun save() {
        val current = state.value
        val vehicle = current.vehicle

        if (vehicle == null) {
            _state.update { it.copy(errorMessage = "Veiculo nao encontrado.") }
            return
        }

        if (current.plate.isBlank() || current.brand.isBlank() || current.model.isBlank()) {
            _state.update { it.copy(errorMessage = "Placa, marca e modelo sao obrigatorios.") }
            return
        }

        val year = current.year.toIntOrNull()
        if (year == null) {
            _state.update { it.copy(errorMessage = "Informe um ano valido.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }

            try {
                repository.updateVehicle(
                    vehicle.copy(
                        plate = current.plate.uppercase().trim(),
                        brand = current.brand.trim(),
                        model = current.model.trim(),
                        year = year,
                        color = current.color.trim(),
                        isPendingSync = true
                    )
                )

                _state.update { it.copy(isSaving = false) }
                _uiEvent.emit(VehicleEditUiEvent.NavigateBack)
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = e.message ?: "Nao foi possivel salvar o veiculo."
                    )
                }
            }
        }
    }

    fun delete() {
        val vehicle = state.value.vehicle

        if (vehicle == null) {
            _state.update { it.copy(errorMessage = "Veiculo nao encontrado.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true, errorMessage = null) }

            try {
                repository.deleteVehicle(vehicle)
                _state.update { it.copy(isDeleting = false) }
                _uiEvent.emit(VehicleEditUiEvent.NavigateToList)
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isDeleting = false,
                        errorMessage = e.message ?: "Nao foi possivel excluir o veiculo."
                    )
                }
            }
        }
    }
}

data class VehicleEditState(
    val vehicle: Vehicle? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val errorMessage: String? = null,
    val plate: String = "",
    val brand: String = "",
    val model: String = "",
    val year: String = "",
    val color: String = ""
)

sealed interface VehicleEditUiEvent {
    data object NavigateBack : VehicleEditUiEvent
    data object NavigateToList : VehicleEditUiEvent
}
