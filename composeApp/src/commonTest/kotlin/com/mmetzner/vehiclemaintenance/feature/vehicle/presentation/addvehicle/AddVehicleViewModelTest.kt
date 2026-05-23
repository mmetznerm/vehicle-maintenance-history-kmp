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
    fun `quando os dados sao validos deve salvar no repositorio e emitir evento de voltar`() = runTest {
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
            yearStr = "2022"
        )
        advanceUntilIdle()

        assertNotNull(repository.addedVehicle, "O veículo deveria ter sido enviado ao repositório")
        assertEquals("ABC1234", repository.addedVehicle?.plate)
        assertEquals("Civic", repository.addedVehicle?.model)
        assertEquals(2022, repository.addedVehicle?.year)
        assertEquals(1, emittedEvents.size)
        assertTrue(emittedEvents.first() is AddVehicleUiEvent.NavigateBack)
        job.cancel()
    }

    @Test
    fun `quando a placa esta em branco NAO deve salvar e NAO deve emitir evento`() = runTest {
        val emittedEvents = mutableListOf<AddVehicleUiEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiEvent.collect { emittedEvents.add(it) }
        }

        viewModel.saveVehicle(plate = "   ", model = "Civic", brand = "Honda", yearStr = "2022")
        advanceUntilIdle()

        assertNull(repository.addedVehicle, "Não deve salvar se a placa for inválida")
        assertTrue(emittedEvents.isEmpty(), "Não deve fechar a tela se houve erro de validação")
        job.cancel()
    }

    @Test
    fun `quando o ano for invalido NAO deve salvar e NAO deve emitir evento`() = runTest {
        val emittedEvents = mutableListOf<AddVehicleUiEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiEvent.collect { emittedEvents.add(it) }
        }

        viewModel.saveVehicle(plate = "ABC1234", model = "Civic", brand = "Honda", yearStr = "Dois Mil e Vinte")
        advanceUntilIdle()

        assertNull(repository.addedVehicle)
        assertTrue(emittedEvents.isEmpty())
        job.cancel()
    }
}


