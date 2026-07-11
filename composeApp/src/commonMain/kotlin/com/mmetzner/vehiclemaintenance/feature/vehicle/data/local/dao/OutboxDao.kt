package com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.OutboxOperationEntity

@Dao
interface OutboxDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(operation: OutboxOperationEntity)

    @Query(
        """
        SELECT * FROM outbox_operations
        WHERE status IN ('PENDING', 'FAILED')
        ORDER BY rowid
        """
    )
    suspend fun getPendingOperations(): List<OutboxOperationEntity>

    @Query("UPDATE outbox_operations SET status = 'SYNCING', lastError = NULL WHERE id = :id")
    suspend fun markSyncing(id: String)

    @Query(
        """
        UPDATE outbox_operations
        SET status = 'FAILED',
            retryCount = retryCount + 1,
            lastError = :error
        WHERE id = :id
        """
    )
    suspend fun markFailed(
        id: String,
        error: String
    )

    @Query("DELETE FROM outbox_operations WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM outbox_operations WHERE aggregateType = :aggregateType AND aggregateId = :aggregateId")
    suspend fun deleteForAggregate(
        aggregateType: String,
        aggregateId: String
    )

    @Query("DELETE FROM outbox_operations WHERE parentAggregateId = :parentAggregateId")
    suspend fun deleteForParentAggregate(parentAggregateId: String)
}
