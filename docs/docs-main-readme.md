# Kotlin Multiplatform Learning Guide

A comprehensive guide to building cross-platform mobile applications with Kotlin Multiplatform (KMP) and Compose Multiplatform.

## About This Project

FavoritesApp is a movie favorites application that demonstrates how to build a complete KMP application that runs on both Android and iOS from a single codebase.

### Features
- Fetch movies from TMDB API
- View movie details
- Save/remove favorites to local database
- Works on Android and iOS

### Tech Stack
| Layer | Library |
|-------|---------|
| UI | Compose Multiplatform |
| Navigation | Voyager |
| State Management | ScreenModel + StateFlow |
| Networking | Ktor |
| Database | SQLDelight |
| Dependency Injection | Koin |
| Image Loading | Coil 3 |

---

## Learning Path

### Phase 1: Foundations
1. [Project Setup & Structure](./01-project-setup/README.md)
   - Understanding KMP project structure
   - Source sets: commonMain, androidMain, iosMain
   - The expect/actual pattern

### Phase 2: UI Development
2. [Compose Multiplatform UI](./02-compose-multiplatform-ui/README.md)
   - Building shared UI components
   - Compose Multiplatform vs Jetpack Compose
   - Handling platform differences in UI

3. [Navigation with Voyager](./03-navigation/README.md)
   - Setting up Voyager navigation
   - Navigating between screens
   - Passing data between screens

### Phase 3: Data Layer
4. [Networking with Ktor](./04-networking/README.md)
   - Setting up Ktor HTTP client
   - Making API requests
   - JSON serialization with Kotlinx Serialization

5. [Local Database with SQLDelight](./05-local-database/README.md)
   - Setting up SQLDelight
   - Creating database schema
   - Platform-specific database drivers

### Phase 4: Architecture
6. [Dependency Injection with Koin](./06-dependency-injection/README.md)
   - Setting up Koin in KMP
   - Creating modules
   - Injecting dependencies

### Additional Topics
7. [Managing Resources](./07-resources/README.md)
   - Adding images and icons
   - Using custom resources across platforms

---

## Prerequisites

- Android Studio (latest stable)
- Xcode (for iOS development)
- Kotlin Multiplatform plugin for Android Studio
- Basic knowledge of Kotlin and Jetpack Compose

---

## Getting Started

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle
4. Run on Android emulator or iOS simulator

---

## Architecture Overview

```
com.learning.favoritesapp/
├── data/
│   ├── local/          # Database setup
│   ├── remote/         # API client and DTOs
│   └── repository/     # Data repositories
├── di/                 # Dependency injection
├── model/              # Domain models
└── ui/
    ├── components/     # Reusable UI components
    └── screens/        # App screens
```

---

## License

This project is for educational purposes.
