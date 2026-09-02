package ir.almasbu.waqiah.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ir.almasbu.waqiah.data.ClosingDua

/**
 * دعای پایان ختم، به‌صورت مودال روی صفحه‌ی متن سوره.
 * هر بند، متن عربی را بالای ترجمه‌ی فارسی همان بند نشان می‌دهد.
 */
@Composable
fun ClosingDuaDialog(dua: ClosingDua, onDismiss: () -> Unit) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Column {
        Text(dua.title, style = MaterialTheme.typography.titleMedium)
        Text(
          text = dua.subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 460.dp)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(18.dp),
      ) {
        dua.parts.forEachIndexed { index, part ->
          if (index > 0) HorizontalDivider()
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // دعا متن پیوسته است، پس راست‌چین می‌شود نه وسط‌چین.
            ArabicText(
              text = part.arabic,
              fontSizeSp = 21f,
              align = TextAlign.Start,
            )
            Text(
              text = part.persian,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }
        Text(
          text = "منبع: ${dua.source}",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss) { Text("بستن") }
    },
  )
}
