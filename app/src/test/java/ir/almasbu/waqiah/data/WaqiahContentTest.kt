package ir.almasbu.waqiah.data

import androidx.test.core.app.ApplicationProvider
import ir.almasbu.waqiah.audio.RecitationPlayer
import ir.almasbu.waqiah.audio.Reciter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * فایل `assets/waqiah.json` با اسکریپت ساخته می‌شود، پس این تست‌ها همان چیزی
 * را می‌سنجند که واقعاً داخل APK می‌رود: کامل بودن ۹۶ آیه، پر بودن هر چهار
 * ترجمه، پوشش کاملِ تفسیر و درست بودن جمعِ تلاوت‌های هر روش ختم.
 */
@RunWith(RobolectricTestRunner::class)
class WaqiahContentTest {

  private val content: WaqiahContent by lazy {
    WaqiahRepository.load(ApplicationProvider.getApplicationContext())
  }

  @Test
  fun surahMetadataIsCorrect() {
    assertEquals(56, content.surah.number)
    assertEquals(96, content.surah.ayahCount)
    assertEquals(27, content.surah.juz)
    assertEquals("الواقعة", content.surah.name)
  }

  @Test
  fun allNinetySixAyatArePresentAndInOrder() {
    assertEquals(96, content.ayat.size)
    content.ayat.forEachIndexed { index, ayah ->
      assertEquals("آیه‌ها باید به ترتیب باشند", index + 1, ayah.number)
      assertTrue("متن عربی آیه ${ayah.number} خالی است", ayah.arabic.isNotBlank())
    }
  }

  @Test
  fun bismillahIsSeparateFromTheFirstAyah() {
    assertTrue(content.bismillah.isNotBlank())
    val first = content.ayat.first().arabic
    assertTrue(
      "بسم‌الله نباید داخل متن آیه‌ی اول مانده باشد: $first",
      !first.contains("ٱلرَّحِيم") && !first.contains("الرَّحِيم"),
    )
  }

  @Test
  fun allFourTranslationsAreComplete() {
    val expected = setOf("ansarian", "ghomshei", "gharaati", "makarem")
    assertEquals(expected, content.translators.map { it.id }.toSet())

    content.ayat.forEach { ayah ->
      expected.forEach { id ->
        val text = ayah.translations[id]
        assertTrue("ترجمه‌ی $id برای آیه ${ayah.number} خالی است", !text.isNullOrBlank())
      }
    }
  }

  @Test
  fun tafsirCoversEveryAyahExactlyOnce() {
    val covered = mutableListOf<Int>()
    content.tafsir.forEach { section ->
      assertTrue("بازه‌ی تفسیر معکوس است: ${section.from}-${section.to}", section.from <= section.to)
      assertTrue("متن تفسیر ${section.from}-${section.to} خالی است", section.text.isNotBlank())
      for (n in section.from..section.to) covered += n
    }
    assertEquals("هر آیه باید دقیقاً یک بار پوشش داده شود", (1..96).toList(), covered.sorted())
    assertEquals(96, covered.size)
  }

  @Test
  fun khatmMethodsHaveConsistentPatterns() {
    assertTrue("باید دست‌کم یک روش ختم وجود داشته باشد", content.khatmMethods.isNotEmpty())

    content.khatmMethods.forEach { method ->
      assertTrue("روش ${method.id} روزی ندارد", method.days > 0)
      assertTrue("روش ${method.id} تلاوتی ندارد", method.total > 0)
      assertEquals(method.perDay.size, method.days)
      assertEquals(method.perDay.sum(), method.total)
      assertTrue("هر روز باید دست‌کم یک تلاوت داشته باشد", method.perDay.all { it >= 1 })
      assertTrue("روش ${method.id} منبع ندارد", method.source.isNotBlank())
    }

    // ختم چهارده‌روزه‌ی منقول از آیت‌الله بهجت: ۱+۲+…+۱۴ = ۱۰۵
    val bahjat = content.khatmMethods.single { it.id == "bahjat14" }
    assertEquals(14, bahjat.days)
    assertEquals(105, bahjat.total)
    assertEquals((1..14).toList(), bahjat.perDay)
    assertEquals("این ختم دعای روزانه و دعای پنج‌شنبه دارد", 2, bahjat.duas.size)
    assertTrue(bahjat.duas.any { it.occasion == "thursday" })
    assertTrue(bahjat.duas.all { it.arabic.isNotBlank() })

    // ختم یک‌هفته‌ای، طبق منبع مجموعاً ۴۱ مرتبه است
    assertEquals(41, content.khatmMethods.single { it.id == "rizq41" }.total)
  }

  @Test
  fun uthmaniTextHasNoCircleMarksLeft() {
    // U+06DF («صفر گرد کوچک») و U+06DE (رب‌الحزب) روی دستگاه به‌صورت یک
    // دایره‌ی درشتِ جدا کنار کلمه دیده می‌شدند و باید حذف شده باشند.
    content.ayat.forEach { ayah ->
      assertFalse(
        "آیه ${ayah.number} هنوز نشانه‌ی دایره‌ای دارد: ${ayah.arabic}",
        ayah.arabic.contains('۟') || ayah.arabic.contains('۞'),
      )
    }
  }

