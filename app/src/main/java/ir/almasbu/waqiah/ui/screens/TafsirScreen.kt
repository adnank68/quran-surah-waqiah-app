package ir.almasbu.waqiah.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import ir.almasbu.waqiah.ui.components.AyahBadge
import ir.almasbu.waqiah.ui.components.SectionCard
import ir.almasbu.waqiah.ui.components.WaqiahTopBar
import ir.almasbu.waqiah.util.PersianNumbers

@Composable
fun TafsirScreen(state: UiState, onBack: () -> Unit) {
  Scaffold(topBar = { WaqiahTopBar("تفسیر سوره واقعه", onBack) }) { padding ->
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
            text = "${content.tafsirTitle} — ${content.tafsirAuthor}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
          )
          Text(
            text = "تفسیر کاملِ هر ۹۶ آیه، در ${PersianNumbers.of(content.tafsir.size)} بخش. هر بخش، آیه‌هایی را که در تفسیر با هم بررسی شده‌اند کنار هم می‌آورد.",
            style = MaterialTheme.typography.bodyMedium,
          )
        }
      }

      items(content.tafsir, key = { "${it.from}-${it.to}" }) { section ->
        SectionCard {
          AyahBadge(
            if (section.from == section.to) {
              "آیه ${PersianNumbers.of(section.from)}"
            } else {
              "آیات ${PersianNumbers.of(section.from)} تا ${PersianNumbers.of(section.to)}"
            }
          )
          Text(section.text, style = MaterialTheme.typography.bodyMedium)
        }
      }

      item {
        SectionCard {
          HorizontalDivider()
          Text(
            text = "منبع: ${content.tafsirSource}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    }
  }
}
