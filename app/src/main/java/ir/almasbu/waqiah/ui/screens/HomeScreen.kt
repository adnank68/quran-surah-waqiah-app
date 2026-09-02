package ir.almasbu.waqiah.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ir.almasbu.waqiah.ui.Routes
import ir.almasbu.waqiah.ui.UiState
import ir.almasbu.waqiah.ui.components.ProgressRing
import ir.almasbu.waqiah.ui.components.SectionCard
import ir.almasbu.waqiah.ui.theme.GreenDark
import ir.almasbu.waqiah.ui.theme.GreenMid
import ir.almasbu.waqiah.util.Jalali
import ir.almasbu.waqiah.util.PersianNumbers

@Composable
fun HomeScreen(
  state: UiState,
  onToggleToday: (Int) -> Unit,
  onNavigate: (String) -> Unit,
) {
  Scaffold { padding ->
    if (!state.isLoaded) {
      Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
      }
      return@Scaffold
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
      contentPadding = PaddingValues(bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      item { Header(state) }
      item {
        Box(Modifier.padding(horizontal = 16.dp)) {
          ProgressCard(state)
        }
      }
      item {
        Box(Modifier.padding(horizontal = 16.dp)) {
          TodayCard(state, onToggleToday, onNavigate)
        }
      }
      item {
        NavGrid(onNavigate = onNavigate, modifier = Modifier.padding(horizontal = 16.dp))
      }
    }
  }
}

@Composable
private fun Header(state: UiState) {
  val content = state.content ?: return
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .background(Brush.verticalGradient(listOf(GreenDark, GreenMid)))
      .padding(horizontal = 20.dp, vertical = 22.dp),
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
      Text(
        text = "ختم سوره ${content.surah.nameFa}",
        style = MaterialTheme.typography.headlineSmall,
        color = androidx.compose.ui.graphics.Color.White,
      )
      Text(
        text = "سوره‌ی ${PersianNumbers.of(content.surah.number)} قرآن کریم • " +
          "${PersianNumbers.of(content.surah.ayahCount)} آیه • جزء ${PersianNumbers.of(content.surah.juz)}",
        style = MaterialTheme.typography.bodySmall,
        color = androidx.compose.ui.graphics.Color(0xFFBFD9CD),
      )
      Text(
        text = Jalali.formatFull(state.todayJdn),
        style = MaterialTheme.typography.bodySmall,
        color = androidx.compose.ui.graphics.Color(0xFFBFD9CD),
      )
    }
  }
}

@Composable
private fun ProgressCard(state: UiState) {
  val plan = state.plan
  SectionCard {
    Text(
      text = "پیشرفت ختم",
      style = MaterialTheme.typography.titleMedium,
      color = MaterialTheme.colorScheme.primary,
    )
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
      ProgressRing(
        progress = plan?.progress ?: 0f,
        caption = if (plan == null) {
          "برنامه‌ای فعال نیست"
        } else {
          "${PersianNumbers.of(plan.doneRecitations)} از ${PersianNumbers.of(plan.totalRecitations)} تلاوت"
        },
      )
    }
    if (plan != null) {
      Text(
        text = plan.title,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
      )
      Text(
        text = "${PersianNumbers.of(plan.doneDays.size)} روز از ${PersianNumbers.of(plan.days)} روز انجام شده" +
          " • شروع از ${Jalali.formatShort(plan.startJdn)}",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
      )
    }
  }
}

@Composable
private fun TodayCard(
  state: UiState,
  onToggleToday: (Int) -> Unit,
  onNavigate: (String) -> Unit,
) {
  val plan = state.plan
  val todayIndex = state.todayIndex

  SectionCard {
    Text(
      text = "امروز",
      style = MaterialTheme.typography.titleMedium,
      color = MaterialTheme.colorScheme.primary,
    )

    when {
      plan == null -> {
        Text(
          text = "هنوز برنامه‌ی ختمی شروع نکرده‌اید. می‌توانید یکی از روش‌های ختمِ نقل‌شده را انتخاب کنید یا برنامه‌ی دلخواه خودتان را بسازید.",
          style = MaterialTheme.typography.bodyMedium,
        )
        Button(onClick = { onNavigate(Routes.KHATM) }) { Text("شروع برنامه‌ی ختم") }
      }

      todayIndex == null -> {
        val finished = plan.startJdn + plan.days - 1
        Text(
          text = if (state.todayJdn > finished) {
            "بازه‌ی این برنامه تمام شده است (${Jalali.formatShort(plan.startJdn)} تا ${Jalali.formatShort(finished)})."
          } else {
            "این برنامه از ${Jalali.formatShort(plan.startJdn)} شروع می‌شود."
          },
          style = MaterialTheme.typography.bodyMedium,
        )
        Button(onClick = { onNavigate(Routes.KHATM) }) { Text("مشاهده‌ی برنامه") }
      }

      else -> {
        val done = plan.isDone(todayIndex)
        val count = plan.recitationsOn(todayIndex)
        Text(
          text = "روز ${PersianNumbers.of(todayIndex + 1)} از ${PersianNumbers.of(plan.days)}: " +
            "${PersianNumbers.of(count)} مرتبه سوره واقعه",
          style = MaterialTheme.typography.bodyLarge,
        )
        if (plan.isThursday(todayIndex)) {
          Text(
            text = "امروز پنج‌شنبه است — در ختم‌هایی که دعای مخصوص پنج‌شنبه دارند، آن دعا هم خوانده می‌شود.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleToday(todayIndex) }
            .padding(vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Icon(
            imageVector = if (done) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (done) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(28.dp),
          )
          Text(
            text = if (done) "انجام شد ✓" else "تیکِ امروز را بزنید",
            style = MaterialTheme.typography.bodyLarge,
            color = if (done) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
          )
        }
      }
    }
  }
}

private data class NavItem(
  val title: String,
  val subtitle: String,
  val icon: ImageVector,
  val route: String,
)

@Composable
private fun NavGrid(onNavigate: (String) -> Unit, modifier: Modifier = Modifier) {
  val items = listOf(
    NavItem("متن سوره", "خط عثمان‌طه با ترجمه", Icons.Default.MenuBook, Routes.QURAN),
    NavItem("برنامه‌ی ختم", "تیک روزانه و پیشرفت", Icons.Default.CalendarMonth, Routes.KHATM),
    NavItem("روش‌های ختم", "از منابع معتبر", Icons.Default.TipsAndUpdates, Routes.METHODS),
    NavItem("شأن نزول و فضایل", "روایات و محتوای سوره", Icons.Default.Star, Routes.VIRTUES),
    NavItem("تفسیر سوره", "تفسیر نور — قرائتی", Icons.Default.AutoStories, Routes.TAFSIR),
    NavItem("یادآور و تنظیمات", "ساعت یادآوری روزانه", Icons.Default.Notifications, Routes.SETTINGS),
  )

  Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
    items.chunked(2).forEach { row ->
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        row.forEach { item ->
          NavCard(item, onNavigate, Modifier.weight(1f))
        }
        if (row.size == 1) Box(Modifier.weight(1f))
      }
    }
  }
}

@Composable
private fun NavCard(item: NavItem, onNavigate: (String) -> Unit, modifier: Modifier = Modifier) {
  Card(
    modifier = modifier
      .height(122.dp)
      .clickable { onNavigate(item.route) },
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      Icon(
        imageVector = item.icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.size(28.dp),
      )
      Text(
        text = item.title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
        text = item.subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}
