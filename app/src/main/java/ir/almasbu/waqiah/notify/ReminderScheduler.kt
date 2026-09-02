package ir.almasbu.waqiah.notify

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import ir.almasbu.waqiah.data.AppPrefs
import java.util.Calendar

/**
 * زمان‌بندی یادآور روزانه.
 *
 * از `AlarmManager` استفاده می‌شود و نه `WorkManager`، چون کاربر یک ساعتِ
 * مشخص انتخاب می‌کند و کارِ دوره‌ایِ WorkManager بازه‌ی خطای پانزده‌دقیقه‌ای
 * دارد. هر بار که زنگ می‌خورد، خودِ [ReminderReceiver] زنگ روز بعد را دوباره
 * تنظیم می‌کند (یک آلارم تکرارشونده‌ی دقیق در اندروید وجود ندارد).
 */
object ReminderScheduler {

  private const val REQUEST_CODE = 1001

  fun reschedule(context: Context) {
    val prefs = AppPrefs(context)
    if (prefs.reminderEnabled) {
      schedule(context, prefs.reminderHour, prefs.reminderMinute)
    } else {
      cancel(context)
    }
  }

  fun schedule(context: Context, hour: Int, minute: Int) {
    val manager = context.getSystemService(AlarmManager::class.java) ?: return
    val triggerAt = nextTriggerMillis(hour, minute)
    val pending = pendingIntent(context, mutable = false)

    // اگر کاربر اجازه‌ی «زنگ دقیق» را نداده باشد، به آلارم غیردقیق برمی‌گردیم؛
    // یادآور همچنان می‌آید، فقط ممکن است چند دقیقه دیرتر.
    if (canScheduleExact(manager)) {
      manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
    } else {
      manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
    }
  }

  fun cancel(context: Context) {
    val manager = context.getSystemService(AlarmManager::class.java) ?: return
    manager.cancel(pendingIntent(context, mutable = false))
  }

  fun canScheduleExact(context: Context): Boolean {
    val manager = context.getSystemService(AlarmManager::class.java) ?: return false
    return canScheduleExact(manager)
  }

  private fun canScheduleExact(manager: AlarmManager): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) manager.canScheduleExactAlarms() else true

  /** نزدیک‌ترین رخدادِ آینده‌ی این ساعت؛ اگر امروز گذشته باشد، فردا. */
  fun nextTriggerMillis(hour: Int, minute: Int, from: Calendar = Calendar.getInstance()): Long {
    val next = (from.clone() as Calendar).apply {
      set(Calendar.HOUR_OF_DAY, hour)
      set(Calendar.MINUTE, minute)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }
    if (next.timeInMillis <= from.timeInMillis) {
      next.add(Calendar.DAY_OF_YEAR, 1)
    }
    return next.timeInMillis
  }

  private fun pendingIntent(context: Context, mutable: Boolean): PendingIntent {
    val intent = Intent(context, ReminderReceiver::class.java)
      .setAction(ReminderReceiver.ACTION_REMIND)
    val flags = PendingIntent.FLAG_UPDATE_CURRENT or
      if (mutable) PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_IMMUTABLE
    return PendingIntent.getBroadcast(context, REQUEST_CODE, intent, flags)
  }
}
