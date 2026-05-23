package com.mmetzner.vehiclemaintenance.core.network

import kotlin.test.Test
import kotlin.test.assertEquals

class NetworkErrorMessagesTest {

    @Test
    fun `maps unauthorized login to invalid credentials`() {
        val error = NetworkRequestException(
            statusCode = 401,
            reason = "Unauthorized",
            operation = "Login"
        )

        assertEquals("Invalid email or password.", error.toLoginErrorMessage())
    }

    @Test
    fun `maps generic login failure to connectivity message`() {
        val error = Exception("Network unavailable")

        assertEquals(
            "Could not connect to the server. Check your connection and try again.",
            error.toLoginErrorMessage()
        )
    }

    @Test
    fun `maps vehicle not found to local cache aware message`() {
        val error = NetworkRequestException(
            statusCode = 404,
            reason = "Not Found",
            operation = "Fetch vehicle"
        )

        assertEquals(
            "Vehicle not found and no local copy is available.",
            error.toVehicleSearchErrorMessage()
        )
    }

    @Test
    fun `maps vehicle connectivity failure to offline cache message`() {
        val error = Exception("Network unavailable")

        assertEquals(
            "You are offline and this vehicle is not available in the local cache.",
            error.toVehicleSearchErrorMessage()
        )
    }
}
