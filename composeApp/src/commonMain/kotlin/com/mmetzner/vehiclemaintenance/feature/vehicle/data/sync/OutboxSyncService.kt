package com.mmetzner.vehiclemaintenance.feature.vehicle.data.sync

import com.mmetzner.vehiclemaintenance.core.sync.OutboxSyncRequestScheduler
import com.mmetzner.vehiclemaintenance.core.util.randomUuid
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.dao.OutboxDao
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.dao.VehicleDao
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.MaintenanceEntity
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.OutboxAggregateType
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.OutboxOperationEntity
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.OutboxOperationType
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.SyncStatus
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.VehicleEntity
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.mapper.toRequestDto
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote.VehicleRemoteDataSource
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote.dto.CreateMaintenanceRequest
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote.dto.CreateVehicleRequest
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Maintenance
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Vehicle
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.repository.SyncRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class OutboxSyncService(
    private val remoteDataSource: VehicleRemoteDataSource,
    private val vehicleDao: VehicleDao,
    private val outboxDao: OutboxDao,
    private val syncScheduler: OutboxSyncRequestScheduler,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val json: Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
) : SyncRepository {

    private val syncScope = CoroutineScope(ioDispatcher)
    private val syncMutex = Mutex()

    suspend fun enqueueVehicleCreate(entity: VehicleEntity) {
        outboxDao.deleteForAggregate(
            aggregateType = OutboxAggregateType.VEHICLE,
            aggregateId = entity.plate
        )
        outboxDao.insert(
            OutboxOperationEntity(
                id = randomUuid(),
                aggregateType = OutboxAggregateType.VEHICLE,
                aggregateId = entity.plate,
                operation = OutboxOperationType.CREATE,
                payload = json.encodeToString(entity.toRequestDto())
            )
        )
        requestOutboxSync()
    }

    suspend fun enqueueVehicleUpdate(vehicle: Vehicle, entity: VehicleEntity) {
        val remoteVehicleId = vehicle.id
        if (remoteVehicleId == null) {
            outboxDao.deleteForAggregate(
                aggregateType = OutboxAggregateType.VEHICLE,
                aggregateId = entity.plate
            )
            outboxDao.insert(
                OutboxOperationEntity(
                    id = randomUuid(),
                    aggregateType = OutboxAggregateType.VEHICLE,
                    aggregateId = entity.plate,
                    operation = OutboxOperationType.CREATE,
                    payload = json.encodeToString(entity.toRequestDto())
                )
            )
        } else {
            vehicleDao.deleteOtherVehiclesWithId(remoteVehicleId, entity.plate)
            outboxDao.deleteForAggregate(
                aggregateType = OutboxAggregateType.VEHICLE,
                aggregateId = remoteVehicleId
            )
            outboxDao.insert(
                OutboxOperationEntity(
                    id = randomUuid(),
                    aggregateType = OutboxAggregateType.VEHICLE,
                    aggregateId = remoteVehicleId,
                    parentAggregateId = entity.plate,
                    operation = OutboxOperationType.UPDATE,
                    payload = json.encodeToString(vehicle.toRequestDto())
                )
            )
        }
        requestOutboxSync()
    }

    suspend fun enqueueVehicleDelete(vehicle: Vehicle) {
        outboxDao.deleteForAggregate(
            aggregateType = OutboxAggregateType.VEHICLE,
            aggregateId = vehicle.plate
        )
        outboxDao.deleteForParentAggregate(vehicle.plate)

        val remoteVehicleId = vehicle.id
        if (remoteVehicleId != null) {
            outboxDao.insert(
                OutboxOperationEntity(
                    id = randomUuid(),
                    aggregateType = OutboxAggregateType.VEHICLE,
                    aggregateId = remoteVehicleId,
                    parentAggregateId = vehicle.plate,
                    operation = OutboxOperationType.DELETE,
                    payload = ""
                )
            )
        }
        requestOutboxSync()
    }

    suspend fun enqueueMaintenanceCreate(entity: MaintenanceEntity) {
        outboxDao.insert(
            OutboxOperationEntity(
                id = randomUuid(),
                aggregateType = OutboxAggregateType.MAINTENANCE,
                aggregateId = entity.id,
                parentAggregateId = entity.vehiclePlate,
                operation = OutboxOperationType.CREATE,
                payload = json.encodeToString(entity.toRequestDto())
            )
        )
        requestOutboxSync()
    }

    suspend fun enqueueMaintenanceUpdate(
        vehiclePlate: String,
        fallbackVehicleId: String?,
        maintenance: Maintenance,
        entity: MaintenanceEntity
    ) {
        val remoteMaintenanceId = maintenance.remoteId
        val remoteVehicleId = maintenance.vehicleId ?: fallbackVehicleId
        if (remoteMaintenanceId == null || remoteVehicleId == null) {
            outboxDao.deleteForAggregate(
                aggregateType = OutboxAggregateType.MAINTENANCE,
                aggregateId = entity.id
            )
            outboxDao.insert(
                OutboxOperationEntity(
                    id = randomUuid(),
                    aggregateType = OutboxAggregateType.MAINTENANCE,
                    aggregateId = entity.id,
                    parentAggregateId = entity.vehiclePlate,
                    operation = OutboxOperationType.CREATE,
                    payload = json.encodeToString(entity.toRequestDto())
                )
            )
        } else {
            outboxDao.deleteForAggregate(
                aggregateType = OutboxAggregateType.MAINTENANCE,
                aggregateId = remoteMaintenanceId
            )
            outboxDao.insert(
                OutboxOperationEntity(
                    id = randomUuid(),
                    aggregateType = OutboxAggregateType.MAINTENANCE,
                    aggregateId = remoteMaintenanceId,
                    parentAggregateId = remoteVehicleId,
                    operation = OutboxOperationType.UPDATE,
                    payload = json.encodeToString(
                        MaintenanceUpdatePayload(
                            localId = entity.id,
                            vehicleId = remoteVehicleId,
                            request = maintenance.toRequestDto()
                        )
                    )
                )
            )
        }
        requestOutboxSync()
    }

    suspend fun enqueueMaintenanceDelete(
        vehiclePlate: String,
        fallbackVehicleId: String?,
        maintenance: Maintenance
    ) {
        outboxDao.deleteForAggregate(
            aggregateType = OutboxAggregateType.MAINTENANCE,
            aggregateId = maintenance.id
        )

        val remoteMaintenanceId = maintenance.remoteId
        val remoteVehicleId = maintenance.vehicleId ?: fallbackVehicleId
        if (remoteMaintenanceId != null && remoteVehicleId != null) {
            outboxDao.deleteForAggregate(
                aggregateType = OutboxAggregateType.MAINTENANCE,
                aggregateId = remoteMaintenanceId
            )
            outboxDao.insert(
                OutboxOperationEntity(
                    id = randomUuid(),
                    aggregateType = OutboxAggregateType.MAINTENANCE,
                    aggregateId = remoteMaintenanceId,
                    parentAggregateId = remoteVehicleId,
                    operation = OutboxOperationType.DELETE,
                    payload = ""
                )
            )
        }
        requestOutboxSync()
    }

    override suspend fun syncPendingOutbox() = syncMutex.withLock {
        val operations = outboxDao.getPendingOperations()

        for (operation in operations) {
            try {
                outboxDao.markSyncing(operation.id)

                when {
                    operation.aggregateType == OutboxAggregateType.VEHICLE &&
                        operation.operation == OutboxOperationType.CREATE -> syncCreateVehicle(operation)

                    operation.aggregateType == OutboxAggregateType.VEHICLE &&
                        operation.operation == OutboxOperationType.UPDATE -> syncUpdateVehicle(operation)

                    operation.aggregateType == OutboxAggregateType.VEHICLE &&
                        operation.operation == OutboxOperationType.DELETE -> syncDeleteVehicle(operation)

                    operation.aggregateType == OutboxAggregateType.MAINTENANCE &&
                        operation.operation == OutboxOperationType.CREATE -> syncCreateMaintenance(operation)

                    operation.aggregateType == OutboxAggregateType.MAINTENANCE &&
                        operation.operation == OutboxOperationType.UPDATE -> syncUpdateMaintenance(operation)

                    operation.aggregateType == OutboxAggregateType.MAINTENANCE &&
                        operation.operation == OutboxOperationType.DELETE -> syncDeleteMaintenance(operation)

                    else -> error("Unsupported outbox operation ${operation.aggregateType}:${operation.operation}")
                }

                outboxDao.delete(operation.id)
            } catch (e: Exception) {
                outboxDao.markFailed(
                    id = operation.id,
                    error = e.message ?: "Could not sync operation."
                )
            }
        }
    }

    fun requestOutboxSync() {
        syncScheduler.requestSync()
        syncScope.launch {
            syncPendingOutbox()
        }
    }

    private suspend fun syncCreateVehicle(operation: OutboxOperationEntity) {
        val request = json.decodeFromString<CreateVehicleRequest>(operation.payload)
        val response = remoteDataSource.createVehicle(request)

        vehicleDao.updateVehicleAfterSync(
            plate = request.plate,
            id = response.id,
            color = response.color.orEmpty(),
            newStatus = SyncStatus.SYNCED
        )
    }

    private suspend fun syncUpdateVehicle(operation: OutboxOperationEntity) {
        val request = json.decodeFromString<CreateVehicleRequest>(operation.payload)
        val response = remoteDataSource.updateVehicle(operation.aggregateId, request)

        vehicleDao.updateVehicleAfterSync(
            plate = request.plate,
            id = response.id,
            color = response.color.orEmpty(),
            newStatus = SyncStatus.SYNCED
        )
    }

    private suspend fun syncDeleteVehicle(operation: OutboxOperationEntity) {
        remoteDataSource.deleteVehicle(operation.aggregateId)
    }

    private suspend fun syncCreateMaintenance(operation: OutboxOperationEntity) {
        val vehiclePlate = operation.parentAggregateId
            ?: error("Maintenance create operation is missing vehicle plate.")
        val request = json.decodeFromString<CreateMaintenanceRequest>(operation.payload)
        val response = remoteDataSource.createMaintenanceByPlate(vehiclePlate, request)

        vehicleDao.updateMaintenanceAfterSync(
            id = operation.aggregateId,
            remoteId = response.id,
            vehicleId = response.vehicleId,
            newStatus = SyncStatus.SYNCED
        )
    }

    private suspend fun syncUpdateMaintenance(operation: OutboxOperationEntity) {
        val payload = json.decodeFromString<MaintenanceUpdatePayload>(operation.payload)
        val response = remoteDataSource.updateMaintenance(
            vehicleId = payload.vehicleId,
            maintenanceId = operation.aggregateId,
            maintenance = payload.request
        )

        vehicleDao.updateMaintenanceAfterSync(
            id = payload.localId,
            remoteId = response.id,
            vehicleId = response.vehicleId,
            newStatus = SyncStatus.SYNCED
        )
    }

    private suspend fun syncDeleteMaintenance(operation: OutboxOperationEntity) {
        val vehicleId = operation.parentAggregateId
            ?: error("Maintenance delete operation is missing vehicle id.")

        remoteDataSource.deleteMaintenance(
            vehicleId = vehicleId,
            maintenanceId = operation.aggregateId
        )
    }
}

@Serializable
private data class MaintenanceUpdatePayload(
    val localId: String,
    val vehicleId: String,
    val request: CreateMaintenanceRequest
)
