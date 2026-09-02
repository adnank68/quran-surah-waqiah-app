package ir.almasbu.waqiah.util

/** تبدیل ارقام لاتین به ارقام فارسی، برای هر متنی که به کاربر نشان داده می‌شود. */
object PersianNumbers {
  private val DIGITS = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

  fun of(value: Int): String = of(value.toString())

  fun of(text: String): String = buildString(text.length) {
    for (ch in text) append(if (ch in '0'..'9') DIGITS[ch - '0'] else ch)
  }

  /** ساعت و دقیقه‌ی دو رقمی با ارقام فارسی، مثلاً «۰۸:۰۵». */
  fun time(hour: Int, minute: Int): String =
    of("%02d:%02d".format(hour, minute))
}
