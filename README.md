# 🎬 FavoritesApp

A modern, cross-platform movie browsing and favorites management application built with Kotlin Multiplatform and Jetpack Compose. Browse popular movies from The Movie Database (TMDb) API and save your favorites locally with persistent storage.

## 📱 Features

### Core Functionality
- **Browse Popular Movies**: Explore a collection of popular movies fetched from The Movie Database (TMDb) API
- **Movie Details**: View comprehensive information about each movie including:
  - Movie poster with high-quality images
  - Title, genre, release year
  - Duration and rating
  - Detailed synopsis
- **Favorites Management**: 
  - Add movies to favorites with a single tap
  - Remove movies from favorites
  - Persistent local storage across app sessions
  - Dedicated favorites screen to view all saved movies
- **Responsive UI**: Modern, beautiful interface with smooth animations and transitions
- **Cross-Platform**: Runs on both Android and iOS with shared codebase

### User Experience
- **Loading States**: Visual feedback with loading indicators during data fetching
- **Error Handling**: User-friendly error dialogs with clear messaging
- **Empty States**: Helpful prompts when favorites list is empty
- **Visual Feedback**: Button state changes to indicate favorite status
- **Smooth Navigation**: Intuitive navigation between screens using Voyager

## 🛠 Technology Stack

### Core Framework
- **Kotlin Multiplatform**: Share business logic across Android and iOS
- **Compose Multiplatform**: Modern declarative UI framework for all platforms
- **Jetpack Compose**: Material 3 design system

### Networking
- **Ktor Client 3.1.0**: Cross-platform HTTP client for API requests
  - `ktor-client-core`: Core networking functionality
  - `ktor-client-content-negotiation`: JSON content negotiation
  - `ktor-serialization-kotlinx-json`: JSON serialization/deserialization
  - `ktor-client-android`: Android-specific engine
  - `ktor-client-darwin`: iOS-specific engine
- **kotlinx.serialization 1.7.3**: Type-safe JSON parsing

### Local Storage
- **SQLDelight 2.0.2**: Type-safe SQL database for Kotlin Multiplatform
  - `android-driver`: Android SQLite driver
  - `native-driver`: iOS SQLite driver
  - `coroutines-extensions`: Reactive Flow extensions for SQLDelight

### Dependency Injection
- **Koin 3.5.6**: Lightweight dependency injection framework
  - `koin-core`: Core DI functionality
  - `koin-compose`: Compose integration
  - `voyager-koin`: Integration with Voyager navigation

### Navigation & State Management
- **Voyager 1.0.0**: Type-safe navigation library for Compose Multiplatform
  - `voyager-navigator`: Navigation management
  - `voyager-screenmodel`: ViewModel/ScreenModel pattern for navigation
  - `voyager-koin`: Koin integration

### Image Loading
- **Coil 3.3.0**: Efficient image loading and caching library
  - `coil-compose`: Compose integration
  - `coil-network-ktor3`: Ktor-based image loading

### Additional Libraries
- **AndroidX Lifecycle**: ViewModel and lifecycle-aware components
- **Material 3**: Modern Material Design components

## 🏗 Architecture

The application follows **Clean Architecture** principles with **MVVM (Model-View-ViewModel)** pattern for better separation of concerns, testability, and maintainability.

### Architecture Layers

```
┌─────────────────────────────────────────────────┐
│              Presentation Layer                  │
│  ┌────────────┐  ┌──────────────┐               │
│  │  Screens   │  │ ScreenModels │               │
│  │ (Compose)  │  │  (ViewModels)│               │
│  └────────────┘  └──────────────┘               │
└─────────────────────────────────────────────────┘
                      ↓ ↑
┌─────────────────────────────────────────────────┐
│               Domain Layer                       │
│  ┌────────────┐  ┌──────────────┐               │
│  │   Models   │  │ Repositories │               │
│  │  (Movie)   │  │  (Interface) │               │
│  └────────────┘  └──────────────┘               │
└─────────────────────────────────────────────────┘
                      ↓ ↑
┌─────────────────────────────────────────────────┐
│                Data Layer                        │
│  ┌──────────────┐  ┌─────────────┐              │
│  │  Remote API  │  │Local Storage│              │
│  │    (Ktor)    │  │ (SQLDelight)│              │
│  └──────────────┘  └─────────────┘              │
└─────────────────────────────────────────────────┘
```

