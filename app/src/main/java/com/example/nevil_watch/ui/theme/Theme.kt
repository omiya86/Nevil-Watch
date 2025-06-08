package com.example.nevil_watch.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Light Theme Colors - Black and White
private val LightColors = lightColorScheme(
    primary = Color(0xFF000000),          // Pure Black
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF5F5F5),  // Light Grey Background
    onPrimaryContainer = Color(0xFF000000), // Black Text
    secondary = Color(0xFF424242),        // Dark Grey
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEEEEEE), // Light Grey Background
    onSecondaryContainer = Color(0xFF212121), // Dark Grey Text
    tertiary = Color(0xFF757575),         // Medium Grey
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF5F5F5), // Light Grey Background
    onTertiaryContainer = Color(0xFF424242), // Dark Grey Text
    background = Color.White,             // Pure White
    onBackground = Color(0xFF000000),     // Pure Black
    surface = Color.White,
    onSurface = Color(0xFF000000),
    surfaceVariant = Color(0xFFF5F5F5),   // Light Grey Surface
    onSurfaceVariant = Color(0xFF424242), // Dark Grey Text
    error = Color(0xFFB00020),           // Dark Red
    onError = Color.White,
    errorContainer = Color(0xFFF5F5F5),   // Light Grey Background
    onErrorContainer = Color(0xFFB00020), // Dark Red Text
    outline = Color(0xFFE0E0E0),         // Light Grey Outline
    outlineVariant = Color(0xFFEEEEEE),  // Lighter Grey Outline
    scrim = Color(0x52000000)            // Semi-transparent Black
)

// Dark Theme Colors
private val DarkColors = darkColorScheme(
    primary = Color(0xFF90CAF9),          // Light Blue
    onPrimary = Color(0xFF0D47A1),        // Dark Blue
    primaryContainer = Color(0xFF1976D2),  // Medium Blue
    onPrimaryContainer = Color(0xFFE3F2FD), // Light Blue Text
    secondary = Color(0xFF80CBC4),        // Light Teal
    onSecondary = Color(0xFF00695C),      // Dark Teal
    secondaryContainer = Color(0xFF00897B), // Medium Teal
    onSecondaryContainer = Color(0xFFE0F2F1), // Light Teal Text
    tertiary = Color(0xFFB39DDB),         // Light Purple
    onTertiary = Color(0xFF4527A0),       // Dark Purple
    tertiaryContainer = Color(0xFF7E57C2), // Medium Purple
    onTertiaryContainer = Color(0xFFEDE7F6), // Light Purple Text
    background = Color(0xFF121212),       // Dark Background
    onBackground = Color(0xFFE0E0E0),     // Light Grey Text
    surface = Color(0xFF1E1E1E),          // Dark Surface
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2D2D2D),   // Dark Grey Surface
    onSurfaceVariant = Color(0xFFBDBDBD), // Light Grey Text
    error = Color(0xFFEF5350),           // Light Red
    onError = Color(0xFFB71C1C),         // Dark Red
    errorContainer = Color(0xFFC62828),   // Medium Red
    onErrorContainer = Color(0xFFFFEBEE), // Light Red Text
    outline = Color(0xFF424242),         // Dark Grey Outline
    outlineVariant = Color(0xFF616161),  // Medium Grey Outline
    scrim = Color(0x52000000)            // Semi-transparent Black
)

@Composable
fun NevilWatchTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalView.current.context
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                hide(androidx.core.view.WindowInsetsCompat.Type.statusBars())
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}