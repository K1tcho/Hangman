package com.shubham.hangman.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val HangmanColorScheme = lightColorScheme(
    primary = Ink,
    onPrimary = Paper,
    primaryContainer = NotebookBlueSoft,
    onPrimaryContainer = Ink,
    secondary = AccentSage,
    onSecondary = Paper,
    background = Paper,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = PaperDeep,
    onSurfaceVariant = Pencil,
    secondaryContainer = NotebookBlueSoft,
    tertiary = AccentGold,
    onTertiary = Ink,
)

@Composable
fun HangmanTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = HangmanColorScheme,
        typography = AppTypography,
        content = content,
    )
}
