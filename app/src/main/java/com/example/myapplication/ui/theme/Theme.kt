package com.example.myapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PC_PrimaryLight,
    secondary = PC_Secondary,
    tertiary = PC_Accent,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    error = PC_Error
)

private val LightColorScheme = lightColorScheme(
    primary = PC_Primary,
    onPrimary = Color.White,
    secondary = PC_Secondary,
    onSecondary = Color.White,
    tertiary = PC_Accent,
    onTertiary = Color.White,
    background = PC_Background,
    surface = PC_Surface,
    onBackground = PC_TextPrimary,
    onSurface = PC_TextPrimary,
    error = PC_Error
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
