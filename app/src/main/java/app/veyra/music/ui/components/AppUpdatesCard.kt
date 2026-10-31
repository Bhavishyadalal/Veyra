package app.veyra.music.ui.components

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.veyra.music.R
import app.veyra.music.data.AppUpdateChecker
import app.veyra.music.data.settings.AppSettings
import kotlinx.coroutines.launch

/** Continue the installer after the user returns from Android's source-permission screen. */
@Composable
fun rememberUpdateInstaller(): () -> Unit {
    val context = LocalContext.current
    var pending by rememberSaveable { mutableStateOf(false) }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (pending && context.packageManager.canRequestPackageInstalls()) {
            (AppUpdateChecker.download.value as? AppUpdateChecker.DownloadState.Ready)?.let {
                AppUpdateChecker.installApk(context, it.file)
            }
        }
        pending = false
    }
    return {
        val ready = AppUpdateChecker.download.value as? AppUpdateChecker.DownloadState.Ready
        if (ready != null) {
            if (!context.packageManager.canRequestPackageInstalls()) {
                pending = true
                runCatching { permission.launch(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                    .setData(Uri.parse("package:${context.packageName}"))) }
                    .onFailure { pending = false; AppUpdateChecker.installApk(context, ready.file) }
            } else AppUpdateChecker.installApk(context, ready.file)
        }
    }
}

@Composable
fun AppUpdatesCard() {
    val context = LocalContext.current
    val automatic by AppSettings.autoDownloadUpdates.collectAsStateWithLifecycle()
    val checking by AppUpdateChecker.checking.collectAsStateWithLifecycle()
    val message by AppUpdateChecker.checkMessage.collectAsStateWithLifecycle()
    val available by AppUpdateChecker.available.collectAsStateWithLifecycle()
    val download by AppUpdateChecker.download.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val install = rememberUpdateInstaller()
    val reducedMotion = app.veyra.music.ui.motion.rememberVeyraReducedMotion()
    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.auto_download_updates), style = MaterialTheme.typography.titleSmall)
                Text(stringResource(R.string.auto_download_updates_hint), style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = automatic, enabled = AppUpdateChecker.configured, onCheckedChange = {
                AppSettings.setAutoDownloadUpdates(it)
                if (it) scope.launch { AppUpdateChecker.checkAndPrepare(context, automatic = true) }
            })
        }
        Text(message.ifBlank { "Veyra ${app.veyra.music.BuildConfig.VERSION_NAME}" }, style = MaterialTheme.typography.bodySmall)
        if (checking && !reducedMotion) LinearProgressIndicator(Modifier.fillMaxWidth())
        else if (checking) Text(stringResource(R.string.checking), style = MaterialTheme.typography.bodySmall)
        TextButton(enabled = !checking, onClick = { scope.launch { AppUpdateChecker.checkAndPrepare(context) } }) {
            Text(stringResource(R.string.check_app_updates))
        }
        when (val state = download) {
            is AppUpdateChecker.DownloadState.Downloading -> {
                LinearProgressIndicator(progress = { state.fraction }, modifier = Modifier.fillMaxWidth())
                TextButton(onClick = AppUpdateChecker::cancelDownload) { Text(stringResource(android.R.string.cancel)) }
            }
            is AppUpdateChecker.DownloadState.Ready -> Button(onClick = install) { Text(stringResource(R.string.update_ready_to_install)) }
            else -> {
                if (state is AppUpdateChecker.DownloadState.Failed) Text(state.message, color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall)
                available?.let { update ->
                    TextButton(onClick = {
                        if (update.apkUrl != null) AppUpdateChecker.startDownload(context)
                        else runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(update.releaseUrl))) }
                    }) { Text("Download ${update.version}") }
                }
            }
        }
    }
}
