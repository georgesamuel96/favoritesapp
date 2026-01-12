# KMP Project Setup & Structure

This guide explains how to set up a Kotlin Multiplatform project and understand its structure.

## Table of Contents
- [Creating a KMP Project](#creating-a-kmp-project)
- [Project Structure](#project-structure)
- [Understanding Source Sets](#understanding-source-sets)
- [The Expect/Actual Pattern](#the-expectactual-pattern)
- [Gradle Configuration](#gradle-configuration)

---

## Creating a KMP Project

### Using Android Studio

1. Open **Android Studio**
2. Go to **File → New → New Project**
3. Select **Kotlin Multiplatform App**
4. Configure:
   - **Name:** Your app name
   - **Package name:** com.yourname.appname
   - Check **Share UI with Compose Multiplatform**
5. Click **Finish**

---

## Project Structure

A KMP project has a different structure than a standard Android project:

```
YourApp/
├── composeApp/                    # Main shared module
│   ├── src/
│   │   ├── commonMain/           # Shared code (both platforms)
│   │   │   ├── kotlin/
│   │   │   ├── composeResources/ # Shared resources
│   │   │   └── sqldelight/       # Database schemas
│   │   ├── androidMain/          # Android-specific code
│   │   │   └── kotlin/
│   │   └── iosMain/              # iOS-specific code
│   │       └── kotlin/
│   └── build.gradle.kts
├── iosApp/                        # iOS wrapper project
│   └── iosApp.xcodeproj
├── build.gradle.kts               # Root build file
└── settings.gradle.kts
```

---

## Understanding Source Sets

### What Are Source Sets?

Source sets are folders that contain code for specific targets. KMP uses three main source sets:

| Source Set | Purpose | Example Code |
|------------|---------|--------------|
| `commonMain` | Shared code for all platforms | Business logic, UI, data classes |
| `androidMain` | Android-specific code | Android SDK usage |
| `iosMain` | iOS-specific code | iOS framework usage |

### How Code Flows

```
                    ┌─────────────┐
                    │ commonMain  │
                    │ (shared)    │
                    └──────┬──────┘
                           │
              ┌────────────┴────────────┐
              │                         │
       ┌──────▼──────┐          ┌───────▼─────┐
       │ androidMain │          │   iosMain   │
       │ (Android)   │          │   (iOS)     │
       └─────────────┘          └─────────────┘
```

### What Goes Where?

**commonMain** — Most of your code:
```kotlin
// Data classes
data class Movie(val id: String, val title: String)

// Business logic
class MovieRepository {
    fun getMovies(): List<Movie>
}

// Shared UI
@Composable
fun MovieCard(movie: Movie) { }
```

**androidMain** — Android-specific implementations:
```kotlin
// Using Android Context
actual fun getAppVersion(): String {
    return context.packageManager
        .getPackageInfo(context.packageName, 0)
        .versionName
}
```

**iosMain** — iOS-specific implementations:
```kotlin
// Using iOS frameworks
actual fun getAppVersion(): String {
    return NSBundle.mainBundle.infoDictionary
        ?.get("CFBundleShortVersionString") as? String ?: ""
}
```

---

## The Expect/Actual Pattern

When you need platform-specific code, use `expect`/`actual`:

### Step 1: Declare in commonMain

```kotlin
// commonMain/kotlin/Platform.kt
expect fun getPlatformName(): String
```

The `expect` keyword says: "This function will exist, but each platform provides its own implementation."

### Step 2: Implement in Each Platform

```kotlin
// androidMain/kotlin/Platform.android.kt
actual fun getPlatformName(): String = "Android"
```

```kotlin
// iosMain/kotlin/Platform.ios.kt
actual fun getPlatformName(): String = "iOS"
```

### Works With Classes Too

```kotlin
// commonMain
expect class DatabaseDriver {
    fun connect(): Database
}

// androidMain
actual class DatabaseDriver(private val context: Context) {
    actual fun connect(): Database {
        // Android SQLite implementation
    }
}

// iosMain
actual class DatabaseDriver {
    actual fun connect(): Database {
        // iOS SQLite implementation
    }
}
```

### When to Use Expect/Actual

Use it when you need:
- Platform-specific APIs (file system, sensors, etc.)
- Different implementations per platform
- Platform-specific libraries

**Don't use it when:**
- Code can be identical on both platforms
- A multiplatform library already handles it (Ktor, SQLDelight, etc.)

---

## Gradle Configuration

### Root build.gradle.kts

```kotlin
plugins {
    kotlin("multiplatform") version "2.1.0" apply false
    kotlin("plugin.serialization") version "2.1.0" apply false
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.compose") version "1.6.0" apply false
}
```

### composeApp/build.gradle.kts

```kotlin
plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization")
}

kotlin {
    // Android target
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }
    
    // iOS targets
    listOf(
        iosArm64(),           // Physical devices
        iosSimulatorArm64()   // Simulator
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            // Shared dependencies
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
        }
        
        androidMain.dependencies {
            // Android-only dependencies
        }
        
        iosMain.dependencies {
            // iOS-only dependencies
        }
    }
}
```

### Understanding Targets

- `androidTarget` — Compiles to JVM bytecode for Android
- `iosArm64` — Compiles to native binary for physical iPhones
- `iosSimulatorArm64` — Compiles to native binary for iOS Simulator (on Apple Silicon Mac)

---

## Running Your App

### Android
1. Select Android emulator/device
2. Select `composeApp` configuration
3. Click Run

### iOS
1. Select iOS Simulator
2. Select `iosApp` configuration
3. Click Run

---

## Key Takeaways

1. **Source sets** separate shared code from platform-specific code
2. **commonMain** is where most of your code lives
3. **expect/actual** bridges shared code to platform APIs
4. **Gradle** configures which platforms to build for
5. **iOS needs Xcode** to build and run

---

## Next Steps

Continue to [Compose Multiplatform UI](../02-compose-multiplatform-ui/README.md)
