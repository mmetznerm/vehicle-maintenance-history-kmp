package com.mmetzner.vehiclemaintenance.core.network

import kotlin.test.Test
import kotlin.test.assertEquals
import vehiclemaintenance.composeapp.generated.resources.*

class NetworkErrorMessagesTest {

    @Test
    fun `maps unauthorized login to invalid credentials`() {
        val error = NetworkRequestException(
            statusCode = 401,
            reason = "Unauthorized",
            operation = "Login"
        )

        assertEquals(Res.string.error_invalid_credentials, error.toLoginErrorMessage())
    }

    @Test
    fun `maps generic login failure to connectivity message`() {
        val error = Exception("Network unavailable")

        assertEquals(
            Res.string.error_server_connection,
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
            Res.string.error_vehicle_not_found_no_cache,
            error.toVehicleSearchErrorMessage()
        )
    }

    @Test
    fun `maps vehicle connectivity failure to offline cache message`() {
        val error = Exception("Network unavailable")

        assertEquals(
            Res.string.error_vehicle_offline_no_cache,
            error.toVehicleSearchErrorMessage()
        )
    }
}
