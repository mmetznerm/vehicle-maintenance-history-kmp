package com.mmetzner.vehiclemaintenance.core.network

fun Throwable?.toLoginErrorMessage(): String {
    return when (this) {
        is NetworkRequestException -> when (statusCode) {
            400, 401, 403 -> "Invalid email or password."
            in 500..599 -> "Authentication service is temporarily unavailable."
            else -> "Could not sign in. Try again in a moment."
        }

        else -> "Could not connect to the server. Check your connection and try again."
    }
}

fun Throwable?.toVehicleSearchErrorMessage(): String {
    return when (this) {
        is NetworkRequestException -> when (statusCode) {
            401, 403 -> "Your session expired. Please sign in again."
            404 -> "Vehicle not found and no local copy is available."
            in 500..599 -> "Vehicle service is temporarily unavailable and no local copy is available."
            else -> "Could not sync vehicle data and no local copy is available."
        }

        else -> "You are offline and this vehicle is not available in the local cache."
    }
}
