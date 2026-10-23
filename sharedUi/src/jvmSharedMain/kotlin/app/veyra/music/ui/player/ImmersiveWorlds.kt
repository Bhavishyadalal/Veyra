package app.veyra.music.ui.player

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.veyra.music.data.lyrics.LyricLine
import app.veyra.music.data.model.Song
import app.veyra.music.data.settings.*
import app.veyra.music.playback.PlaybackPosition
import app.veyra.music.sharedui.resources.*
import app.veyra.music.ui.themes.LocalThemeVisuals
import app.veyra.music.ui.themes.ThemeScene
import app.veyra.music.ui.themes.ThemeIcon
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.stringResource
import kotlin.math.min

@Composable internal fun ImmersiveStyle.title(): String = stringResource(when(this) {
    ImmersiveStyle.CLASSIC -> Res.string.immersive_classic
    ImmersiveStyle.ARTWORK_CINEMA -> Res.string.immersive_cinema
    ImmersiveStyle.THEME_WORLD -> Res.string.immersive_theme_world
    ImmersiveStyle.LYRICS_FOCUS -> Res.string.immersive_lyrics_focus
    ImmersiveStyle.MINIMAL_NIGHT -> Res.string.immersive_night
    ImmersiveStyle.LANDSCAPE_STAGE -> Res.string.immersive_stage
})
@Composable private fun ImmersiveStyle.description(): String = stringResource(when(this) {
    ImmersiveStyle.CLASSIC -> Res.string.immersive_classic_hint
    ImmersiveStyle.ARTWORK_CINEMA -> Res.string.immersive_cinema_hint
    ImmersiveStyle.THEME_WORLD -> Res.string.immersive_theme_hint
    ImmersiveStyle.LYRICS_FOCUS -> Res.string.immersive_lyrics_hint
    ImmersiveStyle.MINIMAL_NIGHT -> Res.string.immersive_night_hint
    ImmersiveStyle.LANDSCAPE_STAGE -> Res.string.immersive_stage_hint
})
internal fun ImmersiveStyle.symbol(): ImageVector = when(this) {
    ImmersiveStyle.CLASSIC -> Icons.Rounded.AutoAwesome
    ImmersiveStyle.ARTWORK_CINEMA -> Icons.Rounded.Movie
    ImmersiveStyle.THEME_WORLD -> Icons.Rounded.Palette
    ImmersiveStyle.LYRICS_FOCUS -> Icons.Rounded.Lyrics
    ImmersiveStyle.MINIMAL_NIGHT -> Icons.Rounded.DarkMode
    ImmersiveStyle.LANDSCAPE_STAGE -> Icons.Rounded.Panorama
}
internal fun ImmersiveStyle.ink(themeAccent: Color): Color = when(this) {
    ImmersiveStyle.LYRICS_FOCUS -> Color(0xFFD7BBFF)
    ImmersiveStyle.MINIMAL_NIGHT -> Color(0xFFC9DEFF)
    ImmersiveStyle.LANDSCAPE_STAGE -> Color(0xFFFFD18C)
    ImmersiveStyle.ARTWORK_CINEMA -> Color(0xFFE0E8FF)
    else -> themeAccent
}

