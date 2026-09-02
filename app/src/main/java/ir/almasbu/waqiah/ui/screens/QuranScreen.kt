package ir.almasbu.waqiah.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TextDecrease
import androidx.compose.material.icons.filled.TextIncrease
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.almasbu.waqiah.audio.RecitationPlayer
import ir.almasbu.waqiah.data.Ayah
import ir.almasbu.waqiah.data.Translator
import ir.almasbu.waqiah.ui.AyahNumberStyle
import ir.almasbu.waqiah.ui.UiState
import ir.almasbu.waqiah.ui.components.ArabicText
import ir.almasbu.waqiah.ui.components.AyahBadge
import ir.almasbu.waqiah.ui.components.ClosingDuaDialog
import ir.almasbu.waqiah.ui.components.SectionCard
import ir.almasbu.waqiah.ui.components.WaqiahTopBar
import ir.almasbu.waqiah.util.PersianNumbers

private const val BASE_ARABIC_SP = 26f
private const val BASE_TRANSLATION_SP = 15f

/**
 * ترتیب آیتم‌ها در لیست: ۰ کنترل‌ها، ۱ بسم‌الله، سپس آیه‌ها.
 * برای پیمایش خودکار به آیه‌ی در حال تلاوت لازم است.
 */
private const val ITEMS_BEFORE_AYAT = 2

@Composable
fun QuranScreen(
  state: UiState,
  onBack: () -> Unit,
  onSelectTranslation: (String) -> Unit,
  onArabicScale: (Float) -> Unit,
  onTranslationScale: (Float) -> Unit,
  onPlayFromAyah: (Int) -> Unit,
  onTogglePlayPause: () -> Unit,
  onStopRecitation: () -> Unit,
) {
  val listState = rememberLazyListState()
  var showDua by remember { mutableStateOf(false) }
  val currentAyah = state.audio.ayah

  // متن همیشه دنبال صوت می‌رود: با عوض‌شدن قطعه‌ی در حال تلاوت، لیست خودش
  // به همان‌جا می‌لغزد تا کاربر لازم نباشد دستی اسکرول کند.
  // نگاشت: بسم‌الله (۰) ← آیتم ۱، آیه‌ی n ← آیتم n+1.
  LaunchedEffect(currentAyah) {
    if (currentAyah != null) {
      listState.animateScrollToItem(currentAyah + ITEMS_BEFORE_AYAT - 1)
    }
  }

  // تلاوت فقط تا وقتی است که کاربر در همین صفحه باشد؛ با خروج از صفحه‌ی
  // سوره، صوت قطع می‌شود و در پس‌زمینه ادامه پیدا نمی‌کند.
  DisposableEffect(Unit) {
    onDispose { onStopRecitation() }
  }

  val content = state.content
  if (showDua && content != null) {
    ClosingDuaDialog(dua = content.closingDua, onDismiss = { showDua = false })
  }

  Scaffold(
    topBar = { WaqiahTopBar("متن سوره واقعه", onBack) },
    bottomBar = {
      if (content != null) {
        PlayerBar(
          currentAyah = currentAyah,
          isPlaying = state.audio.isPlaying,
          error = state.audio.error,
          onTogglePlayPause = onTogglePlayPause,
          onStop = onStopRecitation,
        )
      }
    },
  ) { padding ->
    if (content == null) {
      Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
      return@Scaffold
    }

    LazyColumn(
      state = listState,
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
      contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      item {
        Controls(
          translators = content.translators,
          selected = state.selectedTranslation,
          arabicScale = state.arabicFontScale,
          translationScale = state.translationFontScale,
          onSelectTranslation = onSelectTranslation,
          onArabicScale = onArabicScale,
          onTranslationScale = onTranslationScale,
        )
      }

      item {
        // بسم‌الله هم صوت خودش را دارد و مثل بقیه با ضربه پخش می‌شود.
        val isCurrent = currentAyah == RecitationPlayer.BISMILLAH
        SectionCard(
          modifier = highlightIfCurrent(isCurrent)
            .clickable { onPlayFromAyah(RecitationPlayer.BISMILLAH) }
        ) {
          ArabicText(content.bismillah, BASE_ARABIC_SP * state.arabicFontScale)
        }
      }

      items(content.ayat, key = { it.number }) { ayah ->
        AyahCard(
          ayah = ayah,
          translators = content.translators,
          selected = state.selectedTranslation,
          numberStyle = state.ayahNumberStyle,
          arabicSizeSp = BASE_ARABIC_SP * state.arabicFontScale,
          translationSizeSp = BASE_TRANSLATION_SP * state.translationFontScale,
          isCurrent = ayah.number == currentAyah,
          onTap = { onPlayFromAyah(ayah.number) },
        )
      }

      item {
        Button(
          onClick = { showDua = true },
          modifier = Modifier.fillMaxWidth(),
        ) {
          Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp))
          Text(
            text = "  ${content.closingDua.title}",
            style = MaterialTheme.typography.titleSmall,
          )
        }
      }

      item {
        Text(
          text = "با زدن روی هر آیه، تلاوت از همان‌جا شروع می‌شود.\n" +
            "متن عثمانی از مصحف Tanzil، تلاوت از استاد محمد صدیق منشاوی.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        )
      }
    }
  }
}

