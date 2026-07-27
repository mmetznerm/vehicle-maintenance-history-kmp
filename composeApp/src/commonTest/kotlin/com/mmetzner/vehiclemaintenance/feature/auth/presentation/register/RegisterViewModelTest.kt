package com.mmetzner.vehiclemaintenance.feature.auth.presentation.register

import com.mmetzner.vehiclemaintenance.feature.auth.domain.repository.AuthRepository
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
class RegisterViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeRegisterAuthRepository
    private lateinit var viewModel: RegisterViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeRegisterAuthRepository()
        viewModel = RegisterViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `does not call repository when required fields are blank`() = runTest {
        viewModel.createAccount()
        advanceUntilIdle()

        assertFalse(repository.createAccountCalled)
        assertFalse(viewModel.state.value.isAuthenticated)
        assertEquals(Res.string.error_register_required, viewModel.state.value.errorMessage)
    }

    @Test
    fun `does not call repository when password is too short`() = runTest {
        viewModel.onFullNameChanged("Maycon Metzner")
        viewModel.onEmailOrPhoneChanged("maycon@example.com")
        viewModel.onPasswordChanged("123")

        viewModel.createAccount()
        advanceUntilIdle()

        assertFalse(repository.createAccountCalled)
        assertEquals(Res.string.error_password_too_short, viewModel.state.value.errorMessage)
    }

    @Test
    fun `marks state as authenticated when account creation succeeds`() = runTest {
        viewModel.onFullNameChanged("Maycon Metzner")
        viewModel.onEmailOrPhoneChanged("maycon@example.com")
        viewModel.onPasswordChanged("password123")

        viewModel.createAccount()
        advanceUntilIdle()

        assertTrue(repository.createAccountCalled)
        assertTrue(viewModel.state.value.isAuthenticated)
        assertFalse(viewModel.state.value.isLoading)
    }
}

private class FakeRegisterAuthRepository : AuthRepository {
    var createAccountCalled = false
    var result: Result<Unit> = Result.success(Unit)

    override suspend fun hasActiveSession(): Boolean = false

    override suspend fun login(email: String, password: String): Result<Unit> {
        return result
    }

    override suspend fun createAccount(
        fullName: String,
        emailOrPhone: String,
        password: String
    ): Result<Unit> {
        createAccountCalled = true
        return result
    }

    override suspend fun logout() = Unit
}
