package ir.almasbu.waqiah.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ir.almasbu.waqiah.R

/** وزیرمتن برای کل رابط فارسی (SIL OFL). */
val Vazirmatn = FontFamily(
  Font(R.font.vazirmatn_regular, FontWeight.Normal),
  Font(R.font.vazirmatn_medium, FontWeight.Medium),
  Font(R.font.vazirmatn_bold, FontWeight.Bold),
)

/**
 * خط عثمان‌طه (KFGQPC HAFS Uthmanic Script) — فقط برای متن عربی آیات و دعاها.
 */
val UthmanicHafs = FontFamily(Font(R.font.uthmanic_hafs, FontWeight.Normal))

private val base = Typography()

val WaqiahTypography = Typography(
  displayLarge = base.displayLarge.copy(fontFamily = Vazirmatn),
  displayMedium = base.displayMedium.copy(fontFamily = Vazirmatn),
  displaySmall = base.displaySmall.copy(fontFamily = Vazirmatn),
  headlineLarge = base.headlineLarge.copy(fontFamily = Vazirmatn),
  headlineMedium = base.headlineMedium.copy(fontFamily = Vazirmatn),
  headlineSmall = base.headlineSmall.copy(fontFamily = Vazirmatn),
  titleLarge = base.titleLarge.copy(fontFamily = Vazirmatn),
  titleMedium = base.titleMedium.copy(fontFamily = Vazirmatn),
  titleSmall = base.titleSmall.copy(fontFamily = Vazirmatn),
  bodyLarge = base.bodyLarge.copy(fontFamily = Vazirmatn, lineHeight = 28.sp),
  bodyMedium = base.bodyMedium.copy(fontFamily = Vazirmatn, lineHeight = 26.sp),
  bodySmall = base.bodySmall.copy(fontFamily = Vazirmatn, lineHeight = 22.sp),
  labelLarge = base.labelLarge.copy(fontFamily = Vazirmatn),
  labelMedium = base.labelMedium.copy(fontFamily = Vazirmatn),
  labelSmall = base.labelSmall.copy(fontFamily = Vazirmatn),
)

/** سبک متن عربیِ آیه؛ ارتفاع خط سخاوتمند تا اعراب و علائم وقف بریده نشوند. */
fun ayahTextStyle(fontSizeSp: Float) = TextStyle(
  fontFamily = UthmanicHafs,
  fontSize = fontSizeSp.sp,
  lineHeight = (fontSizeSp * 2.0f).sp,
)

/**
 * سبک متن دعاها — عمداً وزیرمتن، نه خط قرآنی.
 *
 * فونت KFGQPC فقط برای ترکیب‌هایی ساخته شده که در خودِ قرآن می‌آیند. متن
 * دعاها رسم‌الخط فارسی دارد و مثلاً دنباله‌ی «ی + الف خنجری» (یٰا) در آن
 * لنگرِ چسباندن ندارد؛ نتیجه‌اش این بود که رندرر به‌جای هر «یٰ» یک دایره‌ی
 * خالی می‌گذاشت و کل دعا خراب دیده می‌شد. وزیرمتن این ترکیب‌ها را درست
 * می‌چیند. **خط قرآنی فقط برای متن قرآن.**
 */
fun duaTextStyle(fontSizeSp: Float) = TextStyle(
  fontFamily = Vazirmatn,
  fontSize = fontSizeSp.sp,
  lineHeight = (fontSizeSp * 2.1f).sp,
)
