package ir.almasbu.waqiah.notify

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import ir.almasbu.waqiah.MainActivity
import ir.almasbu.waqiah.R
import ir.almasbu.waqiah.WaqiahApp
import ir.almasbu.waqiah.data.AppPrefs
import ir.almasbu.waqiah.util.Jalali
import ir.almasbu.waqiah.util.PersianNumbers

/** یادآورِ روزانه را نشان می‌دهد و بلافاصله زنگ روز بعد را تنظیم می‌کند. */
class ReminderReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action != ACTION_REMIND) return

    showNotification(context)

    // آلارم دقیق یک‌بار مصرف است؛ زنگ فردا را همین‌جا دوباره می‌گذاریم.
    ReminderScheduler.reschedule(context)
  }

  private fun showNotification(context: Context) {
    if (!hasNotificationPermission(context)) return

    val prefs = AppPrefs(context)
    val plan = prefs.plan
    val todayIndex = plan?.todayIndex(Jalali.todayJdn())

    val text = when {
      plan == null ->
        "برای شروع، یک برنامه‌ی ختم انتخاب کنید."
      todayIndex == null ->
        "امروز بیرون از بازه‌ی برنامه‌ی شماست."
      plan.isDone(todayIndex) ->
        "ختم امروز را انجام داده‌اید. خدا قبول کند."
      else -> {
        val count = PersianNumbers.of(plan.recitationsOn(todayIndex))
        val day = PersianNumbers.of(todayIndex + 1)
        "روز $day: امروز $count مرتبه سوره واقعه."
      }
    }

    val contentIntent = PendingIntent.getActivity(
      context,
      0,
      Intent(context, MainActivity::class.java)
        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP),
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    val notification = NotificationCompat.Builder(context, WaqiahApp.REMINDER_CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_notification)
      .setContentTitle("یادآور ختم سوره واقعه")
      .setContentText(text)
      .setStyle(NotificationCompat.BigTextStyle().bigText(text))
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setContentIntent(contentIntent)
      .setAutoCancel(true)
      .build()

    context.getSystemService(NotificationManager::class.java)
      ?.notify(NOTIFICATION_ID, notification)
  }

  private fun hasNotificationPermission(context: Context): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
      ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
      PackageManager.PERMISSION_GRANTED

  companion object {
    const val ACTION_REMIND = "ir.almasbu.waqiah.REMIND"
    private const val NOTIFICATION_ID = 2001
  }
}