### Project Structure

```
composeApp/src/
├── commonMain/kotlin/com/learning/favoritesapp/
│   ├── App.kt                          # Application entry point
│   ├── di/                             # Dependency Injection
│   │   └── AppModule.kt                # Koin module configuration
│   ├── data/                           # Data layer
│   │   ├── local/                      # Local storage
│   │   │   ├── DriverFactory.kt        # Database driver interface
│   │   │   └── DatabaseFactory.kt      # Database initialization
│   │   ├── remote/                     # Remote data sources
│   │   │   ├── MovieApi.kt             # TMDb API client
│   │   │   ├── MovieDto.kt             # API response models
│   │   │   └── MovieMapper.kt          # DTO to domain mapping
│   │   └── repository/                 # Repository implementations
│   │       ├── MovieRepository.kt      # Movie data management
│   │       └── FavoritesRepository.kt  # Favorites CRUD operations
│   ├── model/                          # Domain models
│   │   ├── Movie.kt                    # Movie data class
│   │   └── FakeData.kt                 # Mock data for development
│   └── ui/                             # Presentation layer
│       ├── components/                 # Reusable UI components
│       │   ├── MovieCard.kt            # Movie card component
│       │   └── ToolbarComponent.kt     # Custom toolbar
│       └── screens/                    # Feature screens
│           ├── list/                   # Movie list feature
│           │   ├── ListScreen.kt       # List UI
│           │   └── ListViewModel.kt    # List state management
│           ├── details/                # Movie details feature
│           │   ├── DetailsScreen.kt    # Details UI
│           │   └── DetailsScreenModel.kt # Details state
│           └── favorites/              # Favorites feature
│               ├── FavoritesScreen.kt  # Favorites UI
│               └── FavoritesScreenModel.kt # Favorites state
├── commonMain/sqldelight/com/learning/favoritesapp/db/
│   └── FavoriteMovie.sq                # SQL schema & queries
├── androidMain/kotlin/
│   ├── MainActivity.kt                 # Android entry point
│   └── data/local/
│       └── DriverFactory.android.kt    # Android SQLite driver
└── iosMain/kotlin/
    └── data/local/
        └── DriverFactory.ios.kt        # iOS SQLite driver
```

### Architecture Components

#### 1. **Presentation Layer**
- **Screens**: Composable functions that define the UI using Jetpack Compose
  - `ListScreen`: Displays grid of popular movies
  - `DetailsScreen`: Shows detailed movie information
  - `FavoritesScreen`: Displays saved favorite movies
- **ScreenModels/ViewModels**: Manage UI state and business logic
  - `ListViewModel`: Handles movie loading and error states
  - `DetailsScreenModel`: Manages favorite toggle state
  - `FavoritesScreenModel`: Manages favorites list state
- **UI State**: Immutable data classes representing screen state
  - `ListUiState`: Loading, success, and error states for movie list
  - `FavoritesUiState`: Favorites list and loading states

#### 2. **Domain Layer**
- **Models**: Pure Kotlin data classes representing business entities
  - `Movie`: Core movie entity with all properties
- **Repository Interfaces**: Define contracts for data operations

#### 3. **Data Layer**
- **Remote Data Source**:
  - `MovieApi`: Ktor client for TMDb API integration
  - `MovieDto`: API response models
  - `MovieMapper`: Transforms API responses to domain models
- **Local Data Source**:
  - SQLDelight database with type-safe queries
  - `FavoriteMovie.sq`: SQL schema and query definitions
  - Platform-specific database drivers (Android & iOS)
- **Repositories**:
  - `MovieRepository`: Fetches movies from remote API
  - `FavoritesRepository`: CRUD operations for favorites using SQLDelight

#### 4. **Dependency Injection**
- **Koin Module**: Centralized dependency graph
  - Database instance (Singleton)
  - API client (Singleton)
  - Repositories (Singleton)
  - ScreenModels (Factory)

### Key Design Patterns

1. **Repository Pattern**: Abstracts data sources from business logic
2. **Unidirectional Data Flow**: UI state flows down, events flow up
3. **Single Source of Truth**: State managed in ScreenModels
4. **Reactive Programming**: Kotlin Flows for reactive data streams
5. **Dependency Injection**: Koin for loose coupling and testability
6. **Platform-Specific Implementations**: Expect/actual for platform differences

