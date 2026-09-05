# AGENTS.md

## Project

Vehicle Maintenance History KMP is the mobile companion application for the
Vehicle Maintenance History platform.

The application targets:

- Android
- iOS

The project uses Kotlin Multiplatform and Compose Multiplatform.

## Architecture

Use the existing Clean Architecture structure.

Main concepts:

- Presentation
- Domain
- Data

The application follows an MVVM/MVI-oriented architecture depending on the
existing feature implementation.

Do not introduce a different architectural pattern without a specific task.

## Offline First

The mobile application is designed to work offline.

Local data is stored using Room KMP / SQLite.

User actions that modify server data must be persisted locally before being
considered complete when offline support applies.

The Outbox Pattern is used to queue operations that must eventually be
synchronized with the backend.

## Synchronization

Synchronization must be:

- resilient
- retryable
- idempotent where possible
- safe when connectivity changes
- safe when the application is restarted

Do not assume network availability.

Network failures must not cause loss of locally persisted user data.

## API

The backend API is the source of truth for synchronized server data.

Use Ktor for HTTP communication.

Authentication must use the existing token storage abstraction.

Never:

- hardcode tokens
- log authentication tokens
- store secrets in source control

## Local Database

Use Room KMP / SQLite for persistent local data.

Database changes must:

- include the appropriate migration
- preserve existing user data
- be tested

Do not delete or recreate the database as a shortcut for migrations.

## Sync Outbox

Outbox operations should contain enough information to:

- identify the operation
- identify the affected entity
- identify the operation type
- preserve required payload
- track processing state
- support retry behavior

Failed operations must not silently disappear.

## Error Handling

Network errors should be treated separately from business validation errors.

Examples:

- timeout
- no connectivity
- server unavailable
- authentication failure
- validation failure
- conflict

Use the existing project error-handling conventions.

## UI

Use Compose Multiplatform and existing design patterns.

Do not introduce a new UI architecture or design system for a single feature.

Keep screens focused on user workflows.

## Testing

Behavior changes must include tests.

Prefer:

- unit tests for domain logic
- repository tests for persistence/network behavior
- synchronization tests
- ViewModel/state tests where appropriate

Critical offline/synchronization behavior must be tested.

## Dependencies

Do not add dependencies unless required.

Prefer existing libraries already used by the project.

## Scope Control

Do not:

- refactor unrelated features
- rename unrelated classes
- change architecture without a requirement
- upgrade dependencies without a task
- implement backend functionality inside the mobile repository

Backend changes belong in:

vehicle-maintenance-history

## Roadmap

Use the VMH task identifier format.

Example:

VMH-004.04

The same identifier must be used across repositories when a feature spans
backend and mobile.

## Completion Checklist

Before considering a task complete:

- [ ] Requirements implemented
- [ ] Offline behavior considered
- [ ] Tests added or updated
- [ ] Existing tests passing
- [ ] No unrelated changes
- [ ] No secrets committed
- [ ] Android build remains valid
- [ ] iOS/shared code remains valid when applicable