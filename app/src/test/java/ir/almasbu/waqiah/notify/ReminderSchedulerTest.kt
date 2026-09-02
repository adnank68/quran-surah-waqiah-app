package ir.almasbu.waqiah.notify

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.Calendar

/**
 * `nextTriggerMillis` منطق خالص است، ولی چون داخل شیئی زندگی می‌کند که به
 * `AlarmManager` ارجاع دارد، با Robolectric اجرا می‌شود تا بارگذاری کلاس
 * روی JVM ساده به مشکل نخورد.
 */
@RunWith(RobolectricTestRunner::class)
class ReminderSchedulerTest {

  private fun calendarAt(year: Int, month: Int, day: Int, hour: Int, minute: Int): Calendar =
    Calendar.getInstance().apply {
      clear()
      set(year, month - 1, day, hour, minute, 0)
    }

  @Test
  fun picksTodayWhenTheTimeIsStillAhead() {
    val now = calendarAt(2026, 9, 2, 8, 0)
    val trigger = Calendar.getInstance().apply {
      timeInMillis = ReminderScheduler.nextTriggerMillis(20, 30, now)
    }

    assertEquals(2, trigger.get(Calendar.DAY_OF_MONTH))
    assertEquals(20, trigger.get(Calendar.HOUR_OF_DAY))
    assertEquals(30, trigger.get(Calendar.MINUTE))
  }

  @Test
  fun rollsToTomorrowWhenTheTimeHasPassed() {
    val now = calendarAt(2026, 9, 2, 21, 0)
    val trigger = Calendar.getInstance().apply {
      timeInMillis = ReminderScheduler.nextTriggerMillis(20, 30, now)
    }

    assertEquals(3, trigger.get(Calendar.DAY_OF_MONTH))
    assertEquals(20, trigger.get(Calendar.HOUR_OF_DAY))
  }

  @Test
  fun rollsToTomorrowAtExactlyTheSameMinute() {
    // اگر آلارم دقیقاً سرِ همین لحظه باشد، باید برای فردا تنظیم شود وگرنه
    // بلافاصله دوباره شلیک می‌شود و یک حلقه‌ی بی‌پایان می‌سازد.
    val now = calendarAt(2026, 9, 2, 20, 30)
    val trigger = Calendar.getInstance().apply {
      timeInMillis = ReminderScheduler.nextTriggerMillis(20, 30, now)
    }

    assertEquals(3, trigger.get(Calendar.DAY_OF_MONTH))
  }

  @Test
  fun theNextTriggerIsAlwaysInTheFuture() {
    val now = calendarAt(2026, 9, 2, 13, 45)
    for (hour in 0..23) {
      for (minute in listOf(0, 30, 59)) {
        val next = ReminderScheduler.nextTriggerMillis(hour, minute, now)
        assertTrue(
          "زنگ $hour:$minute در گذشته تنظیم شد",
          next > now.timeInMillis,
        )
        assertTrue(
          "زنگ نباید بیش از ۲۴ ساعت جلوتر باشد",
          next - now.timeInMillis <= 24L * 60 * 60 * 1000,
        )
      }
    }
  }

  @Test
  fun crossesMonthBoundary() {
    val now = calendarAt(2026, 9, 30, 23, 0)
    val trigger = Calendar.getInstance().apply {
      timeInMillis = ReminderScheduler.nextTriggerMillis(7, 0, now)
    }

    assertEquals(Calendar.OCTOBER, trigger.get(Calendar.MONTH))
    assertEquals(1, trigger.get(Calendar.DAY_OF_MONTH))
    assertEquals(7, trigger.get(Calendar.HOUR_OF_DAY))
  }
}
