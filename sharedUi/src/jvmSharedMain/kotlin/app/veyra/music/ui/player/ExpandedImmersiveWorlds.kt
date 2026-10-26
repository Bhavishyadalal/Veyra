package app.veyra.music.ui.player

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.veyra.music.data.lyrics.LyricLine
import app.veyra.music.data.model.Song
import app.veyra.music.data.settings.ImmersiveStyle
import app.veyra.music.playback.PlaybackPosition
import app.veyra.music.sharedui.resources.*
import app.veyra.music.ui.motion.rememberVeyraReducedMotion
import app.veyra.music.ui.themes.ThemeIcon
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

internal val ImmersiveStyle.isExpandedWorld: Boolean
    get() = when (this) {
        ImmersiveStyle.AURORA_DRIFT, ImmersiveStyle.VINYL_LOUNGE,
        ImmersiveStyle.NEON_TUNNEL, ImmersiveStyle.PAPER_REVERIE,
        ImmersiveStyle.ORBITAL_ATLAS, ImmersiveStyle.PRISM_BLOOM -> true
        else -> false
    }

internal val ImmersiveStyle.hasLyrics: Boolean
    get() = when (this) {
        ImmersiveStyle.ARTWORK_CINEMA, ImmersiveStyle.MINIMAL_NIGHT,
        ImmersiveStyle.VINYL_LOUNGE, ImmersiveStyle.PAPER_REVERIE, ImmersiveStyle.ORBITAL_ATLAS -> false
        else -> true
    }

internal fun ImmersiveStyle.deckColor(): Color = when (this) {
    ImmersiveStyle.AURORA_DRIFT -> Color(0xFF04151B)
    ImmersiveStyle.VINYL_LOUNGE -> Color(0xFF1C1511)
    ImmersiveStyle.NEON_TUNNEL -> Color(0xFF10091F)
    ImmersiveStyle.PAPER_REVERIE -> Color(0xFF30271F)
    ImmersiveStyle.ORBITAL_ATLAS -> Color(0xFF070D20)
    ImmersiveStyle.PRISM_BLOOM -> Color(0xFF1C0D26)
    else -> Color(0xFF0B1019)
}

/** One clock, read by drawing/layers only. No frame-driven screen recomposition or fake FFT. */
@Composable
internal fun rememberImmersiveScenePhase(active: Boolean): State<Float> {
    val phase = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(active) {
        if (!active) return@LaunchedEffect
        var previous = withFrameNanos { it }
        while (true) {
            val now = withFrameNanos { it }
            // Returning from a stall never pays back a large jump in the scene.
            val seconds = ((now - previous) / 1_000_000_000f).coerceIn(0f, .05f)
            previous = now
            phase.floatValue = (phase.floatValue + seconds / 30f) % 1f
        }
    }
    return phase
}

