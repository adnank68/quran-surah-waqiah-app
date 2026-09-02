package ir.almasbu.waqiah.data

/** یک آیه با متن عثمانی و ترجمه‌هایش (کلید = شناسه‌ی مترجم). */
data class Ayah(
  val number: Int,
  val arabic: String,
  val translations: Map<String, String>,
)

data class Translator(
  val id: String,
  val name: String,
  val short: String,
)

/** یک بخش از تفسیر که آیه‌های `from` تا `to` را با هم پوشش می‌دهد. */
data class TafsirSection(
  val from: Int,
  val to: Int,
  val text: String,
)

data class Dua(
  val title: String,
  val arabic: String,
  val persian: String,
  /** `daily` یا `thursday` — تعیین می‌کند در کدام روزها خوانده می‌شود. */
  val occasion: String,
)

/**
 * یک روش ختم. [perDay] تعداد تلاوت هر روز است، پس طول آن = تعداد روزهای ختم
 * و مجموعش = تعداد کل تلاوت‌ها.
 */
data class KhatmMethod(
  val id: String,
  val title: String,
  val purpose: String,
  val intro: String,
  val note: String,
  val perDay: List<Int>,
  val conditions: List<String>,
  val duas: List<Dua>,
  val source: String,
) {
  val days: Int get() = perDay.size
  val total: Int get() = perDay.sum()
}

data class AboutSection(val title: String, val body: String)

data class Virtue(val text: String, val by: String)

data class SourceRef(val label: String, val value: String)

data class SurahInfo(
  val name: String,
  val nameFa: String,
  val number: Int,
  val ayahCount: Int,
  val juz: Int,
  val revelationPlace: String,
  val revelationOrder: Int,
)

data class WaqiahContent(
  val surah: SurahInfo,
  val bismillah: String,
  val translators: List<Translator>,
  val ayat: List<Ayah>,
  val tafsirTitle: String,
  val tafsirAuthor: String,
  val tafsirSource: String,
  val tafsir: List<TafsirSection>,
  val about: List<AboutSection>,
  val virtues: List<Virtue>,
  val virtuesSource: String,
  val khatmMethods: List<KhatmMethod>,
  val sources: List<SourceRef>,
)