  @Test
  fun hamzaAlefIsWrittenAsMaddaAlef() {
    // «ءَا» باید به «آ» تبدیل شده باشد — مثلاً ٱلْءَاخِرِينَ ← ٱلْآخِرِينَ
    content.ayat.forEach { ayah ->
      assertFalse(
        "آیه ${ayah.number} هنوز «ءَا» دارد: ${ayah.arabic}",
        ayah.arabic.contains("ءَا"),
      )
    }
    // آیه‌های ۱۴، ۴۰، ۴۹ باید حالا «آ» داشته باشند
    listOf(14, 40, 49).forEach { n ->
      val ayah = content.ayat.single { it.number == n }
      assertTrue("آیه $n باید «آ» داشته باشد: ${ayah.arabic}", ayah.arabic.contains('آ'))
    }
  }

  @Test
  fun closingDuaIsCompleteWithTranslations() {
    val dua = content.closingDua
    assertTrue(dua.title.isNotBlank())
    assertTrue(dua.subtitle.isNotBlank())
    assertTrue(dua.source.isNotBlank())
    assertTrue("دعای پایان ختم باید بند داشته باشد", dua.parts.isNotEmpty())
    dua.parts.forEachIndexed { index, part ->
      assertTrue("بند ${index + 1}: متن عربی خالی", part.arabic.isNotBlank())
      assertTrue("بند ${index + 1}: ترجمه خالی", part.persian.isNotBlank())
    }
  }

  @Test
  fun everyReciterHasEveryTrackIncludingBismillahBundled() {
    // تلاوت آفلاین است: برای **هر قاری**، بسم‌الله (۰) و هر ۹۶ آیه باید فایل
    // صوتی خودشان را داخل APK داشته باشند. اگر پوشه‌ی یک قاری ناقص باشد،
    // پخش وسط سوره می‌ایستد.
    val assets = ApplicationProvider.getApplicationContext<android.content.Context>().assets
    Reciter.entries.forEach { reciter ->
      (RecitationPlayer.BISMILLAH..RecitationPlayer.LAST_AYAH).forEach { n ->
        val path = reciter.assetPathFor(n)
        // `open` به‌جای `openFd` — رفتارش زیر Robolectric قابل‌اتکاتر است و
        // برای «فایل هست و خالی نیست» همین کافی است.
        val size = assets.open(path).use { it.readBytes().size }
        assertTrue("فایل صوتی $path خیلی کوچک است ($size بایت)", size > 2000)
      }
    }
  }

  @Test
  fun reciterAssetPathsAreFolderScopedAndZeroPadded() {
    assertEquals("audio/minshawi/000.mp3", Reciter.MINSHAWI.assetPathFor(RecitationPlayer.BISMILLAH))
    assertEquals("audio/minshawi/007.mp3", Reciter.MINSHAWI.assetPathFor(7))
    assertEquals("audio/mansouri/096.mp3", Reciter.MANSOURI.assetPathFor(96))

    // هر قاری باید پوشه‌ی خودش را داشته باشد، وگرنه فایل‌ها روی هم می‌افتند
    assertEquals(
      Reciter.entries.size,
      Reciter.entries.map { it.folder }.toSet().size,
    )
    assertEquals(Reciter.MINSHAWI, Reciter.fromId("minshawi"))
    assertEquals(Reciter.MANSOURI, Reciter.fromId("mansouri"))
    assertEquals("قاری ناشناخته باید به پیش‌فرض برگردد", Reciter.DEFAULT, Reciter.fromId("nope"))
  }

  @Test
  fun duaTextHasNoUnsupportedPresentationForms() {
    // «ﮦ» (U+FBA6) در هیچ‌کدام از دو فونت اپ نیست و مربع خالی می‌شود؛
    // در سند اصلی بود و باید به «ه» عادی تبدیل شده باشد.
    val allDuaText = buildString {
      content.closingDua.parts.forEach { append(it.arabic).append(it.persian) }
      content.khatmMethods.forEach { method ->
        method.duas.forEach { append(it.arabic).append(it.persian) }
      }
    }
    assertFalse("متن دعا شکل نمایشی پشتیبانی‌نشده دارد", allDuaText.contains('ﮦ'))
  }

  @Test
  fun virtuesAndSourcesArePresent() {
    assertTrue(content.virtues.isNotEmpty())
    content.virtues.forEach {
      assertTrue(it.text.isNotBlank())
      assertTrue("هر روایت باید گوینده/راوی داشته باشد", it.by.isNotBlank())
    }
    assertTrue(content.virtuesSource.isNotBlank())
    assertTrue(content.about.isNotEmpty())
    assertTrue(content.sources.isNotEmpty())
  }
}
