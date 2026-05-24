# Vehicle Maintenance KMP

A Kotlin Multiplatform app for searching and registering vehicle maintenance history, built as a professional portfolio project focused on modern mobile architecture, backend integration, and an incremental offline-first strategy.

The goal of this repository is not to showcase the largest possible number of libraries. The goal is to build a coherent, sustainable, production-inspired mobile codebase: shared UI with Compose Multiplatform, predictable state management, local persistence, shared networking, and pragmatic synchronization.

## Status

This project is under active development.

Currently implemented:

- Kotlin Multiplatform targeting Android and iOS.
- Shared UI with Compose Multiplatform.
- Custom Material 3 visual theme.
- Modern login, vehicle search, vehicle registration, and maintenance registration screens.
- Android launcher icon.
- Shared navigation.
- Dependency injection with Koin.
- Shared networking with Ktor Client.
- Initial login/JWT integration.
- Ktor Bearer token attachment for authenticated requests.
- Platform token storage abstraction.
- Android token storage backed by Android Keystore encryption.
- Local persistence with Room Multiplatform.
- Initial vehicle search flow.
- Local vehicle registration.
- Local maintenance registration.
- Local-first reads for vehicles and maintenance records.
- Initial best-effort sync for pending records.
- Shared ViewModel tests.

Not implemented yet:

- Real backend integration.
- Image upload.
- Refresh token flow.
- iOS Keychain-backed token storage.
- Robust outbox with retry/backoff.
- Mobile observability.
- Conflict resolution.

These items are part of the incremental roadmap.

## Domain

The app models a simple and realistic vehicle maintenance history flow:

1. The user searches by license plate.
2. The app reads from the local database first.
3. The network layer tries to synchronize the latest data.
4. If the vehicle exists, the app displays vehicle details and maintenance history.
5. If the vehicle does not exist, the user can register a new vehicle.
6. The user can register maintenance records locally.
7. Offline operations remain pending until a future synchronization.

The expected backend is a Java Spring Boot microservices architecture using REST APIs, JWT, PostgreSQL, Kafka, Docker, and OpenAPI.

## Stack

- Kotlin Multiplatform
- Compose Multiplatform
- Android
- iOS
- Ktor Client
- Kotlinx Serialization
- Coroutines
- Flow / StateFlow
- Koin
- Room Multiplatform
- Navigation Compose
- Gradle Kotlin DSL
- Kotlin Test

## Architecture

The current architecture uses pragmatic feature-based organization, with clear layers and no unnecessary abstractions:

```text
composeApp/src/commonMain/kotlin/com/mmetzner/vehiclemaintenance
+-- core
|   +-- auth
|   +-- database
|   +-- di
|   +-- navigation
|   +-- network
|   +-- ui
|   +-- util
+-- feature
    +-- auth
    |   +-- data
    |   +-- domain
    |   +-- presentation
    +-- vehicle
        +-- data
        |   +-- local
        |   |   +-- dao
        |   |   +-- entity
        |   +-- mapper
        |   +-- remote
        |       +-- dto
        +-- domain
        |   +-- model
        |   +-- repository
        +-- presentation
            +-- addmaintenance
            +-- addvehicle
            +-- search
```

### Layers

`presentation`

Contains Compose screens, ViewModels, and UI state. The UI observes `StateFlow` and sends explicit events to ViewModels.

`domain`

Contains feature models and contracts. This layer is intentionally lightweight because the project does not yet need a large use case hierarchy.

`data`

Coordinates local persistence, remote calls, and mapping. The repository is the main offline-first orchestration boundary.

`core`

Contains shared infrastructure: database, dependency injection, navigation, networking, and multiplatform utilities.

## UI Direction

The app uses a clean Material 3 interface with a restrained teal, graphite, white, and amber palette. The current UI direction is inspired by modern transactional apps: compact forms, clear primary actions, strong hierarchy, and low visual noise.

Current screens:

- Login
- Vehicle search
- Vehicle registration
- Maintenance registration

The design intentionally avoids a marketing-style layout. The goal is to look like a real operational mobile app that a mechanic shop, fleet operator, or vehicle owner could use repeatedly.

## Offline-First Strategy

The offline-first strategy is being built incrementally.

Current decision:

- The local database is the primary source of truth for reads.
- Vehicle search observes local data through `Flow`.
- Network synchronization updates local data.
- New records are persisted locally with `SyncStatus.PENDING`.
- The app attempts to synchronize pending records in the background using a best-effort approach.

This already exercises a local-first flow without introducing a complex sync engine too early.

Planned evolution:

- Introduce a dedicated outbox for pending operations.
- Store operation type, payload, retry count, error, and timestamps.
- Implement retry with backoff.
- Separate manual sync from automatic sync.
- Add connectivity awareness.
- Add a dedicated queue for image uploads.
- Represent sync states such as `PENDING`, `SYNCING`, `SYNCED`, and `FAILED`.

At this stage, the project intentionally avoids heavier solutions such as event sourcing, CRDTs, or a generic conflict engine. For this domain, simple eventual consistency and backend idempotency are more realistic choices.

## Networking

Networking is shared in `commonMain` using Ktor Client.

The base URL configuration uses `expect/actual`:

- Android Emulator: `http://10.0.2.2:8080`
- iOS Simulator: `http://localhost:8080`

Vehicle API calls go through `VehicleRemoteDataSource`, keeping endpoint details out of the repository.

Authentication uses `AuthRemoteDataSource` for login and a shared `AuthRepository` for session checks and logout. Ktor's Bearer auth plugin attaches the stored access token to authenticated requests while skipping the login endpoint.

