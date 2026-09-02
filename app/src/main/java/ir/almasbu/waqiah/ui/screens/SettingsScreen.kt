package ir.almasbu.waqiah.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import ir.almasbu.waqiah.notify.ReminderScheduler
import ir.almasbu.waqiah.ui.UiState
import ir.almasbu.waqiah.ui.components.SectionCard
import ir.almasbu.waqiah.ui.components.WaqiahTopBar
import ir.almasbu.waqiah.util.PersianNumbers

@Composable
fun SettingsScreen(
  state: UiState,
  onBack: () -> Unit,
  onReminderEnabled: (Boolean) -> Unit,
  onReminderTime: (Int, Int) -> Unit,
) {
  val context = LocalContext.current
  var showTimePicker by remember { mutableStateOf(false) }

  // روی اندروید ۱۳ به بالا، بدون این اجازه هیچ نوتیفیکیشنی نشان داده نمی‌شود.
  val notificationPermission = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { granted -> onReminderEnabled(granted) }

  if (showTimePicker) {
    TimePickerDialog(
      initialHour = state.reminderHour,
      initialMinute = state.reminderMinute,
      onDismiss = { showTimePicker = false },
      onConfirm = { hour, minute ->
        showTimePicker = false
        onReminderTime(hour, minute)
      },
    )
  }

  Scaffold(topBar = { WaqiahTopBar("یادآور و تنظیمات", onBack) }) { padding ->
    val content = state.content
    if (content == null) {
      Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
      return@Scaffold
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
      contentPadding = PaddingValues(14.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      item {
        SectionCard {
          Text(
            text = "یادآور روزانه",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Text("یادآوری برای انجام ختم", style = MaterialTheme.typography.bodyMedium)
            Switch(
              checked = state.reminderEnabled,
              onCheckedChange = { enabled ->
                if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                  notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                  onReminderEnabled(enabled)
                }
              },
            )
          }

          HorizontalDivider()

          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Column {
              Text("ساعت یادآوری", style = MaterialTheme.typography.bodyMedium)
              Text(
                text = PersianNumbers.time(state.reminderHour, state.reminderMinute),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
              )
            }
            OutlinedButton(onClick = { showTimePicker = true }) { Text("تغییر ساعت") }
          }

          if (state.reminderEnabled && !ReminderScheduler.canScheduleExact(context)) {
            HorizontalDivider()
            Text(
              text = "برای اینکه یادآور دقیقاً سرِ همین ساعت بیاید، اجازه‌ی «زنگ هشدار دقیق» لازم است. بدون آن هم یادآور می‌آید، ولی ممکن است کمی دیرتر.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(onClick = {
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                runCatching {
                  context.startActivity(
                    Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                      .setData(Uri.parse("package:${context.packageName}"))
                  )
                }
              }
            }) { Text("دادن اجازه") }
          }
        }
      }

      item {
        SectionCard {
          Text(
            text = "منابع محتوا",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
          )
          Text(
            text = "همه‌ی محتوای این اپ آفلاین است و از منابع زیر گرفته شده:",
            style = MaterialTheme.typography.bodySmall,
          )
        }
      }

      items(content.sources) { source ->
        SectionCard {
          Text(
            text = source.label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary,
          )
          Text(source.value, style = MaterialTheme.typography.bodySmall)
        }
      }

      item {
        SectionCard {
          Text(
            text = "درباره‌ی اپ",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
          )
          Text(
            text = "این اپ کاملاً آفلاین کار می‌کند و هیچ دسترسی اینترنتی ندارد؛ هیچ داده‌ای از دستگاه شما جایی فرستاده نمی‌شود. برنامه‌ی ختم و تنظیمات فقط روی همین دستگاه ذخیره می‌شود.",
            style = MaterialTheme.typography.bodySmall,
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
  initialHour: Int,
  initialMinute: Int,
  onDismiss: () -> Unit,
  onConfirm: (Int, Int) -> Unit,
) {
  val pickerState = rememberTimePickerState(
    initialHour = initialHour,
    initialMinute = initialMinute,
    is24Hour = true,
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("ساعت یادآوری روزانه") },
    text = {
      Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        TimePicker(state = pickerState)
      }
    },
    confirmButton = {
      TextButton(onClick = { onConfirm(pickerState.hour, pickerState.minute) }) {
        Text("ثبت")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("انصراف") }
    },
  )
}
