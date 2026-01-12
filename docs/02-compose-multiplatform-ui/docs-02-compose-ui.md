# Compose Multiplatform UI

This guide covers building shared UI with Compose Multiplatform.

## Table of Contents
- [Overview](#overview)
- [Compose Multiplatform vs Jetpack Compose](#compose-multiplatform-vs-jetpack-compose)
- [Creating Composables](#creating-composables)
- [Building Reusable Components](#building-reusable-components)
- [Loading Images with Coil](#loading-images-with-coil)
- [Theming](#theming)

---

## Overview

Compose Multiplatform allows you to write UI code once in `commonMain` and run it on Android, iOS, Desktop, and Web.

```kotlin
// commonMain - runs on ALL platforms
@Composable
fun Greeting(name: String) {
    Text("Hello, $name!")
}
```

---

## Compose Multiplatform vs Jetpack Compose

### What's the Same (Almost Everything!)

| Feature | Works in KMP? |
|---------|---------------|
| `Column`, `Row`, `Box` | ✅ Yes |
| `Text`, `Button`, `Image` | ✅ Yes |
| `LazyColumn`, `LazyRow` | ✅ Yes |
| `Modifier` | ✅ Yes |
| `remember`, `mutableStateOf` | ✅ Yes |
| Material 3 components | ✅ Yes |
| Animations | ✅ Yes |

### What's Different

| Feature | Jetpack Compose | Compose Multiplatform |
|---------|-----------------|----------------------|
| Resources | `R.drawable.image` | `Res.drawable.image` |
| Image loading | Coil/Glide for Android | Coil 3 (multiplatform) |
| Previews | `@Preview` | `@Preview` (limited on iOS) |

---

## Creating Composables

### Basic Screen Structure

```kotlin
// commonMain/kotlin/ui/screens/HomeScreen.kt
@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF09090B))
            .padding(16.dp)
    ) {
        Text(
            text = "Welcome",
            style = TextStyle(
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = { /* action */ }) {
            Text("Click Me")
        }
    }
}
```

### Lists with LazyColumn

```kotlin
@Composable
fun MovieList(movies: List<Movie>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(movies) { movie ->
            MovieCard(movie = movie)
        }
    }
}
```

### Grid with LazyVerticalGrid

```kotlin
@Composable
fun MovieGrid(movies: List<Movie>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(movies) { movie ->
            MovieCard(movie = movie)
        }
    }
}
```

---

## Building Reusable Components

### Card Component Example

```kotlin
// commonMain/kotlin/ui/components/MovieCard.kt
@Composable
fun MovieCard(
    movie: Movie,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF18181B))
            .clickable { onClick() }
    ) {
        // Image
        AsyncImage(
            model = movie.posterUrl,
            contentDescription = movie.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )
        
        // Content
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = movie.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = movie.year,
                    color = Color.Gray
                )
                Text(
                    text = "★ ${movie.rating}",
                    color = Color(0xFFFFB900)
                )
            }
        }
    }
}
```

### Toolbar Component Example

```kotlin
@Composable
fun ToolbarComponent(
    title: String,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_back),
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            Text(
                text = title,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            
            actions()
        }
        
        HorizontalDivider(color = Color.Gray)
    }
}

// Usage
ToolbarComponent(
    title = "Movies",
    showBackButton = false,
    actions = {
        IconButton(onClick = { /* open favorites */ }) {
            Icon(
                painter = painterResource(Res.drawable.ic_heart),
                contentDescription = "Favorites",
                tint = Color.White
            )
        }
    }
)
```

---

## Loading Images with Coil

### Setup

Add dependencies in `build.gradle.kts`:

```kotlin
commonMain.dependencies {
    implementation("io.coil-kt.coil3:coil-compose:3.0.4")
    implementation("io.coil-kt.coil3:coil-network-ktor3:3.0.4")
}

androidMain.dependencies {
    implementation("io.ktor:ktor-client-android:3.1.0")
}

iosMain.dependencies {
    implementation("io.ktor:ktor-client-darwin:3.1.0")
}
```

Add internet permission for Android in `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### Using AsyncImage

```kotlin
import coil3.compose.AsyncImage

@Composable
fun MoviePoster(url: String) {
    AsyncImage(
        model = url,
        contentDescription = "Movie poster",
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentScale = ContentScale.Crop
    )
}
```

### With Loading and Error States

```kotlin
AsyncImage(
    model = url,
    contentDescription = "Movie poster",
    modifier = Modifier.fillMaxWidth().height(300.dp),
    contentScale = ContentScale.Crop,
    onState = { state ->
        when (state) {
            is AsyncImagePainter.State.Loading -> {
                // Show loading indicator
            }
            is AsyncImagePainter.State.Error -> {
                // Show error placeholder
            }
            is AsyncImagePainter.State.Success -> {
                // Image loaded
            }
            else -> {}
        }
    }
)
```

---

## Theming

### Basic Theme Setup

```kotlin
// commonMain/kotlin/ui/theme/Theme.kt
@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF7F22FE),
            background = Color(0xFF09090B),
            surface = Color(0xFF18181B)
        ),
        content = content
    )
}
```

### Using the Theme

```kotlin
@Composable
fun App() {
    AppTheme {
        Navigator(ListScreen())
    }
}
```

---

## State Management in Composables

### Using remember and mutableStateOf

```kotlin
@Composable
fun Counter() {
    var count by remember { mutableStateOf(0) }
    
    Column {
        Text("Count: $count")
        Button(onClick = { count++ }) {
            Text("Increment")
        }
    }
}
```

### Collecting StateFlow

```kotlin
@Composable
fun MovieListScreen(screenModel: ListScreenModel) {
    val uiState by screenModel.uiState.collectAsState()
    
    when {
        uiState.isLoading -> CircularProgressIndicator()
        uiState.error != null -> Text("Error: ${uiState.error}")
        else -> MovieList(movies = uiState.movies)
    }
}
```

---

## Key Takeaways

1. **Almost all Jetpack Compose knowledge transfers** to Compose Multiplatform
2. **Resources are handled differently** — use `Res.drawable` instead of `R.drawable`
3. **Use Coil 3** for multiplatform image loading
4. **Create reusable components** with configurable parameters
5. **State management** works the same as in Jetpack Compose

---

## Next Steps

Continue to [Navigation with Voyager](../03-navigation/README.md)
