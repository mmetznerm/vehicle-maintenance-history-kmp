package com.mmetzner.vehiclemaintenance.feature.vehicle.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "maintenances",
    foreignKeys = [
        ForeignKey(
            entity = VehicleEntity::class,
            parentColumns = ["plate"],
            childColumns = ["vehiclePlate"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("vehiclePlate")]
)
data class MaintenanceEntity(
    @PrimaryKey val id: String,
    val vehiclePlate: String,
    val date: String,
    val description: String,
    val workshopName: String?
)