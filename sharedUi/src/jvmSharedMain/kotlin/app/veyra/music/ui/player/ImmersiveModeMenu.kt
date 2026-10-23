package app.veyra.music.ui.player

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.veyra.music.data.settings.*
import app.veyra.music.sharedui.resources.*
import org.jetbrains.compose.resources.stringResource

/** A visual mode gallery with a fixed heading and independently scrollable controls. */
@Composable internal fun ImmersiveMenuContent(options:ExperienceOptions,artwork:String?,onOptions:(ExperienceOptions)->Unit,
    onSelectStyle:(ImmersiveStyle)->Unit,onSource:()->Unit,onTiming:()->Unit,onSongOptions:()->Unit,
    onDismiss:()->Unit={}) {
    val largeText=LocalDensity.current.fontScale>=1.4f
    var more by rememberSaveable {mutableStateOf(false)}
    var customize by rememberSaveable {mutableStateOf(false)}
    Column(Modifier.fillMaxWidth().heightIn(max=820.dp)) {
        Column(Modifier.fillMaxWidth().padding(horizontal=20.dp).padding(bottom=16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                if(!largeText) Surface(shape=RoundedCornerShape(16.dp),color=MaterialTheme.colorScheme.primaryContainer) {
                    Icon(Icons.Rounded.AutoAwesome,null,Modifier.padding(11.dp).size(24.dp),tint=MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Text(stringResource(Res.string.immersive_worlds),Modifier.weight(1f),style=MaterialTheme.typography.headlineSmall)
                IconButton(onClick=onDismiss) {
                    Icon(Icons.Rounded.Close,stringResource(Res.string.immersive_close_menu))
                }
            }
            Text(stringResource(Res.string.immersive_worlds_hint),style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
            Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(7.dp)) {
                Icon(Icons.Rounded.CheckCircle,null,Modifier.size(16.dp),tint=MaterialTheme.colorScheme.primary)
                Text(stringResource(Res.string.immersive_now_showing,options.immersiveStyle.title()),
                    style=MaterialTheme.typography.labelLarge,color=MaterialTheme.colorScheme.primary,
                    maxLines=if(largeText)3 else 1,overflow=TextOverflow.Ellipsis)
            }
        }
        HorizontalDivider(color=MaterialTheme.colorScheme.outlineVariant.copy(.35f))
        Column(Modifier.weight(1f,fill=false).fillMaxWidth().verticalScroll(rememberScrollState())
            .padding(horizontal=20.dp).padding(top=18.dp,bottom=28.dp),verticalArrangement=Arrangement.spacedBy(18.dp)) {
            BoxWithConstraints(Modifier.fillMaxWidth().selectableGroup()) {
                val fontScale=LocalDensity.current.fontScale
                val columns=when {
                    fontScale>=1.4f || maxWidth<320.dp -> 1
                    maxWidth>=760.dp -> 3
                    else -> 2
                }
                Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
                    ImmersiveStyle.entries.chunked(columns).forEach { row ->
                        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min),horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                            row.forEach { style -> Box(Modifier.weight(1f).fillMaxHeight()) {
                                ImmersiveModeCard(style,options.immersiveStyle==style,artwork,Modifier.fillMaxHeight()) {onSelectStyle(style)}
                            }}
                            repeat(columns-row.size) {Spacer(Modifier.weight(1f))}
                        }
                    }
                }
            }
            if(options.immersiveStyle==ImmersiveStyle.CLASSIC) {
                ImmersiveMenuSection(Icons.Rounded.Tune,stringResource(Res.string.immersive_customize_classic),customize,{customize=!customize}) {
                    ImmersiveChoices(stringResource(Res.string.immersive_background),ImmersiveBackground.entries,options.immersiveBackground,
                        { value -> stringResource(when(value) {
                            ImmersiveBackground.SONG -> Res.string.immersive_song_background
                            ImmersiveBackground.THEME -> Res.string.immersive_theme_background
                            ImmersiveBackground.BLEND -> Res.string.immersive_blend_background
                            ImmersiveBackground.MINIMAL -> Res.string.immersive_minimal_background
                        }) }) {onOptions(options.copy(immersiveBackground=it))}
                    ImmersiveChoices(stringResource(Res.string.immersive_cover),ImmersiveCover.entries,options.immersiveCover,
                        { value -> stringResource(when(value) {
                            ImmersiveCover.FILL -> Res.string.immersive_fill
                            ImmersiveCover.FIT -> Res.string.immersive_fit
                            ImmersiveCover.ZOOMED -> Res.string.immersive_zoomed
                        }) }) {onOptions(options.copy(immersiveCover=it))}
                    ImmersiveChoices(stringResource(Res.string.immersive_lyrics_position),ImmersiveLyricsPosition.entries,options.immersiveLyrics,
                        { value -> stringResource(when(value) {
                            ImmersiveLyricsPosition.TOP -> Res.string.immersive_top
                            ImmersiveLyricsPosition.CENTER -> Res.string.immersive_center
                            ImmersiveLyricsPosition.BOTTOM -> Res.string.immersive_bottom
                            ImmersiveLyricsPosition.FULL -> Res.string.immersive_full
                        }) }) {onOptions(options.copy(immersiveLyrics=it))}
                }
            }
            ImmersiveMenuSection(Icons.Rounded.Settings,stringResource(Res.string.immersive_more_controls),more,{more=!more}) {
                if(options.immersiveStyle !in listOf(ImmersiveStyle.ARTWORK_CINEMA,ImmersiveStyle.MINIMAL_NIGHT)) {
                    ImmersiveMenuAction(Icons.Rounded.Lyrics,stringResource(Res.string.immersive_lyrics_source),onSource)
                    ImmersiveMenuAction(Icons.Rounded.Timer,stringResource(Res.string.immersive_lyrics_timing),onTiming)
                }
                ImmersiveMenuAction(Icons.Rounded.MusicNote,stringResource(Res.string.immersive_song_options),onSongOptions)
            }
        }
    }
}

