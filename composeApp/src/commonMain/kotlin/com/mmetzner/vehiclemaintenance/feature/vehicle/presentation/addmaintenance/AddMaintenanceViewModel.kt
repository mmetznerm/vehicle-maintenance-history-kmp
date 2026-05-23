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
            is AddMaintenanceEvent.UpdateWorkshop -> _state.update { it.copy(workshopName = event.value) }
            is AddMaintenanceEvent.UpdateValue -> _state.update { it.copy(totalValue = event.value) }
            is AddMaintenanceEvent.SetPlate -> _state.update { it.copy(vehiclePlate = event.plate) }
            is AddMaintenanceEvent.Save -> saveMaintenance()
        }
    }

    private fun saveMaintenance() {
        val s = _state.value
        if (s.date.isBlank() || s.description.isBlank()) {
            _state.update { it.copy(error = "Data e descrição são obrigatórios.") }
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
                        description = s.description,
                        workshopName = s.workshopName,
                        mileage = s.mileage.toIntOrNull(),
                        totalValue = s.totalValue.toDoubleOrNull(),
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





