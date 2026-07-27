package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.addvehicle

import com.mmetzner.vehiclemaintenance.repository.FakeOfflineFirstRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class AddVehicleViewModelTest {

    private lateinit var repository: FakeOfflineFirstRepository
    private lateinit var viewModel: AddVehicleViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeOfflineFirstRepository()
        viewModel = AddVehicleViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saves valid data and emits back navigation event`() = runTest {
        val emittedEvents = mutableListOf<AddVehicleUiEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiEvent.collect { event ->
                emittedEvents.add(event)
            }
        }

        viewModel.saveVehicle(
            plate = "ABC1234",
            model = "Civic",
            brand = "Honda",
            yearStr = "2022",
            color = "Silver"
        )
        advanceUntilIdle()

        assertNotNull(repository.addedVehicle, "The vehicle should have been sent to the repository")
        assertEquals("ABC1234", repository.addedVehicle?.plate)
        assertEquals("Civic", repository.addedVehicle?.model)
        assertEquals(2022, repository.addedVehicle?.year)
        assertEquals("Silver", repository.addedVehicle?.color)
        assertEquals(1, emittedEvents.size)
        assertTrue(emittedEvents.first() is AddVehicleUiEvent.NavigateBack)
        job.cancel()
    }

    @Test
    fun `does not save or emit event when plate number is blank`() = runTest {
        val emittedEvents = mutableListOf<AddVehicleUiEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiEvent.collect { emittedEvents.add(it) }
        }

        viewModel.saveVehicle(plate = "   ", model = "Civic", brand = "Honda", yearStr = "2022", color = "")
        advanceUntilIdle()

        assertNull(repository.addedVehicle, "Should not save when the plate number is invalid")
        assertTrue(emittedEvents.isEmpty(), "Should not close the screen after a validation error")
        job.cancel()
    }

    @Test
    fun `does not save or emit event when year is invalid`() = runTest {
        val emittedEvents = mutableListOf<AddVehicleUiEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiEvent.collect { emittedEvents.add(it) }
        }

        viewModel.saveVehicle(
            plate = "ABC1234",
            model = "Civic",
            brand = "Honda",
            yearStr = "Two Thousand Twenty",
            color = ""
        )
        advanceUntilIdle()

        assertNull(repository.addedVehicle)
        assertTrue(emittedEvents.isEmpty())
        job.cancel()
    }
}


