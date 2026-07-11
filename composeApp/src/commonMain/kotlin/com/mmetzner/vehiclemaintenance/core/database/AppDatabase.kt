package com.mmetzner.vehiclemaintenance.core.database

import androidx.room.*
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.MaintenanceEntity
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.MaintenancePhotoEntity
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.VehicleEntity
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.dao.VehicleDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [VehicleEntity::class, MaintenanceEntity::class, MaintenancePhotoEntity::class],
    version = 4,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4)
    ]
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase>

expect class DatabaseBuilder {
    fun createBuilder(): RoomDatabase.Builder<AppDatabase>
}

fun createRoomDatabase(builder: DatabaseBuilder): AppDatabase {
    return builder.createBuilder()
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}



