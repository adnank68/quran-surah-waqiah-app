package ir.almasbu.waqiah.notify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * آلارم‌ها با ری‌استارت دستگاه (و همچنین با به‌روزرسانی اپ یا تغییر ساعت
 * سیستم) پاک می‌شوند؛ اینجا دوباره تنظیم‌شان می‌کنیم.
 */
class BootReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    when (intent.action) {
      Intent.ACTION_BOOT_COMPLETED,
      Intent.ACTION_MY_PACKAGE_REPLACED,
      Intent.ACTION_TIME_CHANGED,
      Intent.ACTION_TIMEZONE_CHANGED,
      -> ReminderScheduler.reschedule(context)
    }
  }
}
