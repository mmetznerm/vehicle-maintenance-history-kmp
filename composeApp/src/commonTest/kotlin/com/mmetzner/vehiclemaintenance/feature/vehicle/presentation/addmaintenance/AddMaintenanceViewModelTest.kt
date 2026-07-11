package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.addmaintenance

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
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AddMaintenanceViewModelTest {

    private lateinit var repository: FakeOfflineFirstRepository
    private lateinit var viewModel: AddMaintenanceViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeOfflineFirstRepository()
        viewModel = AddMaintenanceViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `quando dados sao validos deve salvar manutencao local para sincronizar depois`() = runTest {
        viewModel.onEvent(AddMaintenanceEvent.SetPlate("ABC1234"))
        viewModel.onEvent(AddMaintenanceEvent.UpdateDate("2026-07-11"))
        viewModel.onEvent(AddMaintenanceEvent.UpdateMileage("46000"))
        viewModel.onEvent(AddMaintenanceEvent.UpdateValue("300,50"))
        viewModel.onEvent(AddMaintenanceEvent.UpdateDescription("Revisao"))

        viewModel.onEvent(AddMaintenanceEvent.Save)
        advanceUntilIdle()

        val maintenance = repository.addedMaintenance
        assertEquals("ABC1234", repository.addedMaintenanceVehiclePlate)
        assertEquals("2026-07-11", maintenance?.date)
        assertEquals("Revisao", maintenance?.description)
        assertEquals(46000, maintenance?.mileage)
        assertEquals(300.50, maintenance?.totalValue)
        assertNull(maintenance?.workshopName)
        assertTrue(maintenance?.isPendingSync == true)
        assertTrue(viewModel.state.value.success)
        assertFalse(viewModel.state.value.isSaving)
    }

    @Test
    fun `quando descricao esta em branco nao deve salvar`() = runTest {
        viewModel.onEvent(AddMaintenanceEvent.SetPlate("ABC1234"))
        viewModel.onEvent(AddMaintenanceEvent.UpdateDate("2026-07-11"))
        viewModel.onEvent(AddMaintenanceEvent.UpdateMileage("46000"))
        viewModel.onEvent(AddMaintenanceEvent.UpdateValue("300,50"))

        viewModel.onEvent(AddMaintenanceEvent.Save)
        advanceUntilIdle()

        assertNull(repository.addedMaintenance)
        assertEquals("Informe a descricao do servico.", viewModel.state.value.error)
    }
}
