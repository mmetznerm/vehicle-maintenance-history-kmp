package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.maintenanceedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmetzner.vehiclemaintenance.core.network.toVehicleSearchErrorMessage
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Maintenance
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

class MaintenanceEditViewModel(
    private val repository: VehicleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MaintenanceEditState())
    val state: StateFlow<MaintenanceEditState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<MaintenanceEditUiEvent>()
    val uiEvent: SharedFlow<MaintenanceEditUiEvent> = _uiEvent.asSharedFlow()

    private var loadedVehicleId: String? = null
    private var loadedMaintenanceId: String? = null
    private var formInitialized = false

    fun load(vehicleId: String, maintenanceId: String) {
        if (loadedVehicleId == vehicleId && loadedMaintenanceId == maintenanceId) return

        loadedVehicleId = vehicleId
        loadedMaintenanceId = maintenanceId
        formInitialized = false
        _state.value = MaintenanceEditState(isLoading = true)

        observeVehicle(vehicleId, maintenanceId)
        refresh(vehicleId)
    }

    private fun observeVehicle(vehicleId: String, maintenanceId: String) {
        viewModelScope.launch {
            try {
                repository.observeVehicleById(vehicleId).collect { vehicle ->
                    val maintenance = vehicle?.maintenances
                        ?.firstOrNull { it.id == maintenanceId || it.remoteId == maintenanceId }
                    val shouldInitialize = vehicle != null && maintenance != null && !formInitialized
                    if (shouldInitialize) {
                        formInitialized = true
                    }
                    val initialMaintenance = maintenance.takeIf { shouldInitialize }

                    _state.update { current ->
                        current.copy(
                            vehicle = vehicle,
                            maintenance = maintenance,
                            isLoading = false,
                            errorMessage = null,
                            description = initialMaintenance?.description ?: current.description,
                            date = initialMaintenance?.date ?: current.date,
                            mileage = initialMaintenance?.mileage?.toString() ?: current.mileage,
                            totalValue = initialMaintenance?.totalValue?.toString() ?: current.totalValue
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Could not load maintenance."
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

    fun onDescriptionChanged(value: String) {
        _state.update { it.copy(description = value) }
    }

    fun onDateChanged(value: String) {
        _state.update { it.copy(date = value) }
    }

    fun onMileageChanged(value: String) {
        _state.update { it.copy(mileage = value.filter(Char::isDigit)) }
    }

    fun onTotalValueChanged(value: String) {
        _state.update { it.copy(totalValue = value) }
    }

    fun save() {
        val current = state.value
        val vehicle = current.vehicle
        val maintenance = current.maintenance

        if (vehicle == null || maintenance == null) {
            _state.update { it.copy(errorMessage = "Manutencao nao encontrada.") }
            return
        }

        val validationError = validateMaintenance(current)
        if (validationError != null) {
            _state.update { it.copy(errorMessage = validationError) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }

            try {
                repository.updateMaintenance(
                    vehiclePlate = vehicle.plate,
                    fallbackVehicleId = vehicle.id,
                    maintenance = maintenance.copy(
                        date = current.date.trim(),
                        description = current.description.trim(),
                        workshopName = null,
                        mileage = current.mileage.toIntOrNull(),
                        totalValue = current.totalValue.toCurrencyDoubleOrNull(),
                        isPendingSync = true
                    )
                )

                _state.update { it.copy(isSaving = false) }
                _uiEvent.emit(MaintenanceEditUiEvent.NavigateBack)
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = e.message ?: "Nao foi possivel salvar a manutencao."
                    )
                }
            }
        }
    }

    fun delete() {
        val vehicle = state.value.vehicle
        val maintenance = state.value.maintenance

        if (vehicle == null || maintenance == null) {
            _state.update { it.copy(errorMessage = "Manutencao nao encontrada.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true, errorMessage = null) }

            try {
                repository.deleteMaintenance(
                    vehiclePlate = vehicle.plate,
                    fallbackVehicleId = vehicle.id,
                    maintenance = maintenance
                )

                _state.update { it.copy(isDeleting = false) }
                _uiEvent.emit(MaintenanceEditUiEvent.NavigateBack)
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isDeleting = false,
                        errorMessage = e.message ?: "Nao foi possivel excluir a manutencao."
                    )
                }
            }
        }
    }
}

data class MaintenanceEditState(
    val vehicle: Vehicle? = null,
    val maintenance: Maintenance? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val errorMessage: String? = null,
    val description: String = "",
    val date: String = "",
    val mileage: String = "",
    val totalValue: String = ""
)

sealed interface MaintenanceEditUiEvent {
    data object NavigateBack : MaintenanceEditUiEvent
}

private fun String.toCurrencyDoubleOrNull(): Double? {
    return filter { char ->
        char.isDigit() || char == '.' || char == ','
    }
        .replace(',', '.')
        .toDoubleOrNull()
}

private fun validateMaintenance(state: MaintenanceEditState): String? {
    val mileage = state.mileage.toIntOrNull()
    val totalValue = state.totalValue.toCurrencyDoubleOrNull()

    return when {
        state.date.isBlank() -> "Informe a data da manutencao."
        state.mileage.isBlank() -> "Informe o odometro."
        mileage == null || mileage < 0 -> "Informe um odometro valido."
        state.totalValue.isBlank() -> "Informe o custo total."
        totalValue == null || totalValue < 0.0 -> "Informe um custo valido."
        state.description.isBlank() -> "Informe a descricao do servico."
        state.description.trim().length > 500 -> "Use no maximo 500 caracteres."
        else -> null
    }
}
