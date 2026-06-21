# Project Overview: Warbler Weather

Warbler Weather is a modular Android application focused on providing weather information.

## Project Structure
- `app`: Main application entry point and navigation setup.
- `core`: Shared resources, network handling, and base libraries.
- `feature-weather`: Weather-related UI components and logic (using Jetpack Compose).
- `feature-location`: Location-related functionality.
- `feature-settings`: Application settings and preferences.

## Tech Stack
- Language: Kotlin
- UI: Jetpack Compose (Material3)
- Architecture: MVVM, Modularization
- DI: Hilt
- Navigation: Jetpack Navigation 3
- Networking: Retrofit
- Persistence: Room
- Concurrency: Kotlin Coroutines

## Guidelines for Junie
- Always build and run tests before submitting.
- Follow established project structure and naming conventions.
- Use `gradle/libs.versions.toml` for all dependency management.
- Maintain consistent coding style with existing codebase.