@Composable private fun ImmersiveMenuSection(icon:ImageVector,title:String,expanded:Boolean,onClick:()->Unit,
    content:@Composable ColumnScope.()->Unit) {
    Surface(shape=RoundedCornerShape(24.dp),color=MaterialTheme.colorScheme.surfaceContainer,
        border=BorderStroke(1.dp,MaterialTheme.colorScheme.outlineVariant.copy(.35f))) {
        Column {
            Surface(onClick=onClick,color=MaterialTheme.colorScheme.surfaceContainer) {
                Row(Modifier.fillMaxWidth().padding(16.dp),verticalAlignment=Alignment.CenterVertically,
                    horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                    Icon(icon,null,Modifier.size(22.dp),tint=MaterialTheme.colorScheme.primary)
                    Text(title,Modifier.weight(1f),style=MaterialTheme.typography.titleSmall)
                    Icon(if(expanded)Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        stringResource(if(expanded)Res.string.immersive_collapse else Res.string.immersive_expand),Modifier.size(20.dp))
                }
            }
            if(expanded) {
                HorizontalDivider(Modifier.padding(horizontal=16.dp),color=MaterialTheme.colorScheme.outlineVariant.copy(.35f))
                Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp),content=content)
            }
        }
    }
}

@Composable private fun ImmersiveMenuAction(icon:ImageVector,title:String,onClick:()->Unit) {
    Surface(onClick=onClick,shape=RoundedCornerShape(16.dp),color=MaterialTheme.colorScheme.surfaceContainerHigh) {
        Row(Modifier.fillMaxWidth().heightIn(min=52.dp).padding(horizontal=14.dp,vertical=12.dp),
            verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
            Icon(icon,null,Modifier.size(20.dp),tint=MaterialTheme.colorScheme.primary)
            Text(title,Modifier.weight(1f),style=MaterialTheme.typography.bodyMedium)
            Icon(Icons.Rounded.ChevronRight,null,Modifier.size(18.dp),tint=MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