Current structure:

```text
core/network
+-- ApiConfig.kt
+-- createHttpClient.kt

core/auth
+-- AuthTokens.kt
+-- AuthTokenStore.kt

feature/auth/data/remote
+-- AuthRemoteDataSource.kt
+-- dto

feature/vehicle/data/remote
+-- VehicleRemoteDataSource.kt
+-- dto
    +-- VehicleResponse.kt
    +-- MaintenanceResponse.kt
```

## Local Persistence

The project uses Room Multiplatform with the bundled SQLite driver.

Current entities:

- `VehicleEntity`
- `MaintenanceEntity`
- `MaintenancePhotoEntity`

Relationships:

- A vehicle has many maintenance records.
- A maintenance record has many photos.

Entities already carry `syncStatus`, preparing the codebase for a more robust offline-first sync flow.

## Authentication

Authentication is implemented as a mobile foundation, but it still depends on the future Spring Boot backend to become end-to-end.

Current behavior:

- Login screen calls `/v1/auth/login`.
- Access and refresh tokens are stored through a multiplatform `AuthTokenStore`.
- Android persists tokens using Android Keystore-backed encryption.
- iOS currently uses a simple persistent implementation and should move to Keychain before production use.
- Logout clears stored tokens and returns the user to the login flow.

Planned behavior:

- Refresh access tokens when the backend contract is available.
- Handle expired sessions centrally.
- Add HTTP 401 handling with deterministic logout or token refresh.
- Move iOS token storage to Keychain.

## Technical Decisions

### Room Multiplatform Instead of SQLDelight

Room Multiplatform reduces friction for developers coming from Android and keeps a familiar DAO/entity/relationship model.

Trade-off:

- Good for productivity and readability for Android reviewers.
- Less explicit than SQLDelight for SQL control and manual migration design.

### Shared Compose Multiplatform UI

The UI lives in `commonMain`, allowing screens and state handling to be reused across Android and iOS.

Trade-off:

- Excellent for demonstrating real KMP usage.
- Requires care with native UX expectations and platform-specific limitations.

### Repository as the Main Boundary

The project does not introduce interfaces for every data source at this stage. The main feature contract is the repository.

Trade-off:

- Less boilerplate.
- Simpler architecture.
- Deeper sync tests may justify additional abstractions later.

### Use Cases on Demand

Use cases will be introduced only when business logic is significant enough to justify a dedicated layer.

At this stage, creating one use case per repository call would be architecture theater rather than a practical need.

## Running the Project

Prerequisites:

- Recent Android Studio version with Kotlin Multiplatform support.
- JDK compatible with the Android Gradle Plugin.
- Xcode for iOS builds.
- Local backend running on port `8080` when real integration is available.

Build Android:

```powershell
.\gradlew.bat :composeApp:assembleDebug
```

Compile Android Kotlin and run shared tests:

```powershell
.\gradlew.bat :composeApp:compileDebugKotlinAndroid :composeApp:allTests
```

Run iOS:

Open the `iosApp` project in Xcode and run it on a simulator.

## Tests

The project has tests in `commonTest`, covering ViewModel behavior and the initial local-first flow.

Examples:

- When local cache exists, search shows success even if the network fails.
- When there is no local cache and the network fails, search shows an error.
- Valid vehicle input is saved through the repository and emits a navigation event.
- Invalid input does not trigger saving.

## Roadmap

### Phase 1: Mobile Foundation

- KMP Android/iOS structure.
- Compose Multiplatform.
- Koin.
- Room Multiplatform.
- Ktor Client.
- Shared navigation.
- Initial vehicle flow.

Status: in progress.

### Phase 2: Real Backend Integration

- Environment-based configuration.
- Contracts aligned with OpenAPI.
- Centralized HTTP error handling.
- Refresh token flow.
- Expired session handling.
- iOS Keychain token storage.
- Backend-driven auth validation.

### Phase 3: Professional Offline-First

- Dedicated outbox.
- Retry with backoff.
- Manual sync.
- Automatic sync.
- Connectivity awareness.
- Sync states in the UI.

### Phase 4: Image Upload

- Multiple photo selection.
- Local persistence for pending files.
- Deferred upload.
- Independent retry per photo.
- Photo/maintenance association after sync.

### Phase 5: Production Quality

- Structured logs.
- Sync metrics.
- Repository tests.
- Mapper tests.
- Data source tests using Ktor MockEngine.
- Architecture documentation.
- GitHub Actions CI.

## What This Project Demonstrates

- Incremental evolution of a KMP app.
- Clear separation between UI, domain, and data.
- Pragmatic use of modern mobile architecture.
- Offline-first thinking from the beginning.
- Backend integration readiness for Java Spring Boot.
- Awareness of portfolio vs production trade-offs.
- Real multiplatform care, including Android/iOS differences.

## Current Limitations

- Synchronization does not yet have persistent retry.
- Sync failures are not yet richly exposed to the UI.
- The networking layer does not yet support refresh tokens.
- Image upload is currently only represented in the local data model.
- The real backend is not connected yet.
- iOS token storage still needs a Keychain implementation before production use.

These limitations are intentional so the project remains incremental and reviewable.

## Commit Convention

Suggested semantic commits for the project evolution:

```text
feat: add vehicle search flow
feat: add local vehicle persistence
feat: add maintenance registration
refactor: extract vehicle remote data source and api config
fix: make shared vehicle flow compile across kmp targets
test: cover vehicle search offline behavior
docs: document kmp architecture and roadmap
```

## License

This project does not have a license yet.

Before publishing it as open source, add an explicit license such as MIT or Apache 2.0.
