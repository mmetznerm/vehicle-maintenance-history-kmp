package com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey val plate: String,
    val id: String? = null,
    val model: String,
    val brand: String,
    val year: Int,
    @ColumnInfo(defaultValue = "")
    val color: String = "",
    @ColumnInfo(defaultValue = SyncStatus.SYNCED)
    val syncStatus: String = SyncStatus.SYNCED
)



