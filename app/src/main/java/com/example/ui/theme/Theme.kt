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
    primary = AmberGold,
    onPrimary = Color(0xFF201300),
    primaryContainer = TerracottaDark,
    onPrimaryContainer = AmberGoldLight,
    secondary = TerracottaLight,
    onSecondary = Color(0xFF350F02),
    tertiary = NileAzure,
    onTertiary = Color.White,
    background = DarkWoodBg,
    onBackground = Color(0xFFF3ECE4),
    surface = DarkSurface,
    onSurface = Color(0xFFF3ECE4),
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = Color(0xFFD4C5B8),
  )

private val LightColorScheme =
  lightColorScheme(
    primary = TerracottaPrimary,
    onPrimary = Color.White,
    primaryContainer = AmberGoldLight,
    onPrimaryContainer = Color(0xFF452200),
    secondary = AmberGoldDark,
    onSecondary = Color.White,
    tertiary = NileAzureDark,
    onTertiary = Color.White,
    background = WarmParchmentBg,
    onBackground = Color(0xFF26190E),
    surface = WarmParchmentSurface,
    onSurface = Color(0xFF26190E),
    surfaceVariant = WarmParchmentElevated,
    onSurfaceVariant = Color(0xFF4A382A),
  )

@Composable
fun AfriMahjongTheme(
  darkTheme: Boolean = true, // Default to rich cinematic warm dark theme
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

