package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.vehicledetails

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
import vehiclemaintenance.composeapp.generated.resources.*

@OptIn(ExperimentalCoroutinesApi::class)
class VehicleDetailsViewModelTest {

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
    fun `shows vehicle details from local cache`() = runTest {
        repository.databaseFlow.value = Vehicle(
            plate = "ABC1234",
            model = "Civic",
            brand = "Honda",
            year = 2022,
            maintenances = emptyList(),
            id = "vehicle-id"
        )

        val viewModel = VehicleDetailsViewModel(repository)
        viewModel.load("vehicle-id")
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
        assertEquals("ABC1234", viewModel.state.value.vehicle?.plate)
        assertTrue(repository.syncCalled)
    }

    @Test
    fun `shows error when sync fails and cache is empty`() = runTest {
        repository.databaseFlow.value = null
        repository.networkResult = Result.failure(Exception("No internet"))

        val viewModel = VehicleDetailsViewModel(repository)
        viewModel.load("vehicle-id")
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
        assertTrue(viewModel.state.value.vehicle == null)
        assertEquals(
            Res.string.error_vehicle_offline_no_cache,
            viewModel.state.value.errorMessage
        )
    }
}
