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
val InkDark = Color(0xFF16201C)

/** حالت روشن/تاریک؛ `SYSTEM` از تنظیم خود گوشی پیروی می‌کند. */
enum class ThemeMode(val id: String, val label: String) {
  SYSTEM("system", "طبق تنظیم گوشی"),
  LIGHT("light", "روشن"),
  DARK("dark", "تیره");

  companion object {
    fun fromId(id: String?): ThemeMode = entries.firstOrNull { it.id == id } ?: SYSTEM
  }
}

/**
 * رنگ پس‌زمینه‌ی قابل‌انتخاب. هر پالت هم نسخه‌ی روشن دارد هم تیره، تا انتخاب
 * رنگ و انتخاب حالت روشن/تاریک مستقل از هم بمانند.
 */
enum class BackgroundPalette(
  val id: String,
  val label: String,
  val lightBackground: Color,
  val lightSurface: Color,
  val darkBackground: Color,
  val darkSurface: Color,
) {
  DEFAULT("default", "پیش‌فرض", Color(0xFFFBF8F1), Color(0xFFFFFFFF), Color(0xFF101513), Color(0xFF171D1A)),
  PAPER("paper", "کاغذ کاهی", Color(0xFFF4ECD8), Color(0xFFFCF7EA), Color(0xFF1A1712), Color(0xFF241F18)),
  MINT("mint", "سبز ملایم", Color(0xFFE7F2EC), Color(0xFFF6FBF8), Color(0xFF0D1714), Color(0xFF14201C)),
  SKY("sky", "آبی ملایم", Color(0xFFEAF0F6), Color(0xFFF8FBFD), Color(0xFF0E1419), Color(0xFF151D24)),
  GRAY("gray", "خاکستری", Color(0xFFF0F0EE), Color(0xFFFFFFFF), Color(0xFF141414), Color(0xFF1E1E1E)),
  BLACK("black", "مشکی", Color(0xFFF7F7F7), Color(0xFFFFFFFF), Color(0xFF000000), Color(0xFF0C0C0C));

  companion object {
    fun fromId(id: String?): BackgroundPalette = entries.firstOrNull { it.id == id } ?: DEFAULT
  }
}

private fun lightScheme(palette: BackgroundPalette) = lightColorScheme(
  primary = GreenDark,
  onPrimary = Color.White,
  primaryContainer = GreenSoft,
  onPrimaryContainer = GreenDark,
  secondary = Gold,
  onSecondary = Color.White,
  secondaryContainer = GoldSoft,
  onSecondaryContainer = Color(0xFF4A3600),
  background = palette.lightBackground,
  onBackground = InkDark,
  surface = palette.lightSurface,
  onSurface = InkDark,
  surfaceVariant = Color(0xFFEFEAE0),
  onSurfaceVariant = Color(0xFF4C5450),
)

private fun darkScheme(palette: BackgroundPalette) = darkColorScheme(
  primary = Color(0xFF7FD1B0),
  onPrimary = Color(0xFF00382A),
  primaryContainer = Color(0xFF00513D),
  onPrimaryContainer = Color(0xFF9BF0CD),
  secondary = GoldSoft,
  onSecondary = Color(0xFF3E2E00),
  secondaryContainer = Color(0xFF5A4300),
  onSecondaryContainer = Color(0xFFFFE0A3),
  background = palette.darkBackground,
  onBackground = Color(0xFFE1E3E0),
  surface = palette.darkSurface,
  onSurface = Color(0xFFE1E3E0),
  surfaceVariant = Color(0xFF2A3330),
  onSurfaceVariant = Color(0xFFBFC9C4),
)

@Composable
fun WaqiahTheme(
  themeMode: ThemeMode = ThemeMode.SYSTEM,
  palette: BackgroundPalette = BackgroundPalette.DEFAULT,
  content: @Composable () -> Unit,
) {
  val dark = when (themeMode) {
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
  }
  MaterialTheme(
    colorScheme = if (dark) darkScheme(palette) else lightScheme(palette),
    typography = WaqiahTypography,
    content = content,
  )
}
