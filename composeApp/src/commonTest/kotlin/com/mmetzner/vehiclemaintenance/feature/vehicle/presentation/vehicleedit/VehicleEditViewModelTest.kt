package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.vehicleedit

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
class VehicleEditViewModelTest {

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
    fun `loads form from local vehicle cache`() = runTest {
        repository.databaseFlow.value = Vehicle(
            plate = "ABC1234",
            model = "Civic",
            brand = "Honda",
            year = 2022,
            maintenances = emptyList(),
            id = "vehicle-id",
            color = "Silver"
        )

        val viewModel = VehicleEditViewModel(repository)
        viewModel.load("vehicle-id")
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
        assertEquals("ABC1234", viewModel.state.value.plate)
        assertEquals("Honda", viewModel.state.value.brand)
        assertEquals("Civic", viewModel.state.value.model)
        assertEquals("2022", viewModel.state.value.year)
        assertEquals("Silver", viewModel.state.value.color)
        assertTrue(repository.syncCalled)
    }

    @Test
    fun `saves local changes for later synchronization`() = runTest {
        repository.databaseFlow.value = Vehicle(
            plate = "ABC1234",
            model = "Civic",
            brand = "Honda",
            year = 2022,
            maintenances = emptyList(),
            id = "vehicle-id",
            color = "Silver"
        )

        val viewModel = VehicleEditViewModel(repository)
        viewModel.load("vehicle-id")
        advanceUntilIdle()

        viewModel.onModelChanged("Corolla")
        viewModel.onBrandChanged("Toyota")
        viewModel.onYearChanged("2024")
        viewModel.onColorChanged("White")
        viewModel.save()
        advanceUntilIdle()

        val updatedVehicle = repository.updatedVehicle
        assertEquals("vehicle-id", updatedVehicle?.id)
        assertEquals("Toyota", updatedVehicle?.brand)
        assertEquals("Corolla", updatedVehicle?.model)
        assertEquals(2024, updatedVehicle?.year)
        assertEquals("White", updatedVehicle?.color)
        assertEquals(true, updatedVehicle?.isPendingSync)
    }

    @Test
    fun `deletes vehicle locally for later synchronization`() = runTest {
        val vehicle = Vehicle(
            plate = "ABC1234",
            model = "Civic",
            brand = "Honda",
            year = 2022,
            maintenances = emptyList(),
            id = "vehicle-id"
        )
        repository.databaseFlow.value = vehicle

        val viewModel = VehicleEditViewModel(repository)
        viewModel.load("vehicle-id")
        advanceUntilIdle()

        viewModel.delete()
        advanceUntilIdle()

        assertEquals(vehicle, repository.deletedVehicle)
    }
}
