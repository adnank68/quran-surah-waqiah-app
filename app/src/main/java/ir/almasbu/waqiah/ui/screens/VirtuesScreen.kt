package ir.almasbu.waqiah.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.almasbu.waqiah.ui.UiState
import ir.almasbu.waqiah.ui.components.SectionCard
import ir.almasbu.waqiah.ui.components.WaqiahTopBar
import ir.almasbu.waqiah.util.PersianNumbers

@Composable
fun VirtuesScreen(state: UiState, onBack: () -> Unit) {
  Scaffold(topBar = { WaqiahTopBar("شأن نزول و فضایل", onBack) }) { padding ->
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
            text = "سوره‌ی ${content.surah.nameFa}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
          )
          Text(
            text = "محل نزول: ${content.surah.revelationPlace}\n" +
              "ترتیب در مصحف: ${PersianNumbers.of(content.surah.number)}\n" +
              "ترتیب نزول: ${PersianNumbers.of(content.surah.revelationOrder)}\n" +
              "تعداد آیات: ${PersianNumbers.of(content.surah.ayahCount)}\n" +
              "جزء: ${PersianNumbers.of(content.surah.juz)}",
            style = MaterialTheme.typography.bodyMedium,
          )
        }
      }

      items(content.about) { section ->
        SectionCard {
          Text(
            text = section.title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
          )
          Text(section.body, style = MaterialTheme.typography.bodyMedium)
        }
      }

      item {
        SectionCard {
          Text(
            text = "فضیلت سوره",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
          )
          Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            content.virtues.forEachIndexed { index, virtue ->
              if (index > 0) HorizontalDivider()
              Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("«${virtue.text}»", style = MaterialTheme.typography.bodyMedium)
                Text(
                  text = virtue.by,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.secondary,
                )
              }
            }
          }
          HorizontalDivider()
          Text(
            text = "منبع: ${content.virtuesSource}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    }
  }
}
