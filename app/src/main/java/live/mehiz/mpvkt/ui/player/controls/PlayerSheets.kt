package live.mehiz.mpvkt.ui.player.controls

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.vivvvek.seeker.Segment
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import live.mehiz.mpvkt.database.entities.CustomButtonEntity
import live.mehiz.mpvkt.ui.player.Decoder
import live.mehiz.mpvkt.ui.player.Panels
import live.mehiz.mpvkt.ui.player.Sheets
import live.mehiz.mpvkt.ui.player.TrackNode
import live.mehiz.mpvkt.ui.player.controls.components.sheets.AudioTracksSheet
import live.mehiz.mpvkt.ui.player.controls.components.sheets.ChaptersSheet
import live.mehiz.mpvkt.ui.player.controls.components.sheets.DecodersSheet
import live.mehiz.mpvkt.ui.player.controls.components.sheets.MoreSheet
import live.mehiz.mpvkt.ui.player.controls.components.sheets.PlaybackSpeedSheet
import live.mehiz.mpvkt.ui.player.controls.components.sheets.SubtitlesSheet

@Composable
fun PlayerSheets(
  sheetShown: Sheets,

  // subtitles sheet
  subtitles: ImmutableList<TrackNode>,
  onAddSubtitle: (Uri) -> Unit,
  onSelectSubtitle: (Int) -> Unit,
  // audio sheet
  audioTracks: ImmutableList<TrackNode>,
  onAddAudio: (Uri) -> Unit,
  onSelectAudio: (TrackNode) -> Unit,
  // chapters sheet
  chapter: Segment?,
  chapters: ImmutableList<Segment>,
  onSeekToChapter: (Int) -> Unit,
  // Decoders sheet
  decoder: Decoder,
  onUpdateDecoder: (Decoder) -> Unit,
  // Speed sheet
  speed: Float,
  speedPresets: List<Float>,
  onSpeedChange: (Float) -> Unit,
  onAddSpeedPreset: (Float) -> Unit,
  onRemoveSpeedPreset: (Float) -> Unit,
  onResetSpeedPresets: () -> Unit,
  onMakeDefaultSpeed: (Float) -> Unit,
  onResetDefaultSpeed: () -> Unit,
  // More sheet
  sleepTimerTimeRemaining: Int,
  onStartSleepTimer: (Int) -> Unit,
  buttons: ImmutableList<CustomButtonEntity>,

  onOpenPanel: (Panels) -> Unit,
  onDismissRequest: () -> Unit,
) {
  // State for the basic file picker overlay
  var showSubtitleFilePicker by remember { mutableStateOf(false) }
  var showAudioFilePicker by remember { mutableStateOf(false) }

  when (sheetShown) {
    Sheets.None -> {}
    Sheets.SubtitleTracks -> {
      SubtitlesSheet(
        tracks = subtitles.toImmutableList(),
        onSelect = onSelectSubtitle,
        onAddSubtitle = { showSubtitleFilePicker = true },
        onOpenSubtitleSettings = { onOpenPanel(Panels.SubtitleSettings) },
        onOpenSubtitleDelay = { onOpenPanel(Panels.SubtitleDelay) },
        onDismissRequest = onDismissRequest,
      )
      if (showSubtitleFilePicker) {
        BasicFilePicker(
          onFileSelected = { uri ->
            showSubtitleFilePicker = false
            onAddSubtitle(uri)
          },
          onDismiss = { showSubtitleFilePicker = false },
        )
      }
    }

    Sheets.AudioTracks -> {
      AudioTracksSheet(
        tracks = audioTracks,
        onSelect = onSelectAudio,
        onAddAudioTrack = { showAudioFilePicker = true },
        onOpenDelayPanel = { onOpenPanel(Panels.AudioDelay) },
        onDismissRequest,
      )
      if (showAudioFilePicker) {
        BasicFilePicker(
          onFileSelected = { uri ->
            showAudioFilePicker = false
            onAddAudio(uri)
          },
          onDismiss = { showAudioFilePicker = false },
        )
      }
    }

    Sheets.Chapters -> {
      if (chapter == null) return
      ChaptersSheet(
        chapters,
        currentChapter = chapter,
        onClick = { onSeekToChapter(chapters.indexOf(it)) },
        onDismissRequest,
      )
    }

    Sheets.Decoders -> {
      DecodersSheet(
        selectedDecoder = decoder,
        onSelect = onUpdateDecoder,
        onDismissRequest,
      )
    }

    Sheets.More -> {
      MoreSheet(
        remainingTime = sleepTimerTimeRemaining,
        onStartTimer = onStartSleepTimer,
        onDismissRequest = onDismissRequest,
        onEnterFiltersPanel = { onOpenPanel(Panels.VideoFilters) },
        customButtons = buttons,
      )
    }

    Sheets.PlaybackSpeed -> {
      PlaybackSpeedSheet(
        speed,
        onSpeedChange = onSpeedChange,
        speedPresets = speedPresets,
        onAddSpeedPreset = onAddSpeedPreset,
        onRemoveSpeedPreset = onRemoveSpeedPreset,
        onResetPresets = onResetSpeedPresets,
        onMakeDefault = onMakeDefaultSpeed,
        onResetDefault = onResetDefaultSpeed,
        onDismissRequest = onDismissRequest,
      )
    }
  }
}
