package app.veyra.music.ui.player

import app.veyra.music.ui.layout.stackForReadability
import app.veyra.music.ui.components.LocalVeyraContrastControls
import app.veyra.music.ui.components.VeyraSurfaces
import app.veyra.music.ui.motion.VeyraMotion

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

internal enum class ControlDecoration { DEFAULT, GLYPHS, RINGS, FILLED }

/** Fixed hit region, animated visual only. The same surface serves transport, menus and modes. */
@Composable
internal fun PlayerControlSurface(
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean? = null,
    busy: Boolean = false,
    keepContentWhileBusy: Boolean = false,
    reducedMotion: Boolean = false,
    tint: Color = Color.White,
    visualSize: Dp? = null,
    shape: Shape = CircleShape,
    containerColor: Color = Color.Transparent,
    framed: Boolean = false,
    grouped: Boolean = false,
    onHold: (() -> Unit)? = null,
    decoration: ControlDecoration = ControlDecoration.DEFAULT,
    content: @Composable BoxScope.() -> Unit,
) {
    val pack = app.veyra.music.ui.themes.LocalThemeVisuals.current
    val controlTint = pack?.accent ?: tint
    val effectiveShape = remember(grouped,pack?.materialStyle,pack?.buttonCorner,shape) {
        if (grouped) RectangleShape else pack?.let { app.veyra.music.ui.themes.themeControlShape(it) } ?: shape
    }
    val contrast = LocalVeyraContrastControls.current
    val controlBackground = if (grouped) containerColor else if (contrast && containerColor == Color.Transparent) controlTint.copy(alpha = 0.12f) else if (pack != null && containerColor == Color.Transparent) pack.raised.copy(alpha=.70f * app.veyra.music.ui.themes.LocalThemeArtworkOptions.current.controlOpacity) else containerColor
    val decoratedBackground = when(decoration) {
        ControlDecoration.GLYPHS,ControlDecoration.RINGS -> Color.Transparent
        ControlDecoration.FILLED -> controlTint.copy(alpha=.2f)
        else -> controlBackground
    }
    val indicatorSize = if ((visualSize ?: 48.dp) >= 64.dp) 36.dp else 24.dp
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val available = enabled && (!busy || keepContentWhileBusy) && LocalPlayerControlsInteractive.current
    var held by remember {mutableStateOf(false)}
    val currentHold by rememberUpdatedState(onHold)
    val holdTimeout=androidx.compose.ui.platform.LocalViewConfiguration.current.longPressTimeoutMillis
    LaunchedEffect(pressed,available,onHold!=null) {
        if(pressed && available && onHold!=null) {
            held=false
            kotlinx.coroutines.delay(holdTimeout)
            held=true
            while(true) {currentHold?.invoke();kotlinx.coroutines.delay(400)}
        }
    }
    val scale =
        animateFloatAsState(
            if (pressed && available && !reducedMotion && !grouped) (if (pack?.sceneStyle == "playful" || pack?.materialStyle in setOf("punk_gel","punk_sticker")) 0.90f else if(pack?.materialStyle == "punk_torn") .97f else 0.95f) else 1f,
            if ((pack?.sceneStyle == "playful" || pack?.materialStyle in setOf("punk_gel","punk_sticker")) && !reducedMotion)
                androidx.compose.animation.core.spring(dampingRatio = .8f, stiffness = 650f)
            else tween(playerMotionMillis(reducedMotion, VeyraMotion.Press), easing = VeyraMotion.Easing),
            label = "playerControlPress",
        )
    val sketchTilt = animateFloatAsState(
        if ((pack?.sceneStyle == "sketch" || pack?.materialStyle in setOf("flame","punk_torn","punk_chrome")) && pressed && available && !reducedMotion && !grouped) -2.5f else 0f,
        tween(playerMotionMillis(reducedMotion, 140), easing = VeyraMotion.Easing), label = "sketchPencilPress")
    val opacity =
        animateFloatAsState(
            if (enabled || busy) 1f else 0.35f,
            tween(playerMotionMillis(reducedMotion, VeyraMotion.Feedback)),
            label = "playerControlAvailability",
        )
    Box(
        modifier
            .heightIn(min = VeyraSurfaces.MinTouch)
            .widthIn(min = VeyraSurfaces.MinTouch)
            .clip(effectiveShape)
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = available,
                role = Role.Button,
                onClick = {if(held)held=false else onClick()},
            )
            .semantics {
                contentDescription = description
                selected?.let { this.selected = it }
                if (busy) progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            (if (visualSize != null) Modifier.size(visualSize) else Modifier.fillMaxSize())
                .graphicsLayer {
                    rotationZ = sketchTilt.value
                    scaleX = scale.value
                    scaleY = scale.value
                    alpha = opacity.value
                }
                .clip(effectiveShape)
                .background(decoratedBackground)
                .then(if(!grouped && decoration != ControlDecoration.GLYPHS && decoration != ControlDecoration.RINGS && pack?.materialStyle?.startsWith("punk_") == true) app.veyra.music.ui.themes.punkControlFinish(Modifier,pack) else Modifier)
                .then(
                    if (!grouped && decoration!=ControlDecoration.GLYPHS && (decoration==ControlDecoration.RINGS || framed || contrast || pack != null))
                        Modifier.background(
                                Brush.verticalGradient(
                                    listOf(controlTint.copy(alpha = 0.13f), Color.Transparent)
                                )
                            )
                            .border(1.dp, controlTint.copy(alpha = 0.13f), effectiveShape)
                    else Modifier
                )
                .background(
                    if (pressed && available) controlTint.copy(alpha = 0.12f) else Color.Transparent
                )
                .clearAndSetSemantics {},
            contentAlignment = Alignment.Center,
        ) {
            if (keepContentWhileBusy) {
                androidx.compose.runtime.CompositionLocalProvider(
                    app.veyra.music.ui.themes.LocalThemeControlPressed provides (pressed && available)) { content() }
                if (busy) {
                    val badge=Modifier.align(Alignment.BottomEnd).padding(6.dp).size(14.dp)
                    if(reducedMotion) Canvas(badge) {drawArc(controlTint,-90f,260f,false,style=Stroke(1.5.dp.toPx(),cap=StrokeCap.Round))}
                    else CircularProgressIndicator(badge,color=controlTint,strokeWidth=1.5.dp)
                }
            } else Crossfade(
                busy,
                modifier = Modifier.fillMaxSize(),
                animationSpec = tween(playerMotionMillis(reducedMotion, VeyraMotion.Feedback)),
                label = "playerControlBusy",
            ) { waiting ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (waiting) {
                        if (reducedMotion)
                            Canvas(Modifier.size(indicatorSize)) {
                                drawArc(
                                    tint,
                                    -90f,
                                    260f,
                                    false,
                                    style = Stroke(2.dp.toPx(), cap = StrokeCap.Round),
                                )
                            }
                        else
                            CircularProgressIndicator(
                                Modifier.size(indicatorSize),
                                color = controlTint,
                                strokeWidth = 2.dp,
                            )
                    } else androidx.compose.runtime.CompositionLocalProvider(
                        app.veyra.music.ui.themes.LocalThemeControlPressed provides (pressed && available)) { content() }
                }
            }
        }
    }
}

