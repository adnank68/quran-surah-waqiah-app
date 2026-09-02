package ir.almasbu.waqiah.util

import java.util.Calendar

/**
 * تبدیل تاریخ میلادی ↔ هجری خورشیدی.
 *
 * این پیاده‌سازی، ترجمه‌ی مستقیمِ الگوریتم مرجع `jalaali-js` است (که خودش بر
 * پایه‌ی الگوریتم کاظم برکوفسکی است) و از جدولِ «سال‌های شکست» استفاده می‌کند،
 * نه از فرمول‌های تقریبیِ ۳۳ساله. فرمول‌های تقریبی برای بعضی سال‌ها یک روز خطا
 * می‌دهند؛ در پروژه‌ی «برنامه‌ریزی و دستیار شمسی» دقیقاً همین خطا پیش آمده بود،
 * پس اینجا با تست واحد در برابر تاریخ‌های شناخته‌شده مهار شده است.
 *
 * واحد مشترک همه‌ی محاسبات، «شماره‌ی روز ژولینی» (JDN) به‌صورت عدد صحیح است.
 */
object Jalali {

  /** سال‌های شکست در الگوریتم برکوفسکی؛ محدوده‌ی معتبر: ۶۱- تا ۳۱۷۷ شمسی. */
  private val BREAKS = intArrayOf(
    -61, 9, 38, 199, 426, 686, 756, 818, 1111, 1181, 1210,
    1635, 2060, 2097, 2192, 2262, 2324, 2394, 2456, 3178,
  )

  val MONTH_NAMES = arrayOf(
    "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
    "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند",
  )

  /** نام روزهای هفته به ترتیب تقویم ایرانی (۰ = شنبه). */
  val WEEKDAY_NAMES = arrayOf(
    "شنبه", "یک‌شنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنج‌شنبه", "جمعه",
  )

  const val THURSDAY = 5
  const val FRIDAY = 6

  private fun div(a: Int, b: Int) = a / b
  private fun mod(a: Int, b: Int) = a % b

  private class JalCal(val leap: Int, val gy: Int, val march: Int)

  private fun jalCal(jy: Int): JalCal {
    val bl = BREAKS.size
    val gy = jy + 621
    var leapJ = -14
    var jp = BREAKS[0]
    require(jy >= jp && jy < BREAKS[bl - 1]) { "سال شمسی خارج از محدوده‌ی معتبر: $jy" }

    var jump = 0
    for (i in 1 until bl) {
      val jm = BREAKS[i]
      jump = jm - jp
      if (jy < jm) break
      leapJ += div(jump, 33) * 8 + div(mod(jump, 33), 4)
      jp = jm
    }
    var n = jy - jp

    leapJ += div(n, 33) * 8 + div(mod(n, 33) + 3, 4)
    if (mod(jump, 33) == 4 && jump - n == 4) leapJ += 1

    val leapG = div(gy, 4) - div((div(gy, 100) + 1) * 3, 4) - 150
    val march = 20 + leapJ - leapG

    if (jump - n < 6) n = n - jump + div(jump + 4, 33) * 33
    var leap = mod(mod(n + 1, 33) - 1, 4)
    if (leap == -1) leap = 4

    return JalCal(leap, gy, march)
  }

  /** شماره‌ی روز ژولینی برای یک تاریخ میلادی. */
  fun gregorianToJdn(gy: Int, gm: Int, gd: Int): Int {
    var d = div((gy + div(gm - 8, 6) + 100100) * 1461, 4) +
      div(153 * mod(gm + 9, 12) + 2, 5) + gd - 34840408
    d = d - div(div(gy + 100100 + div(gm - 8, 6), 100) * 3, 4) + 752
    return d
  }

  /** تاریخ میلادی از روی شماره‌ی روز ژولینی: `[سال، ماه، روز]`. */
  fun jdnToGregorian(jdn: Int): IntArray {
    var j = 4 * jdn + 139361631
    j += div(div(4 * jdn + 183187720, 146097) * 3, 4) * 4 - 3908
    val i = div(mod(j, 1461), 4) * 5 + 308
    val gd = div(mod(i, 153), 5) + 1
    val gm = mod(div(i, 153), 4) + 1
    val gy = div(j, 1461) - 100100 + div(8 - gm, 6)
    return intArrayOf(gy, gm, gd)
  }

  /** شماره‌ی روز ژولینی برای یک تاریخ شمسی. */
  fun jalaliToJdn(jy: Int, jm: Int, jd: Int): Int {
    val r = jalCal(jy)
    return gregorianToJdn(r.gy, 3, r.march) + (jm - 1) * 31 - div(jm, 7) * (jm - 7) + jd - 1
  }

  /** تاریخ شمسی از روی شماره‌ی روز ژولینی: `[سال، ماه، روز]`. */
  fun jdnToJalali(jdn: Int): IntArray {
    val gy = jdnToGregorian(jdn)[0]
    var jy = gy - 621
    // `r` عمداً برای سالِ اولیه حساب می‌شود و بعد از کم‌کردن یک سال هم همان
    // `r.leap` به کار می‌رود — این دقیقاً رفتار الگوریتم مرجع است.
    val r = jalCal(jy)
    val jdn1f = gregorianToJdn(gy, 3, r.march)
    var k = jdn - jdn1f

    if (k >= 0) {
      if (k <= 185) return intArrayOf(jy, 1 + div(k, 31), mod(k, 31) + 1)
      k -= 186
    } else {
      jy -= 1
      k += 179
      if (r.leap == 1) k += 1
    }
    return intArrayOf(jy, 7 + div(k, 30), mod(k, 30) + 1)
  }

  /** آیا این سال شمسی کبیسه است؟ */
  fun isLeapJalaliYear(jy: Int): Boolean = jalCal(jy).leap == 0

  /** شماره‌ی روز هفته (۰ = شنبه … ۶ = جمعه). */
  fun weekdayOf(jdn: Int): Int = mod(mod(jdn, 7) + 2, 7)

  fun weekdayName(jdn: Int): String = WEEKDAY_NAMES[weekdayOf(jdn)]

  /** شماره‌ی روز ژولینیِ «امروز» بر اساس ساعت محلی دستگاه. */
  fun todayJdn(now: Calendar = Calendar.getInstance()): Int = gregorianToJdn(
    now.get(Calendar.YEAR),
    now.get(Calendar.MONTH) + 1,
    now.get(Calendar.DAY_OF_MONTH),
  )

  /** مثلاً «چهارشنبه ۱۱ شهریور ۱۴۰۵». */
  fun formatFull(jdn: Int): String {
    val (jy, jm, jd) = jdnToJalali(jdn).let { Triple(it[0], it[1], it[2]) }
    return "${weekdayName(jdn)} ${PersianNumbers.of(jd)} ${MONTH_NAMES[jm - 1]} ${PersianNumbers.of(jy)}"
  }

  /** مثلاً «۱۱ شهریور». */
  fun formatShort(jdn: Int): String {
    val j = jdnToJalali(jdn)
    return "${PersianNumbers.of(j[2])} ${MONTH_NAMES[j[1] - 1]}"
  }
}
