package live.mehiz.mpvkt.util

import android.content.Context
import android.os.Environment
import java.io.File

/**
 * Provides publicly accessible directory paths for mpv config and watch_later files,
 * so that Syncthing or any other app can access them.
 *
 * Uses a public directory at /sdcard/mpv/ which is accessible to all apps with
 * MANAGE_EXTERNAL_STORAGE permission and can be synced via Syncthing.
 *
 * Falls back to app-specific external storage if public dir is not accessible.
 */
object ConfigDirs {

  private const val SUBDIR = "mpvktfork"

  /**
   * The public base directory: /sdcard/mpv/
   * Created if it doesn't exist.
   * Falls back to app-specific external files dir if public storage not accessible.
   */
  private fun baseDir(context: Context): File {
    // Try public /sdcard/mpv/ first
    val publicBase = File(Environment.getExternalStorageDirectory(), "mpv")
    if (publicBase.exists() && publicBase.canWrite()) {
      return publicBase
    }
    // Try to create it
    try {
      if (publicBase.mkdirs() || publicBase.exists()) {
        // Test if we can actually write to it
        val testFile = File(publicBase, ".mpvkt_test")
        testFile.createNewFile()
        if (testFile.exists()) {
          testFile.delete()
          return publicBase
        }
      }
    } catch (e: Exception) {
      // Fall through to app-specific storage
    }

    // Fall back to app-specific external storage (always accessible)
    val externalFilesDir = context.getExternalFilesDir(null)
      ?: return File(context.filesDir, SUBDIR).also { it.mkdirs() }

    val base = File(externalFilesDir, SUBDIR)
    if (!base.exists()) base.mkdirs()
    return base
  }

  /**
   * The public config directory where mpv.conf, input.conf, scripts, etc. live.
   */
  fun configDir(context: Context): File {
    val dir = File(baseDir(context), "config")
    if (!dir.exists()) dir.mkdirs()
    return dir
  }

  /**
   * The public watch_later directory where mpv stores resume-position files.
   */
  fun watchLaterDir(context: Context): File {
    val dir = File(baseDir(context), "watch_later")
    if (!dir.exists()) dir.mkdirs()
    return dir
  }

  /**
   * The public cache directory for fonts etc.
   */
  fun cacheDir(context: Context): File {
    val dir = File(File(baseDir(context), "cache"), "fonts")
    if (!dir.exists()) dir.mkdirs()
    return dir
  }

  /**
   * Returns the full path string for the config directory.
   */
  fun configPath(context: Context): String = configDir(context).path

  /**
   * Returns the full path string for the watch_later directory.
   */
  fun watchLaterPath(context: Context): String = watchLaterDir(context).path

  /**
   * Returns the full path string for the cache directory.
   */
  fun cachePath(context: Context): String = cacheDir(context).path
}
