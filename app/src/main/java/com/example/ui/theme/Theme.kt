package com.example.ui.theme

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

private val DarkColorScheme =
  darkColorScheme(
    primary = BrandPrimaryLight,
    onPrimary = BrandPrimaryDark,
    primaryContainer = Color(0xFF4F378B),
    onPrimaryContainer = BrandPrimaryLight,
    secondary = BrandAccentLight,
    onSecondary = Color(0xFF003732),
    secondaryContainer = BrandAccent,
    onSecondaryContainer = Color(0xFFE0F2FE),
    tertiary = Color(0xFFFFD8E4),
    onTertiary = Color(0xFF492532),
    background = DarkBackground,
    onBackground = Color(0xFFE6E1E5),
    surface = DarkSurface,
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = DarkBorder,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    primaryContainer = BrandPrimaryLight,
    onPrimaryContainer = BrandPrimaryDark,
    secondary = BrandAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCE8E3),
    onSecondaryContainer = Color(0xFF00201D),
    tertiary = Color(0xFF7D5260),
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = GlassTextPrimary,
    surface = LightSurface,
    onSurface = GlassTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = GlassTextSecondary,
    outline = LightBorder,
    error = ErrorRed,
    onError = Color.White
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

