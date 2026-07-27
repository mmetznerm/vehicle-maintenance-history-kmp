package com.mmetzner.vehiclemaintenance.core.network

import com.mmetzner.vehiclemaintenance.core.auth.AuthTokens
import com.mmetzner.vehiclemaintenance.core.auth.InMemoryAuthTokenStore
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest

class CreateHttpClientTest {

    @Test
    fun `adds bearer token when token is available`() = runTest {
        val tokenStore = InMemoryAuthTokenStore()
        tokenStore.saveTokens(AuthTokens(accessToken = "access-token"))

        var authorizationHeader: String? = null
        val client = createHttpClient(
            authTokenStore = tokenStore,
            engine = MockEngine { request ->
                authorizationHeader = request.headers[HttpHeaders.Authorization]
                respond(
                    content = "{}",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
        )

        client.get("https://example.com/v1/vehicles/ABC1234")

        assertEquals("Bearer access-token", authorizationHeader)
    }

    @Test
    fun `does not add bearer token to login request`() = runTest {
        val tokenStore = InMemoryAuthTokenStore()
        tokenStore.saveTokens(AuthTokens(accessToken = "access-token"))

        var authorizationHeader: String? = null
        val client = createHttpClient(
            authTokenStore = tokenStore,
            engine = MockEngine { request ->
                authorizationHeader = request.headers[HttpHeaders.Authorization]
                respond(
                    content = "{}",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
        )

        client.get("https://example.com/v1/auth/login")

        assertNull(authorizationHeader)
    }

    @Test
    fun `does not add bearer token to register request`() = runTest {
        val tokenStore = InMemoryAuthTokenStore()
        tokenStore.saveTokens(AuthTokens(accessToken = "access-token"))

        var authorizationHeader: String? = null
        val client = createHttpClient(
            authTokenStore = tokenStore,
            engine = MockEngine { request ->
                authorizationHeader = request.headers[HttpHeaders.Authorization]
                respond(
                    content = "{}",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
        )

        client.get("https://example.com/v1/auth/register")

        assertNull(authorizationHeader)
    }

    @Test
    fun `does not add bearer token to logout request`() = runTest {
        val tokenStore = InMemoryAuthTokenStore()
        tokenStore.saveTokens(AuthTokens(accessToken = "access-token"))

        var authorizationHeader: String? = null
        val client = createHttpClient(
            authTokenStore = tokenStore,
            engine = MockEngine { request ->
                authorizationHeader = request.headers[HttpHeaders.Authorization]
                respond(
                    content = "{}",
                    status = HttpStatusCode.NoContent,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
        )

        client.post("https://example.com/v1/auth/logout")

        assertNull(authorizationHeader)
    }
}
