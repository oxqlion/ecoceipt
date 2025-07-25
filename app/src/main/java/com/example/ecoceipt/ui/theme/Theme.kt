package com.example.ecoceipt.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Green80,
    secondary = GreenGrey80,
    tertiary = Mint80,
    background = Color(0xFF0D1F12),
    surface = Color(0xFF1B5E20),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Green80,
    onSurface = Green80
)

private val LightColorScheme = lightColorScheme(
    primary = Green40,
    secondary = GreenGrey40,
    tertiary = Mint40,
    background = EcoColors.Background,
    surface = EcoColors.Surface,
    onPrimary = EcoColors.OnPrimary,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = EcoColors.OnBackground,
    onSurface = EcoColors.OnSurface
)

@Composable
fun EcoceiptTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}