# CareMyCar

CareMyCar is an Android app for managing a vehicle's lifecycle: authentication, vehicle profiles, maintenance history and recommendations, parts marketplace, and service orders with report exports. It is built with Jetpack Compose and a clean MVVM-style structure, using Hilt for dependency injection and Retrofit/OkHttp for networking.

## Highlights
- Secure authentication with encrypted local storage for access tokens and user data.
- Vehicle catalog and user-owned vehicles management.
- Maintenance tracking with upcoming and recommended services.
- Parts catalog and marketplace purchase flow.
- Service orders lifecycle (create, start, complete, cancel) and PDF report downloads.

## Tech Stack
- Kotlin + Jetpack Compose (Material 3)
- Hilt (DI), ViewModel/UiState, Repository pattern
- Retrofit + OkHttp (REST client, auth interceptor, logging)
- Coroutines
- EncryptedSharedPreferences (secure storage)
- Glide + Lottie (media and animations)

## Architecture
The app follows a Compose-first MVVM approach:
- `screens/` defines UI screens and user flows.
- `viewmodel`/`session` classes provide state and business logic.
- `repository/` encapsulates API access and token management.
- `api/ApiService.kt` defines all REST endpoints.
- `NetworkModule.kt` wires dependencies using Hilt.

## Getting Started
### Prerequisites
- Android Studio (latest stable)
- JDK 17
- Android SDK (minSdk 24, target/compileSdk 36)

### Configure the API base URL
The base URL is set through `apiBaseUrl` in `gradle.properties`.

For local emulator development, the default is:
`http://10.0.2.2:5000/`

Update the `apiBaseUrl` value in `gradle.properties` to match your backend environment.

### Run
1. Open the project in Android Studio.
2. Sync Gradle.
3. Run the `app` configuration on an emulator or device.

## Build
```bash
./gradlew :app:assembleDebug
```

## Test
```bash
./gradlew test
```

## Notable Endpoints
The app integrates with a REST API that provides:
- Auth (`/api/auth/*`)
- Vehicles (`/api/vehicles`, `/api/catalog/vehicles`)
- Maintenance insights (`/api/maintenance/insights/*`)
- Parts and marketplace (`/api/parts`, `/api/orders/marketplace/*`)
- Service orders and reports (`/api/service-orders/*`)

## Project Status
This project is actively developed as a portfolio-grade mobile client. The backend is expected to be available for full functionality; in its absence the UI and local flows can still be explored.

## License
This repository does not include a license. All rights reserved unless a license is added.
