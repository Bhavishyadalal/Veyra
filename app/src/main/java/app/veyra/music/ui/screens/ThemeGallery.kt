package app.veyra.music.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.veyra.music.BuildConfig
import app.veyra.music.R
import app.veyra.music.data.themes.ThemeRepository
import coil3.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ThemeGallery(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val catalog by ThemeRepository.catalog.collectAsStateWithLifecycle()
    val installed by ThemeRepository.installed.collectAsStateWithLifecycle()
    val active by ThemeRepository.active.collectAsStateWithLifecycle()
    val transfer by ThemeRepository.transfer.collectAsStateWithLifecycle()
    val message by ThemeRepository.message.collectAsStateWithLifecycle()
    var pending by rememberSaveable { mutableStateOf<String?>(null) }
    var removing by rememberSaveable { mutableStateOf<String?>(null) }
    var applying by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val focus = androidx.compose.ui.platform.LocalFocusManager.current
    LaunchedEffect(Unit) { focus.clearFocus(); ThemeRepository.clearMessage(); ThemeRepository.refreshCatalog() }
    ModalBottomSheet(onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxWidth().weight(1f,fill=false).verticalScroll(rememberScrollState())
            .padding(horizontal=20.dp).padding(bottom=32.dp), verticalArrangement=Arrangement.spacedBy(16.dp)) {
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                Text(stringResource(R.string.theme_packs),Modifier.weight(1f),style=MaterialTheme.typography.headlineMedium)
                TextButton(onClick=onDismiss) { Text(stringResource(R.string.theme_done)) }
            }
            Text(stringResource(R.string.theme_packs_description),style=MaterialTheme.typography.bodyMedium,
                color=MaterialTheme.colorScheme.onSurfaceVariant)
            HorizontalDivider()
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.theme_original),style=MaterialTheme.typography.titleMedium)
                    Text(if (active == null) stringResource(R.string.theme_active) else stringResource(R.string.theme_restore_hint),
                        style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (active != null) TextButton(enabled=!applying,onClick={pending="@original"}) { Text(stringResource(R.string.theme_apply)) }
            }
            val available = catalog + installed.values.filter { m -> catalog.none { it.id == m.id } }.map { m ->
                app.veyra.music.data.themes.ThemeListing(m.id,m.name,"Installed theme, available offline.",
                    m.version,m.minAppCode,"","",0,
                    "")
            }
            available.forEach { listing ->
                val local = ThemeRepository.directory(listing.id)
                val m = installed[listing.id]
                val preview = if (m != null && local != null) File(local,m.assets.getValue("preview")) else listing.preview
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp))
                    .background(MaterialTheme.colorScheme.surface).padding(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
                    Image(rememberAsyncImagePainter(preview),null,
                        Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(14.dp)),contentScale=ContentScale.Crop)
                    Text(listing.name,style=MaterialTheme.typography.titleLarge)
                    Text(listing.description,style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${if (listing.bytes > 0) "%.1f MB".format(listing.bytes/1048576.0) else "Installed theme"} · Veyra theme engine 1 · ${if (m != null) "Saved offline" else "Download once"}",
                        style=MaterialTheme.typography.labelMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
                    val download = transfer?.takeIf { it.id == listing.id }
                    if (download != null) {
                        val percent = (download.bytes.toFloat()/download.total).coerceIn(0f,1f)
                        LinearProgressIndicator(progress={percent},modifier=Modifier.fillMaxWidth())
                        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                            Text("${(percent*100).toInt()}% · ${if(percent>=1f) "Verifying files…" else "Downloading…"}",style=MaterialTheme.typography.labelMedium)
                            TextButton(onClick=ThemeRepository::cancelDownload) { Text(stringResource(android.R.string.cancel)) }
                        }
                    } else if (active?.id == listing.id && (active?.version ?: 0) >= listing.version) {
                        Text(stringResource(R.string.theme_active),color=MaterialTheme.colorScheme.primary,style=MaterialTheme.typography.labelLarge)
                    } else if (m != null && m.version >= listing.version) {
                        Button(onClick={pending=listing.id},enabled=!applying,modifier=Modifier.fillMaxWidth()) { Text(stringResource(R.string.theme_apply)) }
                        TextButton(onClick={removing=listing.id},enabled=transfer==null && !applying && active?.id != listing.id) { Text(stringResource(R.string.theme_remove)) }
                    } else {
                        Button(onClick={ThemeRepository.download(listing)},
                            enabled=transfer==null && listing.minAppCode<=BuildConfig.VERSION_CODE,
                            modifier=Modifier.fillMaxWidth()) {
                            Text(stringResource(if(listing.minAppCode>BuildConfig.VERSION_CODE) R.string.theme_update_required else if(m != null) R.string.theme_download_update else R.string.theme_download))
                        }
                    }
                }
            }
            if (catalog.isEmpty()) Text(stringResource(R.string.theme_catalog_empty),style=MaterialTheme.typography.bodyMedium)
            message?.let { Text(it,style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant) }
            TextButton(onClick={ThemeRepository.refreshCatalog()}) { Text(stringResource(R.string.theme_refresh_catalog)) }
            Text(stringResource(R.string.theme_safety_hint),style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
    removing?.let { id ->
        AlertDialog(onDismissRequest={removing=null}, title={Text(stringResource(R.string.theme_remove))},
            text={Text(stringResource(R.string.theme_remove_description))},
            confirmButton={TextButton(onClick={scope.launch {ThemeRepository.remove(id);removing=null}}) {Text(stringResource(R.string.theme_remove))}},
            dismissButton={TextButton(onClick={removing=null}) {Text(stringResource(android.R.string.cancel))}})
    }
    pending?.let { selection ->
        AlertDialog(onDismissRequest={if(!applying)pending=null},
            title={Text(stringResource(R.string.theme_restart_title))},
            text={Text(stringResource(R.string.theme_restart_description))},
            confirmButton={TextButton(enabled=!applying,onClick={
                applying=true
                scope.launch {
                    val success=ThemeRepository.apply(selection.takeUnless{it=="@original"})
                    applying=false;pending=null
                    if(success){onDismiss();context.activity()?.recreate()}
                }
            }) {Text(stringResource(R.string.theme_apply_restart))}},
            dismissButton={TextButton(enabled=!applying,onClick={pending=null}) {Text(stringResource(R.string.theme_later))}})
    }
}
private fun Context.activity(): Activity? = when(this) {
    is Activity -> this
    is ContextWrapper -> baseContext.activity()
    else -> null
}