/**
 * Seek/volume input with explicit completion and cancellation, while presentation stays
 * caller-owned.
 */
@Composable
internal fun PlayerTrackInput(
    value: Float,
    enabled: Boolean,
    description: String?,
    onChange: (Float) -> Unit,
    onFinished: (() -> Unit)?,
    onCancelled: (() -> Unit)?,
    onDragging: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    interactionKey: Any? = Unit,
    valueDescription: String? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val active = enabled && LocalPlayerControlsInteractive.current
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val change by rememberUpdatedState(onChange)
    val finish by rememberUpdatedState(onFinished)
    val cancel by rememberUpdatedState(onCancelled)
    val drag by rememberUpdatedState(onDragging)
    Box(
        modifier
            .fillMaxWidth()
            .heightIn(min = VeyraSurfaces.MinTouch)
            .semantics {
                description?.let { contentDescription = it }
                valueDescription?.let { stateDescription = it }
                progressBarRangeInfo = ProgressBarRangeInfo(playerFraction(value), 0f..1f)
                if (!active) disabled()
                setProgress { requested ->
                    if (!active || !requested.isFinite()) false
                    else {
                        change(playerFraction(requested))
                        finish?.invoke()
                        true
                    }
                }
            }
            .pointerInput(active, interactionKey, rtl) {
                if (!active) return@pointerInput
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = true)
                    var completed = false
                    drag(true)
                    try {
                        down.consume()
                        playerPointerFraction(down.position.x, size.width.toFloat(), rtl)
                            ?.let(change)
                        while (true) {
                            val event = awaitPointerEvent()
                            val pointer = event.changes.firstOrNull { it.id == down.id } ?: break
                            if (pointer.isConsumed) break
                            if (!pointer.pressed) {
                                // Some input devices deliver their last position with the release.
                                if (pointer.positionChanged())
                                    playerPointerFraction(
                                            pointer.position.x,
                                            size.width.toFloat(),
                                            rtl,
                                        )
                                        ?.let(change)
                                pointer.consume()
                                completed = true
                                break
                            }
                            if (pointer.positionChanged()) {
                                playerPointerFraction(pointer.position.x, size.width.toFloat(), rtl)
                                    ?.let(change)
                                pointer.consume()
                            }
                        }
                    } finally {
                        drag(false)
                        if (completed) finish?.invoke() else cancel?.invoke()
                    }
                }
            },
        contentAlignment = Alignment.Center,
        content = content,
    )
}

