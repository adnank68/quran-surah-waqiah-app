package ir.almasbu.waqiah.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * محتوای اپ از `assets/waqiah.json` خوانده می‌شود — یک‌بار و بعد در حافظه
 * می‌ماند. اپ کاملاً آفلاین است و هیچ چیزی از شبکه نمی‌گیرد.
 *
 * آن فایل با `tools/build-data.js` از منابع بیرونی ساخته می‌شود؛ برای فهرست
 * منابع، بخش «منابع» در همان اسکریپت و در README را ببینید.
 */
object WaqiahRepository {

  private const val ASSET_NAME = "waqiah.json"

  @Volatile
  private var cached: WaqiahContent? = null

  fun load(context: Context): WaqiahContent =
    cached ?: synchronized(this) {
      cached ?: parse(readAsset(context)).also { cached = it }
    }

  private fun readAsset(context: Context): JSONObject =
    context.assets.open(ASSET_NAME).use { stream ->
      JSONObject(stream.readBytes().toString(Charsets.UTF_8))
    }

  private fun parse(root: JSONObject): WaqiahContent {
    val translators = root.getJSONArray("translators").mapObjects {
      Translator(it.getString("id"), it.getString("name"), it.getString("short"))
    }

    val ayat = root.getJSONArray("ayat").mapObjects { obj ->
      val tr = obj.getJSONObject("tr")
      Ayah(
        number = obj.getInt("n"),
        arabic = obj.getString("ar"),
        translations = translators.associate { it.id to tr.getString(it.id) },
      )
    }

    val tafsirObj = root.getJSONObject("tafsir")

    return WaqiahContent(
      surah = root.getJSONObject("surah").let {
        SurahInfo(
          name = it.getString("name"),
          nameFa = it.getString("nameFa"),
          number = it.getInt("number"),
          ayahCount = it.getInt("ayahCount"),
          juz = it.getInt("juz"),
          revelationPlace = it.getString("revelationPlace"),
          revelationOrder = it.getInt("revelationOrder"),
        )
      },
      bismillah = root.getString("bismillah"),
      translators = translators,
      ayat = ayat,
      tafsirTitle = tafsirObj.getString("title"),
      tafsirAuthor = tafsirObj.getString("author"),
      tafsirSource = tafsirObj.getString("source"),
      tafsir = tafsirObj.getJSONArray("sections").mapObjects {
        TafsirSection(it.getInt("from"), it.getInt("to"), it.getString("text"))
      },
      about = root.getJSONArray("about").mapObjects {
        AboutSection(it.getString("title"), it.getString("body"))
      },
      virtues = root.getJSONArray("virtues").mapObjects {
        Virtue(it.getString("text"), it.getString("by"))
      },
      virtuesSource = root.getString("virtuesSource"),
      khatmMethods = root.getJSONArray("khatmMethods").mapObjects { obj ->
        KhatmMethod(
          id = obj.getString("id"),
          title = obj.getString("title"),
          purpose = obj.getString("purpose"),
          intro = obj.getString("intro"),
          note = obj.optString("note"),
          perDay = obj.getJSONArray("perDay").let { arr -> List(arr.length()) { arr.getInt(it) } },
          conditions = obj.getJSONArray("conditions").let { arr -> List(arr.length()) { arr.getString(it) } },
          duas = obj.getJSONArray("duas").mapObjects {
            Dua(
              title = it.getString("title"),
              arabic = it.getString("ar"),
              persian = it.optString("fa"),
              occasion = it.getString("when"),
            )
          },
          source = obj.getString("source"),
        )
      },
      sources = root.getJSONArray("sources").mapObjects {
        SourceRef(it.getString("label"), it.getString("value"))
      },
    )
  }

  private fun <T> JSONArray.mapObjects(transform: (JSONObject) -> T): List<T> =
    List(length()) { transform(getJSONObject(it)) }
}
