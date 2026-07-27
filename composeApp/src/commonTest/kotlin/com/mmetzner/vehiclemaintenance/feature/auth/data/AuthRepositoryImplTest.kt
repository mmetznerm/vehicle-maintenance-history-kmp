package com.mmetzner.vehiclemaintenance.feature.auth.data

import com.mmetzner.vehiclemaintenance.core.auth.AuthTokens
import com.mmetzner.vehiclemaintenance.core.auth.InMemoryAuthTokenStore
import com.mmetzner.vehiclemaintenance.core.network.ApiConfig
import com.mmetzner.vehiclemaintenance.core.network.createHttpClient
import com.mmetzner.vehiclemaintenance.feature.auth.data.remote.AuthRemoteDataSource
import com.mmetzner.vehiclemaintenance.feature.auth.data.remote.dto.LoginRequest
import com.mmetzner.vehiclemaintenance.feature.auth.data.remote.dto.LogoutRequest
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AuthRepositoryImplTest {

    @Test
    fun `login request uses emailOrPhone field`() {
        val payload = Json.encodeToString(
            LoginRequest(
                emailOrPhone = "driver@example.com",
                password = "password"
            )
        )

        assertTrue(payload.contains("\"emailOrPhone\":\"driver@example.com\""))
        assertFalse(payload.contains("\"email\":"))
    }

    @Test
    fun `logout request uses refreshToken field`() {
        val payload = Json.encodeToString(
            LogoutRequest(refreshToken = "refresh-token")
        )

        assertTrue(payload.contains("\"refreshToken\":\"refresh-token\""))
    }

    @Test
    fun `logout calls backend and clears local tokens`() = runTest {
        val tokenStore = InMemoryAuthTokenStore()
        tokenStore.saveTokens(
            AuthTokens(
                accessToken = "access-token",
                refreshToken = "refresh-token"
            )
        )

        var requestedPath: String? = null
        var requestedMethod: HttpMethod? = null
        val client = createHttpClient(
            authTokenStore = tokenStore,
            engine = MockEngine { request ->
                requestedPath = request.url.encodedPath
                requestedMethod = request.method
                respond(
                    content = "",
                    status = HttpStatusCode.NoContent
                )
            }
        )
        val repository = AuthRepositoryImpl(
            remoteDataSource = AuthRemoteDataSource(
                httpClient = client,
                apiConfig = ApiConfig("https://example.com")
            ),
            tokenStore = tokenStore
        )

        repository.logout()

        assertEquals("/v1/auth/logout", requestedPath)
        assertEquals(HttpMethod.Post, requestedMethod)
        assertNull(tokenStore.getTokens())
        client.close()
    }

    @Test
    fun `logout clears local tokens when backend rejects request`() = runTest {
        val tokenStore = InMemoryAuthTokenStore()
        tokenStore.saveTokens(
            AuthTokens(
                accessToken = "access-token",
                refreshToken = "refresh-token"
            )
        )

        val client = createHttpClient(
            authTokenStore = tokenStore,
            engine = MockEngine {
                respond(
                    content = "",
                    status = HttpStatusCode.InternalServerError
                )
            }
        )
        val repository = AuthRepositoryImpl(
            remoteDataSource = AuthRemoteDataSource(
                httpClient = client,
                apiConfig = ApiConfig("https://example.com")
            ),
            tokenStore = tokenStore
        )

        repository.logout()

        assertNull(tokenStore.getTokens())
        client.close()
    }
}
