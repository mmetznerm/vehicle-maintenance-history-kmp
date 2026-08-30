# ADR 0002: Compose Multiplatform with Clean Architecture & Koin DI

## Status
Accepted

## Context
The application needs to target both Android and iOS while sharing maximum business logic, UI design language, network handlers, and data persistence without sacrificing maintainability or testability.

## Decision
We structured the mobile project using **Compose Multiplatform** and **Clean Architecture**:

1. **Layer Separation:**
   - **Presentation:** Compose UI components, Material 3 styling, Navigation Compose, `StateFlow` and ViewModels (`androidx.lifecycle`).
   - **Domain:** Pure Kotlin models and repository interfaces.
   - **Data:** Repository implementations, Ktor HTTP client DataSources, Room DAOs, and Mappers.
2. **Dependency Injection:** Koin (`koin-core`, `koin-compose`, `koin-viewmodel`) manages lifecycle and dependency graphs cleanly across shared modules.
3. **Ktor Client Abstraction:** Engine-specific network drivers (OkHttp on Android, Darwin on iOS, Mock Engine for unit tests).

## Consequences

### Positive
- Over 90% codebase sharing across Android and iOS.
- Clear isolation of domain logic for unit testing (`kotlin.test`).
- Single source of truth for UI design system (Material 3).

### Negative
- iOS framework configuration requires careful KSP setup for Room.