/** These scenes share the existing transport, seek, lifecycle and lyric engine. */
@Composable
internal fun ExpandedImmersiveWorld(
    style: ImmersiveStyle, song: Song, artwork: String?, position: PlaybackPosition,
    durationMs: Long, lines: List<LyricLine>, looking: Boolean, isPlaying: Boolean,
    controls: Boolean, locked: Boolean, onBlocked: () -> Unit, onSeek: (Long) -> Unit,
    onReveal: () -> Unit, onHide: () -> Unit, bottomSpace: Dp,
) {
    val reducedMotion = rememberVeyraReducedMotion()
    val phase = rememberImmersiveScenePhase(isPlaying && !reducedMotion && style != ImmersiveStyle.PAPER_REVERIE)
    val context = LocalPlatformContext.current
    val request = remember(artwork, context) { ImageRequest.Builder(context).data(artwork).size(720).build() }
    val paper = style == ImmersiveStyle.PAPER_REVERIE
    val ink = if (paper) Color(0xFF392E29) else style.ink(MaterialTheme.colorScheme.primary)
    val playhead = rememberLyricPlayhead(position)
    Box(Modifier.fillMaxSize()) {
        ExpandedSceneBackdrop(style, phase, Modifier.fillMaxSize())
        BoxWithConstraints(Modifier.fillMaxSize().safeDrawingPadding()
            .padding(top = 82.dp, bottom = bottomSpace).padding(horizontal = 24.dp)) {
            val availableHeight = maxHeight
            val largeText = LocalDensity.current.fontScale >= 1.5f
            val wide = maxWidth >= 600.dp && maxWidth > maxHeight && !largeText
            val short = maxHeight < 340.dp
            val heroSize = minOf(if (wide) maxWidth * .40f else maxWidth - 16.dp,
                maxHeight * if (wide) .76f else if (style.hasLyrics) .43f else .56f)
                .coerceIn(64.dp, 320.dp)
            val identity: @Composable () -> Unit = {
                Text(style.title().uppercase(Locale.ROOT), color = ink.copy(alpha = .8f),
                    style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp), textAlign = TextAlign.Center)
                Text(song.title, color = if (paper) ink else Color.White,
                    style = (if (short) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.headlineMedium)
                        .let { if (paper) it.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Normal) else it },
                    textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(song.artist, color = ink.copy(alpha = if (paper) .78f else .85f),
                    style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (!style.hasLyrics && !song.albumName.isNullOrBlank()) Text(song.albumName!!,
                    color = ink.copy(alpha = .6f), style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            val lyrics: @Composable (Modifier) -> Unit = { modifier ->
                if (lines.isEmpty() && !looking) Box(modifier, contentAlignment = Alignment.Center) {
                    Text(stringResource(Res.string.immersive_no_lyrics), color = ink.copy(alpha = .7f),
                        style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                } else LyricsPanel(lines = lines, trackKey = song.videoId, playhead = playhead,
                    looking = looking, isPlaying = isPlaying,
                    onSeekToLine = { if (locked) onBlocked() else onSeek(it) },
                    controlsOpen = controls, onRevealControls = onReveal, onHideControls = onHide,
                    modifier = modifier)
            }
            if (wide) Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(28.dp)) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    SceneHero(style, request, position, durationMs, phase, Modifier.size(heroSize))
                }
                Column(Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    identity()
                    if (style.hasLyrics) lyrics(Modifier.fillMaxWidth().height(150.dp))
                }
            } else Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(if (short) 10.dp else 16.dp)) {
                Spacer(Modifier.height(if (short) 0.dp else 8.dp))
                SceneHero(style, request, position, durationMs, phase, Modifier.size(heroSize))
                identity()
                if (style.hasLyrics && !short && !largeText) lyrics(Modifier.fillMaxWidth()
                    .height((availableHeight - heroSize - 148.dp).coerceIn(80.dp, 220.dp)))
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

/** Static picker uses the same scene geometry and sleeve as the live mode. */
@Composable
internal fun ExpandedImmersiveMiniature(style: ImmersiveStyle, request: ImageRequest, modifier: Modifier) {
    val phase = remember { mutableFloatStateOf(.08f) }
    val position = remember { PlaybackPosition().apply { report(60_000) } }
    Box(modifier) {
        ExpandedSceneBackdrop(style, phase, Modifier.fillMaxSize())
        SceneHero(style, request, position, 180_000, phase,
            Modifier.size(76.dp).align(Alignment.Center).padding(4.dp))
    }
}

@Composable
private fun SceneHero(style: ImmersiveStyle, request: ImageRequest, position: PlaybackPosition,
    durationMs: Long, phase: State<Float>, modifier: Modifier) {
    val ink = style.ink(MaterialTheme.colorScheme.primary)
    val sleeve: @Composable (Modifier) -> Unit = { target ->
        Box(target.background(Color(0xFF202331)), contentAlignment = Alignment.Center) {
            ThemeIcon(Icons.Rounded.MusicNote, null, Modifier.fillMaxSize(.28f), ink, "music")
            AsyncImage(request, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        }
    }
    when (style) {
        ImmersiveStyle.VINYL_LOUNGE -> Box(modifier, contentAlignment = Alignment.Center) {
            Box(Modifier.fillMaxSize().graphicsLayer { rotationZ = phase.value * 360f }) {
                Canvas(Modifier.fillMaxSize()) {
                    val radius = size.minDimension / 2
                    drawCircle(Color(0xFF08090C), radius)
                    drawCircle(Brush.sweepGradient(listOf(Color(0xFF14151A), Color(0xFF393028),
                        Color(0xFF0B0C10), Color(0xFF282B32), Color(0xFF14151A))), radius * .98f)
                    repeat(22) { i -> drawCircle(Color.White.copy(alpha = .075f), radius * (.52f + i * .020f), style = Stroke(.65.dp.toPx())) }
                    drawCircle(ink.copy(alpha = .5f), radius * .48f, style = Stroke(1.dp.toPx()))
                }
                sleeve(Modifier.fillMaxSize(.44f).align(Alignment.Center).clip(CircleShape))
                Box(Modifier.size(7.dp).align(Alignment.Center).background(Color(0xFFEEDFC8), CircleShape))
            }
            // A stationary tone arm gives the spinning artwork a physical anchor.
            Canvas(Modifier.fillMaxSize()) {
                val start = Offset(size.width * .91f, size.height * .10f)
                val end = Offset(size.width * .73f, size.height * .68f)
                drawLine(Color(0xFFAA9579), start, end, size.minDimension * .025f)
                drawCircle(ink, size.minDimension * .045f, start)
                drawLine(Color(0xFFEDD9B7), end, end + Offset(-size.width * .05f, size.height * .04f), size.minDimension * .04f)
            }
        }
        ImmersiveStyle.ORBITAL_ATLAS -> Box(modifier, contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize()) {
                val radius = size.minDimension * .46f
                drawCircle(ink.copy(alpha = .25f), radius, style = Stroke(1.dp.toPx()))
                drawCircle(ink.copy(alpha = .1f), radius * .83f, style = Stroke(.6.dp.toPx()))
                val fraction = if (durationMs > 0) (position.positionMs.toDouble() / durationMs).coerceIn(0.0, 1.0).toFloat() else 0f
                drawArc(ink, -90f, 360f * fraction, false, center - Offset(radius, radius), Size(radius * 2, radius * 2), style = Stroke(2.dp.toPx()))
                val angle = phase.value * 2f * PI.toFloat()
                val planet = center + Offset(cos(angle) * radius, sin(angle) * radius)
                drawCircle(Color(0xFFFFD39C), size.minDimension * .035f, planet)
            }
            sleeve(Modifier.fillMaxSize(.69f).clip(CircleShape))
            Canvas(Modifier.fillMaxSize(.69f)) {
                drawCircle(Brush.radialGradient(listOf(Color.Transparent, Color(0xFF02091B).copy(alpha = .85f)),
                    center = Offset(size.width * .3f, size.height * .25f), radius = size.width))
            }
        }
        ImmersiveStyle.PAPER_REVERIE -> Box(modifier.padding(10.dp).graphicsLayer { rotationZ = -5f }
            .background(Color(0xFFFAF5E9)).border(1.dp, Color(0xFFD7CBBB)).padding(12.dp), contentAlignment = Alignment.Center) {
            sleeve(Modifier.fillMaxSize())
            Box(Modifier.align(Alignment.TopCenter).offset(y = (-20).dp).size(74.dp, 24.dp)
                .graphicsLayer { rotationZ = -9f }.background(Color(0xFFCBAA78).copy(alpha = .75f)))
        }
        ImmersiveStyle.NEON_TUNNEL -> Box(modifier.padding(12.dp).graphicsLayer { rotationZ = -7f }
            .border(2.dp, Color(0xFFF09DFF), RoundedCornerShape(24.dp)).padding(7.dp)) {
            sleeve(Modifier.fillMaxSize().clip(RoundedCornerShape(18.dp)))
        }
        ImmersiveStyle.PRISM_BLOOM -> {
            val shape = RoundedCornerShape(topStartPercent = 42, topEndPercent = 10,
                bottomEndPercent = 42, bottomStartPercent = 10)
            Box(modifier.padding(16.dp).graphicsLayer { rotationZ = 5f }
                .border(2.dp, Brush.linearGradient(listOf(Color(0xFFFFD3E9), Color(0xFFB5FFF0), Color(0xFFCAAFFF))), shape)
                .padding(7.dp)) { sleeve(Modifier.fillMaxSize().clip(shape)) }
        }
        else -> Box(modifier.padding(10.dp).graphicsLayer {
            translationY = sin(phase.value * 2f * PI.toFloat()) * 6.dp.toPx()
        }.border(1.dp, Color(0xFFBDFFEB).copy(alpha = .55f), RoundedCornerShape(28.dp)).padding(7.dp)) {
            sleeve(Modifier.fillMaxSize().clip(RoundedCornerShape(22.dp)))
        }
    }
}

@Composable
private fun ExpandedSceneBackdrop(style: ImmersiveStyle, phase: State<Float>, modifier: Modifier) {
    Box(modifier.background(when (style) {
        ImmersiveStyle.PAPER_REVERIE -> Color(0xFFEBE2D1)
        ImmersiveStyle.VINYL_LOUNGE -> Color(0xFF1E1714)
        ImmersiveStyle.PRISM_BLOOM -> Color(0xFF170D27)
        else -> Color(0xFF050B1D)
    })) {
        if (style == ImmersiveStyle.AURORA_DRIFT) {
            Image(painterResource(Res.drawable.immersive_aurora), null,
                Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = .1f),
                Color(0xFF04121E).copy(alpha = .4f), Color(0xFF04121E).copy(alpha = .8f)))))
        }
        // Brushes and petal paths are built on size/style changes, not on every animation frame.
        Box(Modifier.fillMaxSize().drawWithCache {
            val petalColors = listOf(Color(0xFF62F6C9), Color(0xFFFF84CB), Color(0xFF9A78FF), Color(0xFFFFCC7E))
            val petals = if (style != ImmersiveStyle.PRISM_BLOOM) emptyList() else List(8) { i ->
                Path().apply {
                    val c = Offset(size.width * .5f, size.height * .35f)
                    moveTo(c.x, c.y)
                    cubicTo(c.x - size.width * .8f, c.y - size.height * .26f,
                        c.x - size.width * .3f, c.y - size.height * .57f, c.x, c.y - size.height * .36f)
                    cubicTo(c.x + size.width * .30f, c.y - size.height * .57f,
                        c.x + size.width * .8f, c.y - size.height * .26f, c.x, c.y)
                    close()
                } to Brush.linearGradient(listOf(petalColors[i % 4].copy(alpha = .34f),
                    petalColors[(i + 1) % 4].copy(alpha = .18f), Color(0xFF21123A).copy(alpha = .03f)),
                    start = Offset(0f, size.height * .15f), end = Offset(size.width, size.height * .6f))
            }
            val spotlight = Brush.radialGradient(listOf(Color(0xFFB88347).copy(alpha = .25f), Color.Transparent),
                center = Offset(size.width * .35f, size.height * .25f), radius = size.maxDimension * .7f)
            onDrawBehind {
                val motion = phase.value
                when (style) {
                    ImmersiveStyle.VINYL_LOUNGE -> {
                        drawRect(spotlight)
                        repeat(18) { i ->
                            val x = size.width * (i + .5f) / 18
                            drawLine(Color(0xFFE5C48D).copy(alpha = .04f), Offset(x, 0f), Offset(x, size.height), 1.dp.toPx())
                        }
                    }
                    ImmersiveStyle.NEON_TUNNEL -> neonTunnel(motion)
                    ImmersiveStyle.PAPER_REVERIE -> {
                        repeat(22) { i ->
                            val y = size.height * (i + .5f) / 22
                            drawLine(Color(0xFF897460).copy(alpha = .14f), Offset(0f, y), Offset(size.width, y), .5.dp.toPx())
                        }
                        drawLine(Color(0xFFB66B69).copy(alpha = .3f), Offset(size.width * .12f, 0f), Offset(size.width * .12f, size.height), 1.dp.toPx())
                        repeat(64) { i ->
                            drawCircle(Color(0xFF735B42).copy(alpha = .08f), .6.dp.toPx(), Offset(size.width * ((i * 37 % 101) / 100f), size.height * ((i * 53 % 103) / 102f)))
                        }
                    }
                    ImmersiveStyle.ORBITAL_ATLAS -> {
                        starAtlas(motion)
                        repeat(3) { i ->
                            val w = size.width * (1.1f + i * .45f)
                            drawOval(Color(0xFF829DD8).copy(alpha = .11f), Offset((size.width - w) / 2, size.height * (.12f + i * .1f)),
                                Size(w, w * .68f), style = Stroke(.8.dp.toPx()))
                        }
                    }
                    ImmersiveStyle.PRISM_BLOOM -> {
                        petals.forEachIndexed { i, (path, brush) ->
                            rotate(i * 45f + sin(motion * 2f * PI.toFloat()) * 5f,
                                Offset(size.width * .5f, size.height * .35f)) {
                                drawPath(path, brush)
                                drawPath(path, Color(0xFFFFD7EE).copy(alpha = .22f), style = Stroke(.8.dp.toPx()))
                            }
                        }
                        starAtlas(motion, 24)
                    }
                    else -> starAtlas(motion, 18)
                }
            }
        })
    }
}

