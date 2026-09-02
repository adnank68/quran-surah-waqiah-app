package ir.almasbu.waqiah.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * پخش تلاوت آیه‌به‌آیه.
 *
 * هر آیه یک فایل جداگانه در `assets/audio/NNN.mp3` است. همین باعث می‌شود
 * هماهنگی متن و صوت **دقیق** باشد بدون اینکه به جدول زمان‌بندی نیاز داشته
 * باشیم: آیه‌ی در حال پخش همان فایلی است که باز است، و رفتن به آیه‌ی بعد
 * یعنی باز کردن فایل بعدی در `setOnCompletionListener`.
 */
class RecitationPlayer(context: Context) {

  private val appContext = context.applicationContext
  private val audioManager = appContext.getSystemService(AudioManager::class.java)

  data class State(
    /** شماره‌ی آیه‌ی در حال پخش، یا `null` وقتی چیزی بارگذاری نشده. */
    val ayah: Int? = null,
    val isPlaying: Boolean = false,
    /** وقتی فایل آیه خراب یا غایب باشد، پیام برای نمایش به کاربر. */
    val error: String? = null,
  )

  private val _state = MutableStateFlow(State())
  val state: StateFlow<State> = _state.asStateFlow()

  private var player: MediaPlayer? = null
  private var focusRequest: AudioFocusRequest? = null

  private val focusListener = AudioManager.OnAudioFocusChangeListener { change ->
    when (change) {
      // تماس ورودی یا اپ دیگری صدا را گرفت: مکث کن، خودکار ادامه نده.
      AudioManager.AUDIOFOCUS_LOSS,
      AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
      -> pause()

      AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> player?.setVolume(0.2f, 0.2f)
      AudioManager.AUDIOFOCUS_GAIN -> player?.setVolume(1f, 1f)
    }
  }

  /** پخش از آیه‌ی مشخص شروع می‌شود و تا آخر سوره خودکار ادامه پیدا می‌کند. */
  fun playAyah(ayah: Int) {
    if (ayah !in 1..LAST_AYAH) {
      stop()
      return
    }
    releasePlayer()

    val created = runCatching {
      MediaPlayer().apply {
        setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()
        )
        appContext.assets.openFd(assetPathFor(ayah)).use { fd ->
          setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
        }
        setOnCompletionListener {
          // پایان این آیه → آیه‌ی بعد؛ آخرین آیه که تمام شد، پخش می‌ایستد.
          if (ayah < LAST_AYAH) playAyah(ayah + 1) else stop()
        }
        setOnErrorListener { _, what, extra ->
          Log.e(TAG, "خطای MediaPlayer روی آیه $ayah: what=$what extra=$extra")
          _state.value = State(ayah = ayah, isPlaying = false, error = "پخش این آیه ممکن نشد.")
          true
        }
        prepare()
      }
    }.getOrElse { throwable ->
      Log.e(TAG, "بارگذاری صوت آیه $ayah شکست خورد", throwable)
      _state.value = State(ayah = ayah, isPlaying = false, error = "فایل صوتی این آیه پیدا نشد.")
      return
    }

    player = created
    if (!requestFocus()) {
      _state.value = State(ayah = ayah, isPlaying = false)
      return
    }
    created.start()
    _state.value = State(ayah = ayah, isPlaying = true)
  }

  /** دکمه‌ی پخش/مکث: از آیه‌ی جاری (یا آیه‌ی ۱) ادامه می‌دهد. */
  fun togglePlayPause() {
    val current = _state.value
    when {
      current.isPlaying -> pause()
      player != null && current.ayah != null -> resume()
      else -> playAyah(current.ayah ?: 1)
    }
  }

  fun pause() {
    val p = player ?: return
    if (p.isPlaying) p.pause()
    _state.value = _state.value.copy(isPlaying = false, error = null)
  }

  fun resume() {
    val p = player
    if (p == null) {
      playAyah(_state.value.ayah ?: 1)
      return
    }
    if (!requestFocus()) return
    p.start()
    _state.value = _state.value.copy(isPlaying = true, error = null)
  }

  fun stop() {
    releasePlayer()
    abandonFocus()
    _state.value = State()
  }

  fun release() {
    releasePlayer()
    abandonFocus()
  }

  private fun releasePlayer() {
    player?.run {
      setOnCompletionListener(null)
      setOnErrorListener(null)
      runCatching { if (isPlaying) stop() }
      release()
    }
    player = null
  }

  private fun requestFocus(): Boolean {
    val manager = audioManager ?: return true
    val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
        .setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()
        )
        .setOnAudioFocusChangeListener(focusListener)
        .build()
      focusRequest = request
      manager.requestAudioFocus(request)
    } else {
      @Suppress("DEPRECATION")
      manager.requestAudioFocus(
        focusListener,
        AudioManager.STREAM_MUSIC,
        AudioManager.AUDIOFOCUS_GAIN,
      )
    }
    return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
  }

  private fun abandonFocus() {
    val manager = audioManager ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      focusRequest?.let { manager.abandonAudioFocusRequest(it) }
      focusRequest = null
    } else {
      @Suppress("DEPRECATION")
      manager.abandonAudioFocus(focusListener)
    }
  }

  companion object {
    const val LAST_AYAH = 96
    private const val TAG = "RecitationPlayer"

    /** مسیر فایل صوتی یک آیه داخل assets — مثلاً آیه‌ی ۷ ← `audio/007.mp3`. */
    fun assetPathFor(ayah: Int): String = "audio/%03d.mp3".format(ayah)
  }
}
