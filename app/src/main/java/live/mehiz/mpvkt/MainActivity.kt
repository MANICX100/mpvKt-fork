package live.mehiz.mpvkt

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import live.mehiz.mpvkt.preferences.AppearancePreferences
import live.mehiz.mpvkt.preferences.preference.collectAsState
import live.mehiz.mpvkt.presentation.Screen
import live.mehiz.mpvkt.ui.home.HomeScreen
import live.mehiz.mpvkt.ui.theme.DarkMode
import live.mehiz.mpvkt.ui.theme.MpvKtTheme
import live.mehiz.mpvkt.ui.utils.LocalBackStack
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
  private val appearancePreferences by inject<AppearancePreferences>()

  private val permissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
  ) { /* Results are handled implicitly; permissions are optional */ }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Request notification permission on Android 13+ (TIRAMISU/API 33+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      val notifPermission = ContextCompat.checkSelfPermission(
        this, Manifest.permission.POST_NOTIFICATIONS
      )
      if (notifPermission != PackageManager.PERMISSION_GRANTED) {
        permissionLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
      }
    }

    // Request READ/WRITE_EXTERNAL_STORAGE for Android 10 and below
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
      val readPermission = ContextCompat.checkSelfPermission(
        this, Manifest.permission.READ_EXTERNAL_STORAGE
      )
      val writePermission = ContextCompat.checkSelfPermission(
        this, Manifest.permission.WRITE_EXTERNAL_STORAGE
      )
      if (readPermission != PackageManager.PERMISSION_GRANTED ||
        writePermission != PackageManager.PERMISSION_GRANTED
      ) {
        permissionLauncher.launch(
          arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
          )
        )
      }
    }

    // For Android 11+, request MANAGE_EXTERNAL_STORAGE via system settings
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
      try {
        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
          data = Uri.fromParts("package", packageName, null)
          addCategory(Intent.CATEGORY_DEFAULT)
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        }
        startActivity(intent)
      } catch (e: Exception) {
        val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION).apply {
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
      }
    }

    setContent {
      val dark by appearancePreferences.darkMode.collectAsState()
      val isSystemInDarkTheme = isSystemInDarkTheme()
      enableEdgeToEdge(
        SystemBarStyle.auto(
          lightScrim = Color.White.toArgb(),
          darkScrim = Color.White.toArgb(),
        ) { dark == DarkMode.Dark || (dark == DarkMode.System && isSystemInDarkTheme) },
      )
      MpvKtTheme { Surface { Navigator() } }
    }
  }

  @Composable
  fun Navigator() {
    val backstack = rememberNavBackStack<Screen>(HomeScreen)
    CompositionLocalProvider(LocalBackStack provides backstack) {
      NavDisplay(
        backStack = backstack,
        onBack = { backstack.removeLastOrNull() },
        entryProvider = { route -> NavEntry(route) { (it as Screen).Content() } },
        popTransitionSpec = {
          fadeIn(animationSpec = tween(220)) +
            slideIn(animationSpec = tween(220)) { IntOffset(-it.width / 2, 0) } togetherWith
            fadeOut(animationSpec = tween(220)) +
              slideOut(animationSpec = tween(220)) { IntOffset(it.width / 2, 0) }
        },
        transitionSpec = {
          fadeIn(animationSpec = tween(220)) +
            slideIn(animationSpec = tween(220)) { IntOffset(it.width / 2, 0) } togetherWith
            fadeOut(animationSpec = tween(220)) +
              slideOut(animationSpec = tween(220)) { IntOffset(-it.width / 2, 0) }
        },
        predictivePopTransitionSpec = {
          (
            fadeIn(animationSpec = tween(220)) +
              scaleIn(
                animationSpec = tween(220, delayMillis = 30),
                initialScale = .9f,
                TransformOrigin(-1f, .5f),
              )
            ).togetherWith(
            fadeOut(animationSpec = tween(220)) +
              scaleOut(animationSpec = tween(220, delayMillis = 30), targetScale = .9f, TransformOrigin(-1f, .5f)),
          )
        },
      )
    }
  }
}
