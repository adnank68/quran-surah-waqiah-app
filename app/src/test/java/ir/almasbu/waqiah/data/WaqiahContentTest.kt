package ir.almasbu.waqiah.data

import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
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
