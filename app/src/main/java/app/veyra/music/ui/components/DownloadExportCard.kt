package app.veyra.music.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.veyra.music.R
import app.veyra.music.download.DownloadExport
import app.veyra.music.download.DownloadExportService
import app.veyra.music.download.DownloadStore

/** One shared control keeps Settings and the Downloads sheet in sync. */
@Composable
fun DownloadExportCard(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val state by DownloadExport.state.collectAsStateWithLifecycle()
    val reduced = app.veyra.music.ui.motion.rememberVeyraReducedMotion()
    val fraction by animateFloatAsState(
        targetValue = if (state.total > 0) state.processed.toFloat() / state.total else 0f,
        animationSpec = tween(if (reduced) 0 else 180), label = "exportProgress",
    )
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) DownloadExport.start(context)
    }
    Column(modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(stringResource(R.string.export_downloads_description), style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (state.running) {
            if (state.total > 0) LinearProgressIndicator(
                progress = { fraction },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            ) else if (!reduced) LinearProgressIndicator(Modifier.fillMaxWidth().padding(top = 8.dp))
            Text("${state.processed}/${state.total} · ${state.title}", style = MaterialTheme.typography.bodySmall)
            TextButton(onClick = {
                context.startService(android.content.Intent(context, DownloadExportService::class.java)
                    .setAction("app.veyra.music.download.EXPORT_CANCEL"))
            }) { Text(stringResource(android.R.string.cancel)) }
        } else {
            if (state.total > 0) Text(stringResource(R.string.export_downloads_result, state.copied, state.existing, state.failed),
                style = MaterialTheme.typography.bodySmall)
            if (state.message.isNotEmpty()) Text(state.message, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error)
            TextButton(onClick = {
                if (DownloadStore.needsLegacyPermission() && ContextCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    permission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                } else DownloadExport.start(context)
            }) { Text(stringResource(R.string.export_all_downloads)) }
        }
    }
}
