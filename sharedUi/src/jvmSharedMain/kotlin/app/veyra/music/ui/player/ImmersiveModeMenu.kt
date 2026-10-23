package app.veyra.music.ui.player

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import app.veyra.music.data.settings.*
import app.veyra.music.sharedui.resources.*
import app.veyra.music.ui.themes.ThemeIcon
import org.jetbrains.compose.resources.stringResource

/** Testable sheet body; thumbnails are small native compositions, not additional video players. */
@Composable internal fun ImmersiveMenuContent(options:ExperienceOptions,artwork:String?,onOptions:(ExperienceOptions)->Unit,
    onSelectStyle:(ImmersiveStyle)->Unit,onSource:()->Unit,onTiming:()->Unit,onSongOptions:()->Unit) {
    var more by rememberSaveable {mutableStateOf(false)}
    Column(Modifier.fillMaxWidth().heightIn(max=600.dp).verticalScroll(rememberScrollState()).padding(horizontal=20.dp).padding(bottom=32.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
            ThemeIcon(Icons.Rounded.AutoAwesome,null,Modifier.size(32.dp),MaterialTheme.colorScheme.primary)
            Column {
                Text(stringResource(Res.string.immersive_worlds),style=MaterialTheme.typography.headlineSmall)
                Text(stringResource(Res.string.immersive_worlds_hint),style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Surface(shape=MaterialTheme.shapes.large,color=MaterialTheme.colorScheme.secondaryContainer) {
            Row(Modifier.fillMaxWidth().padding(14.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                ThemeIcon(Icons.Rounded.CheckCircle,null,Modifier.size(20.dp))
                Text(options.immersiveStyle.title(),style=MaterialTheme.typography.titleMedium)
            }
        }
        BoxWithConstraints {
            val columns=if(maxWidth>=600.dp && androidx.compose.ui.platform.LocalDensity.current.fontScale<1.4f)2 else 1
            Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
                ImmersiveStyle.entries.chunked(columns).forEach { row ->
                    Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                        row.forEach { style -> Box(Modifier.weight(1f)) {
                            ImmersiveModeCard(style,options.immersiveStyle==style,artwork) {onSelectStyle(style)}
                        }}
                    }
                }
            }
        }
                        if(options.immersiveStyle==ImmersiveStyle.CLASSIC) {
                            HorizontalDivider()
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

        HorizontalDivider()
        TextButton(onClick={more=!more},modifier=Modifier.fillMaxWidth()) {
            Text(stringResource(Res.string.immersive_more_controls),Modifier.weight(1f))
            ThemeIcon(if(more)Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,null,Modifier.size(20.dp))
        }
        if(more) Column(Modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(4.dp)) {
            if(options.immersiveStyle !in listOf(ImmersiveStyle.ARTWORK_CINEMA,ImmersiveStyle.MINIMAL_NIGHT)) {
                TextButton(onClick=onSource,modifier=Modifier.fillMaxWidth()) {Text(stringResource(Res.string.immersive_lyrics_source))}
                TextButton(onClick=onTiming,modifier=Modifier.fillMaxWidth()) {Text(stringResource(Res.string.immersive_lyrics_timing))}
            }
            TextButton(onClick=onSongOptions,modifier=Modifier.fillMaxWidth()) {Text(stringResource(Res.string.immersive_song_options))}
        }
    }
}
