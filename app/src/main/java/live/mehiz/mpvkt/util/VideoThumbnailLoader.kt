package live.mehiz.mpvkt.util

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Loads and caches the initial frame of video files for display in the folder browser.
 * Uses a memory LRU cache for instant display on scroll.
 */
object VideoThumbnailLoader {

  private const val CACHE_SIZE = 64 // number of thumbnails to cache in memory

  private val memoryCache = object : LruCache<String, Bitmap>(CACHE_SIZE) {
    override fun sizeOf(key: String, value: Bitmap): Int = 1
  }

  /**
   * Loads a video frame thumbnail for the given file path.
   * Returns null if the file is not a video or the frame can't be extracted.
   */
  suspend fun loadThumbnail(context: Context, path: String): Bitmap? = withContext(Dispatchers.IO) {
    // Check memory cache first
    memoryCache.get(path)?.let { return@withContext it }

    val file = File(path)
    if (!file.exists() || !file.isFile) return@withContext null

    val ext = file.extension.lowercase()
    if (ext !in videoExtensions) return@withContext null

    var retriever: MediaMetadataRetriever? = null
    try {
      retriever = MediaMetadataRetriever()
      retriever.setDataSource(path)
      val bitmap = retriever.getFrameAtTime(
        0,
        MediaMetadataRetriever.OPTION_CLOSEST_SYNC,
      )
      if (bitmap != null) {
        memoryCache.put(path, bitmap)
      }
      bitmap
    } catch (e: Exception) {
      null
    } finally {
      try {
        retriever?.release()
      } catch (e: Exception) {
        // ignore
      }
    }
  }

  /**
   * The list of video file extensions for quick lookup.
   */
  private val videoExtensions = setOf(
    "264", "265", "3g2", "3ga", "3gp", "3gp2", "3gpp", "3gpp2", "3iv", "amr", "asf",
    "asx", "av1", "avc", "avf", "avi", "bdm", "bdmv", "clpi", "cpi", "divx", "dv", "evo",
    "evob", "f4v", "flc", "fli", "flic", "flv", "gxf", "h264", "h265", "hdmov", "hdv",
    "hevc", "lrv", "m1u", "m1v", "m2t", "m2ts", "m2v", "m4u", "m4v", "mkv", "mod", "moov",
    "mov", "mp2", "mp2v", "mp4", "mp4v", "mpe", "mpeg", "mpeg2", "mpeg4", "mpg", "mpg4",
    "mpl", "mpls", "mpv", "mpv2", "mts", "mtv", "mxf", "mxu", "nsv", "nut", "ogg", "ogm",
    "ogv", "ogx", "qt", "qtvr", "rm", "rmj", "rmm", "rms", "rmvb", "rmx", "rv", "rvx",
    "sdp", "tod", "trp", "ts", "tsa", "tsv", "tts", "vc1", "vfw", "vob", "vro", "webm",
    "wm", "wmv", "wmx", "x264", "x265", "xvid", "y4m", "yuv",
  )

  /**
   * Returns true if the given file extension is a video file.
   */
  fun isVideoExtension(ext: String): Boolean = ext.lowercase() in videoExtensions
}
