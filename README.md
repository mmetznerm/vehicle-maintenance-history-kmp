# Vehicle Maintenance History — Mobile

[![Mobile CI](https://github.com/mmetznerm/vehicle-maintenance-history-kmp/actions/workflows/ci.yml/badge.svg)](https://github.com/mmetznerm/vehicle-maintenance-history-kmp/actions/workflows/ci.yml)
![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin%20Multiplatform-2.x-7F52FF?logo=kotlin&logoColor=white)
![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-Material3-4285F4?logo=jetpackcompose&logoColor=white)
![Android](https://img.shields.io/badge/Android-Native-3DDC84?logo=android&logoColor=white)
![iOS](https://img.shields.io/badge/iOS-Native-000000?logo=apple&logoColor=white)
![Room KMP](https://img.shields.io/badge/Room-Multiplatform-4285F4?logo=sqlite&logoColor=white)
![Ktor](https://img.shields.io/badge/Ktor-Client-008080?logo=ktor&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

Kotlin Multiplatform companion app for the [Vehicle Maintenance History backend](https://github.com/mmetznerm/vehicle-maintenance-history). It lets a vehicle owner sign in, register a vehicle, and record maintenance history from Android or iOS.

The project is a work in progress focused on a shared mobile codebase, Clean Architecture, and an incremental local-first experience.

## Highlights

- Shared Compose Multiplatform UI for Android and iOS
- Login and owner-registration flows
- Vehicle home, vehicle details, vehicle registration, and maintenance registration screens
- Clean Architecture (Data, Domain, Presentation) with MVVM / MVI presentation state management
- Local persistence with Room Multiplatform & SQLite bundled driver
- Offline-first Outbox Pattern queueing local changes for background server sync
- Shared networking with Ktor Client and JWT token storage abstraction

## Architecture & Data Flow

```mermaid
graph TD
    subgraph Presentation Layer
        UI[Compose Multiplatform UI]
        VM[ViewModels / StateFlow]
    end

    subgraph Domain Layer
        RepoInt[Repository Interfaces]
        Models[Domain Models]
    end

    subgraph Data Layer
        RepoImpl[Repository Implementation]
        Room[(Room KMP Database)]
        Outbox[Outbox Sync Scheduler]
        Ktor[Ktor Client REST Engine]
    end

    UI --> VM
    VM --> RepoInt
    RepoInt --> RepoImpl
    RepoImpl --> Room
    RepoImpl --> Outbox
    Outbox --> Ktor
    Ktor -->|JWT Auth REST API| Backend[Spring Boot Backend]
```

## Stack

- **Platforms:** Android & iOS (Shared Kotlin Multiplatform codebase)
- **UI & Navigation:** Compose Multiplatform, Material 3, Navigation Compose, Material Extended Icons.
- **Architecture & Dependency Injection:** Clean Architecture, MVVM / MVI pattern, Koin (Core, ViewModel, Compose) for Dependency Injection, StateFlow & Coroutines.
- **Local Persistence & Offline Sync:** Room Multiplatform (with KSP schema generation, bundled SQLite Driver), Offline Outbox Pattern (`OutboxOperationEntity`, `SyncStatus`).
- **Networking & Auth:** Ktor Client (OkHttp for Android, Darwin for iOS, Mock engine for unit testing), Ktor Auth (JWT Token Store abstraction), Kotlinx Serialization (JSON).
- **Background Tasks & Testing:** WorkManager (`androidx.work`), `kotlin.test`, `kotlinx-coroutines-test`, Ktor Client Mock.

## CI/CD Pipeline

The project includes an automated GitHub Actions workflow (`Mobile CI`):
- **Build & Test:** Compiles the Android application target (`assembleDebug`) and executes all shared multiplatform unit test suites (`allTests`) on every pull request and push to `main`.

## Screenshots

| Sign-in screen | Dashboard | Vehicle details |
| :---: | :---: | :---: |
| <img src="docs/images/portfolio/login.png" width="240" alt="Sign-in screen" /> | <img src="docs/images/portfolio/dashboard.png" width="240" alt="Dashboard" /> | <img src="docs/images/portfolio/vehicle-details.png" width="240" alt="Vehicle details" /> |

| Vehicle registration | Maintenance registration |
| :---: | :---: |
| <img src="docs/images/portfolio/vehicle-form.png" width="240" alt="Vehicle registration" /> | <img src="docs/images/portfolio/maintenance-form.png" width="240" alt="Maintenance registration" /> |

## Run locally

Prerequisites: a current Android Studio installation and a compatible JDK. Xcode is also required to run the iOS target.

Build the Android app:

```powershell
.\gradlew.bat :composeApp:assembleDebug
```

Run the shared tests:

```powershell
.\gradlew.bat :composeApp:allTests
```

To run on iOS, open `iosApp` in Xcode and start a simulator.

For local API calls, add the API URL to your ignored `local.properties` file. The repository includes `local.properties.example` as a reference; keep your own `sdk.dir` entry unchanged.

## Current scope

Local vehicle and maintenance flows are implemented. Backend synchronization, refresh-token handling, resilient offline retries, image upload, and iOS Keychain storage are planned next.

## Documentation & Architecture

- [Architecture Decision Records (ADRs)](docs/adr/)
- [Pull Request Template](.github/pull_request_template.md)
- [Security Policy](SECURITY.md)

## Related project

The web application and Spring Boot API live in [Vehicle Maintenance History](https://github.com/mmetznerm/vehicle-maintenance-history).
