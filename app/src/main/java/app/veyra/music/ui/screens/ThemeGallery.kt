package app.veyra.music.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.veyra.music.BuildConfig
import app.veyra.music.R
import app.veyra.music.data.themes.*
import app.veyra.music.ui.themes.ThemeStudioArtwork
import app.veyra.music.ui.themes.ThemeStudioRow
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
internal fun ThemeGallery(onDismiss: () -> Unit) {
    val context=LocalContext.current
    val catalog by ThemeRepository.catalog.collectAsStateWithLifecycle()
    val installed by ThemeRepository.installed.collectAsStateWithLifecycle()
    val active by ThemeRepository.active.collectAsStateWithLifecycle()
    val transfer by ThemeRepository.transfer.collectAsStateWithLifecycle()
    val message by ThemeRepository.message.collectAsStateWithLifecycle()
    var pending by rememberSaveable { mutableStateOf<String?>(null) }
    var removing by rememberSaveable { mutableStateOf<String?>(null) }
    var detailId by rememberSaveable { mutableStateOf<String?>(null) }
    var applying by remember { mutableStateOf(false) }
    var restartAfterApply by rememberSaveable { mutableStateOf(false) }
    var installedOnly by rememberSaveable { mutableStateOf(false) }
    val scope=rememberCoroutineScope()
    val focus=androidx.compose.ui.platform.LocalFocusManager.current
    LaunchedEffect(Unit) {focus.clearFocus();ThemeRepository.clearMessage();ThemeRepository.refreshCatalog()}
    val available=remember(catalog,installed) {
        catalog+installed.values.filter { m->catalog.none {it.id==m.id} }.map {m->
            ThemeListing(m.id,m.name,"",m.version,m.minAppCode,"","",0,"")
        }
    }
    val detail=available.firstOrNull {it.id==detailId}
    LaunchedEffect(detailId,available) {if(detailId!=null && detail==null)detailId=null}
    BackHandler(enabled=detailId!=null && !applying) {detailId=null}
    ModalBottomSheet(onDismissRequest={if(!applying)onDismiss()},
        sheetState=rememberModalBottomSheetState(skipPartiallyExpanded=true),containerColor=MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxWidth().heightIn(max=androidx.compose.ui.platform.LocalConfiguration.current.screenHeightDp.dp*.88f)) {
            // Header stays reachable while either collection or details scrolls.
            Row(Modifier.fillMaxWidth().padding(horizontal=12.dp),verticalAlignment=Alignment.CenterVertically) {
                if(detail!=null) IconButton(onClick={detailId=null},enabled=!applying) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack,stringResource(R.string.theme_back_collection))
                }
                Text(stringResource(if(detail==null)R.string.theme_packs else R.string.theme_details),Modifier.weight(1f),style=MaterialTheme.typography.titleLarge)
                if(detail==null) IconButton(onClick={ThemeRepository.refreshCatalog(force=true)},enabled=!applying) {
                    Icon(Icons.Rounded.Refresh,stringResource(R.string.theme_refresh_catalog))
                }
                TextButton(onClick=onDismiss,enabled=!applying) {Text(stringResource(R.string.theme_done))}
            }
            if(detail==null) {
                FlowRow(Modifier.padding(horizontal=20.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected=!installedOnly,onClick={installedOnly=false},label={Text(stringResource(R.string.theme_all))})
                    FilterChip(selected=installedOnly,onClick={installedOnly=true},label={Text(stringResource(R.string.theme_downloaded))})
                }
                LazyColumn(Modifier.fillMaxWidth().weight(1f,fill=false),contentPadding=PaddingValues(horizontal=20.dp,vertical=12.dp),
                    verticalArrangement=Arrangement.spacedBy(8.dp)) {
                    item(key="original") {
                        ThemeStudioRow(stringResource(R.string.theme_original),
                            stringResource(if(active==null)R.string.theme_active else R.string.theme_saved_appearance),null,active==null,
                            onClick={if(active!=null)pending="@original"},enabled=active!=null && !applying)
                    }
                    items(available.filter {!installedOnly || it.id in installed},key={it.id}) { listing->
                        val m=installed[listing.id]
                        val local=ThemeRepository.directory(listing.id)
                        val preview=if(m!=null && local!=null) File(local,m.assets.getValue("preview")) else listing.preview
                        val status=when {
                            transfer?.id==listing.id->stringResource(R.string.theme_downloading)
                            active?.id==listing.id->stringResource(R.string.theme_active)
                            m!=null->stringResource(R.string.theme_downloaded)
                            else->"${"%.1f MB".format(listing.bytes/1048576.0)} · ${stringResource(R.string.theme_discover)}"
                        }
                        ThemeStudioRow(listing.name,status,preview,active?.id==listing.id,onClick={detailId=listing.id},enabled=!applying)
                    }
                    if(installedOnly && installed.isEmpty()) item {Text(stringResource(R.string.theme_no_downloads),style=MaterialTheme.typography.bodyMedium)}
                    if(available.isEmpty()) item {Text(stringResource(R.string.theme_catalog_empty),style=MaterialTheme.typography.bodyMedium)}
                    item(key="icon") {AppIconSetting(initialCollection=IconCollection.THEMES)}
                    message?.let {value->item(key="message") {Text(value,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)}}
                }
            } else {
                val m=installed[detail.id]
                val local=ThemeRepository.directory(detail.id)
                val preview=if(m!=null && local!=null)File(local,m.assets.getValue("preview")) else detail.preview
                val download=transfer?.takeIf {it.id==detail.id}
                LazyColumn(Modifier.fillMaxWidth().weight(1f,fill=false),contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
                    item {ThemeStudioArtwork(detail.name,preview,"v${detail.version}")}
                    if(detail.description.isNotBlank()) item {Text(detail.description,style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)}
                    if(detail.id=="playtime-club") item {Text(stringResource(R.string.theme_playtime_credits),style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)}
                    if(m!=null) item {Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                        listOf(m.palette.accent,m.palette.secondary,m.palette.background,m.palette.raised).forEach {color->
                            Box(Modifier.size(22.dp).background(ThemeRepository.color(color),CircleShape))
                        }
                    }}
                    item {Text(stringResource(R.string.theme_includes),style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.primary)}
                    if(m!=null) item {
                        TextButton(onClick={removing=detail.id},enabled=transfer==null && !applying && active?.id!=detail.id) {
                            Text(stringResource(R.string.theme_remove))
                        }
                    }
                    message?.let {value->item {Text(value,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)}}
                }
                HorizontalDivider()
                Column(Modifier.fillMaxWidth().padding(horizontal=20.dp).padding(top=12.dp,bottom=24.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                    when {
                        download!=null->{
                            val percent=(download.bytes.toFloat()/download.total).coerceIn(0f,1f)
                            LinearProgressIndicator(progress={percent},modifier=Modifier.fillMaxWidth())
                            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                                Text("${(percent*100).toInt()}% · ${stringResource(if(percent>=1f)R.string.theme_verifying else R.string.theme_downloading)}",Modifier.weight(1f),style=MaterialTheme.typography.labelMedium)
                                TextButton(onClick=ThemeRepository::cancelDownload) {Text(stringResource(android.R.string.cancel))}
                            }
                        }
                        active?.id==detail.id && (active?.version ?: 0)>=detail.version->Text(stringResource(R.string.theme_active),color=MaterialTheme.colorScheme.primary)
                        m!=null && m.version>=detail.version->Button(onClick={pending=detail.id},enabled=!applying,modifier=Modifier.fillMaxWidth()) {Text(stringResource(R.string.theme_apply))}
                        else->Button(onClick={ThemeRepository.download(detail)},enabled=transfer==null && detail.minAppCode<=BuildConfig.VERSION_CODE && !applying,modifier=Modifier.fillMaxWidth()) {
                            Text(stringResource(if(detail.minAppCode>BuildConfig.VERSION_CODE)R.string.theme_update_required else if(m!=null)R.string.theme_download_update else R.string.theme_download))
                        }
                    }
                }
            }
        }
    }
    removing?.let {id->
        AlertDialog(onDismissRequest={removing=null},title={Text(stringResource(R.string.theme_remove))},text={Text(stringResource(R.string.theme_remove_description))},
            confirmButton={TextButton(onClick={scope.launch {ThemeRepository.remove(id);removing=null}}) {Text(stringResource(R.string.theme_remove))}},
            dismissButton={TextButton(onClick={removing=null}) {Text(stringResource(android.R.string.cancel))}})
    }
    pending?.let {selection->
        AlertDialog(onDismissRequest={if(!applying)pending=null},title={Text(stringResource(R.string.theme_restart_title))},
            text={Column(verticalArrangement=Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.theme_live_apply_hint))
                Row(Modifier.fillMaxWidth().toggleable(restartAfterApply,enabled=!applying,role=Role.Checkbox,onValueChange={restartAfterApply=it}).heightIn(min=48.dp),verticalAlignment=Alignment.CenterVertically) {
                    Checkbox(restartAfterApply,onCheckedChange=null,enabled=!applying)
                    Text(stringResource(R.string.theme_optional_restart),Modifier.weight(1f),style=MaterialTheme.typography.bodyMedium)
                }
            }},
            confirmButton={TextButton(enabled=!applying,onClick={
                applying=true
                scope.launch {
                    val success=ThemeRepository.apply(selection.takeUnless {it=="@original"})
                    applying=false;pending=null
                    if(success) {onDismiss();if(restartAfterApply)context.activity()?.recreate()}
                }
            }) {Text(stringResource(if(applying)R.string.theme_applying else if(restartAfterApply)R.string.theme_apply_restart else R.string.theme_apply))}},
            dismissButton={TextButton(enabled=!applying,onClick={pending=null}) {Text(stringResource(R.string.theme_later))}})
    }
}
private fun Context.activity(): Activity? = when(this) {
    is Activity->this
    is ContextWrapper->baseContext.activity()
    else->null
}
