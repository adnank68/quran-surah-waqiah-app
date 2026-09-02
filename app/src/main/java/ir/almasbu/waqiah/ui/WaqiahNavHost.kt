package ir.almasbu.waqiah.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ir.almasbu.waqiah.ui.screens.HomeScreen
import ir.almasbu.waqiah.ui.screens.KhatmScreen
import ir.almasbu.waqiah.ui.screens.MethodsScreen
import ir.almasbu.waqiah.ui.screens.QuranScreen
import ir.almasbu.waqiah.ui.screens.SettingsScreen
import ir.almasbu.waqiah.ui.screens.TafsirScreen
import ir.almasbu.waqiah.ui.screens.VirtuesScreen

object Routes {
  const val HOME = "home"
  const val QURAN = "quran"
  const val KHATM = "khatm"
  const val METHODS = "methods"
  const val VIRTUES = "virtues"
  const val TAFSIR = "tafsir"
  const val SETTINGS = "settings"
}

@Composable
fun WaqiahNavHost(viewModel: AppViewModel) {
  val navController = rememberNavController()
  val state by viewModel.state.collectAsStateWithLifecycle()
  val back: () -> Unit = { navController.popBackStack() }

  NavHost(navController = navController, startDestination = Routes.HOME) {
    composable(Routes.HOME) {
      HomeScreen(
        state = state,
        onToggleToday = { index -> viewModel.toggleDay(index) },
        onNavigate = { route -> navController.navigate(route) },
      )
    }
    composable(Routes.QURAN) {
      QuranScreen(
        state = state,
        onBack = back,
        onSelectTranslation = { id -> viewModel.selectTranslation(id) },
        onArabicScale = { scale -> viewModel.setArabicFontScale(scale) },
        onTranslationScale = { scale -> viewModel.setTranslationFontScale(scale) },
        onPlayFromAyah = { ayah -> viewModel.playFromAyah(ayah) },
        onTogglePlayPause = { viewModel.togglePlayPause() },
        onStopRecitation = { viewModel.stopRecitation() },
      )
    }
    composable(Routes.KHATM) {
      KhatmScreen(
        state = state,
        onBack = back,
        onToggleDay = { index -> viewModel.toggleDay(index) },
        onStartMethod = { method -> viewModel.startPlan(method) },
        onStartCustom = { days, perDay -> viewModel.startCustomPlan(days, perDay) },
        onClearPlan = { viewModel.clearPlan() },
        onSeeMethods = { navController.navigate(Routes.METHODS) },
      )
    }
    composable(Routes.METHODS) {
      MethodsScreen(
        state = state,
        onBack = back,
        onStartMethod = { method ->
          viewModel.startPlan(method)
          navController.navigate(Routes.KHATM)
        },
      )
    }
    composable(Routes.VIRTUES) {
      VirtuesScreen(state = state, onBack = back)
    }
    composable(Routes.TAFSIR) {
      TafsirScreen(state = state, onBack = back)
    }
    composable(Routes.SETTINGS) {
      SettingsScreen(
        state = state,
        onBack = back,
        onReminderEnabled = { enabled -> viewModel.setReminderEnabled(enabled) },
        onReminderTime = { hour, minute -> viewModel.setReminderTime(hour, minute) },
        onThemeMode = { mode -> viewModel.setThemeMode(mode) },
        onBackground = { palette -> viewModel.setBackground(palette) },
        onAyahNumberStyle = { style -> viewModel.setAyahNumberStyle(style) },
      )
    }
  }
}
