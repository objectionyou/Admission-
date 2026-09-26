package com.aj75.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * The original web prototype is hard-locked to dark mode (`<html class="dark">`), and this
 * user's other Android project (FocusShield) is also dark-only, so this theme intentionally
 * does not branch on [isSystemInDarkTheme] — the app always renders the dark palette below.
 * Drop a Noto Sans Bengali .ttf into res/font and reference it here later for pixel-perfect
 * glyphs; Android's system font fallback already renders Bengali correctly without it.
 */
private val AJ75DarkColorScheme = darkColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    secondary = BrandSecondary,
    onSecondary = Slate950,
    tertiary = BrandTertiary,
    onTertiary = Slate950,
    background = Slate900,
    onBackground = Slate200,
    surface = Slate800,
    onSurface = Slate200,
    surfaceVariant = Slate700,
    onSurfaceVariant = Slate300,
    error = BrandDanger,
    onError = Color.White,
    outline = GlassCardBorder,
)

private val AJ75Typography = Typography().let { base ->
    Typography(
        displayLarge = base.displayLarge.copy(fontWeight = FontWeight.ExtraBold),
        headlineMedium = base.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
        titleLarge = base.titleLarge.copy(fontWeight = FontWeight.Bold),
        titleMedium = base.titleMedium.copy(fontWeight = FontWeight.Bold),
        bodyLarge = base.bodyLarge.copy(fontSize = 14.sp),
        bodyMedium = base.bodyMedium.copy(fontSize = 13.sp),
        bodySmall = base.bodySmall.copy(fontSize = 11.sp),
        labelSmall = base.labelSmall.copy(fontSize = 10.sp),
    )
}

/** Generous, continuous-looking corners across Buttons/Cards/Sheets/Dialogs/TextFields for the
 *  iOS-glossy feel the user asked for — one shared token set instead of per-component overrides. */
private val AJ75Shapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(26.dp),
    extraLarge = RoundedCornerShape(34.dp),
)

/** Countdown / stat numerals use this heavier style across Home + widgets. */
val StatNumberStyle = TextStyle(fontWeight = FontWeight.Black, fontSize = 28.sp)

@Composable
fun AdmissionJourney75Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AJ75DarkColorScheme,
        typography = AJ75Typography,
        shapes = AJ75Shapes,
        content = content,
    )
}
