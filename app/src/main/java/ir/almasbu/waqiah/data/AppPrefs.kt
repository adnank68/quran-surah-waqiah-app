package ir.almasbu.waqiah.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject

/**
 * همه‌ی حالتِ ماندگارِ اپ در یک `SharedPreferences` نگه داشته می‌شود: حجم داده
 * ناچیز است (یک برنامه‌ی ختم + چند تنظیم)، پس دیتابیس لازم نیست.
 */
class AppPrefs(context: Context) {

  private val prefs: SharedPreferences =
    context.applicationContext.getSharedPreferences("waqiah", Context.MODE_PRIVATE)

  // ——— برنامه‌ی ختم ———

  var plan: KhatmPlan?
    get() = prefs.getString(KEY_PLAN, null)?.let {
      runCatching { KhatmPlan.fromJson(JSONObject(it)) }.getOrNull()
    }
    set(value) = prefs.edit()
      .apply { if (value == null) remove(KEY_PLAN) else putString(KEY_PLAN, value.toJson().toString()) }
      .apply()

  // ——— نمایش قرآن ———

  /**
   * شناسه‌ی ترجمه‌ای که زیر آیه‌ها نشان داده می‌شود، یا `null` یعنی «بدون ترجمه».
   * فقط یک ترجمه در هر لحظه نمایش داده می‌شود (خواسته‌ی کاربر)، پس این یک
   * مقدار تکی است نه مجموعه — کلیدِ مجموعه‌ایِ نسخه‌های قبلی عمداً خوانده
   * نمی‌شود و کاربر یک بار به پیش‌فرض برمی‌گردد.
   */
  var selectedTranslation: String?
    get() = if (prefs.contains(KEY_TRANSLATION)) prefs.getString(KEY_TRANSLATION, null)
    else DEFAULT_TRANSLATION
    set(value) = prefs.edit()
      .apply { if (value == null) putString(KEY_TRANSLATION, null) else putString(KEY_TRANSLATION, value) }
      .apply()

  var arabicFontScale: Float
    get() = prefs.getFloat(KEY_ARABIC_SCALE, 1f)
    set(value) = prefs.edit().putFloat(KEY_ARABIC_SCALE, value.coerceIn(0.7f, 2f)).apply()

  var translationFontScale: Float
    get() = prefs.getFloat(KEY_TRANSLATION_SCALE, 1f)
    set(value) = prefs.edit().putFloat(KEY_TRANSLATION_SCALE, value.coerceIn(0.7f, 2f)).apply()

  /** شناسه‌ی قاریِ انتخاب‌شده برای تلاوت. */
  var reciterId: String
    get() = prefs.getString(KEY_RECITER, null) ?: DEFAULT_RECITER
    set(value) = prefs.edit().putString(KEY_RECITER, value).apply()

  /** `badge` = شماره بالای آیه، `inline` = شماره کنار آیه. */
  var ayahNumberStyle: String
    get() = prefs.getString(KEY_AYAH_NUMBER_STYLE, null) ?: DEFAULT_AYAH_NUMBER_STYLE
    set(value) = prefs.edit().putString(KEY_AYAH_NUMBER_STYLE, value).apply()

  // ——— تم ———

  /** `system` / `light` / `dark` */
  var themeMode: String
    get() = prefs.getString(KEY_THEME_MODE, null) ?: DEFAULT_THEME_MODE
    set(value) = prefs.edit().putString(KEY_THEME_MODE, value).apply()

  /** شناسه‌ی پالت رنگ پس‌زمینه. */
  var backgroundId: String
    get() = prefs.getString(KEY_BACKGROUND, null) ?: DEFAULT_BACKGROUND
    set(value) = prefs.edit().putString(KEY_BACKGROUND, value).apply()

  // ——— یادآور ———

  var reminderEnabled: Boolean
    get() = prefs.getBoolean(KEY_REMINDER_ON, false)
    set(value) = prefs.edit().putBoolean(KEY_REMINDER_ON, value).apply()

  var reminderHour: Int
    get() = prefs.getInt(KEY_REMINDER_HOUR, 20)
    set(value) = prefs.edit().putInt(KEY_REMINDER_HOUR, value.coerceIn(0, 23)).apply()

  var reminderMinute: Int
    get() = prefs.getInt(KEY_REMINDER_MINUTE, 0)
    set(value) = prefs.edit().putInt(KEY_REMINDER_MINUTE, value.coerceIn(0, 59)).apply()

  companion object {
    const val DEFAULT_TRANSLATION = "makarem"
    const val DEFAULT_RECITER = "minshawi"
    const val DEFAULT_AYAH_NUMBER_STYLE = "badge"
    const val DEFAULT_THEME_MODE = "system"
    const val DEFAULT_BACKGROUND = "default"

    private const val KEY_PLAN = "plan"
    private const val KEY_TRANSLATION = "translation"
    private const val KEY_RECITER = "reciter"
    private const val KEY_ARABIC_SCALE = "arabic_scale"
    private const val KEY_TRANSLATION_SCALE = "translation_scale"
    private const val KEY_AYAH_NUMBER_STYLE = "ayah_number_style"
    private const val KEY_THEME_MODE = "theme_mode"
    private const val KEY_BACKGROUND = "background"
    private const val KEY_REMINDER_ON = "reminder_on"
    private const val KEY_REMINDER_HOUR = "reminder_hour"
    private const val KEY_REMINDER_MINUTE = "reminder_minute"
  }
}
