# CareMyCar

CareMyCar is a native Android application designed to help drivers manage their vehicle lifecycle from one place: vehicle registration, maintenance recommendations, service orders, marketplace purchases, and cost estimation. The project is built as a portfolio-ready mobile client with a REST API integration, a Compose-first UI, and a clean MVVM-oriented structure.

<p align="center">
  <img src="docs/images/login.png" width="210" alt="CareMyCar login screen" />
  <img src="docs/images/vehicles-added.png" width="210" alt="Vehicle garage screen with registered vehicles" />
  <img src="docs/images/service-order-created.png" width="210" alt="Maintenance service order created" />
</p>

## Why This Project Stands Out

- End-to-end Android client connected to a deployed backend API.
- Role-oriented experience for customers and agency workflows.
- Vehicle catalog flow with brand/model selection and generated vehicle profiles.
- Maintenance module with automatic recommendations, service quotes, and service order tracking.
- Marketplace flow for spare parts purchases with quantity controls and recent purchases.
- Secure session handling using encrypted local storage.
- Modern Android stack: Kotlin, Jetpack Compose, Material 3, Hilt, Retrofit, OkHttp, Coroutines, and MVVM patterns.

## Core Features

### Authentication

Users can sign in and keep their session protected through encrypted token storage.

<p align="center">
  <img src="docs/images/login.png" width="230" alt="Login screen with email and password fields" />
</p>

### Vehicle Garage

Customers can register, view, update, and delete vehicles. Each vehicle includes catalog metadata such as brand, model, type, fuel, transmission, color, year, and current mileage.

<p align="center">
  <img src="docs/images/vehicles-list.png" width="210" alt="Garage with one registered vehicle" />
  <img src="docs/images/add-vehicle.png" width="210" alt="Add vehicle form using the catalog" />
  <img src="docs/images/vehicles-added.png" width="210" alt="Garage with multiple registered vehicles" />
  <img src="docs/images/delete-vehicle-dialog.png" width="210" alt="Delete vehicle confirmation dialog" />
</p>

### Maintenance And Service Orders

The maintenance module recommends upcoming services based on vehicle data, lets customers request a quote, and creates trackable service orders with confirmation codes.

<p align="center">
  <img src="docs/images/maintenance-recommendations.png" width="210" alt="Automatic maintenance recommendations" />
  <img src="docs/images/service-quote.png" width="210" alt="Service quote and order information" />
  <img src="docs/images/service-order-created.png" width="210" alt="Created service order with confirmation code" />
</p>

### Marketplace

Customers can browse agency-published parts, select quantities, estimate totals, and review recent purchases.

<p align="center">
  <img src="docs/images/products-marketplace.png" width="230" alt="Products marketplace and recent purchases" />
</p>

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose, Material 3
- **Architecture:** MVVM, repository pattern, state-driven UI
- **Networking:** Retrofit, OkHttp, REST API integration
- **Async:** Kotlin Coroutines
- **Dependency Injection:** Hilt
- **Local Security:** EncryptedSharedPreferences
- **Media:** Glide and Lottie
- **Build System:** Gradle with Android Gradle Plugin

## Architecture Overview

CareMyCar follows a Compose-first MVVM approach where UI, state, data access, and networking are separated by responsibility.

- `screens/` contains the Compose screens and user flows.
- ViewModels expose UI state and coordinate screen actions.
- Repositories isolate API access and data operations.
- `api/ApiService.kt` defines the REST contracts consumed by the app.
- Dependency modules configure Retrofit, OkHttp, authentication, and app-level services.

## API Integration

The Android client connects to the deployed CareMyCar backend:

```text
https://caremycarapi-node.onrender.com/
```

The app consumes endpoints for:

- Authentication and user sessions.
- Vehicle catalog and customer vehicles.
- Maintenance insights and recommendations.
- Service orders, quotes, and confirmation codes.
- Marketplace products and customer purchases.

## Getting Started

### Requirements

- Android Studio
- JDK 17
- Android SDK with the configured compile SDK
- Internet connection for backend API access

### Run The App

```bash
./gradlew :app:assembleDebug
```

Then install the generated APK on an emulator or Android device, or run the `app` configuration directly from Android Studio.

### Run Tests

```bash
./gradlew test
```

### Build Release APK

```bash
./gradlew :app:assembleRelease
```

## Project Scope

This project demonstrates practical Android engineering skills across UI polish, API integration, state management, authentication, and user-centered flows. It is suitable for showcasing in a CV or portfolio because it includes both product-level features and implementation details expected in a real mobile client.

## Documentation

Additional UML/use-case documentation is available in:

- `docs/use-case-diagram.md`
- `docs/use-case-client.mmd`
- `docs/use-case-agency.mmd`
- `docs/use-case-diagram.puml`

## License

This repository does not include a public license. All rights reserved unless a license is added.
