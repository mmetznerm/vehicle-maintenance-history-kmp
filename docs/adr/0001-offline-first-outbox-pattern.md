# ADR 0001: Offline-First Architecture with Outbox Pattern

## Status
Accepted

## Context
Mobile users need to view and record vehicle maintenance details regardless of network availability (underground parking, weak cell reception). Direct network calls for mutation operations cause poor user experience, failed transactions, and data loss under spotty connectivity.

## Decision
We adopted an **Offline-First architecture** using **Room Multiplatform** with an **Outbox Pattern**:

1. **Local-First Writes:** All vehicle registrations, maintenance record additions, and updates are committed to the local Room SQLite database immediately.
2. **Outbox Operation Queue:** Mutation operations generate corresponding `OutboxOperationEntity` queue entries marked with `SyncStatus.PENDING`.
3. **Background Synchronization:** An `OutboxSyncRequestScheduler` / worker syncs pending outbox records to the backend Spring Boot REST API when network connectivity is available.
4. **Status Lifecycle:** Operations transition from `PENDING` -> `IN_ALL_SYNCED` or retry upon failure with exponential backoff.

## Consequences

### Positive
- Instant UI response with zero user latency during record creation or editing.
- Complete offline capability for creating and reading vehicle records.
- Resilience against intermittent network disconnects.

### Negative
- Added complexity in handling synchronization conflict resolution.
- Necessity to maintain entity mapping between local Room entities, DTOs, and domain models.
