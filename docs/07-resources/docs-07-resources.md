# Managing Resources in KMP

This guide covers how to add and use resources (images, icons, strings) in Compose Multiplatform.

## Table of Contents
- [Overview](#overview)
- [Resource Location](#resource-location)
- [Adding Images and Icons](#adding-images-and-icons)
- [Using Resources in Code](#using-resources-in-code)
- [Converting SVG to Vector](#converting-svg-to-vector)

---

## Overview

In a standard Android app, resources go in `res/drawable/` and are accessed via `R.drawable.image`.

In Compose Multiplatform, shared resources go in `composeResources/` and are accessed via `Res.drawable.image`.

| Android | Compose Multiplatform |
|---------|----------------------|
| `res/drawable/` | `composeResources/drawable/` |
| `R.drawable.icon` | `Res.drawable.icon` |
| `painterResource(R.drawable.icon)` | `painterResource(Res.drawable.icon)` |

---

## Resource Location

### Folder Structure

Place shared resources in:

```
composeApp/
  src/
    commonMain/
      composeResources/
        drawable/           # Images and icons
          ic_star.xml
          ic_back.xml
          logo.png
        values/             # Strings (optional)
          strings.xml
```

### Important Notes

- Path must be exactly `commonMain/composeResources/`
- Resources here are shared across Android and iOS
- Android-specific resources can still go in `androidMain/res/`

---

## Adding Images and Icons

### Supported Formats

| Format | Support |
|--------|---------|
| PNG | ✅ Recommended for photos/complex images |
| XML (Vector Drawable) | ✅ Recommended for icons |
| SVG | ❌ Not directly supported (convert to XML) |
| WebP | ✅ Supported |
| JPEG | ✅ Supported |

### Adding a PNG Image

1. Place the file in `composeResources/drawable/`
2. Rebuild the project
3. Access via `Res.drawable.filename` (without extension)

```
composeResources/
  drawable/
    logo.png
```

```kotlin
Image(
    painter = painterResource(Res.drawable.logo),
    contentDescription = "Logo"
)
```

### Adding Vector Icons (XML)

1. Create or convert your icon to Android Vector Drawable format (XML)
2. Place in `composeResources/drawable/`
3. Rebuild the project

```xml
<!-- composeResources/drawable/ic_star.xml -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="#FFB900"
        android:pathData="M12,2L15.09,8.26L22,9.27L17,14.14L18.18,21.02L12,17.77L5.82,21.02L7,14.14L2,9.27L8.91,8.26L12,2Z"/>
</vector>
```

---

## Using Resources in Code

### Import Statement

```kotlin
import favoritesapp.composeapp.generated.resources.Res
import favoritesapp.composeapp.generated.resources.ic_star
import org.jetbrains.compose.resources.painterResource
```

Note: The import path depends on your project name. It follows the pattern:
`{projectname}.composeapp.generated.resources.Res`

### Using Icons

```kotlin
import org.jetbrains.compose.resources.painterResource

@Composable
fun StarIcon() {
    Icon(
        painter = painterResource(Res.drawable.ic_star),
        contentDescription = "Star",
        tint = Color(0xFFFFB900)
    )
}
```

### With Unspecified Tint (Keep Original Colors)

If your icon has colors baked in and you don't want to override them:

```kotlin
Icon(
    painter = painterResource(Res.drawable.ic_star),
    contentDescription = "Star",
    tint = Color.Unspecified  // Keeps original XML colors
)
```

### Using Images

```kotlin
@Composable
fun LogoImage() {
    Image(
        painter = painterResource(Res.drawable.logo),
        contentDescription = "App Logo",
        modifier = Modifier.size(100.dp)
    )
}
```

---

## Converting SVG to Vector

Android/KMP doesn't support SVG directly. Convert SVG to Android Vector Drawable XML.

### Method 1: Android Studio (Recommended)

1. Create a temporary folder: `composeApp/src/androidMain/res/drawable/`
2. Right-click the `drawable` folder
3. Select **New → Vector Asset**
4. Choose **Local file (SVG, PSD)**
5. Browse to your SVG file
6. Click **Next → Finish**
7. Copy the generated XML to `commonMain/composeResources/drawable/`
8. Delete the file from `androidMain/res/drawable/`

### Method 2: Online Converter

1. Go to https://svg2vector.com/ or similar
2. Upload your SVG
3. Download the Android Vector XML
4. Place in `composeResources/drawable/`

### Method 3: Export from Figma

1. Select the icon in Figma
2. In the right panel, click **Export**
3. Choose **SVG** format
4. Export and convert using Method 1 or 2

---

## Common Issues

### "Resource not found"

- Rebuild the project after adding resources
- Check the file is in the correct folder
- Check the import path matches your project name

### SVG not working

- Convert SVG to XML Vector Drawable
- Android/iOS don't support SVG directly in Compose Resources

### Icon colors wrong

- Use `tint = Color.Unspecified` to keep original colors
- Or use `tint = Color.White` to apply a specific color

### Resource not updating

- Clean and rebuild: **Build → Clean Project**, then **Build → Rebuild Project**

---

## Complete Example

### Project Structure

```
composeApp/
  src/
    commonMain/
      composeResources/
        drawable/
          ic_back.xml
          ic_star.xml
          ic_heart.xml
          ic_calendar.xml
          ic_clock.xml
```

### Usage in Component

```kotlin
@Composable
fun MovieInfoRow(year: String, duration: String, rating: Double) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Year
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                painter = painterResource(Res.drawable.ic_calendar),
                contentDescription = null,
                tint = Color.Gray
            )
            Text(year, color = Color.Gray)
        }
        
        // Duration
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                painter = painterResource(Res.drawable.ic_clock),
                contentDescription = null,
                tint = Color.Gray
            )
            Text(duration, color = Color.Gray)
        }
        
        // Rating
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                painter = painterResource(Res.drawable.ic_star),
                contentDescription = null,
                tint = Color.Unspecified  // Keep yellow color from XML
            )
            Text("$rating", color = Color(0xFFFFB900))
        }
    }
}
```

---

## Key Takeaways

1. **Resources go in `commonMain/composeResources/drawable/`**
2. **Use XML Vector Drawables** for icons (not SVG)
3. **Convert SVG to XML** using Android Studio's Vector Asset tool
4. **Access via `Res.drawable.name`** — generated automatically
5. **Use `painterResource()`** to load in composables
6. **Rebuild after adding resources** for code generation
7. **Use `tint = Color.Unspecified`** to keep original icon colors

---

## Summary

You've completed the KMP learning guide! You now know how to:

- ✅ Set up a KMP project
- ✅ Build shared UI with Compose Multiplatform
- ✅ Implement navigation with Voyager
- ✅ Make network requests with Ktor
- ✅ Store data locally with SQLDelight
- ✅ Use dependency injection with Koin
- ✅ Manage shared resources

Happy coding! 🎉
