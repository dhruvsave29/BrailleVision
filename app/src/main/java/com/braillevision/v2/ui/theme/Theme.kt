package com.braillevision.v2.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = White,
    primaryContainer = Yellow,
    onPrimaryContainer = Black,
    secondary = Yellow,
    onSecondary = Black,
    secondaryContainer = Orange,
    onSecondaryContainer = White,
    tertiary = Blue,
    onTertiary = White,
    tertiaryContainer = Cyan,
    onTertiaryContainer = Black,
    background = Background,
    onBackground = Black,
    surface = Surface,
    onSurface = Black,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = Black,
    error = Error,
    onError = White,
    errorContainer = Red,
    onErrorContainer = White,
    outline = Black,
    outlineVariant = Black
)

private val DarkColorScheme = darkColorScheme(
    primary = White,
    onPrimary = Black,
    primaryContainer = Yellow,
    onPrimaryContainer = Black,
    secondary = Yellow,
    onSecondary = Black,
    secondaryContainer = Orange,
    onSecondaryContainer = Black,
    tertiary = Cyan,
    onTertiary = Black,
    tertiaryContainer = Blue,
    onTertiaryContainer = White,
    background = Black,
    onBackground = White,
    surface = Black,
    onSurface = White,
    surfaceVariant = DarkSurface,
    onSurfaceVariant = White,
    error = Red,
    onError = White,
    errorContainer = DarkErrorContainer,
    onErrorContainer = White,
    outline = White,
    outlineVariant = White
)

@Composable
fun BrailleVisionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
