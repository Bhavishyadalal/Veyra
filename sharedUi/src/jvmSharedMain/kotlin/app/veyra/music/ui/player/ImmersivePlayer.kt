package app.veyra.music.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.onSizeChanged
import app.veyra.music.ui.themes.ThemeIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.veyra.music.data.canvas.CanvasArtwork
import app.veyra.music.data.lyrics.LyricLine
import app.veyra.music.data.model.Song
import app.veyra.music.data.settings.*
import app.veyra.music.playback.PlaybackPosition
import app.veyra.music.sharedui.resources.*
import app.veyra.music.ui.themes.ThemeScene
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import kotlin.math.abs

/** A separate window so the system-bar and screen-on lifetime ends exactly with this mode. */
@Composable
expect fun ImmersiveWindow(onDismiss: () -> Unit, content: @Composable () -> Unit)

/** Uses the real playback callbacks and shared lyrics renderer; never creates a second audio player. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ImmersivePlayer(
    song: Song, artwork: String?, canvas: CanvasArtwork?, isPlaying: Boolean,
    position: PlaybackPosition, durationMs: Long, lines: List<LyricLine>, looking: Boolean,
    options: ExperienceOptions, onOptions: (ExperienceOptions) -> Unit, onExit: () -> Unit,
    onPlayPause: () -> Unit, onPrevious: () -> Unit, onNext: () -> Unit,
    hasPrevious: Boolean, hasNext: Boolean, locked: Boolean, onBlocked: () -> Unit,
    onSeek: (Long) -> Unit, onSource: () -> Unit, onTiming: () -> Unit, onSongOptions: () -> Unit,
    windowed: Boolean = true,
) {
    var controls by rememberSaveable { mutableStateOf(true) }
    var settings by rememberSaveable { mutableStateOf(false) }
    val lifecycle by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()
    val active = lifecycle.isAtLeast(Lifecycle.State.RESUMED)
    val reduceMotion by PlayerSettings.reduceAnimation.collectAsStateWithLifecycle()
    val playhead = rememberLyricPlayhead(position)
    val density=LocalDensity.current
    var footerHeight by remember {mutableStateOf(104.dp)}
    val threshold = with(density) { 72.dp.toPx() }
    val latestPrevious by rememberUpdatedState(onPrevious)
    val latestNext by rememberUpdatedState(onNext)
    val latestBlocked by rememberUpdatedState(onBlocked)
    val latestLocked by rememberUpdatedState(locked)
    val latestHasPrevious by rememberUpdatedState(hasPrevious)
    val latestHasNext by rememberUpdatedState(hasNext)
    val accessibility = LocalAccessibilityManager.current
    val timeout = accessibility?.calculateRecommendedTimeoutMillis(5500, containsIcons=true, containsText=true, containsControls=true) ?: 5500
    LaunchedEffect(controls, settings, isPlaying, active, timeout) {
        if (controls && !settings && isPlaying && active) { delay(timeout); controls = false }
    }
    val content: @Composable () -> Unit = {
        // White lyric ink always has a dark local backdrop, even with a light downloaded theme.
        val base = MaterialTheme.colorScheme
        MaterialTheme(colorScheme = base.copy(onSurface = Color.White, onBackground = Color.White,
            onSurfaceVariant = Color.White.copy(alpha = .76f))) {
            Box(Modifier.fillMaxSize().background(Color(0xFF080C12)).pointerInput(Unit) {
                detectTapGestures(onTap = { controls = !controls })
            }.pointerInput(threshold) {
                var drag = 0f
                detectHorizontalDragGestures(onDragStart = { drag = 0f }, onDragCancel = { drag = 0f },
                    onDragEnd = {
                        if (abs(drag) >= threshold) {
                            if (latestLocked) latestBlocked()
                            else if (drag < 0 && latestHasNext) latestNext()
                            else if (drag > 0 && latestHasPrevious) latestPrevious()
                        }
                        drag = 0f
                    }) { change, amount -> change.consume(); drag += amount }
            }) {
                val themeArt = options.immersiveBackground == ImmersiveBackground.THEME || options.immersiveBackground == ImmersiveBackground.BLEND
                val songArt = options.immersiveBackground == ImmersiveBackground.SONG || options.immersiveBackground == ImmersiveBackground.BLEND
                if (themeArt) ThemeScene(Modifier.fillMaxSize(), prominent = true)
                if (songArt) {
                    val artModifier = when {
                        options.immersiveBackground == ImmersiveBackground.BLEND -> Modifier.fillMaxWidth().fillMaxHeight(.65f).align(Alignment.TopCenter)
                        options.immersiveCover == ImmersiveCover.ZOOMED -> Modifier.fillMaxWidth().fillMaxHeight(.75f).align(Alignment.TopCenter)
                        else -> Modifier.fillMaxSize()
                    }
                    val fadeArtwork=options.immersiveBackground==ImmersiveBackground.BLEND || options.immersiveCover==ImmersiveCover.ZOOMED
                    Box(artModifier.then(if(fadeArtwork)Modifier.graphicsLayer {compositingStrategy=CompositingStrategy.Offscreen}.drawWithCache {
                        val mask=Brush.verticalGradient(0f to Color.Black,.65f to Color.Black,1f to Color.Transparent,endY=size.height)
                        onDrawWithContent {drawContent();drawRect(mask,blendMode=BlendMode.DstIn)}
                    } else Modifier)) {
                        AsyncImage(artwork, null, Modifier.fillMaxSize(), contentScale = if (options.immersiveCover == ImmersiveCover.FIT) ContentScale.Fit else ContentScale.Crop)
                        if (canvas != null && !reduceMotion) CanvasArtworkPlayer(canvas, isPlaying && active,
                            Modifier.fillMaxSize(), contentMode = if (options.immersiveCover == ImmersiveCover.FIT) CanvasContentMode.FIT_PORTRAIT else CanvasContentMode.CROP)
                    }
                }
                val shade=when(options.immersiveLyrics) {
                    ImmersiveLyricsPosition.TOP -> listOf(.65f,.6f,.45f)
                    ImmersiveLyricsPosition.CENTER -> listOf(.35f,.65f,.65f)
                    ImmersiveLyricsPosition.FULL -> listOf(.6f,.6f,.65f)
                    else -> listOf(.35f,.55f,.8f)
                }
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(shade.map {Color.Black.copy(alpha=it)})))
                val placement = when(options.immersiveLyrics) {
                    ImmersiveLyricsPosition.TOP -> Alignment.TopCenter
                    ImmersiveLyricsPosition.CENTER, ImmersiveLyricsPosition.FULL -> Alignment.Center
                    ImmersiveLyricsPosition.BOTTOM -> Alignment.BottomCenter
                }
                Box(Modifier.fillMaxSize().safeDrawingPadding().padding(top=76.dp, bottom=footerHeight).padding(horizontal=20.dp).clipToBounds()) {
                    if (lines.isEmpty() && !looking) {
                        Column(Modifier.align(placement).padding(28.dp), verticalArrangement=Arrangement.spacedBy(10.dp)) {
                            Text(song.title, style=MaterialTheme.typography.headlineMedium, color=Color.White)
                            Text(song.artist, style=MaterialTheme.typography.titleMedium, color=Color.White.copy(alpha=.8f))
                            Text(stringResource(Res.string.immersive_no_lyrics), style=MaterialTheme.typography.bodyMedium, color=Color.White.copy(alpha=.7f))
                        }
                    } else LyricsPanel(lines=lines, trackKey=song.videoId, playhead=playhead, looking=looking,
                        isPlaying=isPlaying && active, onSeekToLine={ if(locked)onBlocked() else onSeek(it) },
                        controlsOpen=controls, onRevealControls={controls=true}, onHideControls={controls=false},
                        modifier=Modifier.align(placement).fillMaxWidth().fillMaxHeight(if(options.immersiveLyrics==ImmersiveLyricsPosition.FULL)1f else .58f))
                }
                // Exit is always available, including when the other controls auto-hide.
                IconButton(onClick=onExit, modifier=Modifier.align(Alignment.TopStart).safeDrawingPadding().padding(12.dp).background(Color.Black.copy(alpha=.45f),androidx.compose.foundation.shape.CircleShape)) {
                    ThemeIcon(Icons.AutoMirrored.Rounded.ArrowBack,stringResource(Res.string.immersive_exit),Modifier.size(24.dp),Color.White,"back")
                }
                IconButton(onClick={settings=true;controls=true},modifier=Modifier.align(Alignment.TopEnd).safeDrawingPadding().padding(12.dp).background(Color.Black.copy(alpha=.45f),androidx.compose.foundation.shape.CircleShape)) {
                    ThemeIcon(Icons.Rounded.Tune,stringResource(Res.string.immersive_settings),Modifier.size(24.dp),Color.White,"settings")
                }
                val transport: @Composable () -> Unit = {
                    IconButton(onClick=onPrevious,enabled=hasPrevious && !locked) {ThemeIcon(Icons.Rounded.SkipPrevious,stringResource(Res.string.immersive_previous),Modifier.size(24.dp),Color.White,"previous")}
                    FilledIconButton(onClick=onPlayPause) {ThemeIcon(if(isPlaying)Icons.Rounded.Pause else Icons.Rounded.PlayArrow,stringResource(if(isPlaying)Res.string.immersive_pause else Res.string.immersive_play),Modifier.size(24.dp),MaterialTheme.colorScheme.onPrimary,if(isPlaying)"pause" else "play")}
                    IconButton(onClick=onNext,enabled=hasNext && !locked) {ThemeIcon(Icons.Rounded.SkipNext,stringResource(Res.string.immersive_next),Modifier.size(24.dp),Color.White,"next")}
                }
                if(controls) Surface(Modifier.align(Alignment.BottomCenter).safeDrawingPadding().padding(16.dp).widthIn(max=480.dp)
                    .onSizeChanged {footerHeight=maxOf(104.dp,with(density){it.height.toDp()}+36.dp)},
                    shape=MaterialTheme.shapes.extraLarge,color=Color.Black.copy(alpha=.7f),contentColor=Color.White) {
                    if(density.fontScale>1.35f) Column(Modifier.fillMaxWidth().padding(12.dp),horizontalAlignment=Alignment.CenterHorizontally) {
                        Text(song.title,maxLines=1,overflow=TextOverflow.Ellipsis,style=MaterialTheme.typography.titleMedium)
                        Text(song.artist,maxLines=1,overflow=TextOverflow.Ellipsis,style=MaterialTheme.typography.bodySmall)
                        Row(horizontalArrangement=Arrangement.spacedBy(16.dp),verticalAlignment=Alignment.CenterVertically) {transport()}
                    } else Row(Modifier.padding(horizontal=12.dp,vertical=8.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                        Column(Modifier.weight(1f).padding(start=8.dp)) {
                            Text(song.title,maxLines=1,overflow=TextOverflow.Ellipsis,style=MaterialTheme.typography.titleMedium)
                            Text(song.artist,maxLines=1,overflow=TextOverflow.Ellipsis,style=MaterialTheme.typography.bodySmall)
                        }
                        transport()
                    }
                }
                if(settings) MaterialTheme(colorScheme=base) { ModalBottomSheet(onDismissRequest={settings=false}) {
                    Column(Modifier.fillMaxWidth().heightIn(max=600.dp).verticalScroll(rememberScrollState()).padding(horizontal=24.dp).padding(bottom=32.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
                        Text(stringResource(Res.string.immersive_settings),style=MaterialTheme.typography.headlineSmall)
                        ImmersiveChoice(stringResource(Res.string.immersive_background),options.immersiveBackground.name) {onOptions(options.copy(immersiveBackground=ImmersiveBackground.entries[(options.immersiveBackground.ordinal+1)%ImmersiveBackground.entries.size]))}
                        ImmersiveChoice(stringResource(Res.string.immersive_cover),options.immersiveCover.name) {onOptions(options.copy(immersiveCover=ImmersiveCover.entries[(options.immersiveCover.ordinal+1)%ImmersiveCover.entries.size]))}
                        ImmersiveChoice(stringResource(Res.string.immersive_lyrics_position),options.immersiveLyrics.name) {onOptions(options.copy(immersiveLyrics=ImmersiveLyricsPosition.entries[(options.immersiveLyrics.ordinal+1)%ImmersiveLyricsPosition.entries.size]))}
                        TextButton(onClick={settings=false;onSource()}) { Text(stringResource(Res.string.immersive_lyrics_source)) }
                        TextButton(onClick={settings=false;onTiming()}) { Text(stringResource(Res.string.immersive_lyrics_timing)) }
                        TextButton(onClick={settings=false;onSongOptions()}) { Text(stringResource(Res.string.immersive_song_options)) }
                    }
                } }
            }
        }
    }
    if(windowed)ImmersiveWindow(onExit,content) else content()
}

@Composable private fun ImmersiveChoice(label:String,value:String,onClick:()->Unit) {
    OutlinedButton(onClick=onClick,modifier=Modifier.fillMaxWidth()) {
        Text(label,Modifier.weight(1f)); Text(value.lowercase().replace('_',' '))
    }
}
