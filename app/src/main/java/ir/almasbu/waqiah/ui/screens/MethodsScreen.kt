package ir.almasbu.waqiah.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ir.almasbu.waqiah.data.Dua
import ir.almasbu.waqiah.data.KhatmMethod
import ir.almasbu.waqiah.ui.UiState
import ir.almasbu.waqiah.ui.components.ArabicText
import ir.almasbu.waqiah.ui.components.SectionCard
import ir.almasbu.waqiah.ui.components.WaqiahTopBar
import ir.almasbu.waqiah.util.PersianNumbers

@Composable
fun MethodsScreen(
  state: UiState,
  onBack: () -> Unit,
  onStartMethod: (KhatmMethod) -> Unit,
) {
  Scaffold(topBar = { WaqiahTopBar("روش‌های ختم سوره واقعه", onBack) }) { padding ->
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
            text = "درباره‌ی این روش‌ها",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
          )
          Text(
            text = "متن هر روش، همان‌طور که در منبعِ ذکرشده آمده نقل شده است و پایین هر کارت، منبعش نوشته شده. اگر برنامه‌ای فعال داشته باشید، شروع یک ختم جدید جایگزین آن می‌شود.",
            style = MaterialTheme.typography.bodySmall,
          )
        }
      }

      items(content.khatmMethods, key = { it.id }) { method ->
        MethodCard(method = method, onStart = { onStartMethod(method) })
      }
    }
  }
}

@Composable
private fun MethodCard(method: KhatmMethod, onStart: () -> Unit) {
  var expanded by rememberSaveable(method.id) { mutableStateOf(false) }

  SectionCard {
    Text(
      text = method.title,
      style = MaterialTheme.typography.titleMedium,
      color = MaterialTheme.colorScheme.primary,
    )
    Text(
      text = method.purpose,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.secondary,
    )
    Text(
      text = "${PersianNumbers.of(method.days)} روز • مجموعاً ${PersianNumbers.of(method.total)} مرتبه تلاوت",
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    HorizontalDivider()

    Text(method.intro, style = MaterialTheme.typography.bodyMedium)

    if (method.note.isNotBlank()) {
      Text(
        text = method.note,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }

    if (method.conditions.isNotEmpty()) {
      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
          text = "شرایط",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.secondary,
        )
        method.conditions.forEach {
          Text("• $it", style = MaterialTheme.typography.bodySmall)
        }
      }
    }

    if (method.duas.isNotEmpty()) {
      TextButton(onClick = { expanded = !expanded }) {
        Text(if (expanded) "بستن دعاها" else "نمایش دعاهای این ختم (${PersianNumbers.of(method.duas.size)})")
      }
      AnimatedVisibility(visible = expanded) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
          method.duas.forEach { DuaBlock(it) }
        }
      }
    }

    HorizontalDivider()

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Button(onClick = onStart) { Text("شروع این ختم") }
    }

    Text(
      text = "منبع: ${method.source}",
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@Composable
private fun DuaBlock(dua: Dua) {
  Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
    Text(
      text = dua.title + if (dua.occasion == "thursday") " (فقط پنج‌شنبه‌ها)" else "",
      style = MaterialTheme.typography.labelLarge,
      color = MaterialTheme.colorScheme.secondary,
    )
    // دعاها متن پیوسته‌اند نه آیه‌ی مجزا، پس چپ‌وراست‌چین می‌شوند نه وسط‌چین.
    ArabicText(
      text = dua.arabic,
      fontSizeSp = 20f,
      modifier = Modifier.fillMaxWidth(),
      align = TextAlign.Start,
    )
    if (dua.persian.isNotBlank()) {
      Text(
        text = dua.persian,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}
