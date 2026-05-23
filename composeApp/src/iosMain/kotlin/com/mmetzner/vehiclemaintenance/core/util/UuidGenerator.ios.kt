package com.mmetzner.vehiclemaintenance.core.util

import platform.Foundation.NSUUID

actual fun randomUuid(): String = NSUUID().UUIDString()
