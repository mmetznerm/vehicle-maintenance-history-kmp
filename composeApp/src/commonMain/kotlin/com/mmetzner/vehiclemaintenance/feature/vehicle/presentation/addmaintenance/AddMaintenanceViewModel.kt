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
            is AddMaintenanceEvent.UpdateServiceType -> _state.update { it.copy(serviceType = event.value, description = event.value) }
            is AddMaintenanceEvent.UpdateDate -> _state.update { it.copy(date = event.value) }
            is AddMaintenanceEvent.UpdateMileage -> _state.update { it.copy(mileage = event.value) }
            is AddMaintenanceEvent.UpdateDescription -> _state.update { it.copy(description = event.value) }
            is AddMaintenanceEvent.UpdateReplacedParts -> _state.update { it.copy(replacedParts = event.value) }
            is AddMaintenanceEvent.UpdateNotes -> _state.update { it.copy(notes = event.value) }
            is AddMaintenanceEvent.UpdateWorkshop -> _state.update { it.copy(workshopName = event.value) }
            is AddMaintenanceEvent.UpdateValue -> _state.update { it.copy(totalValue = event.value) }
            is AddMaintenanceEvent.SetPlate -> _state.update { it.copy(vehiclePlate = event.plate) }
            is AddMaintenanceEvent.Save -> saveMaintenance()
        }
    }

    private fun saveMaintenance() {
        val s = _state.value
        if (s.date.isBlank() || s.serviceType.isBlank()) {
            _state.update { it.copy(error = "Service type and date are required.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            try {
                repository.addMaintenance(
                    vehiclePlate = s.vehiclePlate,
                    maintenance = Maintenance(
                        id = "", // Repository will generate UUID
                        date = s.date,
                        description = s.toMaintenanceDescription(),
                        workshopName = s.workshopName,
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

private fun String.toCurrencyDoubleOrNull(): Double? {
    return filter { char ->
        char.isDigit() || char == '.' || char == ','
    }
        .replace(',', '.')
        .toDoubleOrNull()
}

private fun AddMaintenanceState.toMaintenanceDescription(): String {
    return buildString {
        append(serviceType.ifBlank { description })

        if (replacedParts.isNotBlank()) {
            append(" | Parts: ")
            append(replacedParts.trim())
        }

        if (notes.isNotBlank()) {
            append(" | Notes: ")
            append(notes.trim())
        }
    }
}





