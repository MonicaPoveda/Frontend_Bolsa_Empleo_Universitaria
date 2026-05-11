package com.example.frontend_bolsa_empleo_universitaria.ui.theme

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
    primary = UniversityGold,
    secondary = UniversityTeal,
    tertiary = UniversityBlue,
    background = Color(0xFF0D1820),
    surface = Color(0xFF122231),
    onPrimary = Color(0xFF211A0B),
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFEAF3F5),
    onSurface = Color(0xFFEAF3F5)
)

private val LightColorScheme = lightColorScheme(
    primary = UniversityBlue,
    secondary = UniversityTeal,
    tertiary = UniversityGold,
    background = UniversitySurface,
    surface = Color.White,
    surfaceVariant = UniversityMist,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color(0xFF2B2109),
    onBackground = UniversityInk,
    onSurface = UniversityInk,
    outline = Color(0xFF9BAEB6)

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun Frontend_Bolsa_Empleo_UniversitariaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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
