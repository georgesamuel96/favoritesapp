# Networking with Ktor

This guide covers making HTTP requests in KMP using Ktor.

## Table of Contents
- [Overview](#overview)
- [Setup](#setup)
- [Creating an HTTP Client](#creating-an-http-client)
- [Making API Requests](#making-api-requests)
- [JSON Serialization](#json-serialization)
- [Creating a Repository](#creating-a-repository)
- [Error Handling](#error-handling)

---

## Overview

Ktor is a multiplatform HTTP client built by JetBrains. It works on Android, iOS, Desktop, and Web.

| Android Alternative | KMP Solution |
|--------------------|--------------|
| Retrofit | Ktor |
| OkHttp | Ktor |
| Gson/Moshi | Kotlinx Serialization |

---

## Setup

### Add Dependencies

In `build.gradle.kts`:

```kotlin
commonMain.dependencies {
    // Ktor client
    implementation("io.ktor:ktor-client-core:3.1.0")
    implementation("io.ktor:ktor-client-content-negotiation:3.1.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.1.0")
    
    // Kotlinx Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
}

androidMain.dependencies {
    implementation("io.ktor:ktor-client-android:3.1.0")
}

iosMain.dependencies {
    implementation("io.ktor:ktor-client-darwin:3.1.0")
}
```

### Add Serialization Plugin

In `build.gradle.kts` plugins:

```kotlin
plugins {
    kotlin("plugin.serialization") version "2.1.0"
}
```

### Android Internet Permission

In `androidMain/AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

---

## Creating an HTTP Client

### Basic Client Setup

```kotlin
// commonMain/kotlin/data/remote/ApiClient.kt
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ApiClient {
    
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true  // Ignore fields not in our data class
                prettyPrint = true
            })
        }
    }
}
```

### What Each Part Does

| Component | Purpose |
|-----------|---------|
| `HttpClient` | Makes HTTP requests |
| `ContentNegotiation` | Handles request/response body conversion |
| `json()` | Configures JSON serialization |
| `ignoreUnknownKeys` | Prevents crashes from unknown JSON fields |

---

## Making API Requests

### GET Request

```kotlin
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class MovieApi {
    
    private val client = HttpClient { /* config */ }
    private val baseUrl = "https://api.themoviedb.org/3"
    private val apiKey = "YOUR_API_KEY"
    
    suspend fun getPopularMovies(page: Int = 1): MovieResponse {
        return client.get("$baseUrl/movie/popular") {
            parameter("api_key", apiKey)
            parameter("page", page)
        }.body()
    }
}
```

### POST Request

```kotlin
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

suspend fun createUser(user: CreateUserRequest): User {
    return client.post("$baseUrl/users") {
        contentType(ContentType.Application.Json)
        setBody(user)
    }.body()
}
```

### Request with Headers

```kotlin
suspend fun getProtectedData(): Data {
    return client.get("$baseUrl/protected") {
        header("Authorization", "Bearer $token")
    }.body()
}
```

---

## JSON Serialization

### Creating Data Transfer Objects (DTOs)

DTOs match the JSON structure from the API:

```kotlin
// commonMain/kotlin/data/remote/MovieDto.kt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieResponse(
    val page: Int,
    val results: List<MovieDto>
)

@Serializable
data class MovieDto(
    val id: Int,
    val title: String,
    @SerialName("poster_path")      // Maps JSON field to Kotlin property
    val posterPath: String?,
    @SerialName("release_date")
    val releaseDate: String?,
    @SerialName("vote_average")
    val voteAverage: Double,
    val overview: String
)
```

### Annotations Explained

| Annotation | Purpose |
|------------|---------|
| `@Serializable` | Enables JSON conversion for this class |
| `@SerialName("json_field")` | Maps JSON field name to different Kotlin property name |

### Why Use `@SerialName`?

JSON from API:
```json
{
  "poster_path": "/abc.jpg",
  "release_date": "2024-01-15"
}
```

Kotlin convention uses camelCase:
```kotlin
@SerialName("poster_path")
val posterPath: String?

@SerialName("release_date")
val releaseDate: String?
```

---

## Creating a Repository

### Mapper: DTO to Domain Model

Keep API details separate from your app's domain models:

```kotlin
// commonMain/kotlin/data/remote/MovieMapper.kt
object MovieMapper {
    
    fun mapToMovie(dto: MovieDto): Movie {
        return Movie(
            id = dto.id.toString(),
            title = dto.title,
            year = dto.releaseDate?.take(4) ?: "Unknown",
            rating = dto.voteAverage,
            posterUrl = dto.posterPath?.let {
                "https://image.tmdb.org/t/p/w500$it"
            } ?: "",
            synopsis = dto.overview
        )
    }
}
```

### Repository Implementation

```kotlin
// commonMain/kotlin/data/repository/MovieRepository.kt
class MovieRepository(
    private val api: MovieApi
) {
    
    suspend fun getPopularMovies(): List<Movie> {
        val response = api.getPopularMovies()
        return response.results.map { MovieMapper.mapToMovie(it) }
    }
    
    suspend fun searchMovies(query: String): List<Movie> {
        val response = api.searchMovies(query)
        return response.results.map { MovieMapper.mapToMovie(it) }
    }
}
```

### Why Use a Repository?

```
┌─────────────┐     ┌──────────────┐     ┌─────────┐
│ ScreenModel │ --> │  Repository  │ --> │   API   │
│    (UI)     │     │ (Data Layer) │     │ (Ktor)  │
└─────────────┘     └──────────────┘     └─────────┘
                           │
                    ┌──────▼──────┐
                    │   Mapper    │
                    │ (DTO→Model) │
                    └─────────────┘
```

Benefits:
- UI doesn't know about API details
- Easy to swap data sources
- Single place for data transformations
- Easier testing

---

## Error Handling

### Basic Try-Catch

```kotlin
class MovieRepository(private val api: MovieApi) {
    
    suspend fun getPopularMovies(): Result<List<Movie>> {
        return try {
            val response = api.getPopularMovies()
            val movies = response.results.map { MovieMapper.mapToMovie(it) }
            Result.success(movies)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### Using in ScreenModel

```kotlin
class ListScreenModel(
    private val repository: MovieRepository
) : ScreenModel {
    
    private val _uiState = MutableStateFlow(ListUiState())
    val uiState: StateFlow<ListUiState> = _uiState
    
    init {
        loadMovies()
    }
    
    private fun loadMovies() {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val movies = repository.getPopularMovies()
                _uiState.update { it.copy(
                    movies = movies,
                    isLoading = false,
                    error = null
                )}
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )}
            }
        }
    }
}

data class ListUiState(
    val movies: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
```

---

## Complete Example

### API Class

```kotlin
// commonMain/kotlin/data/remote/MovieApi.kt
class MovieApi {
    
    private val apiKey = "YOUR_API_KEY"
    private val baseUrl = "https://api.themoviedb.org/3"
    
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }
    
    suspend fun getPopularMovies(page: Int = 1): MovieResponse {
        return client.get("$baseUrl/movie/popular") {
            parameter("api_key", apiKey)
            parameter("page", page)
        }.body()
    }
    
    suspend fun getMovieDetails(movieId: String): MovieDetailDto {
        return client.get("$baseUrl/movie/$movieId") {
            parameter("api_key", apiKey)
        }.body()
    }
    
    suspend fun searchMovies(query: String): MovieResponse {
        return client.get("$baseUrl/search/movie") {
            parameter("api_key", apiKey)
            parameter("query", query)
        }.body()
    }
}
```

---

## Key Takeaways

1. **Ktor is the multiplatform HTTP client** — replaces Retrofit/OkHttp
2. **Use Kotlinx Serialization** for JSON parsing with `@Serializable`
3. **Use `@SerialName`** to map JSON field names to Kotlin properties
4. **Create DTOs** that match API response structure
5. **Use Mappers** to convert DTOs to domain models
6. **Repositories** abstract the data source from UI layer
7. **Handle errors** with try-catch and expose loading/error states

---

## Next Steps

Continue to [Local Database with SQLDelight](../05-local-database/README.md)
