package ir.almasbu.waqiah.ui.screens

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TextDecrease
import androidx.compose.material.icons.filled.TextIncrease
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.almasbu.waqiah.data.Ayah
import ir.almasbu.waqiah.data.Translator
import ir.almasbu.waqiah.ui.UiState
import ir.almasbu.waqiah.ui.components.ArabicText
import ir.almasbu.waqiah.ui.components.AyahBadge
import ir.almasbu.waqiah.ui.components.SectionCard
import ir.almasbu.waqiah.ui.components.WaqiahTopBar
import ir.almasbu.waqiah.util.PersianNumbers

private const val BASE_ARABIC_SP = 26f
private const val BASE_TRANSLATION_SP = 15f

@Composable
fun QuranScreen(
  state: UiState,
  onBack: () -> Unit,
  onToggleTranslation: (String) -> Unit,
  onArabicScale: (Float) -> Unit,
  onTranslationScale: (Float) -> Unit,
) {
  Scaffold(topBar = { WaqiahTopBar("متن سوره واقعه", onBack) }) { padding ->
    val content = state.content
    if (content == null) {
      Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
      return@Scaffold
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
      contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      item {
        Controls(
          translators = content.translators,
          selected = state.selectedTranslations,
          arabicScale = state.arabicFontScale,
          translationScale = state.translationFontScale,
          onToggleTranslation = onToggleTranslation,
          onArabicScale = onArabicScale,
          onTranslationScale = onTranslationScale,
        )
      }

      item {
        SectionCard {
          ArabicText(content.bismillah, BASE_ARABIC_SP * state.arabicFontScale)
        }
      }

      items(content.ayat, key = { it.number }) { ayah ->
        AyahCard(
          ayah = ayah,
          translators = content.translators,
          selected = state.selectedTranslations,
          arabicSizeSp = BASE_ARABIC_SP * state.arabicFontScale,
          translationSizeSp = BASE_TRANSLATION_SP * state.translationFontScale,
        )
      }

      item {
        Text(
          text = "متن عثمانی از مصحف Tanzil و ترجمه‌ها از منابع رسمی همان مترجمان.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        )
      }
    }
  }
}

@Composable
private fun Controls(
  translators: List<Translator>,
  selected: Set<String>,
  arabicScale: Float,
  translationScale: Float,
  onToggleTranslation: (String) -> Unit,
  onArabicScale: (Float) -> Unit,
  onTranslationScale: (Float) -> Unit,
) {
  SectionCard {
    Text(
      text = "ترجمه‌های نمایش‌داده‌شده",
      style = MaterialTheme.typography.titleSmall,
      color = MaterialTheme.colorScheme.primary,
    )
    Row(
      modifier = Modifier.horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      translators.forEach { translator ->
        FilterChip(
          selected = translator.id in selected,
          onClick = { onToggleTranslation(translator.id) },
          label = { Text(translator.short) },
        )
      }
    }
    if (selected.isEmpty()) {
      Text(
        text = "هیچ ترجمه‌ای انتخاب نشده — فقط متن عربی نشان داده می‌شود.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }

    HorizontalDivider()

    FontRow("اندازه‌ی متن عربی", arabicScale, onArabicScale)
    FontRow("اندازه‌ی متن ترجمه", translationScale, onTranslationScale)
  }
}

@Composable
private fun FontRow(label: String, scale: Float, onScale: (Float) -> Unit) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Text(label, style = MaterialTheme.typography.bodyMedium)
    Row(verticalAlignment = Alignment.CenterVertically) {
      IconButton(onClick = { onScale(scale - 0.1f) }) {
        Icon(Icons.Default.TextDecrease, contentDescription = "کوچک‌تر")
      }
      Text(
        text = "${PersianNumbers.of((scale * 100).toInt())}٪",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      IconButton(onClick = { onScale(scale + 0.1f) }) {
        Icon(Icons.Default.TextIncrease, contentDescription = "بزرگ‌تر")
      }
    }
  }
}

@Composable
private fun AyahCard(
  ayah: Ayah,
  translators: List<Translator>,
  selected: Set<String>,
  arabicSizeSp: Float,
  translationSizeSp: Float,
) {
  SectionCard {
    AyahBadge("آیه ${PersianNumbers.of(ayah.number)}")
    ArabicText(ayah.arabic, arabicSizeSp)

    val shown = translators.filter { it.id in selected }
    if (shown.isNotEmpty()) {
      HorizontalDivider()
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        shown.forEach { translator ->
          Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
              text = translator.short,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.secondary,
            )
            Text(
              text = ayah.translations[translator.id].orEmpty(),
              style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = translationSizeSp.sp,
                lineHeight = (translationSizeSp * 1.9f).sp,
              ),
              color = MaterialTheme.colorScheme.onSurface,
            )
          }
        }
      }
    }
  }
}
