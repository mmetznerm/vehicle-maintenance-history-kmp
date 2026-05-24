package com.mmetzner.vehiclemaintenance.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VehicleMaintenanceLightColorScheme = lightColorScheme(
    primary = Color(0xFF1B6B63),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD6F1EC),
    onPrimaryContainer = Color(0xFF0A332F),
    secondary = Color(0xFF4D5F68),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFDCE7EB),
    onSecondaryContainer = Color(0xFF101F26),
    tertiary = Color(0xFFB7791F),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFE2B8),
    onTertiaryContainer = Color(0xFF2B1700),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF7F8FA),
    onBackground = Color(0xFF172026),
    surface = Color(0xFFF7F8FA),
    onSurface = Color(0xFF172026),
    surfaceVariant = Color(0xFFE0E6EA),
    onSurfaceVariant = Color(0xFF5E6A72),
    outline = Color(0xFFCBD5DA),
    outlineVariant = Color(0xFFE0E6EA),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF7F8FA),
    surfaceContainer = Color(0xFFEEF2F3),
    surfaceContainerHigh = Color(0xFFE8EEF0),
    surfaceContainerHighest = Color(0xFFE0E6EA),
    inverseSurface = Color(0xFF2C343A),
    inverseOnSurface = Color(0xFFF0F3F5),
    inversePrimary = Color(0xFF9AD8D0)
)

@Composable
fun VehicleMaintenanceTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = VehicleMaintenanceLightColorScheme,
        content = content
    )
}
