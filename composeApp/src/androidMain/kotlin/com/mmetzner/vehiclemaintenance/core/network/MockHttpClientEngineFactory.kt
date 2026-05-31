package com.mmetzner.vehiclemaintenance.core.network

import android.content.Context
import com.mmetzner.vehiclemaintenance.R
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf

class MockHttpClientEngineFactory(
    private val context: Context,
    private val useMockData: Boolean
) : HttpClientEngineFactory {

    override fun create(): HttpClientEngine? {
        if (!useMockData) return null

        return MockEngine { request ->
            val path = request.url.encodedPath
            val method = request.method.value
            val payloads = MockPayloads(context)

            when {
                method == "POST" && path == "/v1/auth/login" -> {
                    respondJson(payloads.login)
                }

                method == "POST" && path == "/v1/auth/register" -> {
                    respondJson(payloads.login)
                }

                method == "GET" && path.startsWith("/v1/vehicles/") -> {
                    val plate = path.substringAfterLast("/").uppercase()
                    val vehicle = payloads.vehicleByPlate[plate]
                    if (vehicle == null) {
                        respondJson(
                            content = """{"message":"Vehicle not found"}""",
                            status = HttpStatusCode.NotFound
                        )
                    } else {
                        respondJson(vehicle)
                    }
                }

                method == "POST" && path == "/v1/vehicles" -> {
                    respondJson("""{"status":"created"}""", status = HttpStatusCode.Created)
                }

                method == "POST" && path == "/v1/maintenances" -> {
                    respondJson("""{"status":"created"}""", status = HttpStatusCode.Created)
                }

                else -> {
                    respondJson(
                        content = """{"message":"Unhandled mock route: $method $path"}""",
                        status = HttpStatusCode.NotFound
                    )
                }
            }
        }
    }

    private fun MockRequestHandleScope.respondJson(
        content: String,
        status: HttpStatusCode = HttpStatusCode.OK
    ) = respond(
        content = content,
        status = status,
        headers = headersOf(HttpHeaders.ContentType, "application/json")
    )
}

private class MockPayloads(
    private val context: Context
) {
    val login: String = readRawResource(R.raw.mock_login_success)

    val vehicleByPlate = mapOf(
        "ABC-1234" to readRawResource(R.raw.mock_vehicle_abc_1234),
        "ABC1234" to readRawResource(R.raw.mock_vehicle_abc1234)
    )

    private fun readRawResource(resourceId: Int): String {
        return context.resources.openRawResource(resourceId)
            .bufferedReader()
            .use { it.readText() }
    }
}
