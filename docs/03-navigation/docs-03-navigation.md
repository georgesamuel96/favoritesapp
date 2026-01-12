# Navigation with Voyager

This guide covers implementing navigation in KMP using Voyager.

## Table of Contents
- [Overview](#overview)
- [Setup](#setup)
- [Creating Screens](#creating-screens)
- [Basic Navigation](#basic-navigation)
- [Passing Data Between Screens](#passing-data-between-screens)
- [Navigation Actions](#navigation-actions)

---

## Overview

Voyager is a multiplatform navigation library for Compose. It provides:
- Type-safe navigation
- Built-in support for passing data
- ScreenModel (ViewModel equivalent)
- Works on Android, iOS, Desktop, Web

---

## Setup

### Add Dependency

In `build.gradle.kts`:

```kotlin
commonMain.dependencies {
    implementation("cafe.adriel.voyager:voyager-navigator:1.0.0")
    implementation("cafe.adriel.voyager:voyager-screenmodel:1.0.0")
    implementation("cafe.adriel.voyager:voyager-koin:1.0.0") // If using Koin
}
```

---

## Creating Screens

### Basic Screen

Each screen implements the `Screen` interface:

```kotlin
import cafe.adriel.voyager.core.screen.Screen
import androidx.compose.runtime.Composable

class HomeScreen : Screen {
    
    @Composable
    override fun Content() {
        // Your UI here
        Text("Home Screen")
    }
}
```

### Screen with Parameters

```kotlin
class DetailsScreen(
    private val movieId: String
) : Screen {
    
    @Composable
    override fun Content() {
        Text("Details for movie: $movieId")
    }
}
```

### Screen with Object Parameter

For passing complex objects, make them serializable:

```kotlin
import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    val id: String,
    val title: String,
    val posterUrl: String
)

class DetailsScreen(
    private val movie: Movie
) : Screen {
    
    @Composable
    override fun Content() {
        Column {
            Text(movie.title)
            AsyncImage(model = movie.posterUrl, contentDescription = null)
        }
    }
}
```

---

## Basic Navigation

### Setting Up Navigator

In your `App.kt`:

```kotlin
import cafe.adriel.voyager.navigator.Navigator

@Composable
fun App() {
    MaterialTheme {
        Navigator(HomeScreen()) // Starting screen
    }
}
```

### Accessing Navigator in Screens

```kotlin
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class HomeScreen : Screen {
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        
        Column {
            Text("Home Screen")
            
            Button(onClick = {
                navigator.push(DetailsScreen(movieId = "123"))
            }) {
                Text("Go to Details")
            }
        }
    }
}
```

---

## Passing Data Between Screens

### Method 1: Constructor Parameters

```kotlin
// Navigate with data
navigator.push(DetailsScreen(movieId = "123"))

// Receive in screen
class DetailsScreen(private val movieId: String) : Screen {
    @Composable
    override fun Content() {
        Text("Movie ID: $movieId")
    }
}
```

### Method 2: Passing Objects

```kotlin
// Navigate with object
val movie = Movie(id = "123", title = "Inception", posterUrl = "...")
navigator.push(DetailsScreen(movie = movie))

// Receive in screen
class DetailsScreen(private val movie: Movie) : Screen {
    @Composable
    override fun Content() {
        Text("Movie: ${movie.title}")
    }
}
```

---

## Navigation Actions

### Push (Go Forward)

```kotlin
// Add new screen to stack
navigator.push(DetailsScreen(movieId = "123"))
```

### Pop (Go Back)

```kotlin
// Remove current screen, go back
navigator.pop()
```

### Replace

```kotlin
// Replace current screen (no back navigation)
navigator.replace(NewScreen())
```

### Pop to Root

```kotlin
// Go back to first screen
navigator.popUntilRoot()
```

### Replace All

```kotlin
// Clear stack and set new screen
navigator.replaceAll(HomeScreen())
```

---

## Complete Example

### Screen Structure

```kotlin
// HomeScreen.kt
class HomeScreen : Screen {
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        
        HomeScreenContent(
            onMovieClick = { movie ->
                navigator.push(DetailsScreen(movie))
            },
            onFavoritesClick = {
                navigator.push(FavoritesScreen())
            }
        )
    }
}

@Composable
private fun HomeScreenContent(
    onMovieClick: (Movie) -> Unit,
    onFavoritesClick: () -> Unit
) {
    Column {
        // Toolbar
        Row {
            Text("Movies")
            IconButton(onClick = onFavoritesClick) {
                Icon(/* heart icon */)
            }
        }
        
        // Movie list
        LazyColumn {
            items(movies) { movie ->
                MovieCard(
                    movie = movie,
                    onClick = { onMovieClick(movie) }
                )
            }
        }
    }
}
```

```kotlin
// DetailsScreen.kt
class DetailsScreen(private val movie: Movie) : Screen {
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        
        DetailsScreenContent(
            movie = movie,
            onBackClick = { navigator.pop() }
        )
    }
}

@Composable
private fun DetailsScreenContent(
    movie: Movie,
    onBackClick: () -> Unit
) {
    Column {
        IconButton(onClick = onBackClick) {
            Icon(/* back icon */)
        }
        
        Text(movie.title)
        Text(movie.synopsis)
    }
}
```

---

## Best Practices

### 1. Separate Navigation from UI

```kotlin
// Good: Navigation in Content(), UI in separate composable
class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        HomeScreenContent(
            onNavigate = { navigator.push(it) }
        )
    }
}

@Composable
private fun HomeScreenContent(onNavigate: (Screen) -> Unit) {
    // Pure UI, no navigation logic
}
```

### 2. Use Type-Safe Parameters

```kotlin
// Good: Type-safe
class DetailsScreen(private val movie: Movie) : Screen

// Avoid: String-based
class DetailsScreen(private val movieJson: String) : Screen
```

### 3. Handle Back Navigation

```kotlin
// System back button works automatically with Voyager
// For custom back buttons:
IconButton(onClick = { navigator.pop() }) {
    Icon(Icons.Default.ArrowBack, "Back")
}
```

---

## Key Takeaways

1. **Screens implement `Screen` interface** with `Content()` composable
2. **Navigator manages the back stack** — push to go forward, pop to go back
3. **Pass data via constructor parameters** — type-safe and simple
4. **Separate navigation from UI** — makes testing easier
5. **Use `LocalNavigator.currentOrThrow`** to access navigator in screens

---

## Next Steps

Continue to [Networking with Ktor](../04-networking/README.md)
