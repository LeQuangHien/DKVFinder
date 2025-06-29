# DKVFinder

## Overview

DKVFinder is an Android application designed to help users locate EV charging stations. The application follows modern Android development practices, utilizing Kotlin, Jetpack libraries, and a modular architecture.

## Features

*   **Find EV Charging Stations:** Locate nearby or relevant electric vehicle charging points.
*   **View Station Details:** Display information about charging stations (e.g., address, availability - *inferred*).
*   **Manage Favorites:** Allow users to mark stations as favorites for quick access.
*   **Loading, Success, Empty, and Error States:** Provides clear UI feedback for different data fetching states.

## Architecture & Tech Stack

The project follow an MVVM (Model-View-ViewModel) architecture pattern, Clean Architecture and modularization.

*   **Programming Language:** [Kotlin](https://kotlinlang.org/)
*   **Architecture:** MVVM
*   **UI:**
    *   [XML with ViewBinding](https://developer.android.com/topic/libraries/view-binding)
    *   [Material Components for Android](https://material.io/develop/android/docs/getting-started)
*   **Core Jetpack Libraries:**
    *   [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
    *   [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) (or StateFlow converted to LiveData)
    *   [Flow](https://developer.android.com/kotlin/flow) (for asynchronous operations)
    *   [Navigation Component](https://developer.android.com/guide/navigation)
    *   [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) (for Dependency Injection)
*   **Networking:**
    *   [Retrofit](https://square.github.io/retrofit/)
    *   [OkHttp Logging Interceptor](https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor)
*   **Data Handling & Persistence:**
    *   [Room](https://developer.android.com/training/data-storage/room) (for local database)
    *   [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) (for simple key-value storage)
*   **Coroutines:** For managing background threads and asynchronous tasks.
*   **Testing:**
    *   [JUnit 4](https://junit.org/junit4/)
    *   [MockK](https://mockk.io/) (for mocking in unit tests)
    *   [Turbine](https://github.com/cashapp/turbine) (for testing Kotlin Flows)
*   **Build System:** [Gradle](https://gradle.org/) with Kotlin DSL (`build.gradle.kts`)
*   **Modularity:** The project is structured into multiple modules (e.g., `app`, `feature:evcharging`, `core:data`, `core:network`, `core:database`).

## Modules

*   **:app**: The main application module, responsible for assembling the UI and features.
*   **:feature:evcharging**: Contains the logic and UI related to finding and displaying EV charging stations.
*   **:core:common**: Likely contains common utilities, base classes, or shared testing rules (like `MainDispatcherRule`).
*   **:core:data**: Handles data sources, repositories, and data mapping.
*   **:core:database**: Manages local database operations (likely using Room).
*   **:core:datastore**: Manages key-value data storage (likely using Jetpack DataStore).
*   **:core:domain**: Contains business logic, use cases (interactors).
*   **:core:model**: Defines data models/entities used across the application.
*   **:core:network**: Manages remote API calls and network configurations.
