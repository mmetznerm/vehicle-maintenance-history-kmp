package com.mmetzner.vehiclemaintenance.core.network

import io.ktor.http.HttpStatusCode

class NetworkRequestException(
    val statusCode: Int,
    val reason: String,
    operation: String
) : Exception("$operation failed with HTTP $statusCode: $reason")

fun HttpStatusCode.toNetworkRequestException(operation: String): NetworkRequestException {
    return NetworkRequestException(
        statusCode = value,
        reason = description,
        operation = operation
    )
}

fun Int.toNetworkRequestException(operation: String): NetworkRequestException {
    return NetworkRequestException(
        statusCode = this,
        reason = "Not Found",
        operation = operation
    )
}
