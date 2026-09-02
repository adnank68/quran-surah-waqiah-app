package ir.almasbu.waqiah.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ir.almasbu.waqiah.data.KhatmMethod
import ir.almasbu.waqiah.data.KhatmPlan
import ir.almasbu.waqiah.ui.UiState
import ir.almasbu.waqiah.ui.components.ProgressRing
import ir.almasbu.waqiah.ui.components.SectionCard
import ir.almasbu.waqiah.ui.components.WaqiahTopBar
import ir.almasbu.waqiah.util.Jalali
import ir.almasbu.waqiah.util.PersianNumbers

@Composable
fun KhatmScreen(
  state: UiState,
  onBack: () -> Unit,
  onToggleDay: (Int) -> Unit,
  onStartMethod: (KhatmMethod) -> Unit,
  onStartCustom: (Int, Int) -> Unit,
  onClearPlan: () -> Unit,
  onSeeMethods: () -> Unit,
) {
  Scaffold(topBar = { WaqiahTopBar("برنامه‌ی ختم", onBack) }) { padding ->
    val content = state.content
    if (content == null) {
      Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
      return@Scaffold
    }

    val plan = state.plan
    if (plan == null) {
      PlanPicker(
        methods = content.khatmMethods,
        onStartMethod = onStartMethod,
        onStartCustom = onStartCustom,
        onSeeMethods = onSeeMethods,
        modifier = Modifier
          .fillMaxSize()
          .padding(padding),
      )
    } else {
      PlanView(
        plan = plan,
        todayJdn = state.todayJdn,
        onToggleDay = onToggleDay,
        onClearPlan = onClearPlan,
        modifier = Modifier
          .fillMaxSize()
          .padding(padding),
      )
    }
  }
}

// ——— حالت «هنوز برنامه‌ای نیست» ———

@Composable
private fun PlanPicker(
  methods: List<KhatmMethod>,
  onStartMethod: (KhatmMethod) -> Unit,
  onStartCustom: (Int, Int) -> Unit,
  onSeeMethods: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var customDays by remember { mutableIntStateOf(14) }
  var customPerDay by remember { mutableIntStateOf(1) }

  LazyColumn(
    modifier = modifier,
    contentPadding = PaddingValues(14.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    item {
      SectionCard {
        Text(
          text = "یک برنامه انتخاب کنید",
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.primary,
        )
        Text(
          text = "می‌توانید یکی از ختم‌های نقل‌شده از منابع را شروع کنید، یا پایین‌تر برنامه‌ی دلخواه خودتان را بسازید. برنامه از امروز شروع می‌شود.",
          style = MaterialTheme.typography.bodyMedium,
        )
      }
    }

    items(methods) { method ->
      SectionCard {
        Text(
          text = method.title,
          style = MaterialTheme.typography.titleSmall,
          color = MaterialTheme.colorScheme.primary,
        )
        Text(method.purpose, style = MaterialTheme.typography.bodySmall)
        Text(
          text = "${PersianNumbers.of(method.days)} روز • مجموعاً ${PersianNumbers.of(method.total)} مرتبه تلاوت",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Button(onClick = { onStartMethod(method) }) { Text("شروع این ختم") }
          OutlinedButton(onClick = onSeeMethods) { Text("جزئیات و دعاها") }
        }
      }
    }

    item {
      SectionCard {
        Text(
          text = "برنامه‌ی دلخواه",
          style = MaterialTheme.typography.titleSmall,
          color = MaterialTheme.colorScheme.primary,
        )
        Text(
          text = "اگر می‌خواهید خودتان دوره را تعیین کنید:",
          style = MaterialTheme.typography.bodySmall,
        )
        Stepper(
          label = "تعداد روز",
          value = customDays,
          range = 1..365,
          onChange = { customDays = it },
        )
        Stepper(
          label = "تعداد تلاوت در هر روز",
          value = customPerDay,
          range = 1..100,
          onChange = { customPerDay = it },
        )
        Text(
          text = "مجموع: ${PersianNumbers.of(customDays * customPerDay)} مرتبه تلاوت",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Button(onClick = { onStartCustom(customDays, customPerDay) }) {
          Text("شروع برنامه‌ی دلخواه")
        }
      }
    }
  }
}

@Composable
private fun Stepper(label: String, value: Int, range: IntRange, onChange: (Int) -> Unit) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Text(label, style = MaterialTheme.typography.bodyMedium)
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      OutlinedButton(
        onClick = { onChange((value - 1).coerceIn(range)) },
        enabled = value > range.first,
      ) { Text("−") }
      Text(
        text = PersianNumbers.of(value),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(horizontal = 4.dp),
      )
      OutlinedButton(
        onClick = { onChange((value + 1).coerceIn(range)) },
        enabled = value < range.last,
      ) { Text("+") }
    }
  }
}

