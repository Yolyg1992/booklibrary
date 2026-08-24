package com.tustockpro.booklibrary.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Navy =
    Color(0xFF0B1F3A)
private val NavySurface =
    Color(0xFF112A4A)
private val Gold =
    Color(0xFFD4AF37)
private val GoldSoft =
    Color(0xFFF1D88C)
private val Ivory =
    Color(0xFFF7F3EA)
private val Slate =
    Color(0xFF374151)
private val White =
    Color(0xFFFFFFFF)

private val LightColors =
    lightColorScheme(
        primary = Navy,
        onPrimary = White,
        secondary = Gold,
        onSecondary = Navy,
        background = Ivory,
        onBackground = Navy,
        surface = White,
        onSurface = Navy,
        primaryContainer = Color(0xFFDCE6F5),
        onPrimaryContainer = Navy,
        secondaryContainer = GoldSoft,
        onSecondaryContainer = Navy,
        outline = Color(0xFFB59D52)
    )

private val DarkColors =
    darkColorScheme(
        primary = Gold,
        onPrimary = Navy,
        secondary = GoldSoft,
        onSecondary = Navy,
        background = Navy,
        onBackground = White,
        surface = NavySurface,
        onSurface = White,
        primaryContainer = Color(0xFF1D3C64),
        onPrimaryContainer = White,
        secondaryContainer = Color(0xFF5B4A11),
        onSecondaryContainer = White,
        outline = Slate
    )

@Composable
fun BookLibraryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colorScheme =
        if (darkTheme) {
            DarkColors
        } else {
            LightColors
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
