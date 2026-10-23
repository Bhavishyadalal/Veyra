package app.veyra.music.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.veyra.music.R
import app.veyra.music.BuildConfig
import app.veyra.music.data.themes.*
import app.veyra.music.ui.themes.ThemeStudioArtwork
import kotlinx.coroutines.launch

/** Independent picker: removing an override never changes the selected theme. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AssetGallery(onDismiss:()->Unit) {
    val catalog by AssetRepository.catalog.collectAsStateWithLifecycle()
    val installed by AssetRepository.installed.collectAsStateWithLifecycle()
    val active by AssetRepository.active.collectAsStateWithLifecycle()
    val options by AssetRepository.options.collectAsStateWithLifecycle()
    val transfer by AssetRepository.transfer.collectAsStateWithLifecycle()
    val message by AssetRepository.message.collectAsStateWithLifecycle()
    var busy by remember {mutableStateOf(false)}
    val scope=rememberCoroutineScope()
    val context=LocalContext.current
    LaunchedEffect(Unit) {AssetRepository.clearMessage();AssetRepository.refreshCatalog()}
    val entries=remember(catalog,installed) {catalog+installed.values.filter {m->catalog.none {it.id==m.id}}.map {
        AssetListing(it.id,it.name,"",it.version,it.minAppCode,"","",0,"")}}
    ModalBottomSheet(onDismissRequest=onDismiss,sheetState=rememberModalBottomSheetState(skipPartiallyExpanded=true),
        containerColor=MaterialTheme.colorScheme.background) {
        app.veyra.music.ui.themes.ThemeComponentSurface {
        LazyColumn(Modifier.fillMaxWidth().heightIn(max=androidx.compose.ui.platform.LocalConfiguration.current.screenHeightDp.dp*.88f),
            contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
            item {
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                    Text(stringResource(R.string.asset_packs),style=MaterialTheme.typography.headlineSmall)
                    TextButton(onClick=onDismiss) {Text(stringResource(R.string.theme_done))}
                }
                Text(stringResource(R.string.asset_packs_explanation),style=MaterialTheme.typography.bodyMedium)
                TextButton(onClick={AssetRepository.refreshCatalog(true)}) {Text(stringResource(R.string.theme_refresh_catalog))}
            }
            item {
                OutlinedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(active?.name ?: stringResource(R.string.asset_theme_defaults),style=MaterialTheme.typography.titleMedium)
                        Text(stringResource(R.string.asset_layers),style=MaterialTheme.typography.bodySmall)
                        TextButton(enabled=active!=null && !busy,onClick={scope.launch {busy=true;try {AssetRepository.apply(null)}finally {busy=false}}}) {
                            Text(stringResource(R.string.asset_restore))
                        }
                        AssetOptionRow(stringResource(R.string.asset_icons),options.icons) {AssetRepository.setOptions(options.copy(icons=it))}
                        AssetOptionRow(stringResource(R.string.appearance_fonts),options.fonts) {AssetRepository.setOptions(options.copy(fonts=it))}
                        AssetOptionRow(stringResource(R.string.asset_controls),options.controls) {AssetRepository.setOptions(options.copy(controls=it))}
                        AssetOptionRow(stringResource(R.string.asset_motion),options.motion) {AssetRepository.setOptions(options.copy(motion=it))}
                        AssetOptionRow(stringResource(R.string.asset_colors),options.colors) {AssetRepository.setOptions(options.copy(colors=it))}
                        Text(stringResource(R.string.asset_font_priority),style=MaterialTheme.typography.bodySmall)
                    }
                }
            }
            message?.let {text->item {Text(text,style=MaterialTheme.typography.bodySmall)}}
            items(entries,key={it.id}) {entry->
                val local=installed[entry.id]
                val downloading=transfer?.takeIf {it.id==entry.id}
                val selected=active?.id==entry.id && AssetRepository.activeManifest()?.version==local?.version
                val directory=AssetRepository.directory(entry.id)
                val request=remember(entry.id,entry.version,entry.preview,local,directory) {
                    app.veyra.music.ui.themes.themePreviewRequest(context,"asset-${entry.id}",local?.version ?: entry.version,if(local!=null && directory!=null) java.io.File(directory,"previews/specimen.webp") else entry.preview,detail=true)}
                OutlinedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
                        ThemeStudioArtwork(entry.name,request,"ASSETS · v${entry.version}")
                        Text(entry.name,style=MaterialTheme.typography.titleLarge)
                        Text(entry.description,style=MaterialTheme.typography.bodyMedium)
                        if(downloading!=null) {
                            LinearProgressIndicator(progress={if(downloading.total>0)(downloading.bytes.toFloat()/downloading.total).coerceIn(0f,1f) else 0f},modifier=Modifier.fillMaxWidth())
                            TextButton(onClick={AssetRepository.cancelDownload()}) {Text(stringResource(R.string.asset_cancel))}
                        } else Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                            if(local==null || local.version<entry.version) Button(enabled=transfer==null && !busy && entry.minAppCode<=BuildConfig.VERSION_CODE,
                                onClick={AssetRepository.download(entry)}) {Text(stringResource(if(entry.minAppCode>BuildConfig.VERSION_CODE)R.string.asset_update_required else R.string.asset_download))}
                            if(local!=null) {
                                Button(enabled=!selected && !busy,onClick={scope.launch {busy=true;try {AssetRepository.apply(entry.id)}finally {busy=false}}}) {
                                    Text(stringResource(if(selected)R.string.theme_active else R.string.asset_apply))}
                                TextButton(enabled=active?.id!=entry.id && !busy && transfer==null,onClick={scope.launch {busy=true;try {AssetRepository.remove(entry.id)}finally {busy=false}}}) {
                                    Text(stringResource(R.string.asset_remove))}
                            }
                        }
                    }
                }
            }
            if(entries.isEmpty()) item {Text(stringResource(R.string.asset_empty))}
            item {Spacer(Modifier.height(16.dp))}
        }
        }
    }
}

@Composable private fun AssetOptionRow(title:String,checked:Boolean,onChange:(Boolean)->Unit) {
    Row(Modifier.fillMaxWidth(),verticalAlignment=androidx.compose.ui.Alignment.CenterVertically) {
        Text(title,Modifier.weight(1f),style=MaterialTheme.typography.bodyMedium)
        Switch(checked=checked,onCheckedChange=onChange)
    }
}
