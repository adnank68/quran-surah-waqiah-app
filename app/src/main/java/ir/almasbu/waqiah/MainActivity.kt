package ir.almasbu.waqiah

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.almasbu.waqiah.ui.AppViewModel
import ir.almasbu.waqiah.ui.WaqiahNavHost
import ir.almasbu.waqiah.ui.theme.WaqiahTheme

class MainActivity : ComponentActivity() {

  private var viewModel: AppViewModel? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      // ViewModel بیرون از WaqiahTheme ساخته می‌شود، چون خودِ تم از وضعیت آن
      // (حالت روشن/تیره و رنگ پس‌زمینه) خوانده می‌شود.
      val vm: AppViewModel = viewModel(factory = AppViewModel.Factory)
      viewModel = vm
      val state by vm.state.collectAsStateWithLifecycle()

      WaqiahTheme(themeMode = state.themeMode, palette = state.background) {
        // کل اپ فارسی است، پس چیدمان همیشه راست‌به‌چپ می‌ماند و به زبانِ
        // انتخابیِ دستگاه گره نمی‌خورد.
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
          Surface(modifier = Modifier.fillMaxSize()) {
            WaqiahNavHost(vm)
          }
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    // اگر اپ از دیشب باز مانده باشد، «امروز» باید به‌روز شود.
    viewModel?.refreshToday()
  }
}
