package com.mmetzner.vehiclemaintenance.core.util

import java.util.UUID

actual fun randomUuid(): String = UUID.randomUUID().toString()
