package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.addmaintenance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Maintenance
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddMaintenanceViewModel(
    private val repository: VehicleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddMaintenanceState())
    val state: StateFlow<AddMaintenanceState> = _state.asStateFlow()

    fun onEvent(event: AddMaintenanceEvent) {
        when (event) {
            is AddMaintenanceEvent.UpdateDate -> _state.update { it.copy(date = event.value) }
            is AddMaintenanceEvent.UpdateMileage -> _state.update { it.copy(mileage = event.value) }
            is AddMaintenanceEvent.UpdateDescription -> _state.update { it.copy(description = event.value) }
            is AddMaintenanceEvent.UpdateValue -> _state.update { it.copy(totalValue = event.value) }
            is AddMaintenanceEvent.SetPlate -> _state.update { it.copy(vehiclePlate = event.plate) }
            is AddMaintenanceEvent.Save -> saveMaintenance()
        }
    }

    private fun saveMaintenance() {
        val s = _state.value
        val validationError = validateMaintenance(s)
        if (validationError != null) {
            _state.update { it.copy(error = validationError) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            try {
                repository.addMaintenance(
                    vehiclePlate = s.vehiclePlate,
                    maintenance = Maintenance(
                        id = "", // Repository will generate UUID
                        date = s.date.trim(),
                        description = s.description.trim(),
                        workshopName = null,
                        mileage = s.mileage.toIntOrNull(),
                        totalValue = s.totalValue.toCurrencyDoubleOrNull(),
                        isPendingSync = true
                    )
                )
                _state.update { it.copy(isSaving = false, success = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }
}

private fun validateMaintenance(state: AddMaintenanceState): String? {
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

private fun String.toCurrencyDoubleOrNull(): Double? {
    return filter { char ->
        char.isDigit() || char == '.' || char == ','
    }
        .replace(',', '.')
        .toDoubleOrNull()
}