### State Management

The app uses **MVI (Model-View-Intent)** inspired state management:
- **State**: Immutable data classes (`UiState`) represent the current state
- **Events**: User actions trigger ScreenModel functions
- **State Updates**: ScreenModels update state which triggers UI recomposition
- **Reactive Streams**: Kotlin StateFlow for state observation

### Data Flow Example

```
User Taps Movie Card
       ↓
ListScreen captures event
       ↓
Navigator pushes DetailsScreen
       ↓
User taps "Add to Favorites"
       ↓
DetailsScreenModel.toggleFavorite()
       ↓
FavoritesRepository.addFavorite()
       ↓
SQLDelight inserts into database
       ↓
Flow emits new favorite state
       ↓
UI updates button appearance
```

## 🚀 Getting Started

### Prerequisites
- **Android Studio** (Latest version recommended)
- **Xcode** 14+ (for iOS development)
- **JDK** 11 or higher
- **Kotlin** 2.1.0+
- **TMDb API Key** (get one from [themoviedb.org](https://www.themoviedb.org/settings/api))

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/georgesamuel96/favoritesapp.git
   cd FavoritesApp
   ```

2. **Add your TMDb API key**
   - Open `composeApp/src/commonMain/kotlin/com/learning/favoritesapp/data/remote/MovieApi.kt`
   - Replace the `apiKey` with your TMDb API key

3. **Sync project with Gradle**
   - Open the project in Android Studio
   - Let Gradle sync and download dependencies

## 📦 Build and Run

### Android Application

#### Using Android Studio
1. Open the project in Android Studio
2. Select the Android run configuration
3. Click the Run button or press `Shift + F10`

#### Using Terminal
- **macOS/Linux**
  ```bash
  ./gradlew :composeApp:assembleDebug
  ```
- **Windows**
  ```bash
  .\gradlew.bat :composeApp:assembleDebug
  ```

### iOS Application

#### Using Android Studio
1. Select the iOS run configuration
2. Choose your target device/simulator
3. Click the Run button

#### Using Xcode
1. Open the `iosApp` directory in Xcode
2. Select your target device or simulator
3. Build and run the project

## 🧪 Testing

The modular architecture makes the app highly testable:
- **Unit Tests**: Test ViewModels, Repositories, and Mappers
- **Integration Tests**: Test data layer components
- **UI Tests**: Test Compose screens and components

## 📝 API Integration

The app uses [The Movie Database (TMDb) API](https://www.themoviedb.org/documentation/api) for movie data:
- **Endpoint**: `GET /movie/popular`
- **Authentication**: API key required
- **Response**: Paginated list of popular movies

## 🎨 UI/UX Design

- **Material 3 Design**: Modern, accessible components
- **Dark Theme**: Easy on the eyes with dark color scheme
- **Responsive Grid**: Adaptive layout for different screen sizes
- **Smooth Animations**: Polished transitions between screens
- **Loading States**: Clear feedback during data operations
- **Error Handling**: User-friendly error messages

## 🔄 Future Enhancements

- [ ] Search functionality
- [ ] Movie filtering by genre
- [ ] Pull-to-refresh for movie list
- [ ] Pagination for infinite scrolling
- [ ] Movie trailers and videos
- [ ] Share favorite movies
- [ ] Light theme support
- [ ] Tablet optimization
- [ ] Unit and UI tests
- [ ] CI/CD pipeline

## 📄 License

This project is for learning purposes.

## 🙏 Acknowledgments

- [The Movie Database (TMDb)](https://www.themoviedb.org/) for providing the movie data API
- [JetBrains](https://www.jetbrains.com/) for Kotlin Multiplatform and Compose Multiplatform
- [Square](https://square.github.io/sqldelight/) for SQLDelight
- [Koin](https://insert-koin.io/) for dependency injection
- [Ktor](https://ktor.io/) for networking

---

## 📚 Learn More

- [Kotlin Multiplatform Documentation](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [SQLDelight Documentation](https://cashapp.github.io/sqldelight/)
- [Ktor Client Documentation](https://ktor.io/docs/getting-started-ktor-client.html)
- [Koin Documentation](https://insert-koin.io/docs/reference/introduction)

**Made with ❤️ using Kotlin Multiplatform**
