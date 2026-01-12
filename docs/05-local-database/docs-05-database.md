# Local Database with SQLDelight

This guide covers implementing a local database in KMP using SQLDelight.

## Table of Contents
- [Overview](#overview)
- [Setup](#setup)
- [Creating the Schema](#creating-the-schema)
- [Platform-Specific Drivers](#platform-specific-drivers)
- [Creating a Repository](#creating-a-repository)
- [Reactive Queries with Flow](#reactive-queries-with-flow)

---

## Overview

SQLDelight is a multiplatform database library that:
- Generates Kotlin code from SQL statements
- Uses SQLite under the hood
- Works on Android, iOS, Desktop, and Web
- Provides type-safe database access

| Android Alternative | KMP Solution |
|--------------------|--------------|
| Room | SQLDelight |

---

## Setup

### Step 1: Add Plugin (Root build.gradle.kts)

```kotlin
plugins {
    id("app.cash.sqldelight") version "2.0.2" apply false
}
```

### Step 2: Apply Plugin (composeApp/build.gradle.kts)

```kotlin
plugins {
    id("app.cash.sqldelight")
}
```

### Step 3: Add Dependencies

```kotlin
commonMain.dependencies {
    implementation("app.cash.sqldelight:runtime:2.0.2")
    implementation("app.cash.sqldelight:coroutines-extensions:2.0.2")
}

androidMain.dependencies {
    implementation("app.cash.sqldelight:android-driver:2.0.2")
}

iosMain.dependencies {
    implementation("app.cash.sqldelight:native-driver:2.0.2")
}
```

### Step 4: Configure Database

At bottom of `composeApp/build.gradle.kts`:

```kotlin
sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.yourpackage.db")
        }
    }
}
```

---

## Creating the Schema

### Folder Structure

Create `.sq` files in this location:

```
composeApp/
  src/
    commonMain/
      sqldelight/
        com/
          yourpackage/
            db/
              FavoriteMovie.sq
```

### Writing SQL Schema

```sql
-- FavoriteMovie.sq

-- Create table
CREATE TABLE FavoriteMovie (
    id TEXT NOT NULL PRIMARY KEY,
    title TEXT NOT NULL,
    year TEXT NOT NULL,
    rating REAL NOT NULL,
    posterUrl TEXT NOT NULL,
    synopsis TEXT NOT NULL
);

-- Query: Get all favorites
getAllFavorites:
SELECT * FROM FavoriteMovie;

-- Query: Get by ID
getFavoriteById:
SELECT * FROM FavoriteMovie WHERE id = ?;

-- Query: Insert or replace
insertFavorite:
INSERT OR REPLACE INTO FavoriteMovie(id, title, year, rating, posterUrl, synopsis)
VALUES (?, ?, ?, ?, ?, ?);

-- Query: Delete by ID
deleteFavorite:
DELETE FROM FavoriteMovie WHERE id = ?;

-- Query: Check if exists
isFavorite:
SELECT COUNT(*) FROM FavoriteMovie WHERE id = ?;
```

### SQL Types Mapping

| SQLite Type | Kotlin Type |
|-------------|-------------|
| `TEXT` | `String` |
| `INTEGER` | `Long` |
| `REAL` | `Double` |
| `BLOB` | `ByteArray` |

### Generated Code

After building, SQLDelight generates:
- `FavoriteMovie` data class
- `FavoriteMovieQueries` with type-safe functions:
  - `getAllFavorites()`
  - `getFavoriteById(id: String)`
  - `insertFavorite(id, title, year, rating, posterUrl, synopsis)`
  - `deleteFavorite(id: String)`
  - `isFavorite(id: String)`

---

## Platform-Specific Drivers

SQLite requires platform-specific initialization. Use `expect`/`actual`:

### Common Code

```kotlin
// commonMain/kotlin/data/local/DriverFactory.kt
package com.yourpackage.data.local

import app.cash.sqldelight.db.SqlDriver
import com.yourpackage.db.AppDatabase

expect fun createSqlDriver(): SqlDriver

fun createDatabase(): AppDatabase {
    return AppDatabase(createSqlDriver())
}
```

### Android Implementation

```kotlin
// androidMain/kotlin/data/local/DriverFactory.android.kt
package com.yourpackage.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.yourpackage.db.AppDatabase

lateinit var appContext: android.content.Context

actual fun createSqlDriver(): SqlDriver {
    return AndroidSqliteDriver(
        schema = AppDatabase.Schema,
        context = appContext,
        name = "app.db"
    )
}
```

Initialize in `MainActivity.kt`:

```kotlin
import com.yourpackage.data.local.appContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContext = applicationContext
        // ...
    }
}
```

### iOS Implementation

```kotlin
// iosMain/kotlin/data/local/DriverFactory.ios.kt
package com.yourpackage.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.yourpackage.db.AppDatabase

actual fun createSqlDriver(): SqlDriver {
    return NativeSqliteDriver(
        schema = AppDatabase.Schema,
        name = "app.db"
    )
}
```

### iOS: Link SQLite Library

In Xcode:
1. Select project → target → **Build Phases**
2. Expand **Link Binary With Libraries**
3. Click **+** → search **libsqlite3.tbd** → Add

---

## Creating a Repository

### Basic Repository

```kotlin
// commonMain/kotlin/data/repository/FavoritesRepository.kt
package com.yourpackage.data.repository

import com.yourpackage.db.AppDatabase
import com.yourpackage.model.Movie

class FavoritesRepository(
    private val database: AppDatabase
) {
    private val queries = database.favoriteMovieQueries
    
    fun getAllFavorites(): List<Movie> {
        return queries.getAllFavorites()
            .executeAsList()
            .map { it.toMovie() }
    }
    
    fun addFavorite(movie: Movie) {
        queries.insertFavorite(
            id = movie.id,
            title = movie.title,
            year = movie.year,
            rating = movie.rating,
            posterUrl = movie.posterUrl,
            synopsis = movie.synopsis
        )
    }
    
    fun removeFavorite(movieId: String) {
        queries.deleteFavorite(movieId)
    }
    
    fun isFavorite(movieId: String): Boolean {
        return queries.isFavorite(movieId)
            .executeAsOne() > 0
    }
}

// Extension to convert database model to domain model
private fun FavoriteMovie.toMovie(): Movie {
    return Movie(
        id = id,
        title = title,
        year = year,
        rating = rating,
        posterUrl = posterUrl,
        synopsis = synopsis
    )
}
```

---

## Reactive Queries with Flow

SQLDelight can emit updates as Flow, so UI updates automatically when data changes.

### Setup

Import coroutines extensions:

```kotlin
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
```

### Reactive Repository

```kotlin
class FavoritesRepository(
    private val database: AppDatabase
) {
    private val queries = database.favoriteMovieQueries
    
    // Returns Flow that emits whenever data changes
    fun getAllFavorites(): Flow<List<Movie>> {
        return queries.getAllFavorites()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { favorites ->
                favorites.map { it.toMovie() }
            }
    }
    
    // Returns Flow<Boolean> that updates when favorite status changes
    fun isFavorite(movieId: String): Flow<Boolean> {
        return queries.isFavorite(movieId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { it.firstOrNull()?.let { count -> count > 0 } ?: false }
    }
    
    // These don't need to be suspend since SQLDelight handles threading
    fun addFavorite(movie: Movie) {
        queries.insertFavorite(
            id = movie.id,
            title = movie.title,
            year = movie.year,
            rating = movie.rating,
            posterUrl = movie.posterUrl,
            synopsis = movie.synopsis
        )
    }
    
    fun removeFavorite(movieId: String) {
        queries.deleteFavorite(movieId)
    }
}
```

### Using in ScreenModel

```kotlin
class FavoritesScreenModel(
    private val repository: FavoritesRepository
) : ScreenModel {
    
    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState
    
    init {
        loadFavorites()
    }
    
    private fun loadFavorites() {
        screenModelScope.launch {
            // Collects forever, UI updates automatically
            repository.getAllFavorites().collect { favorites ->
                _uiState.update { it.copy(
                    favorites = favorites,
                    isLoading = false
                )}
            }
        }
    }
}
```

### Why Flow?

```kotlin
// Without Flow: Manual refresh needed
val favorites = repository.getAllFavorites() // One-time fetch

// With Flow: Automatic updates
repository.getAllFavorites().collect { favorites ->
    // Called automatically when:
    // - Initial data loads
    // - Movie added to favorites
    // - Movie removed from favorites
}
```

---

## Complete Example

### Schema (FavoriteMovie.sq)

```sql
CREATE TABLE FavoriteMovie (
    id TEXT NOT NULL PRIMARY KEY,
    title TEXT NOT NULL,
    year TEXT NOT NULL,
    rating REAL NOT NULL,
    posterUrl TEXT NOT NULL,
    synopsis TEXT NOT NULL
);

getAllFavorites:
SELECT * FROM FavoriteMovie;

insertFavorite:
INSERT OR REPLACE INTO FavoriteMovie(id, title, year, rating, posterUrl, synopsis)
VALUES (?, ?, ?, ?, ?, ?);

deleteFavorite:
DELETE FROM FavoriteMovie WHERE id = ?;

isFavorite:
SELECT COUNT(*) FROM FavoriteMovie WHERE id = ?;
```

### Repository

```kotlin
class FavoritesRepository(database: AppDatabase) {
    private val queries = database.favoriteMovieQueries
    
    fun getAllFavorites(): Flow<List<Movie>> =
        queries.getAllFavorites()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toMovie() } }
    
    fun isFavorite(id: String): Flow<Boolean> =
        queries.isFavorite(id)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { it.firstOrNull()?.let { c -> c > 0 } ?: false }
    
    fun addFavorite(movie: Movie) = queries.insertFavorite(
        movie.id, movie.title, movie.year,
        movie.rating, movie.posterUrl, movie.synopsis
    )
    
    fun removeFavorite(id: String) = queries.deleteFavorite(id)
}
```

---

## Key Takeaways

1. **SQLDelight generates Kotlin from SQL** — you write SQL, it generates type-safe code
2. **Platform drivers are different** — use `expect`/`actual` pattern
3. **iOS requires linking SQLite** — add `libsqlite3.tbd` in Xcode
4. **Use Flow for reactive data** — UI updates automatically when database changes
5. **Repository pattern** keeps database details out of UI layer

---

## Next Steps

Continue to [Dependency Injection with Koin](../06-dependency-injection/README.md)
