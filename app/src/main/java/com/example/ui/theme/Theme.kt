package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = SpaceNavyDark,
    primaryContainer = SpaceNavySurfaceVariant,
    onPrimaryContainer = ElectricCyan,
    secondary = BioEmerald,
    onSecondary = SpaceNavyDark,
    secondaryContainer = SpaceNavyLight,
    onSecondaryContainer = BioEmerald,
    tertiary = QuantumIndigoLight,
    onTertiary = SpaceNavyDark,
    background = SpaceNavyDark,
    onBackground = TextPrimaryDark,
    surface = SpaceNavySurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = SpaceNavySurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    error = CriticalRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF007A8A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC7F5F9),
    onPrimaryContainer = Color(0xFF002024),
    secondary = Color(0xFF008945),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD2F7DF),
    onSecondaryContainer = Color(0xFF00210C),
    tertiary = Color(0xFF5E35B1),
    onTertiary = Color.White,
    background = SurfaceLight,
    onBackground = TextPrimaryLight,
    surface = CardSurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = TextSecondaryLight,
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

@Composable
fun SciSyncTheme(
    darkTheme: Boolean = true, // Default to sleek scientific dark mode
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
