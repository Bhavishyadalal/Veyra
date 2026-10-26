package app.veyra.music.ui.player

import app.veyra.music.ui.themes.ThemeIcon as Icon
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
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
    playPauseActive: Boolean = isPlaying,
) {
    var controls by rememberSaveable { mutableStateOf(true) }
    var settings by rememberSaveable { mutableStateOf(false) }
    var scrubbing by remember { mutableStateOf(false) }
    var interaction by remember { mutableIntStateOf(0) }
    val revealControls: () -> Unit = { controls = true; interaction++ }
    val onScrubbing: (Boolean) -> Unit = {
        scrubbing = it
        // Retiring a hidden seek bar must never reveal the footer again.
        if (it) revealControls()
    }
    val lifecycle by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()
    val active = lifecycle.isAtLeast(Lifecycle.State.RESUMED)
    val reduceMotion by PlayerSettings.reduceAnimation.collectAsStateWithLifecycle()
    val playhead = rememberLyricPlayhead(position)
    val density=LocalDensity.current
    var footerHeight by remember {mutableStateOf(210.dp)}
    val threshold = with(density) { 72.dp.toPx() }
    val latestPrevious by rememberUpdatedState(onPrevious)
    val latestNext by rememberUpdatedState(onNext)
    val latestBlocked by rememberUpdatedState(onBlocked)
    val latestLocked by rememberUpdatedState(locked)
    val latestHasPrevious by rememberUpdatedState(hasPrevious)
    val latestHasNext by rememberUpdatedState(hasNext)
    val accessibility = LocalAccessibilityManager.current
    val timeout = accessibility?.calculateRecommendedTimeoutMillis(5500, containsIcons=true, containsText=true, containsControls=true) ?: 5500
    LaunchedEffect(controls, settings, scrubbing, isPlaying, active, timeout, interaction) {
        if (controls && !settings && !scrubbing && isPlaying && active) { delay(timeout); controls = false }
    }
    val content: @Composable () -> Unit = {
        // White lyric ink always has a dark local backdrop, even with a light downloaded theme.
        val base = MaterialTheme.colorScheme
        MaterialTheme(colorScheme = base.copy(onSurface = Color.White, onBackground = Color.White,
            onSurfaceVariant = Color.White.copy(alpha = .76f))) {
            BoxWithConstraints(Modifier.fillMaxSize().background(Color(0xFF080C12)).pointerInput(Unit) {
                detectTapGestures(onTap = { if (controls) controls = false else revealControls() })
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
                val wide = maxWidth >= 600.dp && maxWidth > maxHeight
                val ink = options.immersiveStyle.ink(base.primary)
                val deckColor = options.immersiveStyle.deckColor()
                if (options.immersiveStyle != ImmersiveStyle.CLASSIC) {
                    ImmersiveWorld(options.immersiveStyle,song,artwork,position,durationMs,lines,looking,isPlaying && active,
                        controls,locked,onBlocked,onSeek,revealControls,{controls=false},footerHeight)
                } else {
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
                        if (canvas != null && !reduceMotion && !options.dataSaver) CanvasArtworkPlayer(canvas, isPlaying && active,
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
                        controlsOpen=controls, onRevealControls=revealControls, onHideControls={controls=false},
                        modifier=Modifier.align(placement).fillMaxWidth().fillMaxHeight(if(options.immersiveLyrics==ImmersiveLyricsPosition.FULL)1f else .58f))
                }
                }
                // Exit is always available, including when the other controls auto-hide.
                IconButton(onClick=onExit, modifier=Modifier.align(Alignment.TopStart).safeDrawingPadding().padding(12.dp).background(Color.Black.copy(alpha=.45f),androidx.compose.foundation.shape.CircleShape)) {
                    ThemeIcon(Icons.AutoMirrored.Rounded.ArrowBack,stringResource(Res.string.immersive_exit),Modifier.size(24.dp),Color.White,"back")
                }
                FilledTonalButton(onClick={settings=true;revealControls()},modifier=Modifier.align(Alignment.TopEnd).safeDrawingPadding().padding(12.dp).widthIn(max=240.dp),
                    colors=ButtonDefaults.filledTonalButtonColors(containerColor=Color.Black.copy(.65f),contentColor=Color.White)) {
                    ThemeIcon(options.immersiveStyle.symbol(),null,Modifier.size(20.dp),ink)
                    Spacer(Modifier.width(8.dp))
                    Text(options.immersiveStyle.title(),maxLines=1,overflow=TextOverflow.Ellipsis)
                }
                val transport: @Composable () -> Unit = {
                    IconButton(onClick={revealControls();onPrevious()},enabled=hasPrevious && !locked,modifier=Modifier.size(48.dp)) {
                        ThemeIcon(Icons.Rounded.SkipPrevious,stringResource(Res.string.immersive_previous),Modifier.size(28.dp),if(hasPrevious && !locked)Color.White else Color.White.copy(.3f),"previous")
                    }
                    FilledIconButton(onClick={revealControls();onPlayPause()},modifier=Modifier.size(64.dp),shape=androidx.compose.foundation.shape.CircleShape,
                        colors=IconButtonDefaults.filledIconButtonColors(containerColor=ink,contentColor=Color(0xFF10151E))) {
                        ThemeIcon(if(playPauseActive)Icons.Rounded.Pause else Icons.Rounded.PlayArrow,stringResource(if(playPauseActive)Res.string.immersive_pause else Res.string.immersive_play),Modifier.size(32.dp),Color(0xFF10151E),if(playPauseActive)"pause" else "play")
                    }
                    IconButton(onClick={revealControls();onNext()},enabled=hasNext && !locked,modifier=Modifier.size(48.dp)) {
                        ThemeIcon(Icons.Rounded.SkipNext,stringResource(Res.string.immersive_next),Modifier.size(28.dp),if(hasNext && !locked)Color.White else Color.White.copy(.3f),"next")
                    }
                }
                AnimatedVisibility(visible=controls,
                    enter=fadeIn(tween(if(reduceMotion)0 else 160)),
                    exit=fadeOut(tween(if(reduceMotion)0 else 160)),
                    modifier=Modifier.align(Alignment.BottomCenter)) {
                    Surface(Modifier.safeDrawingPadding().padding(horizontal=16.dp,vertical=12.dp).widthIn(max=if(wide)840.dp else 480.dp).fillMaxWidth()
                        .heightIn(max=(maxHeight-100.dp).coerceAtLeast(80.dp))
                        .onSizeChanged {footerHeight=with(density){it.height.toDp()}+28.dp},
                        shape=androidx.compose.foundation.shape.RoundedCornerShape(28.dp),color=deckColor.copy(alpha=if(options.immersiveStyle.isExpandedWorld).94f else .92f),contentColor=Color.White,
                        border=androidx.compose.foundation.BorderStroke(1.dp,ink.copy(.25f))) {
                        Box(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                        if(wide && density.fontScale<=1.35f) Row(Modifier.padding(horizontal=20.dp,vertical=12.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(24.dp)) {
                            Column(Modifier.weight(1f)) {
                                Text(song.title,maxLines=1,overflow=TextOverflow.Ellipsis,style=MaterialTheme.typography.titleMedium)
                                ImmersiveTimeline(song.videoId,position,durationMs,locked,ink,onSeek,onScrubbing)
                            }
                            Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)) {transport()}
                        } else Column(Modifier.padding(horizontal=20.dp,vertical=12.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(4.dp)) {
                            Text(song.title,maxLines=1,overflow=TextOverflow.Ellipsis,style=MaterialTheme.typography.titleMedium)
                            Text(song.artist,maxLines=1,overflow=TextOverflow.Ellipsis,style=MaterialTheme.typography.bodySmall,color=Color.White.copy(.65f))
                            ImmersiveTimeline(song.videoId,position,durationMs,locked,ink,onSeek,onScrubbing)
                            Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(16.dp)) {transport()}
                        }
                        }
                    }
                }
                if(settings) MaterialTheme(colorScheme=base) { ModalBottomSheet(onDismissRequest={settings=false},sheetMaxWidth=900.dp,sheetState=rememberModalBottomSheetState(skipPartiallyExpanded=true),
                    containerColor=base.surface,shape=androidx.compose.foundation.shape.RoundedCornerShape(topStart=32.dp,topEnd=32.dp)) {
                    ImmersiveMenuContent(options,artwork,onOptions,
                        onSelectStyle={onOptions(options.copy(immersiveStyle=it));settings=false;revealControls()},
                        onSource={settings=false;onSource()},onTiming={settings=false;onTiming()},onSongOptions={settings=false;onSongOptions()},onDismiss={settings=false})
                } }
            }
        }
    }
    if(windowed)ImmersiveWindow(onExit,content) else content()
}

@OptIn(ExperimentalLayoutApi::class)
@Composable internal fun <T> ImmersiveChoices(label:String,values:List<T>,selected:T,title:@Composable (T)->String,onSelect:(T)->Unit) {
    Column(verticalArrangement=Arrangement.spacedBy(4.dp)) {
        Text(label,style=MaterialTheme.typography.titleSmall)
        FlowRow(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            values.forEach { value -> FilterChip(selected=value==selected,onClick={onSelect(value)},label={Text(title(value))}) }
        }
    }
}
