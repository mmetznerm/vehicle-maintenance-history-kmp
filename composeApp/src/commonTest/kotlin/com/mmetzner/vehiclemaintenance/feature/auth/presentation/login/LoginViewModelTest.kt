package com.mmetzner.vehiclemaintenance.feature.auth.presentation.login

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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeAuthRepository
    private lateinit var viewModel: LoginViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeAuthRepository()
        viewModel = LoginViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `does not call repository when credentials are blank`() = runTest {
        advanceUntilIdle()

        viewModel.login()
        advanceUntilIdle()

        assertFalse(repository.loginCalled)
        assertFalse(viewModel.state.value.isAuthenticated)
        assertTrue(viewModel.state.value.errorMessage?.contains("required") == true)
    }

    @Test
    fun `marks state as authenticated when login succeeds`() = runTest {
        advanceUntilIdle()

        viewModel.onEmailChanged("user@example.com")
        viewModel.onPasswordChanged("password")

        viewModel.login()
        advanceUntilIdle()

        assertTrue(repository.loginCalled)
        assertTrue(viewModel.state.value.isAuthenticated)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `shows error when login fails`() = runTest {
        advanceUntilIdle()

        repository.result = Result.failure(Exception("Invalid credentials"))
        viewModel.onEmailChanged("user@example.com")
        viewModel.onPasswordChanged("wrong-password")

        viewModel.login()
        advanceUntilIdle()

        assertTrue(repository.loginCalled)
        assertFalse(viewModel.state.value.isAuthenticated)
        assertTrue(viewModel.state.value.errorMessage?.contains("Could not connect") == true)
    }

    @Test
    fun `restores authenticated state when session exists`() = runTest {
        repository.hasActiveSession = true
        viewModel = LoginViewModel(repository)

        advanceUntilIdle()

        assertTrue(viewModel.state.value.isAuthenticated)
    }
}

private class FakeAuthRepository : AuthRepository {
    var result: Result<Unit> = Result.success(Unit)
    var loginCalled = false
    var hasActiveSession = false

    override suspend fun hasActiveSession(): Boolean {
        return hasActiveSession
    }

    override suspend fun login(email: String, password: String): Result<Unit> {
        loginCalled = true
        return result
    }

    override suspend fun logout() = Unit
}
