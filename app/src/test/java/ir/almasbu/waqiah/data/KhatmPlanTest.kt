package ir.almasbu.waqiah.data

import ir.almasbu.waqiah.util.Jalali
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** `org.json` پیاده‌سازی اندرویدی دارد، پس این تست‌ها با Robolectric اجرا می‌شوند. */
@RunWith(RobolectricTestRunner::class)
class KhatmPlanTest {

  private val start = Jalali.gregorianToJdn(2026, 9, 2)

  private fun plan() = KhatmPlan(
    methodId = "bahjat14",
    title = "ختم چهارده‌روزه",
    startJdn = start,
    perDay = (1..14).toList(),
  )

  @Test
  fun totalsMatchThePattern() {
    val p = plan()
    assertEquals(14, p.days)
    assertEquals(105, p.totalRecitations)
    assertEquals(0, p.doneRecitations)
    assertEquals(0f, p.progress, 0.0001f)
  }

  @Test
  fun progressIsWeightedByRecitationsNotDays() {
    // تیک‌زدنِ روز چهاردهم (۱۴ تلاوت) باید خیلی بیشتر از روز اول (۱ تلاوت)
    // پیشرفت بدهد — اگر پیشرفت را روی «تعداد روز» حساب کنیم هر دو یکی می‌شوند.
    val firstDay = plan().toggle(0)
    val lastDay = plan().toggle(13)

    assertEquals(1, firstDay.doneRecitations)
    assertEquals(14, lastDay.doneRecitations)
    assertEquals(1f / 105f, firstDay.progress, 0.0001f)
    assertEquals(14f / 105f, lastDay.progress, 0.0001f)
  }

  @Test
  fun toggleIsReversible() {
    val p = plan().toggle(3)
    assertTrue(p.isDone(3))
    assertFalse(p.toggle(3).isDone(3))
    assertEquals(0, p.toggle(3).doneRecitations)
  }

  @Test
  fun completionNeedsEveryDay() {
    var p = plan()
    for (i in 0 until 13) p = p.toggle(i)
    assertFalse(p.isComplete)

    p = p.toggle(13)
    assertTrue(p.isComplete)
    assertEquals(1f, p.progress, 0.0001f)
    assertEquals(105, p.doneRecitations)
  }

  @Test
  fun todayIndexOnlyInsideTheWindow() {
    val p = plan()
    assertEquals(0, p.todayIndex(start))
    assertEquals(13, p.todayIndex(start + 13))
    assertNull("روز پیش از شروع نباید شماره بگیرد", p.todayIndex(start - 1))
    assertNull("روز پس از پایان نباید شماره بگیرد", p.todayIndex(start + 14))
  }

  @Test
  fun thursdayIsDetectedFromTheRealCalendar() {
    // ۲ سپتامبر ۲۰۲۶ چهارشنبه است، پس روز دومِ برنامه پنج‌شنبه می‌شود.
    val p = plan()
    assertFalse(p.isThursday(0))
    assertTrue(p.isThursday(1))
    assertFalse(p.isThursday(2))
    assertTrue("پنج‌شنبه‌ی هفته‌ی بعد", p.isThursday(8))
  }

  @Test
  fun customPlanHasFlatPattern() {
    val p = KhatmPlan.custom(days = 40, perDay = 3, startJdn = start)
    assertEquals(40, p.days)
    assertEquals(120, p.totalRecitations)
    assertEquals(KhatmPlan.CUSTOM_METHOD_ID, p.methodId)
  }

  @Test
  fun jsonRoundTripKeepsEverything() {
    val original = plan().toggle(0).toggle(5).toggle(13)
    val restored = KhatmPlan.fromJson(original.toJson())
    assertEquals(original, restored)
    assertEquals(original.doneRecitations, restored.doneRecitations)
  }
}