/** Set once by the player host; no extra preference observer for every button. */
internal val LocalPlayerReducedMotion = compositionLocalOf { false }
/** Outgoing animated decks stay visible without retaining invisible click targets. */
internal val LocalPlayerControlsInteractive = compositionLocalOf { true }

@Composable
internal fun PlayerTransportControl(
    painter: androidx.compose.ui.graphics.painter.Painter,
    themeKey: String? = null,
    description: String,
    onClick: () -> Unit,
    iconSize: Dp,
    touchSize: Dp,
    enabled: Boolean = true,
    busy: Boolean = false,
    emphasized: Boolean = false,
    reducedMotion: Boolean = false,
    tint: Color = Color.White,
    containerColor: Color = Color.Transparent,
    heightScale: Float = 1f,
    onHold: (() -> Unit)? = null,
    decoration: ControlDecoration = ControlDecoration.DEFAULT,
) {
    val pack = app.veyra.music.ui.themes.LocalThemeVisuals.current
    PlayerControlSurface(
        description,
        onClick,
        Modifier.size(touchSize),
        enabled = enabled,
        busy = busy,
        keepContentWhileBusy = emphasized,
        onHold = onHold,
        decoration = decoration,
        reducedMotion = reducedMotion,
        tint = tint,
        visualSize = if (emphasized) touchSize - 8.dp else touchSize,
        shape =
            if (emphasized) androidx.compose.foundation.shape.RoundedCornerShape(30.dp)
            else CircleShape,
        framed = emphasized,
        containerColor = if (emphasized) Color.Black.copy(alpha = 0.32f) else containerColor,
    ) {
        val path = themeKey?.let { pack?.asset(it) }
        val normalPainter = if(path==null) painter else androidx.compose.runtime.key(path) {app.veyra.music.ui.themes.rememberThemeGlyphPainter(path,painter,256)}
        val pressedPath = themeKey?.let {pack?.asset("${it}_pressed")}
        // Decode the small pressed state alongside the normal glyph; never flash a blank
        // or native placeholder if a user taps before the pressed file is ready.
        val pressedPainter = if(pressedPath==null) null else androidx.compose.runtime.key(pressedPath) {app.veyra.music.ui.themes.rememberThemeGlyphPainter(pressedPath,normalPainter,256)}
        val pressedReady = pressedPainter?.state?.collectAsState()?.value is coil3.compose.AsyncImagePainter.State.Success
        val themedPainter = if(app.veyra.music.ui.themes.LocalThemeControlPressed.current && pressedReady) pressedPainter else normalPainter
        // A pressed skin is feedback, not a new media item. Draw it directly instead of
        // retaining two bitmap layers for 150 ms on every press and release.
        app.veyra.music.ui.themes.ThemePainterGlyph(
            themedPainter, null,
            Modifier.size(if(pack?.iconStyle == "original" && path != null) (touchSize-16.dp).coerceAtLeast(24.dp) else iconSize)
                .graphicsLayer {scaleY=heightScale},
            pack?.accent ?: tint,preserveColors=path != null && pack?.iconStyle == "original")
    }
}

