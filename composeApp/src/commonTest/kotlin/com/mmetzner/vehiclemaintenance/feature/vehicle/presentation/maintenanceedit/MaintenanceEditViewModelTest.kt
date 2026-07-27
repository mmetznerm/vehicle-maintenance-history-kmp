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
    fun `loads form from local maintenance cache`() = runTest {
        repository.databaseFlow.value = vehicleWithMaintenance()

        val viewModel = MaintenanceEditViewModel(repository)
        viewModel.load("vehicle-id", "maintenance-remote-id")
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
        assertEquals("Oil change", viewModel.state.value.description)
        assertEquals("2026-07-10", viewModel.state.value.date)
        assertEquals("45000", viewModel.state.value.mileage)
        assertEquals("250.0", viewModel.state.value.totalValue)
        assertTrue(repository.syncCalled)
    }

    @Test
    fun `saves local changes for later synchronization`() = runTest {
        repository.databaseFlow.value = vehicleWithMaintenance()

        val viewModel = MaintenanceEditViewModel(repository)
        viewModel.load("vehicle-id", "maintenance-remote-id")
        advanceUntilIdle()

        viewModel.onDescriptionChanged("Inspection")
        viewModel.onDateChanged("2026-07-11")
        viewModel.onMileageChanged("46000")
        viewModel.onTotalValueChanged("300,50")
        viewModel.save()
        advanceUntilIdle()

        val updatedMaintenance = repository.updatedMaintenance
        assertEquals("ABC1234", repository.updatedMaintenanceVehiclePlate)
        assertEquals("maintenance-local-id", updatedMaintenance?.id)
        assertEquals("maintenance-remote-id", updatedMaintenance?.remoteId)
        assertEquals("Inspection", updatedMaintenance?.description)
        assertEquals("2026-07-11", updatedMaintenance?.date)
        assertEquals(46000, updatedMaintenance?.mileage)
        assertEquals(300.50, updatedMaintenance?.totalValue)
        assertEquals(null, updatedMaintenance?.workshopName)
        assertEquals(true, updatedMaintenance?.isPendingSync)
    }

    @Test
    fun `deletes maintenance locally for later synchronization`() = runTest {
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
            description = "Oil change",
            workshopName = "Auto Center",
            mileage = 45000,
            totalValue = 250.0
        )
    )
)
