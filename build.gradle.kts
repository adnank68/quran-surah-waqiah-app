// AGP 9.x پشتیبانی Kotlin را داخل خودش دارد، پس افزونه‌ی جداگانه‌ی
// `org.jetbrains.kotlin.android` اینجا اعمال نمی‌شود (همان الگوی sudoku-pro).
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.kotlin.compose) apply false
}
