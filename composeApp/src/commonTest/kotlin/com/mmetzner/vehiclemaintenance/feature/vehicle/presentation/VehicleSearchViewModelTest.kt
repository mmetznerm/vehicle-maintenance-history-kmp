package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation

import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Vehicle
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.search.VehicleSearchState
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.search.VehicleSearchViewModel
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
import kotlin.test.assertTrue
import vehiclemaintenance.composeapp.generated.resources.*

@OptIn(ExperimentalCoroutinesApi::class)
class VehicleSearchViewModelTest {

    private lateinit var repository: FakeOfflineFirstRepository
    private lateinit var viewModel: VehicleSearchViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeOfflineFirstRepository()
        viewModel = VehicleSearchViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `shows cached vehicle when network sync fails`() = runTest {
        val cachedVehicle = Vehicle("ABC1234", "Civic", "Honda", 2022, emptyList())
        repository.databaseFlow.value = cachedVehicle // Cache contains data
        repository.networkResult = Result.failure(Exception("No internet"))

        viewModel.searchVehicle("ABC1234")
        advanceUntilIdle()

        assertTrue(repository.syncCalled, "Sync should always be attempted")
        assertTrue(viewModel.state.value is VehicleSearchState.Success)
        assertEquals("Civic", (viewModel.state.value as VehicleSearchState.Success).vehicle.model)
    }

    @Test
    fun `shows error when network fails and cache is empty`() = runTest {
        repository.databaseFlow.value = null
        repository.networkResult = Result.failure(Exception("No internet"))

        viewModel.searchVehicle("XYZ9999")
        advanceUntilIdle()

        assertTrue(viewModel.state.value is VehicleSearchState.Error)
        assertEquals(
            Res.string.error_vehicle_offline_no_cache,
            (viewModel.state.value as VehicleSearchState.Error).message
        )
    }
}


