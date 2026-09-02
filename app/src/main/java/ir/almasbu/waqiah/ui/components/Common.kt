package ir.almasbu.waqiah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ir.almasbu.waqiah.ui.theme.ayahTextStyle
import ir.almasbu.waqiah.ui.theme.duaTextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaqiahTopBar(title: String, onBack: (() -> Unit)? = null) {
  TopAppBar(
    title = { Text(title, style = MaterialTheme.typography.titleMedium) },
    navigationIcon = {
      if (onBack != null) {
        IconButton(onClick = onBack) {
          // در چیدمان راست‌به‌چپ، این آیکون خودش قرینه می‌شود.
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
        }
      }
    },
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.primary,
      titleContentColor = MaterialTheme.colorScheme.onPrimary,
      navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
    ),
  )
}

@Composable
fun SectionCard(
  modifier: Modifier = Modifier,
  content: @Composable ColumnScope.() -> Unit,
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
      content = content,
    )
  }
}

@Composable
fun CardTitle(text: String) {
  Text(
    text = text,
    style = MaterialTheme.typography.titleMedium,
    color = MaterialTheme.colorScheme.primary,
  )
}

@Composable
fun BodyText(text: String, modifier: Modifier = Modifier) {
  Text(
    text = text,
    style = MaterialTheme.typography.bodyMedium,
    color = MaterialTheme.colorScheme.onSurface,
    modifier = modifier,
  )
}

@Composable
fun CaptionText(text: String, modifier: Modifier = Modifier) {
  Text(
    text = text,
    style = MaterialTheme.typography.bodySmall,
    color = MaterialTheme.colorScheme.onSurfaceVariant,
    modifier = modifier,
  )
}

/** متن عربی با خط عثمان‌طه، وسط‌چین. */
@Composable
fun ArabicText(
  text: String,
  fontSizeSp: Float,
  modifier: Modifier = Modifier,
  align: TextAlign = TextAlign.Center,
) {
  Text(
    text = text,
    style = ayahTextStyle(fontSizeSp),
    color = MaterialTheme.colorScheme.onSurface,
    textAlign = align,
    modifier = modifier.fillMaxWidth(),
  )
}

/**
 * متن عربیِ دعاها. برخلاف [ArabicText] از خط قرآنی استفاده نمی‌کند —
 * دلیلش در توضیح `duaTextStyle` آمده.
 */
@Composable
fun DuaText(
  text: String,
  modifier: Modifier = Modifier,
  fontSizeSp: Float = 19f,
) {
  Text(
    text = text,
    style = duaTextStyle(fontSizeSp),
    color = MaterialTheme.colorScheme.onSurface,
    textAlign = TextAlign.Start,
    modifier = modifier.fillMaxWidth(),
  )
}

/** شماره‌ی آیه داخل یک مدال گرد. */
@Composable
fun AyahBadge(label: String) {
  Row(
    modifier = Modifier
      .background(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(50),
      )
      .padding(horizontal = 10.dp, vertical = 3.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onPrimaryContainer,
    )
  }
}