// ——— حالت «برنامه در جریان است» ———

@Composable
private fun PlanView(
  plan: KhatmPlan,
  todayJdn: Int,
  onToggleDay: (Int) -> Unit,
  onClearPlan: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var confirmClear by remember { mutableStateOf(false) }

  if (confirmClear) {
    AlertDialog(
      onDismissRequest = { confirmClear = false },
      title = { Text("پایان دادن به برنامه") },
      text = { Text("برنامه‌ی فعلی و تیک‌های ثبت‌شده پاک می‌شود. مطمئن هستید؟") },
      confirmButton = {
        TextButton(onClick = {
          confirmClear = false
          onClearPlan()
        }) { Text("بله، پاک کن") }
      },
      dismissButton = {
        TextButton(onClick = { confirmClear = false }) { Text("انصراف") }
      },
    )
  }

  LazyColumn(
    modifier = modifier,
    contentPadding = PaddingValues(14.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    item {
      SectionCard {
        Text(
          text = plan.title,
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.primary,
        )
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
          ProgressRing(
            progress = plan.progress,
            caption = "${PersianNumbers.of(plan.doneRecitations)} از ${PersianNumbers.of(plan.totalRecitations)} تلاوت",
          )
        }
        Text(
          text = "${PersianNumbers.of(plan.doneDays.size)} روز از ${PersianNumbers.of(plan.days)} روز تیک خورده",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth(),
        )
        if (plan.isComplete) {
          Text(
            text = "ختم کامل شد. خداوند قبول کند.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
          )
        }
        HorizontalDivider()
        OutlinedButton(
          onClick = { confirmClear = true },
          modifier = Modifier.fillMaxWidth(),
        ) { Text("پایان دادن و شروع برنامه‌ی جدید") }
      }
    }

    item {
      Text(
        text = "روزهای برنامه",
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp),
      )
    }

    itemsIndexed(plan.perDay) { index, count ->
      DayRow(
        index = index,
        count = count,
        jdn = plan.jdnOfDay(index),
        isDone = plan.isDone(index),
        isToday = plan.jdnOfDay(index) == todayJdn,
        isThursday = plan.isThursday(index),
        onToggle = { onToggleDay(index) },
      )
    }
  }
}

@Composable
private fun DayRow(
  index: Int,
  count: Int,
  jdn: Int,
  isDone: Boolean,
  isToday: Boolean,
  isThursday: Boolean,
  onToggle: () -> Unit,
) {
  val border = if (isToday) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .border(
        width = if (isToday) 1.5.dp else 1.dp,
        color = border,
        shape = RoundedCornerShape(14.dp),
      )
      .background(
        color = if (isDone) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp),
      )
      .clickable(onClick = onToggle)
      .padding(horizontal = 14.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Icon(
      imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
      contentDescription = if (isDone) "انجام شده" else "انجام نشده",
      tint = if (isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.size(26.dp),
    )
    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
      Text(
        text = "روز ${PersianNumbers.of(index + 1)} — ${PersianNumbers.of(count)} مرتبه",
        style = MaterialTheme.typography.bodyLarge,
      )
      Text(
        text = buildString {
          append(Jalali.formatFull(jdn))
          if (isToday) append(" • امروز")
          if (isThursday) append(" • دعای پنج‌شنبه")
        },
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}
