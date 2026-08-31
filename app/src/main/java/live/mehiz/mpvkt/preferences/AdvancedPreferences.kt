package live.mehiz.mpvkt.preferences

import live.mehiz.mpvkt.BuildConfig
import live.mehiz.mpvkt.preferences.preference.PreferenceStore

class AdvancedPreferences(preferenceStore: PreferenceStore) {
  val mpvConfStorageUri = preferenceStore.getString("mpv_conf_storage_location_uri")
  val mpvConf = preferenceStore.getString(
    "mpv.conf",
    """
    |--af=scaletempo
    |save-position-on-quit=yes
    |ignore-path-in-watch-later-config=yes
    |watch-later-directory=/storage/emulated/0/mpv/watch_later
    """.trimMargin(),
  )
  val inputConf = preferenceStore.getString("input.conf")

  val verboseLogging = preferenceStore.getBoolean("verbose_logging", BuildConfig.BUILD_TYPE != "release")

  val enabledStatisticsPage = preferenceStore.getInt("enabled_stats_page", 0)
}
