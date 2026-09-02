package ir.almasbu.waqiah.util

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * تبدیل تاریخ، جایی است که در پروژه‌ی «برنامه‌ریزی و دستیار شمسی» کدِ
 * تولیدشده یک روز خطا داشت. این تست‌ها آن را در برابر تاریخ‌های شناخته‌شده و
 * مستقلاً قابل‌راستی‌آزمایی مهار می‌کنند.
 */
class JalaliTest {

  private fun gregorianToJalali(gy: Int, gm: Int, gd: Int): IntArray =
    Jalali.jdnToJalali(Jalali.gregorianToJdn(gy, gm, gd))

  @Test
  fun knownDatesConvertCorrectly() {
    // ۲۲ بهمن ۱۳۵۷ — پیروزی انقلاب
    assertArrayEquals(intArrayOf(1357, 11, 22), gregorianToJalali(1979, 2, 11))

    // اول فروردین ۱۴۰۳ (نوروز ۱۴۰۳ برابر ۲۰ مارس ۲۰۲۴ بود)
    assertArrayEquals(intArrayOf(1403, 1, 1), gregorianToJalali(2024, 3, 20))

    // اول فروردین ۱۴۰۴
    assertArrayEquals(intArrayOf(1404, 1, 1), gregorianToJalali(2025, 3, 21))

    // ۱۱ دی ۱۳۷۸
    assertArrayEquals(intArrayOf(1378, 10, 11), gregorianToJalali(2000, 1, 1))

    // ۱۱ شهریور ۱۴۰۵ — روزی که داده‌های این اپ گردآوری شد
    assertArrayEquals(intArrayOf(1405, 6, 11), gregorianToJalali(2026, 9, 2))
  }

  @Test
  fun leapYearsHaveThirtiethOfEsfand() {
    // ۱۴۰۳ کبیسه است، پس ۳۰ اسفند دارد و روز بعدش نوروز ۱۴۰۴ است.
    assertTrue(Jalali.isLeapJalaliYear(1403))
    assertArrayEquals(intArrayOf(1403, 12, 30), gregorianToJalali(2025, 3, 20))

    // ۱۴۰۴ کبیسه نیست.
    assertFalse(Jalali.isLeapJalaliYear(1404))
  }

  @Test
  fun jalaliRoundTripsOverNinetyYears() {
    val start = Jalali.gregorianToJdn(1970, 1, 1)
    val end = Jalali.gregorianToJdn(2060, 12, 31)
    for (jdn in start..end) {
      val j = Jalali.jdnToJalali(jdn)
      assertEquals(
        "برگشتِ تبدیل برای jdn=$jdn (شمسی ${j[0]}/${j[1]}/${j[2]}) نادرست است",
        jdn,
        Jalali.jalaliToJdn(j[0], j[1], j[2]),
      )
    }
  }

  @Test
  fun gregorianRoundTrips() {
    val start = Jalali.gregorianToJdn(1970, 1, 1)
    val end = Jalali.gregorianToJdn(2060, 12, 31)
    for (jdn in start..end) {
      val g = Jalali.jdnToGregorian(jdn)
      assertEquals(jdn, Jalali.gregorianToJdn(g[0], g[1], g[2]))
    }
  }

  @Test
  fun monthAndDayStayInRange() {
    val start = Jalali.gregorianToJdn(1970, 1, 1)
    val end = Jalali.gregorianToJdn(2060, 12, 31)
    for (jdn in start..end) {
      val j = Jalali.jdnToJalali(jdn)
      val (jy, jm, jd) = Triple(j[0], j[1], j[2])
      assertTrue("ماه نامعتبر $jm در $jy", jm in 1..12)
      assertTrue("روز نامعتبر $jd در $jy/$jm", jd in 1..31)
      // فروردین تا شهریور ۳۱ روزه، مهر تا بهمن ۳۰ روزه، اسفند ۲۹ یا ۳۰ روزه.
      if (jm in 7..12) assertTrue("ماه $jm نباید بیش از ۳۰ روز داشته باشد", jd <= 30)
    }
  }

  @Test
  fun weekdayIsCorrect() {
    // ۲ سپتامبر ۲۰۲۶ چهارشنبه است.
    assertEquals(4, Jalali.weekdayOf(Jalali.gregorianToJdn(2026, 9, 2)))
    assertEquals("چهارشنبه", Jalali.weekdayName(Jalali.gregorianToJdn(2026, 9, 2)))

    // ۱۱ فوریه ۱۹۷۹ یک‌شنبه بود.
    assertEquals(1, Jalali.weekdayOf(Jalali.gregorianToJdn(1979, 2, 11)))

    // روزهای متوالی، روزهای هفته‌ی متوالی می‌دهند.
    val base = Jalali.gregorianToJdn(2026, 9, 2)
    for (offset in 0..20) {
      assertEquals((4 + offset) % 7, Jalali.weekdayOf(base + offset))
    }
  }

  @Test
  fun formattingUsesPersianDigits() {
    assertEquals("چهارشنبه ۱۱ شهریور ۱۴۰۵", Jalali.formatFull(Jalali.gregorianToJdn(2026, 9, 2)))
    assertEquals("۱۱ شهریور", Jalali.formatShort(Jalali.gregorianToJdn(2026, 9, 2)))
  }
}
