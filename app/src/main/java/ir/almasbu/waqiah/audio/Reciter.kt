package ir.almasbu.waqiah.audio

/**
 * قاری‌هایی که تلاوتشان داخل اپ هست.
 *
 * فایل‌های هر قاری در `assets/audio/<folder>/` می‌نشینند و نام‌گذاری‌شان یکسان
 * است: `000.mp3` بسم‌الله و `001..096.mp3` آیه‌ها. به همین دلیل عوض کردن قاری
 * هیچ منطق دیگری را تغییر نمی‌دهد، فقط مسیر فایل را.
 */
enum class Reciter(
  val id: String,
  val displayName: String,
  val folder: String,
) {
  MINSHAWI("minshawi", "استاد منشاوی", "minshawi"),
  MANSOURI("mansouri", "کریم منصوری", "mansouri");

  /** مسیر فایل صوتی داخل assets — مثلاً `audio/minshawi/007.mp3`. */
  fun assetPathFor(track: Int): String = "audio/$folder/%03d.mp3".format(track)

  companion object {
    val DEFAULT = MINSHAWI

    fun fromId(id: String?): Reciter = entries.firstOrNull { it.id == id } ?: DEFAULT
  }
}
