package live.mehiz.mpvkt.ui.player.controls

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.File

/**
 * A custom file picker that browses the filesystem without any file type filtering.
 * Shows a simple list of files and folders. By default uses this basic file view,
 * but also offers a button to launch an external file picker if the user prefers.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicFilePicker(
  onFileSelected: (Uri) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var currentPath by remember {
    mutableStateOf(
      android.os.Environment.getExternalStorageDirectory().absolutePath,
    )
  }
  var files by remember { mutableStateOf(listFiles(currentPath)) }

  // Launcher for external file picker
  val externalPicker = rememberLauncherForActivityResult(
    ActivityResultContracts.StartActivityForResult(),
  ) { result ->
    if (result.resultCode == android.app.Activity.RESULT_OK) {
      result.data?.data?.let { uri ->
        onFileSelected(uri)
      }
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
  ) {
    Column(
      modifier = modifier
        .fillMaxWidth()
        .height(500.dp),
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
          text = "Pick a file",
          style = MaterialTheme.typography.titleMedium,
        )
        OutlinedButton(
          onClick = {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
              type = "*/*"
              addCategory(Intent.CATEGORY_OPENABLE)
            }
            externalPicker.launch(Intent.createChooser(intent, "Pick file with..."))
          },
        ) {
          Icon(Icons.Default.Apps, contentDescription = null, modifier = Modifier.size(18.dp))
          Text(" Other picker", style = MaterialTheme.typography.labelMedium)
        }
      }
      HorizontalDivider()

      // Breadcrumb / back row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        IconButton(
          onClick = {
            val parent = File(currentPath).parentFile
            if (parent != null && parent.canRead()) {
              currentPath = parent.absolutePath
              files = listFiles(currentPath)
            }
          },
          enabled = File(currentPath).parentFile != null,
        ) {
          Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Up")
        }
        Text(
          text = currentPath,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(end = 8.dp),
        )
      }
      HorizontalDivider()

      LazyColumn(
        modifier = Modifier.fillMaxWidth(),
      ) {
        items(files, key = { it.absolutePath }) { file ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                if (file.isDirectory) {
                  currentPath = file.absolutePath
                  files = listFiles(currentPath)
                } else {
                  onFileSelected(Uri.fromFile(file))
                }
              }
              .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Icon(
              imageVector = if (file.isDirectory) Icons.Default.Folder else Icons.Default.InsertDriveFile,
              contentDescription = null,
              modifier = Modifier.size(24.dp),
              tint = if (file.isDirectory) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
              text = file.name,
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.onSurface,
            )
          }
        }
      }
    }
  }
}

private fun listFiles(path: String): List<File> {
  val dir = File(path)
  if (!dir.exists() || !dir.isDirectory) return emptyList()
  return dir.listFiles()
    ?.sortedWith(
      compareByDescending<File> { it.isDirectory }
        .thenBy { it.name.lowercase() },
    )
    ?: emptyList()
}
