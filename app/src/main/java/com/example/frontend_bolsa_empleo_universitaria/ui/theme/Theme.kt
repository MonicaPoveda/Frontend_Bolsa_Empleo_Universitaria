package com.example.frontend_bolsa_empleo_universitaria.ui.theme

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
    primary = BolsaTokens.Palette.Primary,
    onPrimary = Color.White,
    primaryContainer = BolsaTokens.Palette.HeaderEnd,
    onPrimaryContainer = Color.White,
    secondary = BolsaTokens.Palette.Secondary,
    onSecondary = Color.White,
    tertiary = BolsaTokens.Palette.Accent,
    onTertiary = Color(0xFF2B2109),
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFE2E8F0),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFE2E8F0),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF64748B),
    error = BolsaTokens.Palette.Error,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = BolsaTokens.Palette.Primary,
    onPrimary = Color.White,
    primaryContainer = BolsaTokens.Palette.PrimaryLight,
    onPrimaryContainer = BolsaTokens.Palette.HeaderStart,
    secondary = BolsaTokens.Palette.Secondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE9FE),
    onSecondaryContainer = BolsaTokens.Palette.HeaderEnd,
    tertiary = BolsaTokens.Palette.Accent,
    onTertiary = Color(0xFF422006),
    background = BolsaTokens.Palette.Background,
    onBackground = BolsaTokens.Palette.TextPrimary,
    surface = BolsaTokens.Palette.Surface,
    onSurface = BolsaTokens.Palette.TextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = BolsaTokens.Palette.TextSecondary,
    outline = BolsaTokens.Palette.Divider,
    error = BolsaTokens.Palette.Error,
    onError = Color.White,
    outlineVariant = Color(0xFFCBD5E1)
)

@Composable
fun Frontend_Bolsa_Empleo_UniversitariaTheme(
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
        shapes = BolsaShapes,
        content = content
    )
}
