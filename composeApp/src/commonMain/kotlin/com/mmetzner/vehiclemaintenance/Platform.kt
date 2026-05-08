package com.mmetzner.vehiclemaintenance

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform