package com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.dao

import androidx.room.*
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.MaintenanceEntity
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.MaintenancePhotoEntity
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.VehicleEntity
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.VehicleWithMaintenances
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {

    @Transaction
    @Query("SELECT * FROM vehicles WHERE plate = :plate")
    fun observeVehicleByPlate(plate: String): Flow<VehicleWithMaintenances?>

    @Transaction
    @Query("SELECT * FROM vehicles WHERE id = :vehicleId")
    fun observeVehicleById(vehicleId: String): Flow<VehicleWithMaintenances?>

    @Transaction
    @Query("SELECT * FROM vehicles ORDER BY plate")
    fun observeVehicles(): Flow<List<VehicleWithMaintenances>>

    @Transaction
    @Query("SELECT * FROM vehicles ORDER BY plate LIMIT 1")
    fun observePrimaryVehicle(): Flow<VehicleWithMaintenances?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: VehicleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaintenances(maintenances: List<MaintenanceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<MaintenancePhotoEntity>)

    /**
     * Sincronização atômica: se a inserção de fotos falhar,
     * nada é salvo, mantendo o estado local íntegro.
     */
    @Transaction
    suspend fun syncVehicleData(
        vehicle: VehicleEntity,
        maintenances: List<MaintenanceEntity>,
        photos: List<MaintenancePhotoEntity>
    ) {
        insertVehicle(vehicle)
        insertMaintenances(maintenances)
        insertPhotos(photos)
    }

    @Query("SELECT * FROM vehicles WHERE syncStatus = :status")
    suspend fun getVehiclesByStatus(status: String): List<VehicleEntity>

    @Query("UPDATE vehicles SET syncStatus = :newStatus WHERE plate = :plate")
    suspend fun updateVehicleSyncStatus(plate: String, newStatus: String)

    @Query("UPDATE vehicles SET id = :id, color = :color, syncStatus = :newStatus WHERE plate = :plate")
    suspend fun updateVehicleAfterSync(plate: String, id: String, color: String, newStatus: String)

    @Query("SELECT * FROM maintenances WHERE syncStatus = :status")
    suspend fun getMaintenancesByStatus(status: String): List<MaintenanceEntity>

    @Query("UPDATE maintenances SET syncStatus = :newStatus WHERE id = :id")
    suspend fun updateMaintenanceSyncStatus(id: String, newStatus: String)

    @Query(
        """
        UPDATE maintenances
        SET remoteId = :remoteId,
            vehicleId = :vehicleId,
            syncStatus = :newStatus
        WHERE id = :id
        """
    )
    suspend fun updateMaintenanceAfterSync(
        id: String,
        remoteId: String,
        vehicleId: String,
        newStatus: String
    )
}


