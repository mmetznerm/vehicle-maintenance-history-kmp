package com.mmetzner.vehiclemaintenance.feature.vehicle.data

import com.mmetzner.vehiclemaintenance.core.util.randomUuid
import com.mmetzner.vehiclemaintenance.core.sync.OutboxSyncRequestScheduler
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.dao.OutboxDao
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.dao.VehicleDao
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.MaintenanceEntity
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.OutboxAggregateType
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.OutboxOperationEntity
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.OutboxOperationType
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.local.entity.SyncStatus
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.mapper.toDomain
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.mapper.toEntity
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.mapper.toPendingEntity
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.mapper.toPhotoEntities
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.mapper.toRequestDto
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote.VehicleRemoteDataSource
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote.dto.CreateMaintenanceRequest
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote.dto.CreateVehicleRequest
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Maintenance
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Vehicle
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.repository.VehicleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class VehicleRepositoryImpl(
    private val remoteDataSource: VehicleRemoteDataSource,
    private val vehicleDao: VehicleDao,
    private val outboxDao: OutboxDao,
    private val syncScheduler: OutboxSyncRequestScheduler
) : VehicleRepository {

    private val syncScope = CoroutineScope(Dispatchers.IO)
    private val syncMutex = Mutex()
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    override suspend fun observeVehicles(): Flow<List<Vehicle>> {
        return vehicleDao.observeVehicles().map { relations ->
            relations.map { it.toDomain() }
        }
    }

    override suspend fun observePrimaryVehicle(): Flow<Vehicle?> {
        return vehicleDao.observePrimaryVehicle().map { relation ->
            relation?.toDomain()
        }
    }

    override suspend fun observeVehicle(plate: String): Flow<Vehicle?> {
        return vehicleDao.observeVehicleByPlate(plate).map { relation ->
            relation?.toDomain()
        }
    }

    override suspend fun observeVehicleById(vehicleId: String): Flow<Vehicle?> {
        return vehicleDao.observeVehicleById(vehicleId).map { relation ->
            relation?.toDomain()
        }
    }

    override suspend fun syncVehicles(): Result<Unit> {
        return try {
            val vehicles = remoteDataSource.listVehicles()

            for (vehicle in vehicles) {
                val maintenances = remoteDataSource.listMaintenances(vehicle.id)

                vehicleDao.syncVehicleData(
                    vehicle = vehicle.toEntity(),
                    maintenances = maintenances.map { it.toEntity(vehicle.plate) },
                    photos = maintenances.flatMap { it.toPhotoEntities(it.id) }
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncVehicle(plate: String): Result<Unit> {
        return try {
            val dto = remoteDataSource.getVehicleByPlate(plate)
            val maintenances = remoteDataSource.listMaintenances(dto.id)

            val vehicleEntity = dto.toEntity()
            val maintenanceEntities = maintenances.map { it.toEntity(dto.plate) }
            val photoEntities = maintenances.flatMap { it.toPhotoEntities(it.id) }

            vehicleDao.syncVehicleData(vehicleEntity, maintenanceEntities, photoEntities)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncVehicleById(vehicleId: String): Result<Unit> {
        return try {
            val dto = remoteDataSource.getVehicle(vehicleId)
            val maintenances = remoteDataSource.listMaintenances(vehicleId)

            val vehicleEntity = dto.toEntity()
            val maintenanceEntities = maintenances.map { it.toEntity(dto.plate) }
            val photoEntities = maintenances.flatMap { it.toPhotoEntities(it.id) }

            vehicleDao.syncVehicleData(vehicleEntity, maintenanceEntities, photoEntities)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addVehicle(vehicle: Vehicle) {
        val entity = vehicle.toPendingEntity()
        vehicleDao.insertVehicle(entity)
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

    override suspend fun updateVehicle(vehicle: Vehicle) {
        val entity = vehicle.toPendingEntity()
        vehicleDao.insertVehicle(entity)

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

    override suspend fun deleteVehicle(vehicle: Vehicle) {
        vehicleDao.deleteVehicleByPlate(vehicle.plate)
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

    override suspend fun addMaintenance(vehiclePlate: String, maintenance: Maintenance) {
        val entity = MaintenanceEntity(
            id = randomUuid(),
            vehiclePlate = vehiclePlate,
            date = maintenance.date,
            description = maintenance.description,
            workshopName = maintenance.workshopName,
            mileage = maintenance.mileage,
            totalValue = maintenance.totalValue,
            syncStatus = SyncStatus.PENDING
        )
        vehicleDao.insertMaintenances(listOf(entity))
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

    override suspend fun updateMaintenance(
        vehiclePlate: String,
        fallbackVehicleId: String?,
        maintenance: Maintenance
    ) {
        val entity = maintenance.toPendingEntity(vehiclePlate, fallbackVehicleId)
        vehicleDao.insertMaintenances(listOf(entity))

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

    override suspend fun deleteMaintenance(
        vehiclePlate: String,
        fallbackVehicleId: String?,
        maintenance: Maintenance
    ) {
        vehicleDao.deleteMaintenanceById(maintenance.id)
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

    private fun requestOutboxSync() {
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
