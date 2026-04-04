package com.nts.financemanager.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

// ── "Digital Vault" Light Theme ──────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary = LightVaultPrimary,
    onPrimary = LightVaultLowest,
    primaryContainer = LightVaultPrimaryLight,
    onPrimaryContainer = LightVaultBackground,
    secondary = LightVaultSuccess,
    onSecondary = LightVaultLowest,
    secondaryContainer = LightVaultSurfaceHighest,
    onSecondaryContainer = LightVaultOnSurface,
    background = LightVaultBackground,
    onBackground = LightVaultOnSurface,
    surface = LightVaultBackground,
    onSurface = LightVaultOnSurface,
    surfaceVariant = LightVaultSurfaceMedium,
    onSurfaceVariant = LightVaultOnSurfaceVariant,
    outline = LightVaultOutlineVariant,
    error = LightVaultError,
    onError = LightVaultLowest,
    errorContainer = LightVaultError,
    onErrorContainer = LightVaultLowest,
    surfaceContainerLowest = LightVaultLowest,
    surfaceContainerLow = LightVaultSurfaceLow,
    surfaceContainer = LightVaultSurfaceMedium,
    surfaceContainerHigh = LightVaultSurfaceHighest,
    surfaceContainerHighest = LightVaultSurfaceHighest
)

// ── "Digital Vault" Dark Theme (Primary Design) ─────────────────────
private val DarkColorScheme = darkColorScheme(
    primary = VaultPrimary,
    onPrimary = VaultLowest,
    primaryContainer = VaultPrimaryDark,
    onPrimaryContainer = VaultBackground,
    secondary = VaultSuccess,
    onSecondary = VaultLowest,
    secondaryContainer = VaultSurfaceHighest,
    onSecondaryContainer = VaultOnSurface,
    background = VaultBackground,
    onBackground = VaultOnSurface,
    surface = VaultBackground,
    onSurface = VaultOnSurface,
    surfaceVariant = VaultSurfaceMedium,
    onSurfaceVariant = VaultOnSurfaceVariant,
    outline = VaultOutlineVariant,
    error = VaultError,
    onError = VaultLowest,
    errorContainer = VaultError,
    onErrorContainer = VaultLowest,
    surfaceContainerLowest = VaultLowest,
    surfaceContainerLow = VaultSurfaceLow,
    surfaceContainer = VaultSurfaceMedium,
    surfaceContainerHigh = VaultSurfaceHighest,
    surfaceContainerHighest = VaultSurfaceHighest
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

