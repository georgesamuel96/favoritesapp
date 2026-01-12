# Dependency Injection with Koin

This guide covers implementing dependency injection in KMP using Koin.

## Table of Contents
- [Overview](#overview)
- [Why Dependency Injection?](#why-dependency-injection)
- [Setup](#setup)
- [Creating Modules](#creating-modules)
- [Injecting Dependencies](#injecting-dependencies)
- [Using with Voyager](#using-with-voyager)

---

## Overview

Koin is a lightweight dependency injection framework for Kotlin. It works on all KMP targets.

| Android Alternative | KMP Solution |
|--------------------|--------------|
| Hilt/Dagger | Koin |

---

## Why Dependency Injection?

### Without DI (Tight Coupling)

```kotlin
class ListScreenModel : ScreenModel {
    // Creates its own dependencies
    private val api = MovieApi()
    private val repository = MovieRepository(api)
}
```

Problems:
- Hard to test (can't mock dependencies)
- Creates multiple instances
- Classes are tightly coupled

### With DI (Loose Coupling)

```kotlin
class ListScreenModel(
    private val repository: MovieRepository  // Injected from outside
) : ScreenModel
```

Benefits:
- Easy to test (inject mock repository)
- Single instance shared across app
- Classes don't know how dependencies are created

---

## Setup

### Add Dependencies

In `build.gradle.kts`:

```kotlin
commonMain.dependencies {
    implementation("io.insert-koin:koin-core:3.5.6")
    implementation("io.insert-koin:koin-compose:1.1.5")
}
```

If using Voyager:

```kotlin
commonMain.dependencies {
    implementation("cafe.adriel.voyager:voyager-koin:1.0.0")
}
```

---

## Creating Modules

### What is a Module?

A module defines how to create your dependencies:

```kotlin
// commonMain/kotlin/di/AppModule.kt
package com.yourpackage.di

import org.koin.dsl.module

val appModule = module {
    // Define dependencies here
}
```

### Defining Dependencies

```kotlin
val appModule = module {
    
    // Singleton: One instance, reused everywhere
    single { MovieApi() }
    
    // Singleton with dependency: get() retrieves MovieApi
    single { MovieRepository(api = get()) }
    
    // Singleton: Database
    single { createDatabase() }
    
    // Singleton with dependency
    single { FavoritesRepository(database = get()) }
    
    // Factory: New instance each time
    factory { ListScreenModel(repository = get()) }
}
```

### Understanding `single` vs `factory`

| Keyword | Behavior | Use For |
|---------|----------|---------|
| `single` | Creates one instance, reused everywhere | Repositories, API clients, Database |
| `factory` | Creates new instance each time | ScreenModels, ViewModels |

### Understanding `get()`

`get()` retrieves a dependency from Koin:

```kotlin
single { MovieRepository(api = get()) }
//                            ^^^^^ 
//                     Gets MovieApi instance
```

Koin figures out which dependency to inject based on the type.

---

## Initializing Koin

### In App.kt (Compose Multiplatform)

```kotlin
import org.koin.compose.KoinApplication
import com.yourpackage.di.appModule

@Composable
fun App() {
    KoinApplication(application = {
        modules(appModule)
    }) {
        MaterialTheme {
            Navigator(ListScreen())
        }
    }
}
```

### Multiple Modules

```kotlin
// Separate modules by feature
val networkModule = module {
    single { MovieApi() }
}

val databaseModule = module {
    single { createDatabase() }
}

val repositoryModule = module {
    single { MovieRepository(get()) }
    single { FavoritesRepository(get()) }
}

// Combine in App.kt
KoinApplication(application = {
    modules(networkModule, databaseModule, repositoryModule)
})
```

---

## Injecting Dependencies

### In Composables with koinInject

```kotlin
import org.koin.compose.koinInject

@Composable
fun SomeScreen() {
    val repository: MovieRepository = koinInject()
    // Use repository
}
```

### In Voyager Screens with getScreenModel

```kotlin
import cafe.adriel.voyager.koin.getScreenModel

class ListScreen : Screen {
    
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<ListScreenModel>()
        val uiState by screenModel.uiState.collectAsState()
        
        // Use uiState
    }
}
```

---

## Using with Voyager

### ScreenModel with Dependencies

```kotlin
// ScreenModel that needs dependencies
class ListScreenModel(
    private val repository: MovieRepository
) : ScreenModel {
    // ...
}

// Register in Koin module
val appModule = module {
    single { MovieApi() }
    single { MovieRepository(get()) }
    factory { ListScreenModel(get()) }  // Factory creates new instance
}
```

### Screen Using ScreenModel

```kotlin
class ListScreen : Screen {
    
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<ListScreenModel>()
        val uiState by screenModel.uiState.collectAsState()
        
        ListScreenContent(
            uiState = uiState,
            // ...
        )
    }
}
```

### ScreenModel with Runtime Parameters

When you need to pass data to ScreenModel (like a Movie object):

```kotlin
class DetailsScreenModel(
    private val movie: Movie,
    private val repository: FavoritesRepository
) : ScreenModel

// In the Screen
class DetailsScreen(private val movie: Movie) : Screen {
    
    @Composable
    override fun Content() {
        val repository: FavoritesRepository = koinInject()
        val screenModel = rememberScreenModel {
            DetailsScreenModel(movie, repository)
        }
        // ...
    }
}
```

---

## Complete Example

### Module Definition

```kotlin
// commonMain/kotlin/di/AppModule.kt
package com.yourpackage.di

import com.yourpackage.data.local.createDatabase
import com.yourpackage.data.remote.MovieApi
import com.yourpackage.data.repository.FavoritesRepository
import com.yourpackage.data.repository.MovieRepository
import com.yourpackage.ui.screens.list.ListScreenModel
import com.yourpackage.ui.screens.favorites.FavoritesScreenModel
import org.koin.dsl.module

val appModule = module {
    // Data Layer
    single { createDatabase() }
    single { MovieApi() }
    
    // Repositories
    single { MovieRepository(api = get()) }
    single { FavoritesRepository(database = get()) }
    
    // ScreenModels
    factory { ListScreenModel(repository = get()) }
    factory { FavoritesScreenModel(repository = get()) }
}
```

### App Initialization

```kotlin
// commonMain/kotlin/App.kt
@Composable
fun App() {
    KoinApplication(application = {
        modules(appModule)
    }) {
        MaterialTheme {
            Navigator(ListScreen())
        }
    }
}
```

### Screen Usage

```kotlin
// ListScreen.kt
class ListScreen : Screen {
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<ListScreenModel>()
        val uiState by screenModel.uiState.collectAsState()
        
        ListScreenContent(
            uiState = uiState,
            onMovieClick = { movie ->
                navigator.push(DetailsScreen(movie))
            }
        )
    }
}

// DetailsScreen.kt (with runtime parameter)
class DetailsScreen(private val movie: Movie) : Screen {
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val repository: FavoritesRepository = koinInject()
        val screenModel = rememberScreenModel {
            DetailsScreenModel(movie, repository)
        }
        val isFavorite by screenModel.isFavorite.collectAsState()
        
        DetailsScreenContent(
            movie = movie,
            isFavorite = isFavorite,
            onFavoriteClick = { screenModel.toggleFavorite() },
            onBackClick = { navigator.pop() }
        )
    }
}
```

---

## Dependency Graph

```
┌─────────────────────────────────────────────────────┐
│                    Koin Module                       │
├─────────────────────────────────────────────────────┤
│                                                      │
│  ┌──────────┐    ┌────────────────┐                │
│  │ MovieApi │───▶│ MovieRepository │               │
│  └──────────┘    └───────┬────────┘                │
│                          │                          │
│                          ▼                          │
│                  ┌───────────────┐                  │
│                  │ListScreenModel│                  │
│                  └───────────────┘                  │
│                                                      │
│  ┌──────────┐    ┌───────────────────┐             │
│  │ Database │───▶│FavoritesRepository│             │
│  └──────────┘    └────────┬──────────┘             │
│                           │                         │
│                           ▼                         │
│               ┌─────────────────────┐              │
│               │FavoritesScreenModel │              │
│               └─────────────────────┘              │
└─────────────────────────────────────────────────────┘
```

---

## Key Takeaways

1. **Koin provides DI for KMP** — lightweight and easy to use
2. **`single`** creates singletons — use for repositories, databases, API clients
3. **`factory`** creates new instances — use for ScreenModels
4. **`get()`** retrieves dependencies by type
5. **`koinInject()`** injects in composables
6. **`getScreenModel()`** gets ScreenModels in Voyager screens
7. **Use `rememberScreenModel`** when passing runtime parameters

---

## Next Steps

Continue to [Managing Resources](../07-resources/README.md)