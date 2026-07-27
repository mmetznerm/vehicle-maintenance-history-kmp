package com.mmetzner.vehiclemaintenance.core.network

import org.jetbrains.compose.resources.StringResource
import vehiclemaintenance.composeapp.generated.resources.*

fun Throwable?.toLoginErrorMessage(): StringResource {
    return when (this) {
        is NetworkRequestException -> when (statusCode) {
            400, 401, 403 -> Res.string.error_invalid_credentials
            in 500..599 -> Res.string.error_auth_service_unavailable
            else -> Res.string.error_sign_in_failed
        }

        else -> Res.string.error_server_connection
    }
}

fun Throwable?.toVehicleSearchErrorMessage(): StringResource {
    return when (this) {
        is NetworkRequestException -> when (statusCode) {
            401, 403 -> Res.string.error_session_expired
            404 -> Res.string.error_vehicle_not_found_no_cache
            in 500..599 -> Res.string.error_vehicle_service_unavailable
            else -> Res.string.error_vehicle_sync_failed
        }

        else -> Res.string.error_vehicle_offline_no_cache
    }
}