private fun DrawScope.starAtlas(phase: Float, count: Int = 48) {
    repeat(count) { i ->
        val point = Offset(size.width * ((i * 37 % 101) / 100f), size.height * ((i * 53 % 103) / 102f))
        val alpha = .12f + .30f * ((sin(phase * 2f * PI.toFloat() + i) + 1f) / 2f)
        drawCircle(Color(0xFFDCE8FF).copy(alpha = alpha), if (i % 7 == 0) 1.5.dp.toPx() else .6.dp.toPx(), point)
        if (i % 11 == 0) {
            drawLine(Color(0xFFBECEEF).copy(alpha = .35f), point - Offset(4.dp.toPx(), 0f), point + Offset(4.dp.toPx(), 0f), .5.dp.toPx())
            drawLine(Color(0xFFBECEEF).copy(alpha = .35f), point - Offset(0f, 4.dp.toPx()), point + Offset(0f, 4.dp.toPx()), .5.dp.toPx())
        }
    }
}

private fun DrawScope.neonTunnel(phase: Float) {
    val horizon = size.height * .53f
    val center = Offset(size.width / 2, horizon)
    val violet = Color(0xFFAA75FF)
    repeat(13) { i ->
        val end = Offset(size.width * (i - 3) / 6, size.height)
        drawLine(violet.copy(alpha = .23f), center, end, .8.dp.toPx())
    }
    repeat(9) { i ->
        val t = ((i + phase) / 9f).coerceIn(0f, 1f)
        val y = horizon + (size.height - horizon) * t * t
        drawLine(Color(0xFF8EDFF1).copy(alpha = .23f), Offset(0f, y), Offset(size.width, y), .7.dp.toPx())
    }
    repeat(5) { i ->
        val w = size.width * (.85f - i * .13f)
        val h = size.height * (.48f - i * .055f)
        val light = if (i % 2 == 0) violet else Color(0xFFEF87D7)
        val corner = androidx.compose.ui.geometry.CornerRadius(w * .18f)
        val origin = Offset((size.width - w) / 2, horizon - h)
        drawRoundRect(light.copy(alpha = .07f), origin, Size(w, h), corner, style = Stroke(7.dp.toPx()))
        drawRoundRect(light.copy(alpha = .25f + i * .06f), origin, Size(w, h), corner,
            style = Stroke(if (i == 0) 1.6.dp.toPx() else .8.dp.toPx()))
    }
    drawLine(Color(0xFFF788E5).copy(alpha = .55f), Offset(0f, horizon), Offset(size.width, horizon), 1.dp.toPx())
    starAtlas(phase, 24)
}
