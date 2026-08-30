package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.home

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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class VehicleHomeViewModelTest {

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
    fun `shows home content when a local vehicle exists`() = runTest {
        repository.databaseFlow.value = Vehicle("ABC1234", "Civic", "Honda", 2022, emptyList())

        val viewModel = VehicleHomeViewModel(repository, repository)
        advanceUntilIdle()

        assertTrue(viewModel.state.value is VehicleHomeState.Content)
        assertEquals(
            "ABC1234",
            (viewModel.state.value as VehicleHomeState.Content).vehicle.plate
        )
    }

    @Test
    fun `shows onboarding when no local vehicle exists`() = runTest {
        repository.databaseFlow.value = null

        val viewModel = VehicleHomeViewModel(repository, repository)
        advanceUntilIdle()

        assertTrue(viewModel.state.value is VehicleHomeState.Empty)
    }
}