/** Draw callbacks keep the playhead and Automix pulse out of parent recomposition. */
@Composable
internal fun PlayerProgressTrack(
    fraction: () -> Float,
    height: Dp,
    activeColor: () -> Color,
    inactiveColor: Color,
    markerColor: Color = Color.White.copy(alpha = 0.5f),
    transitionWindow: ClosedFloatingPointRange<Float>? = null,
    fillFactor: () -> Float = { 1f },
    thumbVisible: Boolean = false,
) {
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val pack = app.veyra.music.ui.themes.LocalThemeVisuals.current
    Canvas(Modifier.fillMaxWidth().height(if (pack == null) height else height * (pack.seekHeight/7f)).graphicsLayer { scaleX = if (rtl) -1f else 1f }) {
        val radius = androidx.compose.ui.geometry.CornerRadius(size.height / 2f)
        drawRoundRect(pack?.outline ?: inactiveColor, cornerRadius = radius)
        transitionWindow
            ?.takeIf { it.start.isFinite() && it.endInclusive.isFinite() }
            ?.let { window ->
                val from = size.width * playerFraction(window.start)
                val to = size.width * playerFraction(window.endInclusive)
                if (to > from)
                    drawRoundRect(
                        markerColor,
                        topLeft = Offset(from, 0f),
                        size = androidx.compose.ui.geometry.Size(to - from, size.height),
                        cornerRadius = radius,
                    )
            }
        val factor = playerFraction(fillFactor())
        val filled = size.width * playerFraction(fraction()) * factor
        if (pack?.seekStyle == "sketch") {
            val count = (size.width / 10.dp.toPx()).toInt().coerceIn(1, 100)
            repeat(count) { i ->
                val x = size.width * i / count
                drawLine(pack.secondary.copy(alpha=.5f), Offset(x, size.height), Offset(x+3.dp.toPx(), 0f), .65.dp.toPx())
            }
        }
        if (filled > 0f)
            drawRoundRect(
                pack?.accent ?: activeColor(),
                size =
                    androidx.compose.ui.geometry.Size(
                        filled.coerceAtLeast(size.height * factor).coerceAtMost(size.width),
                        size.height,
                    ),
                cornerRadius = radius,
            )
        if (pack?.seekStyle == "web" && filled > 0f && factor > 0.95f) {
            // The web motif follows the real seek fraction, including RTL and scrubbing.
            val x=filled.coerceIn(0f,size.width)
            val cy=size.height/2
            val d=size.height*.65f
            val diamond=androidx.compose.ui.graphics.Path().apply {
                moveTo(x,cy-d);lineTo(x+d,cy);lineTo(x,cy+d);lineTo(x-d,cy);close()
            }
            drawPath(diamond,pack.text)
            for (strand in 1..8) {
                val sx=size.width*strand/9
                if (sx > filled) drawLine(pack.secondary.copy(alpha=.4f),Offset(sx,0f),Offset(sx+size.height,size.height),.7.dp.toPx())
            }
        }
        if (pack?.seekStyle == "bubbles" && factor > .95f) {
            // Real seek state drives the bubbles; no independent animation clock.
            val r = size.height * .22f
            for (dot in 1..14) {
                val x = size.width * dot / 15f
                if (x > filled + size.height) drawCircle(pack.secondary.copy(alpha=.65f),r,Offset(x,size.height/2))
            }
            if (filled > 0f) {
                val x=filled.coerceIn(size.height/2,(size.width-size.height/2).coerceAtLeast(size.height/2))
                drawCircle(pack.text,size.height*.5f,Offset(x,size.height/2))
                drawCircle(pack.accent,size.height*.23f,Offset(x,size.height/2))
            }
        }
        if (pack?.seekStyle == "ember" && filled > 0f && factor > 0.95f) {
            // A quiet ember playhead, with no extra animation or input surface.
            drawCircle(pack.accent, radius = size.height * .65f,
                center = Offset(filled.coerceIn(0f,size.width), size.height/2))
            drawCircle(Color(0xFFFFF1D4), radius = size.height * .22f,
                center = Offset(filled.coerceIn(0f,size.width), size.height/2))
        }
        if (pack != null && factor > .95f) {
            app.veyra.music.ui.themes.drawAdvancedTrackDetails(this, pack, playerFraction(fraction()), true)
        }
        if(thumbVisible) {
            val radius=6.dp.toPx()
            drawCircle(pack?.accent ?: activeColor(),radius,Offset(filled.coerceIn(minOf(radius,size.width/2),maxOf(size.width-radius,size.width/2)),size.height/2))
        }
    }
}