/** Art and lyric presentations only: transport, window and playback ownership stay shared. */
@Composable internal fun ImmersiveWorld(
    style: ImmersiveStyle, song: Song, artwork: String?, position: PlaybackPosition,
    durationMs: Long, lines: List<LyricLine>, looking: Boolean, isPlaying: Boolean,
    controls: Boolean, locked: Boolean, onBlocked: () -> Unit, onSeek: (Long) -> Unit,
    onReveal: () -> Unit, onHide: () -> Unit, bottomSpace: androidx.compose.ui.unit.Dp,
) {
    val theme = LocalThemeVisuals.current
    val ink = style.ink(MaterialTheme.colorScheme.primary)
    val playhead = rememberLyricPlayhead(position)
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val wide = maxWidth >= 600.dp && maxWidth > maxHeight
        val compact = maxHeight < 620.dp
        val edge = if(compact) 12.dp else 24.dp
        val artSize = if(compact) 112.dp else 196.dp
        // A bounded native gradient is also the offline/missing-artwork fallback.
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF17223D), Color(0xFF06090F)))))
        when(style) {
            ImmersiveStyle.ARTWORK_CINEMA -> {
                AsyncImage(artwork, null, Modifier.fillMaxSize(), contentScale=ContentScale.Crop)
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Black.copy(.12f),Color.Black.copy(.2f),Color.Black.copy(.93f)))))
            }
            ImmersiveStyle.THEME_WORLD -> {
                ThemeScene(Modifier.fillMaxSize(), prominent=true)
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Black.copy(.3f),Color.Black.copy(.12f),Color.Black.copy(.82f)))))
            }
            ImmersiveStyle.LYRICS_FOCUS -> {
                // Static ribbon-like light fields: no screen-sized blur, ticker or video decoder.
                Box(Modifier.fillMaxSize().background(Brush.radialGradient(listOf(Color(0xFF382252),Color(0xFF080910)))))
            }
            ImmersiveStyle.MINIMAL_NIGHT -> Box(Modifier.fillMaxSize().background(Color(0xFF030508)))
            else -> Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Color(0xFF15213C),Color(0xFF070B14)))))
        }
        val sleeve: @Composable (Modifier) -> Unit = { modifier ->
            Box(modifier.clip(RoundedCornerShape(if(style==ImmersiveStyle.MINIMAL_NIGHT) 1000.dp else 28.dp))
                .background(Color.White.copy(.06f)).border(1.dp,ink.copy(.25f),RoundedCornerShape(if(style==ImmersiveStyle.MINIMAL_NIGHT) 1000.dp else 28.dp)),contentAlignment=Alignment.Center) {
                ThemeIcon(Icons.Rounded.MusicNote,null,Modifier.size(42.dp),ink,"music")
                AsyncImage(artwork,null,Modifier.fillMaxSize(),contentScale=ContentScale.Crop)
            }
        }
        val identity: @Composable () -> Unit = {
            Text(song.title,style=if(compact)MaterialTheme.typography.headlineSmall else MaterialTheme.typography.headlineLarge,
                maxLines=2,overflow=TextOverflow.Ellipsis,color=Color.White,textAlign=TextAlign.Center)
            Text(song.artist,style=MaterialTheme.typography.titleMedium,maxLines=1,overflow=TextOverflow.Ellipsis,color=Color.White.copy(.72f))
        }
        val lyrics: @Composable (Modifier) -> Unit = { modifier ->
            if(lines.isEmpty() && !looking) Box(modifier,contentAlignment=Alignment.Center) {
                Text(stringResource(Res.string.immersive_no_lyrics),Modifier.padding(16.dp),style=MaterialTheme.typography.bodyLarge,color=Color.White.copy(.65f),textAlign=TextAlign.Center)
            } else LyricsPanel(lines=lines,trackKey=song.videoId,playhead=playhead,looking=looking,
                isPlaying=isPlaying,onSeekToLine={if(locked)onBlocked() else onSeek(it)},controlsOpen=controls,
                onRevealControls=onReveal,onHideControls=onHide,modifier=modifier)
        }
        Box(Modifier.fillMaxSize().safeDrawingPadding().padding(top=76.dp,bottom=bottomSpace).padding(horizontal=edge)) {
            when(style) {
                ImmersiveStyle.ARTWORK_CINEMA -> Column(Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(bottom=20.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(8.dp)) {identity()}
                ImmersiveStyle.MINIMAL_NIGHT -> Column(Modifier.align(Alignment.Center).fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(if(compact)12.dp else 24.dp)) {
                    Box(Modifier.size(if(compact)140.dp else 236.dp),contentAlignment=Alignment.Center) {
                        OrbitalProgress(position,durationMs,ink,Modifier.fillMaxSize())
                        sleeve(Modifier.size(if(compact)104.dp else 176.dp))
                    }
                    identity()
                }
                ImmersiveStyle.LANDSCAPE_STAGE -> if(wide) Row(Modifier.fillMaxSize(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(28.dp)) {
                    Box(Modifier.weight(.85f).fillMaxHeight(),contentAlignment=Alignment.Center) { sleeve(Modifier.fillMaxSize().aspectRatio(1f,matchHeightConstraintsFirst=true)) }
                    Column(Modifier.weight(1.15f).fillMaxHeight(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(8.dp)) {
                        identity(); lyrics(Modifier.fillMaxWidth().weight(1f))
                    }
                } else Column(Modifier.fillMaxSize(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(12.dp)) {
                    sleeve(Modifier.size(artSize));identity()
                    Text(stringResource(Res.string.immersive_rotate_hint),style=MaterialTheme.typography.labelMedium,color=ink)
                    lyrics(Modifier.fillMaxWidth().weight(1f))
                }
                else -> Column(Modifier.fillMaxSize(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(12.dp)) {
                    if(style==ImmersiveStyle.THEME_WORLD) {
                        sleeve(Modifier.size(if(compact)72.dp else 112.dp));identity()
                        if(theme==null) Text(stringResource(Res.string.immersive_theme_empty),color=ink,style=MaterialTheme.typography.labelMedium,textAlign=TextAlign.Center)
                    } else Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(14.dp)) {
                        sleeve(Modifier.size(56.dp))
                        Column(Modifier.weight(1f)) {
                            Text(song.title,color=Color.White,style=MaterialTheme.typography.titleLarge,maxLines=1,overflow=TextOverflow.Ellipsis)
                            Text(song.artist,color=ink.copy(.8f),style=MaterialTheme.typography.bodyMedium,maxLines=1,overflow=TextOverflow.Ellipsis)
                        }
                    }
                    lyrics(Modifier.fillMaxWidth().weight(1f))
                }
            }
        }
    }
}

/** Reads the ticking playhead in drawing only, never at the full-screen composition root. */
@Composable private fun OrbitalProgress(position: PlaybackPosition,durationMs: Long,ink: Color,modifier: Modifier) {
    Canvas(modifier) {
        val diameter=min(size.width,size.height)-12.dp.toPx()
        val origin=Offset((size.width-diameter)/2,(size.height-diameter)/2)
        drawArc(ink.copy(.14f),-90f,360f,false,origin,Size(diameter,diameter),style=Stroke(1.dp.toPx()))
        val fraction=if(durationMs>0)(position.positionMs.toDouble()/durationMs).coerceIn(0.0,1.0).toFloat() else 0f
        drawArc(ink,-90f,360f*fraction,false,origin,Size(diameter,diameter),style=Stroke(2.dp.toPx(),cap=androidx.compose.ui.graphics.StrokeCap.Round))
    }
}

@Composable internal fun ImmersiveModeCard(style: ImmersiveStyle,selected: Boolean,artwork:String?=null,onClick: () -> Unit) {
    val ink=style.ink(MaterialTheme.colorScheme.primary)
    Surface(onClick=onClick,shape=RoundedCornerShape(24.dp),color=if(selected)MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
        border=androidx.compose.foundation.BorderStroke(if(selected)2.dp else 1.dp,if(selected)MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),modifier=Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(16.dp)) {
            ImmersiveMiniature(style,artwork,ink)
            Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(4.dp)) {
                Text(style.title(),style=MaterialTheme.typography.titleMedium)
                Text(style.description(),style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
            }
            RadioButton(selected=selected,onClick=null,modifier=Modifier.size(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable internal fun ImmersiveTimeline(songKey: String,position: PlaybackPosition,durationMs: Long,locked: Boolean,
    ink: Color,onSeek: (Long)->Unit,onScrubbing:(Boolean)->Unit) {
    var drag by remember(songKey,durationMs) {mutableStateOf<Float?>(null)}
    val fraction=if(durationMs>0)(position.positionMs.toDouble()/durationMs).coerceIn(0.0,1.0).toFloat() else 0f
    val label=stringResource(Res.string.immersive_seek)
    val shown=drag ?: fraction
    Column {
        Slider(value=shown,onValueChange={drag=it;onScrubbing(true)},onValueChangeFinished={
            drag?.let {if(!locked && durationMs>0)onSeek((it.toDouble()*durationMs).toLong().coerceIn(0,durationMs))}
            drag=null;onScrubbing(false)
        },enabled=!locked && durationMs>0,modifier=Modifier.fillMaxWidth().height(32.dp).semantics {contentDescription=label},
            colors=SliderDefaults.colors(thumbColor=ink,activeTrackColor=ink,inactiveTrackColor=Color.White.copy(.18f)),
            thumb={ Box(Modifier.size(12.dp).background(if(locked)ink.copy(.35f) else ink,CircleShape)) },
            track={ state -> Canvas(Modifier.fillMaxWidth().height(4.dp)) {
                val rtl=layoutDirection==androidx.compose.ui.unit.LayoutDirection.Rtl
                val start=if(rtl)size.width else 0f
                val end=if(rtl)0f else size.width
                val y=size.height/2
                drawLine(Color.White.copy(.18f),Offset(start,y),Offset(end,y),size.height,androidx.compose.ui.graphics.StrokeCap.Round)
                drawLine(if(locked)ink.copy(.35f) else ink,Offset(start,y),Offset(start+(end-start)*state.value.coerceIn(0f,1f),y),size.height,androidx.compose.ui.graphics.StrokeCap.Round)
            } })
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
            Text(immersiveTime(if(drag!=null)(shown.toDouble()*durationMs).toLong() else position.positionMs),style=MaterialTheme.typography.labelSmall,color=Color.White.copy(.75f))
            Text(if(durationMs>0)immersiveTime(durationMs) else stringResource(Res.string.immersive_live),style=MaterialTheme.typography.labelSmall,color=Color.White.copy(.75f))
        }
    }
    DisposableEffect(songKey,durationMs) {onDispose {onScrubbing(false)}}
}
private fun immersiveTime(ms: Long):String {
    val seconds=ms.coerceAtLeast(0)/1000
    val minutes=seconds/60
    return if(minutes>=60)"${minutes/60}:${(minutes%60).toString().padStart(2,'0')}:${(seconds%60).toString().padStart(2,'0')}"
        else "$minutes:${(seconds%60).toString().padStart(2,'0')}"
}

/** A static mode-specific composition at a bounded 96px request size. */
@Composable private fun ImmersiveMiniature(style:ImmersiveStyle,artwork:String?,ink:Color) {
    val context=coil3.compose.LocalPlatformContext.current
    val theme=LocalThemeVisuals.current
    val art=if(style==ImmersiveStyle.THEME_WORLD) theme?.asset("header")?.let {java.io.File(it)} else artwork
    val request=remember(art,context) {coil3.request.ImageRequest.Builder(context).data(art).size(96).build()}
    Box(Modifier.size(width=78.dp,height=104.dp).clip(RoundedCornerShape(14.dp)).background(Brush.verticalGradient(listOf(Color(0xFF25213D),Color(0xFF080B11))))) {
        when(style) {
            ImmersiveStyle.ARTWORK_CINEMA,ImmersiveStyle.THEME_WORLD,ImmersiveStyle.CLASSIC -> {
                ThemeIcon(style.symbol(),null,Modifier.size(28.dp).align(Alignment.Center),ink.copy(.7f))
                AsyncImage(request,null,Modifier.fillMaxSize(),contentScale=ContentScale.Crop,alpha=if(style==ImmersiveStyle.THEME_WORLD).6f else .85f)
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent,Color.Black.copy(.8f)))))
            }
            ImmersiveStyle.MINIMAL_NIGHT -> Box(Modifier.size(48.dp).align(Alignment.Center).border(1.dp,ink.copy(.5f),CircleShape).padding(6.dp).clip(CircleShape)) {
                AsyncImage(request,null,Modifier.fillMaxSize(),contentScale=ContentScale.Crop)
            }
            ImmersiveStyle.LANDSCAPE_STAGE -> Row(Modifier.align(Alignment.Center).padding(8.dp),horizontalArrangement=Arrangement.spacedBy(5.dp)) {
                AsyncImage(request,null,Modifier.size(28.dp,40.dp).clip(RoundedCornerShape(5.dp)),contentScale=ContentScale.Crop)
                Column(verticalArrangement=Arrangement.spacedBy(5.dp)) {repeat(3){Box(Modifier.size(20.dp,3.dp).background(ink.copy(alpha=1f-it*.25f),CircleShape))}}
            }
            ImmersiveStyle.LYRICS_FOCUS -> Column(Modifier.align(Alignment.Center).padding(12.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                repeat(3){Box(Modifier.fillMaxWidth().height(if(it==1)5.dp else 3.dp).background(ink.copy(if(it==1)1f else .3f),CircleShape))}
            }
        }
        Column(Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(8.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(5.dp)) {
            Box(Modifier.fillMaxWidth().height(2.dp).background(ink.copy(.6f),CircleShape))
            Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                ThemeIcon(Icons.Rounded.SkipPrevious,null,Modifier.size(8.dp),ink)
                Box(Modifier.size(14.dp).background(ink,CircleShape),contentAlignment=Alignment.Center) {
                    ThemeIcon(Icons.Rounded.PlayArrow,null,Modifier.size(9.dp),Color.Black)
                }
                ThemeIcon(Icons.Rounded.SkipNext,null,Modifier.size(8.dp),ink)
            }
        }
    }
}
