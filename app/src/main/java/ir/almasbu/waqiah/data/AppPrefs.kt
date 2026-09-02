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

  /** شناسه‌ی ترجمه‌هایی که کاربر خواسته زیر آیه‌ها نشان داده شوند. */
  var selectedTranslations: Set<String>
    get() = prefs.getStringSet(KEY_TRANSLATIONS, null) ?: setOf(DEFAULT_TRANSLATION)
    set(value) = prefs.edit().putStringSet(KEY_TRANSLATIONS, value).apply()

  var arabicFontScale: Float
    get() = prefs.getFloat(KEY_ARABIC_SCALE, 1f)
    set(value) = prefs.edit().putFloat(KEY_ARABIC_SCALE, value.coerceIn(0.7f, 2f)).apply()

  var translationFontScale: Float
    get() = prefs.getFloat(KEY_TRANSLATION_SCALE, 1f)
    set(value) = prefs.edit().putFloat(KEY_TRANSLATION_SCALE, value.coerceIn(0.7f, 2f)).apply()

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

  private companion object {
    const val KEY_PLAN = "plan"
    const val KEY_TRANSLATIONS = "translations"
    const val KEY_ARABIC_SCALE = "arabic_scale"
    const val KEY_TRANSLATION_SCALE = "translation_scale"
    const val KEY_REMINDER_ON = "reminder_on"
    const val KEY_REMINDER_HOUR = "reminder_hour"
    const val KEY_REMINDER_MINUTE = "reminder_minute"
    const val DEFAULT_TRANSLATION = "makarem"
  }
}
