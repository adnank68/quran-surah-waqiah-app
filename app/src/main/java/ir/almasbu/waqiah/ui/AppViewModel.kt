package ir.almasbu.waqiah.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ir.almasbu.waqiah.audio.RecitationPlayer
import ir.almasbu.waqiah.audio.Reciter
import ir.almasbu.waqiah.data.AppPrefs
import ir.almasbu.waqiah.data.KhatmMethod
import ir.almasbu.waqiah.data.KhatmPlan
import ir.almasbu.waqiah.data.WaqiahContent
import ir.almasbu.waqiah.data.WaqiahRepository
import ir.almasbu.waqiah.notify.ReminderScheduler
import ir.almasbu.waqiah.ui.theme.BackgroundPalette
import ir.almasbu.waqiah.ui.theme.ThemeMode
import ir.almasbu.waqiah.util.Jalali
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** جای نمایش شماره‌ی آیه. */
enum class AyahNumberStyle(val id: String, val label: String) {
  BADGE("badge", "بالای آیه"),
  INLINE("inline", "کنار آیه");

  companion object {
    fun fromId(id: String?): AyahNumberStyle = entries.firstOrNull { it.id == id } ?: BADGE
  }
}

data class UiState(
  val content: WaqiahContent? = null,
  val plan: KhatmPlan? = null,
  val todayJdn: Int = Jalali.todayJdn(),
  /** شناسه‌ی تنها ترجمه‌ی نمایش‌داده‌شده، یا `null` یعنی بدون ترجمه. */
  val selectedTranslation: String? = null,
  val reciter: Reciter = Reciter.DEFAULT,
  val arabicFontScale: Float = 1f,
  val translationFontScale: Float = 1f,
  val ayahNumberStyle: AyahNumberStyle = AyahNumberStyle.BADGE,
  val themeMode: ThemeMode = ThemeMode.SYSTEM,
  val background: BackgroundPalette = BackgroundPalette.DEFAULT,
  val reminderEnabled: Boolean = false,
  val reminderHour: Int = 20,
  val reminderMinute: Int = 0,
  val audio: RecitationPlayer.State = RecitationPlayer.State(),
) {
  val isLoaded: Boolean get() = content != null

  /** شماره‌ی روز امروز در برنامه‌ی جاری، یا `null`. */
  val todayIndex: Int? get() = plan?.todayIndex(todayJdn)
}

class AppViewModel(app: Application) : AndroidViewModel(app) {

  private val prefs = AppPrefs(app)
  private val player = RecitationPlayer(app)

  private val _state = MutableStateFlow(
    UiState(
      plan = prefs.plan,
      selectedTranslation = prefs.selectedTranslation,
      reciter = Reciter.fromId(prefs.reciterId),
      arabicFontScale = prefs.arabicFontScale,
      translationFontScale = prefs.translationFontScale,
      ayahNumberStyle = AyahNumberStyle.fromId(prefs.ayahNumberStyle),
      themeMode = ThemeMode.fromId(prefs.themeMode),
      background = BackgroundPalette.fromId(prefs.backgroundId),
      reminderEnabled = prefs.reminderEnabled,
      reminderHour = prefs.reminderHour,
      reminderMinute = prefs.reminderMinute,
    )
  )
  val state: StateFlow<UiState> = _state.asStateFlow()

  init {
    // پخش‌کننده باید از همان اول قاریِ ذخیره‌شده را بشناسد.
    player.setReciter(_state.value.reciter)
    viewModelScope.launch {
      // ۱۵۲ کیلوبایت JSON؛ خارج از ریسمان اصلی خوانده می‌شود تا اولین فریم نپرد.
      val content = withContext(Dispatchers.IO) { WaqiahRepository.load(getApplication()) }
      _state.update { it.copy(content = content) }
    }
    viewModelScope.launch {
      player.state.collect { audio -> _state.update { it.copy(audio = audio) } }
    }
  }

