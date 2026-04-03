package com.nts.financemanager.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── M3 Tonal Palette ──────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4A6800),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFCBF071),
    onPrimaryContainer = Color(0xFF141F00),
    secondary = Color(0xFF5A6248),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFDEE7C6),
    onSecondaryContainer = Color(0xFF171E0A),
    background = Color(0xFFFDFCF4),
    onBackground = Color(0xFF1B1C17),
    surface = Color(0xFFFDFCF4),
    onSurface = Color(0xFF1B1C17),
    surfaceVariant = Color(0xFFE3E4D3),
    onSurfaceVariant = Color(0xFF46483C),
    outline = Color(0xFF77786B),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFB0D358),
    onPrimary = Color(0xFF253600),
    primaryContainer = Color(0xFF374E00),
    onPrimaryContainer = Color(0xFFCBF071),
    secondary = Color(0xFFC2CBAB),
    onSecondary = Color(0xFF2C331D),
    secondaryContainer = Color(0xFF424A32),
    onSecondaryContainer = Color(0xFFDEE7C6),
    background = Color(0xFF1B1C17),
    onBackground = Color(0xFFE4E3DB),
    surface = Color(0xFF1B1C17),
    onSurface = Color(0xFFE4E3DB),
    surfaceVariant = Color(0xFF46483C),
    onSurfaceVariant = Color(0xFFC7C8B8)
)

@Composable
fun FinanceManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
