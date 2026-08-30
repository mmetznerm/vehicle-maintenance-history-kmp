# Vehicle Maintenance History — Mobile

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

For local API calls, the Android emulator defaults to `http://10.0.2.2:8080`. You can override it in `local.properties`:

```properties
api.baseUrl=http://10.0.2.2:8080
```

## Current scope

Local vehicle and maintenance flows are implemented. Backend synchronization, refresh-token handling, resilient offline retries, image upload, and iOS Keychain storage are planned next.

## Related project

The web application and Spring Boot API live in [Vehicle Maintenance History](https://github.com/mmetznerm/vehicle-maintenance-history).
