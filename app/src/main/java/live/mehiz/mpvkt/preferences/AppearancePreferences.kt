package live.mehiz.mpvkt.preferences

import android.os.Build
import live.mehiz.mpvkt.preferences.preference.PreferenceStore
import live.mehiz.mpvkt.preferences.preference.getEnum
import live.mehiz.mpvkt.ui.theme.DarkMode

class AppearancePreferences(preferenceStore: PreferenceStore) {
  val darkMode = preferenceStore.getEnum("dark_mode", DarkMode.System)
  // Default to true on Android 12+ (API 31+), enabling the Android 17 Material3 Expressive scheme.
  val materialYou = preferenceStore.getBoolean("material_you", Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
}
