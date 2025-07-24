package com.example.tim_sam_2.ui.theme

import android.app.Activity
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
fun Tim_sam_2Theme(
    darkTheme: Boolean = false, // Force light mode always
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled to use our custom green theme
    content: @Composable () -> Unit
) {
    // Always use light color scheme
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}