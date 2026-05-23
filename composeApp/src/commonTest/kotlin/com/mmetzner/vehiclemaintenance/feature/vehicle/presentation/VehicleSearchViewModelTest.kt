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
    fun `quando tem cache deve mostrar Sucesso mesmo se a rede falhar`() = runTest {
        val cachedVehicle = Vehicle("ABC1234", "Civic", "Honda", 2022, emptyList())
        repository.databaseFlow.value = cachedVehicle // Banco tem dado
        repository.networkResult = Result.failure(Exception("Sem internet"))

        viewModel.searchVehicle("ABC1234")
        advanceUntilIdle()

        assertTrue(repository.syncCalled, "O sync deve ser tentado sempre")
        assertTrue(viewModel.state.value is VehicleSearchState.Success)
        assertEquals("Civic", (viewModel.state.value as VehicleSearchState.Success).vehicle.model)
    }

    @Test
    fun `quando NAO tem cache e a rede falha deve mostrar Erro`() = runTest {
        repository.databaseFlow.value = null
        repository.networkResult = Result.failure(Exception("Sem internet"))

        viewModel.searchVehicle("XYZ9999")
        advanceUntilIdle()

        assertTrue(viewModel.state.value is VehicleSearchState.Error)
        assertTrue((viewModel.state.value as VehicleSearchState.Error).message.contains("offline"))
    }
}


