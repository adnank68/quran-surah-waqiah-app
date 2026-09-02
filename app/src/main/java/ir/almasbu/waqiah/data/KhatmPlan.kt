package ir.almasbu.waqiah.data

import ir.almasbu.waqiah.util.Jalali
import org.json.JSONArray
import org.json.JSONObject

/**
 * برنامه‌ی ختمِ در جریان کاربر.
 *
 * [startJdn] روزِ شروع است (شماره‌ی روز ژولینی) و [perDay] تعداد تلاوتِ هر روز.
 * [doneDays] شماره‌ی روزهای تیک‌خورده را نگه می‌دارد (۰ برای روز اول).
 */
data class KhatmPlan(
  val methodId: String,
  val title: String,
  val startJdn: Int,
  val perDay: List<Int>,
  val doneDays: Set<Int> = emptySet(),
) {
  val days: Int get() = perDay.size
  val totalRecitations: Int get() = perDay.sum()

  val doneRecitations: Int
    get() = doneDays.sumOf { perDay.getOrElse(it) { 0 } }

  /** درصد پیشرفت بر پایه‌ی تعداد تلاوت‌ها، نه تعداد روزها. */
  val progress: Float
    get() = if (totalRecitations == 0) 0f else doneRecitations.toFloat() / totalRecitations

  val isComplete: Boolean get() = doneDays.size >= days

  fun jdnOfDay(index: Int): Int = startJdn + index

  fun recitationsOn(index: Int): Int = perDay.getOrElse(index) { 0 }

  /** شماره‌ی روزِ امروز در این برنامه، یا `null` اگر امروز بیرون از بازه باشد. */
  fun todayIndex(todayJdn: Int): Int? {
    val index = todayJdn - startJdn
    return if (index in 0 until days) index else null
  }

  fun isDone(index: Int): Boolean = index in doneDays

  fun toggle(index: Int): KhatmPlan =
    copy(doneDays = if (index in doneDays) doneDays - index else doneDays + index)

  /** آیا این روز از برنامه، پنج‌شنبه است؟ (برای دعای مخصوص پنج‌شنبه) */
  fun isThursday(index: Int): Boolean =
    Jalali.weekdayOf(jdnOfDay(index)) == Jalali.THURSDAY

  fun toJson(): JSONObject = JSONObject().apply {
    put("methodId", methodId)
    put("title", title)
    put("startJdn", startJdn)
    put("perDay", JSONArray(perDay))
    put("doneDays", JSONArray(doneDays.sorted()))
  }

  companion object {
    const val CUSTOM_METHOD_ID = "custom"

    fun fromMethod(method: KhatmMethod, startJdn: Int) = KhatmPlan(
      methodId = method.id,
      title = method.title,
      startJdn = startJdn,
      perDay = method.perDay,
    )

    fun custom(days: Int, perDay: Int, startJdn: Int) = KhatmPlan(
      methodId = CUSTOM_METHOD_ID,
      title = "برنامه‌ی دلخواه",
      startJdn = startJdn,
      perDay = List(days) { perDay },
    )

    fun fromJson(json: JSONObject): KhatmPlan {
      val perDayArray = json.getJSONArray("perDay")
      val doneArray = json.getJSONArray("doneDays")
      return KhatmPlan(
        methodId = json.getString("methodId"),
        title = json.getString("title"),
        startJdn = json.getInt("startJdn"),
        perDay = List(perDayArray.length()) { perDayArray.getInt(it) },
        doneDays = buildSet { for (i in 0 until doneArray.length()) add(doneArray.getInt(i)) },
      )
    }
  }
}