  override fun onCleared() {
    player.release()
    super.onCleared()
  }

  /** پس از برگشت از پس‌زمینه، ممکن است روز عوض شده باشد. */
  fun refreshToday() {
    _state.update { it.copy(todayJdn = Jalali.todayJdn()) }
  }

  // ——— تلاوت ———

  fun togglePlayPause() = player.togglePlayPause()

  /** با زدن روی یک آیه، تلاوت از همان‌جا شروع می‌شود. */
  fun playFromAyah(ayah: Int) = player.playAyah(ayah)

  fun stopRecitation() = player.stop()

  /** عوض کردن قاری؛ اگر وسط تلاوت باشد، از همان آیه با صدای جدید ادامه می‌دهد. */
  fun setReciter(reciter: Reciter) {
    prefs.reciterId = reciter.id
    _state.update { it.copy(reciter = reciter) }
    player.setReciter(reciter)
  }

  // ——— برنامه‌ی ختم ———

  fun startPlan(method: KhatmMethod) = setPlan(KhatmPlan.fromMethod(method, Jalali.todayJdn()))

  fun startCustomPlan(days: Int, perDay: Int) =
    setPlan(KhatmPlan.custom(days.coerceIn(1, 365), perDay.coerceIn(1, 100), Jalali.todayJdn()))

  fun clearPlan() = setPlan(null)

  fun toggleDay(index: Int) {
    val current = _state.value.plan ?: return
    setPlan(current.toggle(index))
  }

  private fun setPlan(plan: KhatmPlan?) {
    prefs.plan = plan
    _state.update { it.copy(plan = plan, todayJdn = Jalali.todayJdn()) }
  }

  // ——— نمایش قرآن ———

  /**
   * فقط یک ترجمه در هر لحظه دیده می‌شود: انتخاب یک ترجمه، ترجمه‌ی قبلی را از
   * حالت انتخاب درمی‌آورد، و زدن دوباره روی همان، ترجمه را کلاً برمی‌دارد.
   */
  fun selectTranslation(id: String) {
    val next = if (_state.value.selectedTranslation == id) null else id
    prefs.selectedTranslation = next
    _state.update { it.copy(selectedTranslation = next) }
  }

  fun setArabicFontScale(scale: Float) {
    prefs.arabicFontScale = scale
    _state.update { it.copy(arabicFontScale = prefs.arabicFontScale) }
  }

  fun setTranslationFontScale(scale: Float) {
    prefs.translationFontScale = scale
    _state.update { it.copy(translationFontScale = prefs.translationFontScale) }
  }

  fun setAyahNumberStyle(style: AyahNumberStyle) {
    prefs.ayahNumberStyle = style.id
    _state.update { it.copy(ayahNumberStyle = style) }
  }

  // ——— تم ———

  fun setThemeMode(mode: ThemeMode) {
    prefs.themeMode = mode.id
    _state.update { it.copy(themeMode = mode) }
  }

  fun setBackground(palette: BackgroundPalette) {
    prefs.backgroundId = palette.id
    _state.update { it.copy(background = palette) }
  }

  // ——— یادآور ———

  fun setReminderEnabled(enabled: Boolean) {
    prefs.reminderEnabled = enabled
    _state.update { it.copy(reminderEnabled = enabled) }
    ReminderScheduler.reschedule(getApplication())
  }

  fun setReminderTime(hour: Int, minute: Int) {
    prefs.reminderHour = hour
    prefs.reminderMinute = minute
    _state.update { it.copy(reminderHour = prefs.reminderHour, reminderMinute = prefs.reminderMinute) }
    ReminderScheduler.reschedule(getApplication())
  }

  companion object {
    val Factory = object : ViewModelProvider.Factory {
      @Suppress("UNCHECKED_CAST")
      override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: androidx.lifecycle.viewmodel.CreationExtras,
      ): T {
        val app = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
        return AppViewModel(app) as T
      }
    }
  }
}