/** Large text gets its own quality line rather than colliding with the two timestamps. */
@Composable
internal fun PlayerTimelineLabels(
    elapsed: String,
    remaining: String,
    tint: Color,
    modifier: Modifier = Modifier,
    center: @Composable BoxScope.() -> Unit,
) {
    val fontScale = androidx.compose.ui.platform.LocalDensity.current.fontScale
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val stacked = stackForReadability(maxWidth.value, fontScale, 320f)
        Column(Modifier.fillMaxWidth()) {
            Box(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    androidx.compose.material3.Text(
                        elapsed,
                        style =
                            androidx.compose.material3.MaterialTheme.typography.labelMedium.copy(
                                fontFeatureSettings = "tnum"
                            ),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                        color = tint.copy(alpha = 0.82f),
                    )
                    androidx.compose.material3.Text(
                        remaining,
                        style =
                            androidx.compose.material3.MaterialTheme.typography.labelMedium.copy(
                                fontFeatureSettings = "tnum"
                            ),
                        maxLines = 1,
                        textAlign = androidx.compose.ui.text.style.TextAlign.End,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                        color = tint.copy(alpha = 0.82f),
                    )
                }
                if (!stacked)
                    Box(
                        Modifier.matchParentSize(),
                        contentAlignment = Alignment.Center,
                        content = center,
                    )
            }
            if (stacked)
                Box(
                    Modifier.fillMaxWidth().padding(top = 5.dp),
                    contentAlignment = Alignment.Center,
                    content = center,
                )
        }
    }
}

/** One continuous frame for connected controls; the children paint only selection/press fills. */
@Composable
internal fun PlayerControlGroup(
    tint: Color,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val pack = app.veyra.music.ui.themes.LocalThemeVisuals.current
    val shape = remember(pack?.materialStyle,pack?.buttonCorner) {pack?.let {
        app.veyra.music.ui.themes.themeControlShape(it)
    } ?: CircleShape}
    Row(modifier.clip(shape)
        .background(pack?.raised?.copy(alpha = .70f * app.veyra.music.ui.themes.LocalThemeArtworkOptions.current.controlOpacity) ?: tint.copy(alpha = .12f))
        .then(if (pack != null || LocalVeyraContrastControls.current)
            Modifier.background(Brush.verticalGradient(listOf(tint.copy(alpha=.13f),Color.Transparent)))
                .border(1.dp,tint.copy(alpha=.20f),shape)
            else Modifier), verticalAlignment=Alignment.CenterVertically,content=content)
}

@Composable
internal fun PlayerControlGroupDivider(tint: Color) {
    Box(Modifier.width(1.dp).fillMaxHeight().padding(vertical=12.dp)
        .background(tint.copy(alpha=.20f)))
}
