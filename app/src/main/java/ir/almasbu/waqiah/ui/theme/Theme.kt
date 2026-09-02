package ir.almasbu.waqiah.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// سبزِ محرابی با لهجه‌ی طلایی — رنگ‌بندی رایج جلد و تذهیب قرآن.
val GreenDark = Color(0xFF0B3D2E)
val GreenMid = Color(0xFF15604A)
val GreenSoft = Color(0xFFE3F0EA)
val Gold = Color(0xFFB8860B)
val GoldSoft = Color(0xFFF2D493)
val Sand = Color(0xFFFBF8F1)
val InkDark = Color(0xFF16201C)

private val LightColors = lightColorScheme(
  primary = GreenDark,
  onPrimary = Color.White,
  primaryContainer = GreenSoft,
  onPrimaryContainer = GreenDark,
  secondary = Gold,
  onSecondary = Color.White,
  secondaryContainer = GoldSoft,
  onSecondaryContainer = Color(0xFF4A3600),
  background = Sand,
  onBackground = InkDark,
  surface = Color.White,
  onSurface = InkDark,
  surfaceVariant = Color(0xFFEFEAE0),
  onSurfaceVariant = Color(0xFF4C5450),
)

private val DarkColors = darkColorScheme(
  primary = Color(0xFF7FD1B0),
  onPrimary = Color(0xFF00382A),
  primaryContainer = Color(0xFF00513D),
  onPrimaryContainer = Color(0xFF9BF0CD),
  secondary = GoldSoft,
  onSecondary = Color(0xFF3E2E00),
  secondaryContainer = Color(0xFF5A4300),
  onSecondaryContainer = Color(0xFFFFE0A3),
  background = Color(0xFF101513),
  onBackground = Color(0xFFE1E3E0),
  surface = Color(0xFF171D1A),
  onSurface = Color(0xFFE1E3E0),
  surfaceVariant = Color(0xFF2A3330),
  onSurfaceVariant = Color(0xFFBFC9C4),
)

@Composable
fun WaqiahTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = if (darkTheme) DarkColors else LightColors,
    typography = WaqiahTypography,
    content = content,
  )
}
