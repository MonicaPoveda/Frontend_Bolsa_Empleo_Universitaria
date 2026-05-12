package com.example.frontend_bolsa_empleo_universitaria.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Tokens visuales unificados (referencia: experiencia Mis Postulaciones).
 */
object BolsaTokens {
    object Palette {
        val Background = Color(0xFFF4F7FF)
        val Surface = Color(0xFFFFFFFF)
        val Primary = Color(0xFF2563EB)
        val PrimaryLight = Color(0xFFEFF6FF)
        val Secondary = Color(0xFF7C3AED)
        val Accent = Color(0xFFF59E0B)
        val TextPrimary = Color(0xFF0F172A)
        val TextSecondary = Color(0xFF64748B)
        val Divider = Color(0xFFE2E8F0)
        val Success = Color(0xFF10B981)
        val Warning = Color(0xFFF59E0B)
        val Error = Color(0xFFEF4444)
        val Info = Color(0xFF3B82F6)
        val HeaderStart = Color(0xFF1E40AF)
        val HeaderEnd = Color(0xFF6D28D9)
    }

    object Dimens {
        val screenPadding = 20.dp
        val cardRadius = 20.dp
        val chipRadius = 50.dp
        val buttonRadius = 14.dp
        val fieldRadius = 16.dp
        val iconSm = 20.dp
        val iconMd = 24.dp
        val iconLg = 28.dp
        val touchMin = 48.dp
    }

    val headerGradientLinear: Brush
        get() = Brush.linearGradient(
            colors = listOf(Palette.HeaderStart, Palette.HeaderEnd),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )

    val headerGradientVertical: Brush
        get() = Brush.verticalGradient(listOf(Palette.HeaderStart, Palette.HeaderEnd))
}
