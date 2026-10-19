package app.veyra.music.ui.screens

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.veyra.music.data.home.HomeSectionOption
import app.veyra.music.ui.components.PAGE_GUTTER
import app.veyra.music.ui.player.PlayerSettings
import app.veyra.music.sharedui.resources.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun HomeExperienceHeader(
    offline: Boolean,
    showingSaved: Boolean,
    updating: Boolean,
    notice: String?,
    sections: List<HomeSectionOption>,
    onCustomize: (() -> Unit)?,
    onDownloads: (() -> Unit)?,
    onLocalMusic: (() -> Unit)?,
    reducedMotion: Boolean = false,
) {
    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
    Column(Modifier.fillMaxWidth().padding(horizontal = PAGE_GUTTER), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        if (onCustomize != null) Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(Res.string.home_your_space), Modifier.weight(1f),
                style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextButton(onClick = onCustomize, enabled = sections.isNotEmpty()) {
                Icon(Icons.Rounded.Tune, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(Res.string.home_customize))
            }
        }
        if (offline || showingSaved || updating || notice != null) {
            Row(Modifier.fillMaxWidth().padding(vertical = 6.dp).semantics { liveRegion = LiveRegionMode.Polite },
                horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                if (offline) Icon(Icons.Rounded.CloudOff, null, Modifier.size(18.dp), MaterialTheme.colorScheme.onSurfaceVariant)
                else if (updating && !reducedMotion) CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                else if (updating) Icon(Icons.Rounded.Refresh, null, Modifier.size(18.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(stringResource(when {
                        offline -> Res.string.home_offline_title
                        notice != null -> Res.string.home_refresh_failed
                        showingSaved -> Res.string.home_saved_title
                        else -> Res.string.home_refreshing_title
                    }), style = MaterialTheme.typography.labelLarge)
                    if (offline || showingSaved || notice != null) Text(
                        if (offline) stringResource(Res.string.home_offline_subtitle) else notice ?: stringResource(Res.string.home_saved_subtitle),
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        if (offline) {
            // FlowRow wraps controls at large font scales and on compact screens.
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (onDownloads != null) OutlinedButton(onClick = onDownloads) {
                    Icon(Icons.Rounded.Download, null, Modifier.size(18.dp)); Spacer(Modifier.width(8.dp))
                    Text(stringResource(Res.string.home_open_downloads))
                }
                if (onLocalMusic != null) OutlinedButton(onClick = onLocalMusic) {
                    Icon(Icons.Rounded.Folder, null, Modifier.size(18.dp)); Spacer(Modifier.width(8.dp))
                    Text(stringResource(Res.string.home_open_local_music))
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
    }
}

@Composable
internal fun HomeHiddenSectionsNotice(onCustomize: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(horizontal = PAGE_GUTTER, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(Res.string.home_all_sections_hidden), style = MaterialTheme.typography.titleMedium)
        Text(stringResource(Res.string.home_all_sections_hidden_subtitle),
            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        TextButton(onClick = onCustomize) { Text(stringResource(Res.string.home_customize)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeSectionEditor(
    sections: List<HomeSectionOption>,
    onMove: (String, Int) -> Unit,
    onVisible: (String, Boolean) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit,
) {
    val reduced by PlayerSettings.reduceAnimation.collectAsStateWithLifecycle()
    ModalBottomSheet(onDismissRequest = onDismiss) {
        HomeSectionEditorContent(sections, onMove, onVisible, onReset, onDismiss, reduced)
    }
}

@Composable
internal fun HomeSectionEditorContent(
    sections: List<HomeSectionOption>,
    onMove: (String, Int) -> Unit,
    onVisible: (String, Boolean) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit,
    reducedMotion: Boolean = false,
) {
    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
    Column {
        Column(Modifier.fillMaxWidth().padding(horizontal = PAGE_GUTTER)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(Res.string.home_customize), Modifier.weight(1f),
                    style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                TextButton(onClick = onDismiss) { Text(stringResource(Res.string.home_editor_done)) }
            }
            Text(stringResource(Res.string.home_customize_subtitle), style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextButton(onClick = onReset) { Text(stringResource(Res.string.home_reset_sections)) }
        }
        LazyColumn(Modifier.fillMaxWidth().heightIn(max = 480.dp),
            contentPadding = PaddingValues(start = PAGE_GUTTER, end = PAGE_GUTTER, bottom = 24.dp)) {
            itemsIndexed(sections, key = { _, section -> section.id }) { index, section ->
                val upLabel = stringResource(Res.string.home_move_section_up, section.title)
                val downLabel = stringResource(Res.string.home_move_section_down, section.title)
                val visibilityLabel = stringResource(Res.string.home_show_section, section.title)
                Column(Modifier.animateItem(
                    fadeInSpec = null, fadeOutSpec = null,
                    placementSpec = tween(durationMillis = if (reducedMotion) 0 else 180),
                )) {
                    Text(section.title, Modifier.fillMaxWidth().padding(top = 14.dp, bottom = 2.dp),
                        style = MaterialTheme.typography.titleSmall)
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(if (section.visible) Res.string.home_section_shown else Res.string.home_section_hidden),
                            Modifier.weight(1f), style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        IconButton(onClick = { onMove(section.id, -1) }, enabled = index > 0) {
                            Icon(Icons.Rounded.ArrowUpward, upLabel)
                        }
                        IconButton(onClick = { onMove(section.id, 1) }, enabled = index < sections.lastIndex) {
                            Icon(Icons.Rounded.ArrowDownward, downLabel)
                        }
                        Switch(checked = section.visible, onCheckedChange = { onVisible(section.id, it) },
                            modifier = Modifier.semantics { contentDescription = visibilityLabel })
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = .4f))
                }
            }
        }
    }
    }
}
