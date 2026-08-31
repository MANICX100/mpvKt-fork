package live.mehiz.mpvkt.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings

/**
 * Utility for checking and requesting MANAGE_EXTERNAL_STORAGE permission
 * needed to access the public SD card config/watch_later directories for Syncthing.
 */
object StoragePermissionHelper {

  /**
   * Returns true if the app has all-files access (MANAGE_EXTERNAL_STORAGE).
   */
  fun hasAllFilesAccess(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      Environment.isExternalStorageManager()
    } else {
      true
    }
  }

  /**
   * Launches the system settings page for the user to grant MANAGE_EXTERNAL_STORAGE.
   */
  fun requestAllFilesAccess(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
        data = Uri.fromParts("package", context.packageName, null)
        addCategory(Intent.CATEGORY_DEFAULT)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
      }
      context.startActivity(intent)
    }
  }
}
