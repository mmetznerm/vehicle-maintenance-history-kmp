package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.vehiclelist

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
class VehicleListViewModelTest {

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
    fun `quando existe lista local deve mostrar veiculos do cache`() = runTest {
        repository.databaseListFlow.value = listOf(
            Vehicle("ABC1234", "Civic", "Honda", 2022, emptyList())
        )

        val viewModel = VehicleListViewModel(repository)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
        assertEquals(1, viewModel.state.value.vehicles.size)
        assertEquals("ABC1234", viewModel.state.value.vehicles.first().plate)
        assertTrue(repository.syncVehiclesCalled)
    }

    @Test
    fun `quando sync falha sem cache deve mostrar erro e lista vazia`() = runTest {
        repository.databaseListFlow.value = emptyList()
        repository.networkResult = Result.failure(Exception("Sem internet"))

        val viewModel = VehicleListViewModel(repository)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
        assertTrue(viewModel.state.value.vehicles.isEmpty())
        assertTrue(viewModel.state.value.errorMessage?.contains("offline") == true)
    }
}
