package ir.almasbu.waqiah.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ir.almasbu.waqiah.data.AppPrefs
import ir.almasbu.waqiah.data.KhatmMethod
import ir.almasbu.waqiah.data.KhatmPlan
import ir.almasbu.waqiah.data.WaqiahContent
import ir.almasbu.waqiah.data.WaqiahRepository
import ir.almasbu.waqiah.notify.ReminderScheduler
import ir.almasbu.waqiah.util.Jalali
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class UiState(
  val content: WaqiahContent? = null,
  val plan: KhatmPlan? = null,
  val todayJdn: Int = Jalali.todayJdn(),
  val selectedTranslations: Set<String> = emptySet(),
  val arabicFontScale: Float = 1f,
  val translationFontScale: Float = 1f,
  val reminderEnabled: Boolean = false,
  val reminderHour: Int = 20,
  val reminderMinute: Int = 0,
) {
  val isLoaded: Boolean get() = content != null

  /** شماره‌ی روز امروز در برنامه‌ی جاری، یا `null`. */
  val todayIndex: Int? get() = plan?.todayIndex(todayJdn)
}

class AppViewModel(app: Application) : AndroidViewModel(app) {

  private val prefs = AppPrefs(app)

  private val _state = MutableStateFlow(
    UiState(
      plan = prefs.plan,
      selectedTranslations = prefs.selectedTranslations,
      arabicFontScale = prefs.arabicFontScale,
      translationFontScale = prefs.translationFontScale,
      reminderEnabled = prefs.reminderEnabled,
      reminderHour = prefs.reminderHour,
      reminderMinute = prefs.reminderMinute,
    )
  )
  val state: StateFlow<UiState> = _state.asStateFlow()

  init {
    viewModelScope.launch {
      // ۱۴۸ کیلوبایت JSON؛ خارج از ریسمان اصلی خوانده می‌شود تا اولین فریم نپرد.
      val content = withContext(Dispatchers.IO) { WaqiahRepository.load(getApplication()) }
      _state.update { it.copy(content = content) }
    }
  }

  /** پس از برگشت از پس‌زمینه، ممکن است روز عوض شده باشد. */
  fun refreshToday() {
    _state.update { it.copy(todayJdn = Jalali.todayJdn()) }
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

  fun toggleTranslation(id: String) {
    val next = _state.value.selectedTranslations.let {
      if (id in it) it - id else it + id
    }
    prefs.selectedTranslations = next
    _state.update { it.copy(selectedTranslations = next) }
  }

  fun setArabicFontScale(scale: Float) {
    prefs.arabicFontScale = scale
    _state.update { it.copy(arabicFontScale = prefs.arabicFontScale) }
  }

  fun setTranslationFontScale(scale: Float) {
    prefs.translationFontScale = scale
    _state.update { it.copy(translationFontScale = prefs.translationFontScale) }
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
