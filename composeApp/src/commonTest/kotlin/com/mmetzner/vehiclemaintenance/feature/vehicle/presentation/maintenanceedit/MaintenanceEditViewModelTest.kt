package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.maintenanceedit

import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Maintenance
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Vehicle
import com.mmetzner.vehiclemaintenance.repository.FakeOfflineFirstRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MaintenanceEditViewModelTest {

    private lateinit var repository: FakeOfflineFirstRepository
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeOfflineFirstRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `quando existe manutencao local deve preencher formulario pelo cache`() = runTest {
        repository.databaseFlow.value = vehicleWithMaintenance()

        val viewModel = MaintenanceEditViewModel(repository)
        viewModel.load("vehicle-id", "maintenance-remote-id")
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
        assertEquals("Troca de oleo", viewModel.state.value.serviceType)
        assertEquals("2026-07-10", viewModel.state.value.date)
        assertEquals("45000", viewModel.state.value.mileage)
        assertEquals("250.0", viewModel.state.value.totalValue)
        assertTrue(repository.syncCalled)
    }

    @Test
    fun `ao salvar deve persistir alteracao local para sincronizar depois`() = runTest {
        repository.databaseFlow.value = vehicleWithMaintenance()

        val viewModel = MaintenanceEditViewModel(repository)
        viewModel.load("vehicle-id", "maintenance-remote-id")
        advanceUntilIdle()

        viewModel.onServiceTypeChanged("Revisao")
        viewModel.onDateChanged("2026-07-11")
        viewModel.onMileageChanged("46000")
        viewModel.onTotalValueChanged("300,50")
        viewModel.save()
        advanceUntilIdle()

        val updatedMaintenance = repository.updatedMaintenance
        assertEquals("ABC1234", repository.updatedMaintenanceVehiclePlate)
        assertEquals("maintenance-local-id", updatedMaintenance?.id)
        assertEquals("maintenance-remote-id", updatedMaintenance?.remoteId)
        assertEquals("Revisao", updatedMaintenance?.description)
        assertEquals("2026-07-11", updatedMaintenance?.date)
        assertEquals(46000, updatedMaintenance?.mileage)
        assertEquals(300.50, updatedMaintenance?.totalValue)
        assertEquals(true, updatedMaintenance?.isPendingSync)
    }

    @Test
    fun `ao excluir deve remover manutencao local para sincronizar depois`() = runTest {
        val vehicle = vehicleWithMaintenance()
        repository.databaseFlow.value = vehicle

        val viewModel = MaintenanceEditViewModel(repository)
        viewModel.load("vehicle-id", "maintenance-remote-id")
        advanceUntilIdle()

        viewModel.delete()
        advanceUntilIdle()

        assertEquals("ABC1234", repository.deletedMaintenanceVehiclePlate)
        assertEquals(vehicle.maintenances?.first(), repository.deletedMaintenance)
    }
}

private fun vehicleWithMaintenance() = Vehicle(
    plate = "ABC1234",
    model = "Civic",
    brand = "Honda",
    year = 2022,
    id = "vehicle-id",
    maintenances = listOf(
        Maintenance(
            id = "maintenance-local-id",
            remoteId = "maintenance-remote-id",
            vehicleId = "vehicle-id",
            date = "2026-07-10",
            description = "Troca de oleo",
            workshopName = "Auto Center",
            mileage = 45000,
            totalValue = 250.0
        )
    )
)