// ——— نوار پخش ———

@Composable
private fun PlayerBar(
  currentAyah: Int?,
  isPlaying: Boolean,
  error: String?,
  onTogglePlayPause: () -> Unit,
  onStop: () -> Unit,
) {
  Surface(
    tonalElevation = 3.dp,
    color = MaterialTheme.colorScheme.surface,
  ) {
    // بدون این، نوار پخش زیر دکمه‌های ناوبری گوشی می‌افتد و ضربه‌ها به
    // نوار سیستم می‌رسند نه به دکمه‌ی پخش — یعنی عملاً کار نمی‌کند.
    Column(
      Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
    ) {
      HorizontalDivider()
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        FilledIconButton(onClick = onTogglePlayPause) {
          Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isPlaying) "مکث" else "پخش",
          )
        }

        if (currentAyah != null) {
          IconButton(onClick = onStop) {
            Icon(Icons.Default.Stop, contentDescription = "توقف")
          }
        }

        Column(Modifier.weight(1f)) {
          Text(
            text = "تلاوت استاد منشاوی",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary,
          )
          Text(
            text = error
              ?: when {
                currentAyah == null -> "برای شروع، پخش را بزنید یا روی یک آیه ضربه بزنید"
                isPlaying -> "در حال تلاوت ${trackLabel(currentAyah)}"
                else -> "متوقف روی ${trackLabel(currentAyah)}"
              },
            style = MaterialTheme.typography.bodySmall,
            color = if (error != null) {
              MaterialTheme.colorScheme.error
            } else {
              MaterialTheme.colorScheme.onSurfaceVariant
            },
          )
        }
      }
    }
  }
}

/** «بسم‌الله» یا «آیه‌ی ۴۵» — قطعه‌ی صفر بسم‌الله است. */
private fun trackLabel(track: Int): String =
  if (track == RecitationPlayer.BISMILLAH) {
    "بسم‌الله"
  } else {
    "آیه‌ی ${PersianNumbers.of(track)}"
  }

// ——— کنترل‌های نمایش ———

@Composable
private fun Controls(
  translators: List<Translator>,
  selected: String?,
  arabicScale: Float,
  translationScale: Float,
  onSelectTranslation: (String) -> Unit,
  onArabicScale: (Float) -> Unit,
  onTranslationScale: (Float) -> Unit,
) {
  SectionCard {
    Text(
      text = "ترجمه",
      style = MaterialTheme.typography.titleSmall,
      color = MaterialTheme.colorScheme.primary,
    )
    Row(
      modifier = Modifier.horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      translators.forEach { translator ->
        FilterChip(
          selected = translator.id == selected,
          onClick = { onSelectTranslation(translator.id) },
          label = { Text(translator.short) },
        )
      }
    }
    Text(
      text = if (selected == null) {
        "بدون ترجمه — فقط متن عربی نشان داده می‌شود. یکی را انتخاب کنید."
      } else {
        "هر بار فقط یک ترجمه نمایش داده می‌شود؛ زدن دوباره روی همان، ترجمه را برمی‌دارد."
      },
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

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

// ——— کارت آیه ———

@Composable
private fun AyahCard(
  ayah: Ayah,
  translators: List<Translator>,
  selected: String?,
  numberStyle: AyahNumberStyle,
  arabicSizeSp: Float,
  translationSizeSp: Float,
  isCurrent: Boolean,
  onTap: () -> Unit,
) {
  SectionCard(modifier = highlightIfCurrent(isCurrent).clickable(onClick = onTap)) {
    when (numberStyle) {
      AyahNumberStyle.BADGE -> {
        AyahBadge("آیه ${PersianNumbers.of(ayah.number)}")
        ArabicText(ayah.arabic, arabicSizeSp)
      }

      AyahNumberStyle.INLINE -> {
        Row(verticalAlignment = Alignment.CenterVertically) {
          NumberCircle(ayah.number)
          ArabicText(
            text = ayah.arabic,
            fontSizeSp = arabicSizeSp,
            modifier = Modifier.weight(1f),
          )
        }
      }
    }

    val translator = translators.firstOrNull { it.id == selected }
    if (translator != null) {
      HorizontalDivider()
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

/** قابِ طلاییِ دورِ قطعه‌ی در حال تلاوت. */
@Composable
private fun highlightIfCurrent(isCurrent: Boolean): Modifier =
  if (isCurrent) {
    Modifier.border(
      width = 2.dp,
      color = MaterialTheme.colorScheme.secondary,
      shape = RoundedCornerShape(18.dp),
    )
  } else {
    Modifier
  }

/** شماره‌ی آیه داخل یک دایره، برای حالت «کنار آیه». */
@Composable
private fun NumberCircle(number: Int) {
  Box(
    modifier = Modifier
      .size(34.dp)
      .background(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(50),
      ),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = PersianNumbers.of(number),
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onPrimaryContainer,
    )
  }
}
