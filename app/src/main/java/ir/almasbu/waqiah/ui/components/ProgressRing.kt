package ir.almasbu.waqiah.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ir.almasbu.waqiah.util.PersianNumbers
import kotlin.math.roundToInt

/**
 * نمودار دایره‌ای پیشرفت ختم.
 *
 * [progress] بین ۰ و ۱ است. کمان از بالای دایره شروع می‌شود و ساعتگرد پیش
 * می‌رود؛ در چیدمان راست‌به‌چپ هم همین جهت درست است، چون این یک نمودار است نه
 * متن.
 */
@Composable
fun ProgressRing(
  progress: Float,
  modifier: Modifier = Modifier,
  size: Dp = 168.dp,
  strokeWidth: Dp = 14.dp,
  caption: String? = null,
) {
  val target = progress.coerceIn(0f, 1f)
  val animated by animateFloatAsState(
    targetValue = target,
    animationSpec = tween(durationMillis = 650),
    label = "khatm-progress",
  )

  val trackColor = MaterialTheme.colorScheme.surfaceVariant
  val startColor = MaterialTheme.colorScheme.primary
  val endColor = MaterialTheme.colorScheme.secondary

  Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
    Canvas(modifier = Modifier.size(size)) {
      val stroke = strokeWidth.toPx()
      val inset = stroke / 2f
      val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
      val topLeft = Offset(inset, inset)

      drawArc(
        color = trackColor,
        startAngle = 0f,
        sweepAngle = 360f,
        useCenter = false,
        topLeft = topLeft,
        size = arcSize,
        style = Stroke(width = stroke, cap = StrokeCap.Round),
      )

      if (animated > 0f) {
        drawArc(
          brush = Brush.sweepGradient(listOf(startColor, endColor, startColor)),
          startAngle = -90f,
          sweepAngle = 360f * animated,
          useCenter = false,
          topLeft = topLeft,
          size = arcSize,
          style = Stroke(width = stroke, cap = StrokeCap.Round),
        )
      }
    }

    Column(
      percent = (target * 100).roundToInt(),
      caption = caption,
      accent = if (target >= 1f) endColor else startColor,
    )
  }
}

@Composable
private fun Column(percent: Int, caption: String?, accent: Color) {
  androidx.compose.foundation.layout.Column(
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
      text = "${PersianNumbers.of(percent)}٪",
      style = MaterialTheme.typography.headlineMedium,
      color = accent,
    )
    if (caption != null) {
      Text(
        text = caption,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
      )
    }
  }
}
