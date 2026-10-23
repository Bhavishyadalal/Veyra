package app.veyra.music.ui.screens

import app.veyra.music.ui.themes.ThemeIcon as Icon
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.veyra.music.AppIcon
import app.veyra.music.R
import app.veyra.music.data.themes.*
import app.veyra.music.ui.icons.SettingsIcons
import app.veyra.music.ui.themes.ThemeIconChoice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** A single ordinary Settings row replaces the formerly expanded eighteen-icon grid. */
@Composable
internal fun AppIconSetting() {
    val context=LocalContext.current
    var choosing by rememberSaveable { mutableStateOf(false) }
    val current by AppIcon.selected.collectAsStateWithLifecycle()
    LaunchedEffect(choosing) {
        if(!choosing) withContext(Dispatchers.IO) { AppIcon.current(context) }
    }
    SettingsRow(icon=SettingsIcons.Icons,title=stringResource(R.string.app_icon),
        subtitle=current?.let { stringResource(it.labelRes) } ?: stringResource(R.string.icon_choose),
        onClick={choosing=true})
    if(choosing) AppIconChooser(onDismiss={choosing=false},onChanged={})
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun AppIconChooser(onDismiss: () -> Unit, onChanged: (AppIcon) -> Unit) {
    val context=LocalContext.current
    val textResources = androidx.compose.ui.platform.LocalResources.current
    val scope=rememberCoroutineScope()
    val reducedMotion=app.veyra.music.ui.motion.rememberVeyraReducedMotion()
    var selected by remember { mutableStateOf<AppIcon?>(null) }
    var groupName by rememberSaveable { mutableStateOf(IconCollection.ALL.name) }
    var familyName by rememberSaveable { mutableStateOf(GlassFamily.LIQUID.name) }
    var initialized by rememberSaveable { mutableStateOf(false) }
    var changing by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(Unit) {
        val actual=withContext(Dispatchers.IO) { AppIcon.current(context) }
        selected=actual
        if(!initialized) {
            familyName=(IconPickerPolicy.family(actual) ?: GlassFamily.LIQUID).name
            initialized=true
        }
    }
    val group=IconCollection.valueOf(groupName)
    val family=GlassFamily.valueOf(familyName)
    val options=remember(group,family) { IconPickerPolicy.options(group,family) }
    val visible=options.filter { textResources.getString(it.labelRes).contains(query.trim(),ignoreCase=true) }
    ModalBottomSheet(onDismissRequest={if(!changing)onDismiss()},
        sheetState=rememberModalBottomSheetState(skipPartiallyExpanded=true),containerColor=MaterialTheme.colorScheme.background) {
        app.veyra.music.ui.themes.ThemeComponentSurface {
        Column(Modifier.fillMaxWidth().heightIn(max=LocalConfiguration.current.screenHeightDp.dp*.85f)
            .padding(horizontal=20.dp).padding(bottom=24.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.app_icon),Modifier.weight(1f),style=MaterialTheme.typography.titleLarge)
                TextButton(onClick=onDismiss,enabled=!changing) {Text(stringResource(R.string.theme_done))}
            }
            if(selected==null || changing) {
                if(reducedMotion) LinearProgressIndicator(progress={.65f},modifier=Modifier.fillMaxWidth())
                else LinearProgressIndicator(Modifier.fillMaxWidth())
            }
            selected?.let { current ->
                Text(stringResource(R.string.icon_current_selection,stringResource(current.labelRes)),
                    style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
            }
            OutlinedTextField(value=query,onValueChange={query=it},singleLine=true,enabled=!changing,
                modifier=Modifier.fillMaxWidth(),label={Text(stringResource(R.string.icon_search))},
                leadingIcon={Icon(Icons.Rounded.Search,null)},
                trailingIcon={if(query.isNotEmpty()) IconButton(onClick={query=""},enabled=!changing) {
                    Icon(Icons.Rounded.Close,stringResource(R.string.theme_clear_search))
                }},shape=androidx.compose.foundation.shape.RoundedCornerShape(20.dp))
            LazyVerticalGrid(columns=GridCells.Fixed(when {
                    LocalDensity.current.fontScale>1.8f || LocalConfiguration.current.screenWidthDp<280->1
                    LocalConfiguration.current.screenWidthDp>=600 && LocalDensity.current.fontScale<1.4f->4
                    else->2
                }),
                modifier=Modifier.fillMaxWidth().weight(1f,fill=false).selectableGroup(),
                verticalArrangement=Arrangement.spacedBy(10.dp),horizontalArrangement=Arrangement.spacedBy(10.dp),
                contentPadding=PaddingValues(vertical=8.dp)) {
                item(key="collections",span={GridItemSpan(maxLineSpan)}) {
                    FlowRow(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                        IconCollection.entries.forEach { collection ->
                            FilterChip(selected=collection==group,onClick={groupName=collection.name;query=""},enabled=!changing,
                                label={Text(stringResource(when(collection) {
                                    IconCollection.ALL->R.string.icon_collection_all
                                    IconCollection.VEYRA->R.string.icon_collection_veyra
                                    IconCollection.GLASS->R.string.icon_collection_glass
                                    IconCollection.THEMES->R.string.icon_collection_themes
                                    IconCollection.ATELIER->R.string.icon_collection_atelier
                                    IconCollection.ASSETS->R.string.icon_collection_assets
                                }))})
                        }
                    }
                }
                if(group==IconCollection.GLASS) item(key="families",span={GridItemSpan(maxLineSpan)}) {
                    FlowRow(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                        GlassFamily.entries.forEach { f ->
                            FilterChip(selected=f==family,onClick={familyName=f.name;query=""},enabled=!changing,
                                label={Text(stringResource(when(f) {
                                    GlassFamily.LIQUID->R.string.icon_family_liquid
                                    GlassFamily.SAMSUNG->R.string.icon_family_samsung
                                    GlassFamily.PIXEL->R.string.icon_family_pixel
                                }))})
                        }
                    }
                }
                if(visible.isEmpty()) item(key="empty",span={GridItemSpan(maxLineSpan)}) {
                    Text(stringResource(R.string.icon_no_search_results),style=MaterialTheme.typography.bodyMedium)
                }
                items(visible,key={it.alias}) { option ->
                    ThemeIconChoice(stringResource(option.labelRes),selected==option,!changing && selected!=null,
                        onClick={if(selected!=option) {
                            changing=true;error=false
                            scope.launch {
                                val success=withContext(Dispatchers.IO) { runCatching { AppIcon.apply(context,option) }.isSuccess }
                                changing=false
                                if(success) {
                                    selected=option;onChanged(option)
                                    android.widget.Toast.makeText(context,textResources.getString(R.string.app_icon_changed),android.widget.Toast.LENGTH_SHORT).show()
                                } else error=true
                            }
                        }}) {
                        Image(app.veyra.music.ui.theme.rememberLauncherIconPainter(option),null,Modifier.fillMaxSize(),contentScale=ContentScale.Fit)
                    }
                }
                if(error) item(key="error",span={GridItemSpan(maxLineSpan)}) {
                    Text(stringResource(R.string.app_icon_failed),style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.error)
                }
                item(key="hint",span={GridItemSpan(maxLineSpan)}) {
                    Text(stringResource(R.string.icon_chooser_hint),style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        }
    }
}
