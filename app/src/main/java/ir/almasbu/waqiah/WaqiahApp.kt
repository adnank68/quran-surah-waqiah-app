package ir.almasbu.waqiah

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class WaqiahApp : Application() {

  override fun onCreate() {
    super.onCreate()
    createReminderChannel()
  }

  private fun createReminderChannel() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
    val channel = NotificationChannel(
      REMINDER_CHANNEL_ID,
      getString(R.string.reminder_channel_name),
      NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
      description = getString(R.string.reminder_channel_description)
    }
    getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
  }

  companion object {
    const val REMINDER_CHANNEL_ID = "khatm_reminder"
  }
}
