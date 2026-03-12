package com.ui.animatedmenu.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFC107),
    secondary = Color(0xFFCDBBA7),
    tertiary = Color(0xFF1A1A1A),
    background = Color(0xFFCDBBA7),
    surface = Color(0xFF1A1A1A)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFFFC107),
    secondary = Color(0xFFCDBBA7),
    tertiary = Color(0xFF1A1A1A),
    background = Color(0xFFCDBBA7),
    surface = Color(0xFF1A1A1A)
)

@Composable
fun AnimatedMenuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
