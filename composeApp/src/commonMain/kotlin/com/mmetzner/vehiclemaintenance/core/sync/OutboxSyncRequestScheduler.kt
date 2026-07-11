package com.mmetzner.vehiclemaintenance.core.sync

interface OutboxSyncRequestScheduler {
    fun requestSync()
    fun startPeriodicSync()
}

class NoOpOutboxSyncRequestScheduler : OutboxSyncRequestScheduler {
    override fun requestSync() = Unit
    override fun startPeriodicSync() = Unit
}
