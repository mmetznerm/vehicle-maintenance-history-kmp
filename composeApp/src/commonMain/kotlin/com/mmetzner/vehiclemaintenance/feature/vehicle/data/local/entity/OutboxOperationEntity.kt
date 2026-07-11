package com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "outbox_operations")
data class OutboxOperationEntity(
    @PrimaryKey val id: String,
    val aggregateType: String,
    val aggregateId: String,
    val parentAggregateId: String? = null,
    val operation: String,
    val payload: String,
    val status: String = OutboxStatus.PENDING,
    val retryCount: Int = 0,
    val lastError: String? = null
)

object OutboxAggregateType {
    const val VEHICLE = "VEHICLE"
    const val MAINTENANCE = "MAINTENANCE"
}

object OutboxOperationType {
    const val CREATE = "CREATE"
    const val UPDATE = "UPDATE"
    const val DELETE = "DELETE"
}

object OutboxStatus {
    const val PENDING = "PENDING"
    const val SYNCING = "SYNCING"
    const val FAILED = "FAILED"
}
