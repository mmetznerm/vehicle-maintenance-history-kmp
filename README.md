# Vehicle Maintenance History — Mobile

[![Mobile CI](https://github.com/mmetznerm/vehicle-maintenance-history-kmp/actions/workflows/ci.yml/badge.svg)](https://github.com/mmetznerm/vehicle-maintenance-history-kmp/actions/workflows/ci.yml)
![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin%20Multiplatform-7F52FF)
![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-4285F4)

Kotlin Multiplatform companion app for the [Vehicle Maintenance History backend](https://github.com/mmetznerm/vehicle-maintenance-history). It lets a vehicle owner sign in, register a vehicle, and record maintenance history from Android or iOS.

The project is a work in progress focused on a shared mobile codebase and an incremental local-first experience.

## Highlights

- Shared Compose Multiplatform UI for Android and iOS
- Login and owner-registration flows
- Vehicle home, vehicle registration, and maintenance registration screens
- Local persistence with Room Multiplatform
- Shared networking with Ktor and JWT token storage abstraction
- Best-effort synchronization foundation for pending records

## Stack

Kotlin Multiplatform, Compose Multiplatform, Material 3, Ktor Client, Room Multiplatform, Koin, Coroutines/Flow, Kotlinx Serialization, and Gradle Kotlin DSL.

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

## Related project

The web application and Spring Boot API live in [Vehicle Maintenance History](https://github.com/mmetznerm/vehicle-maintenance-history).
