@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package app.veyra.music.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.PointerButtons
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.*
import app.veyra.music.ui.icons.VeyraIcons
import java.io.File
import javax.swing.SwingUtilities
import javax.xml.parsers.DocumentBuilderFactory
import org.jetbrains.skia.EncodedImageFormat

private val DarkColors =
    darkColorScheme(
        primary = Color(0xFFD9C4FF),
        onPrimary = Color(0xFF35204F),
        primaryContainer = Color(0xFF422D5C),
        onPrimaryContainer = Color(0xFFF0E3FF),
        secondary = Color(0xFFE9BEA8),
        onSecondary = Color(0xFF422B20),
        background = Color.Black,
        onBackground = Color.White,
        surface = Color(0xFF0D0D0F),
        onSurface = Color.White,
        surfaceVariant = Color(0xFF1C1C1E),
        onSurfaceVariant = Color(0xFFACA8B9),
        outline = Color(0xFF383443),
    )

private val LightColors =
    lightColorScheme(
        primary = Color(0xFF69408C),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFF0E3FF),
        onPrimaryContainer = Color(0xFF35204F),
        secondary = Color(0xFF84523B),
        onSecondary = Color.White,
        background = Color.White,
        onBackground = Color.Black,
        surface = Color(0xFFF7F7F9),
        onSurface = Color.Black,
        surfaceVariant = Color(0xFFF2F2F7),
        onSurfaceVariant = Color(0xFF6E6E73),
        outline = Color(0xFFE5E5EA),
    )

private val VeyraFontFamily =
    FontFamily(
        Font(
            File("${System.getProperty("veyra.root")}/app/src/main/res/font/veyra_regular.ttf"),
            FontWeight.W400,
        ),
        Font(
            File("${System.getProperty("veyra.root")}/app/src/main/res/font/veyra_medium.ttf"),
            FontWeight.W500,
        ),
        Font(
            File("${System.getProperty("veyra.root")}/app/src/main/res/font/veyra_semibold.ttf"),
            FontWeight.W600,
        ),
        Font(
            File("${System.getProperty("veyra.root")}/app/src/main/res/font/veyra_bold.ttf"),
            FontWeight.W700,
        ),
        Font(
            File("${System.getProperty("veyra.root")}/app/src/main/res/font/veyra_heavy.ttf"),
            FontWeight.W800,
        ),
    )
private val VeyraTypography =
    Typography(
            displayLarge =
                TextStyle(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 34.sp,
                    lineHeight = 42.sp,
                    letterSpacing = (-0.8).sp,
                ),
            displayMedium =
                TextStyle(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 32.sp,
                    lineHeight = 40.sp,
                    letterSpacing = (-0.6).sp,
                ),
            displaySmall =
                TextStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 36.sp),
            headlineLarge =
                TextStyle(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 30.sp,
                    lineHeight = 38.sp,
                    letterSpacing = (-0.7).sp,
                ),
            headlineMedium =
                TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    lineHeight = 32.sp,
                    letterSpacing = (-0.4).sp,
                ),
            headlineSmall =
                TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 30.sp),
            titleLarge =
                TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    lineHeight = 28.sp,
                    letterSpacing = (-0.3).sp,
                ),
            titleMedium =
                TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    letterSpacing = (-0.2).sp,
                ),
            titleSmall =
                TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
            bodyLarge =
                TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
            bodyMedium =
                TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
            bodySmall =
                TextStyle(fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 18.sp),
            labelLarge =
                TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 20.sp),
            labelMedium =
                TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp, lineHeight = 16.sp),
            labelSmall =
                TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 11.sp, lineHeight = 16.sp),
        )
        .withFamily(VeyraFontFamily)

private fun Typography.withFamily(family: FontFamily) =
    Typography(
        displayLarge = displayLarge.copy(fontFamily = family),
        displayMedium = displayMedium.copy(fontFamily = family),
        displaySmall = displaySmall.copy(fontFamily = family),
        headlineLarge = headlineLarge.copy(fontFamily = family),
        headlineMedium = headlineMedium.copy(fontFamily = family),
        headlineSmall = headlineSmall.copy(fontFamily = family),
        titleLarge = titleLarge.copy(fontFamily = family),
        titleMedium = titleMedium.copy(fontFamily = family),
        titleSmall = titleSmall.copy(fontFamily = family),
        bodyLarge = bodyLarge.copy(fontFamily = family),
        bodyMedium = bodyMedium.copy(fontFamily = family),
        bodySmall = bodySmall.copy(fontFamily = family),
        labelLarge = labelLarge.copy(fontFamily = family),
        labelMedium = labelMedium.copy(fontFamily = family),
        labelSmall = labelSmall.copy(fontFamily = family),
    )

@Composable
private fun transportAsset(name: String) =
    rememberVectorPainter(
        remember(name) {
            val file =
                File(
                    "${System.getProperty("veyra.root")}/sharedUi/src/commonMain/composeResources/drawable/$name.xml"
                )
            val root =
                DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(file)
                    .documentElement
            val builder =
                ImageVector.Builder(
                    name,
                    24.dp,
                    24.dp,
                    root.getAttribute("android:viewportWidth").toFloat(),
                    root.getAttribute("android:viewportHeight").toFloat(),
                )
            val paths = root.getElementsByTagName("path")
            for (index in 0 until paths.length) {
                val path = paths.item(index) as org.w3c.dom.Element
                builder.addPath(
                    addPathNodes(path.getAttribute("android:pathData")),
                    fill = SolidColor(Color.White),
                )
            }
            builder.build()
        }
    )

@Composable
private fun PlayerStudy(kind: String) {
    val compact = kind == "compact"
    val reduced = kind == "reduced"
    var playing by remember { mutableStateOf(kind != "paused") }
    var lyrics by remember { mutableStateOf(kind == "lyrics") }
    var queue by remember { mutableStateOf(kind == "queue") }
    var position by remember { mutableFloatStateOf(0.34f) }
    var volume by remember { mutableFloatStateOf(0.68f) }
    var dragging by remember { mutableStateOf(false) }
    val busy = kind == "buffering"
    Column(
        Modifier.fillMaxSize()
            .background(Color(0xff101114))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 28.dp)
    ) {
        Text(
            "VEYRA / 0.1.4-beta1",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = .5f),
        )
        Text(
            "Player details",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            modifier = Modifier.padding(top = 14.dp),
        )
        Text(
            "Native component review · ${kind.replace('-',' ')}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = .6f),
            modifier = Modifier.padding(top = 6.dp, bottom = 26.dp),
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            PlayerControlSurface(
                "Like",
                {},
                Modifier.size(48.dp),
                selected = true,
                reducedMotion = reduced,
                framed = true,
                visualSize = 40.dp,
                containerColor = Color.Black.copy(alpha = .25f),
            ) {
                Icon(
                    VeyraIcons.HeartFilled,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(21.dp),
                )
            }
            PlayerControlSurface(
                "More options",
                {},
                Modifier.size(48.dp),
                reducedMotion = reduced,
                framed = true,
                visualSize = 40.dp,
                containerColor = Color.Black.copy(alpha = .25f),
            ) {
                Icon(
                    Icons.Rounded.MoreHoriz,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
        Spacer(Modifier.height(18.dp))
        Text(
            "Seek with confidence",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
        )
        Text(
            "Illustrative track position",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = .5f),
        )
        PlayerTrackInput(
            position,
            kind != "locked",
            "Playback position",
            { position = it },
            {},
            {},
            { dragging = it },
            valueDescription = "1:24 / 4:08",
        ) {
            PlayerProgressTrack(
                { position },
                if (dragging) 12.dp else 7.dp,
                { Color.White },
                Color.White.copy(alpha = .22f),
            )
        }
        PlayerTimelineLabels("1:24", "−2:44", Color.White) {
            Text(
                if (busy) "Loading audio" else "High quality",
                color = Color.White.copy(alpha = 0.65f),
                style = MaterialTheme.typography.labelMedium,
            )
        }
        Spacer(Modifier.height(18.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PlayerTransportControl(
                transportAsset("ic_player_previous"),
                "Previous",
                {},
                if (compact) 30.dp else 36.dp,
                56.dp,
                enabled = kind != "locked",
                reducedMotion = reduced,
            )
            PlayerTransportControl(
                transportAsset(if (playing) "ic_player_pause" else "ic_player_play"),
                if (busy) "Loading audio" else if (playing) "Pause" else "Play",
                { playing = !playing },
                if (compact) 52.dp else 68.dp,
                if (compact) 76.dp else 92.dp,
                busy = busy,
                emphasized = true,
                reducedMotion = reduced,
            )
            PlayerTransportControl(
                transportAsset("ic_player_next"),
                "Next",
                {},
                if (compact) 30.dp else 36.dp,
                56.dp,
                enabled = kind != "locked",
                reducedMotion = reduced,
            )
        }
        Spacer(Modifier.height(18.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("−", color = Color.White.copy(alpha = .6f))
            PlayerTrackInput(
                volume,
                true,
                "Volume",
                { volume = it },
                {},
                {},
                {},
                Modifier.weight(1f).padding(horizontal = 12.dp),
            ) {
                PlayerProgressTrack(
                    { volume },
                    6.dp,
                    { Color.White.copy(alpha = .9f) },
                    Color.White.copy(alpha = .22f),
                )
            }
            Text("+", color = Color.White.copy(alpha = .6f))
        }
        Spacer(Modifier.height(22.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (queue) Spacer(Modifier.width(0.dp))
            else
                PlayerControlSurface(
                    if (lyrics) "Close lyrics" else "Open lyrics",
                    { lyrics = !lyrics },
                    Modifier.size(48.dp),
                    selected = lyrics,
                    reducedMotion = reduced,
                    containerColor =
                        if (lyrics) Color.White.copy(alpha = .2f) else Color.Transparent,
                ) {
                    Icon(
                        VeyraIcons.LyricsQuote,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp),
                    )
                }
            Row(Modifier.background(Color.White.copy(alpha = .1f), RoundedCornerShape(24.dp))) {
                val options =
                    if (queue)
                        listOf(
                            "Shuffle" to VeyraIcons.Shuffle,
                            "Repeat" to VeyraIcons.Repeat,
                            "Autoplay" to VeyraIcons.Infinity,
                        )
                    else
                        listOf(
                            "Audio output" to Icons.Rounded.Headphones,
                            "Listen together" to Icons.Rounded.Person,
                        )
                for ((label, icon) in options) {
                    PlayerControlSurface(
                        label,
                        {},
                        Modifier.size(if (queue) 48.dp else 64.dp, 48.dp),
                        selected = if (queue) label != "Repeat" else null,
                        reducedMotion = reduced,
                        shape = RoundedCornerShape(16.dp),
                        containerColor =
                            if (queue && label != "Repeat") Color.White.copy(alpha = .16f)
                            else Color.Transparent,
                    ) {
                        Icon(icon, null, tint = Color.White, modifier = Modifier.size(26.dp))
                    }
                }
            }
            PlayerControlSurface(
                if (queue) "Close queue" else "Up next",
                { queue = !queue },
                Modifier.size(48.dp),
                selected = queue,
                reducedMotion = reduced,
                containerColor = if (queue) Color.White.copy(alpha = .2f) else Color.Transparent,
            ) {
                Icon(VeyraIcons.Queue, null, tint = Color.White, modifier = Modifier.size(26.dp))
            }
        }
        Text(
            "This phone",
            color = Color.White.copy(alpha = .5f),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 18.dp),
        )
        Text(
            "Original transport glyphs · fixed touch targets\nSelected, disabled and busy states remain explicit.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = .4f),
            modifier = Modifier.padding(top = 30.dp),
        )
    }
}

private fun SemanticsNode.all(): List<SemanticsNode> = listOf(this) + children.flatMap { it.all() }

private fun ImageComposeScene.nodes() = semanticsOwners.flatMap { it.rootSemanticsNode.all() }

private fun ImageComposeScene.named(label: String) =
    nodes().single {
        it.config.getOrNull(SemanticsProperties.ContentDescription)?.contains(label) == true
    }

private fun ImageComposeScene.frame(time: Long = 1_000_000_000L) {
    render(time).close()
}

private fun ImageComposeScene.tap(x: Float, y: Float) {
    sendPointerEvent(
        PointerEventType.Press,
        Offset(x, y),
        buttons = PointerButtons(isPrimaryPressed = true),
    )
    frame()
    sendPointerEvent(PointerEventType.Release, Offset(x, y), buttons = PointerButtons())
    frame()
}

fun main() =
    SwingUtilities.invokeAndWait {
        val output = File(System.getProperty("veyra.preview.output"))
        output.mkdirs()
        for ((kind, width, height, scale) in
            listOf(
                Quad("playing", 390, 800, 1f),
                Quad("paused", 390, 800, 1f),
                Quad("buffering", 390, 800, 1f),
                Quad("locked", 390, 800, 1f),
                Quad("lyrics", 390, 800, 1f),
                Quad("queue", 390, 800, 1f),
                Quad("compact", 640, 760, 1f),
                Quad("reduced", 390, 800, 1f),
                Quad("large-text", 320, 1100, 1.6f),
                Quad("rtl", 390, 800, 1f),
            )) {
            val scene =
                ImageComposeScene(
                    width * 2,
                    height * 2,
                    density = Density(2f, scale),
                    layoutDirection =
                        if (kind == "rtl") LayoutDirection.Rtl else LayoutDirection.Ltr,
                ) {
                    MaterialTheme(colorScheme = DarkColors, typography = VeyraTypography) {
                        PlayerStudy(kind)
                    }
                }
            scene.frame(0)
            val image = scene.render(1_000_000_000L)
            File(output, "$kind.png").writeBytes(image.encodeToData(EncodedImageFormat.PNG)!!.bytes)
            image.close()
            scene.close()
        }
        var checks = 0
        fun verify(name: String, body: () -> Boolean) {
            check(body()) { name }
            checks++
            println("PASS $name")
        }
        var callbacks = 0
        var value = .25f
        var finish = 0
        var cancel = 0
        val enabled = mutableStateOf(true)
        val key = mutableStateOf("track-a")
        val interactive = mutableStateOf(true)
        val busy = mutableStateOf(false)
        val selected = mutableStateOf(false)
        val scene =
            ImageComposeScene(780, 1000, density = Density(2f)) {
                MaterialTheme(colorScheme = DarkColors, typography = VeyraTypography) {
                    CompositionLocalProvider(
                        LocalPlayerControlsInteractive provides interactive.value
                    ) {
                        Column {
                            PlayerTransportControl(
                                transportAsset("ic_player_play"),
                                "Transport",
                                { callbacks++ },
                                48.dp,
                                92.dp,
                                busy = busy.value,
                                emphasized = true,
                                reducedMotion = true,
                            )
                            PlayerControlSurface(
                                "Mode",
                                { selected.value = !selected.value },
                                Modifier.size(48.dp),
                                selected = selected.value,
                                reducedMotion = true,
                            ) {
                                Text("M")
                            }
                            PlayerTrackInput(
                                value,
                                enabled.value,
                                "Position",
                                { value = it },
                                { finish++ },
                                { cancel++ },
                                {},
                                interactionKey = key.value,
                            ) {
                                PlayerProgressTrack({ value }, 7.dp, { Color.White }, Color.Gray)
                            }
                        }
                    }
                }
            }
        scene.frame(0)
        val button = scene.named("Transport")
        verify("button role and fixed 92dp touch area") {
            button.config[SemanticsProperties.Role] == Role.Button &&
                button.boundsInRoot.width == 184f
        }
        scene.tap(button.boundsInRoot.center.x, button.boundsInRoot.center.y)
        verify("transport tap invokes exactly once") { callbacks == 1 }
        busy.value = true
        scene.frame()
        verify("buffering is indeterminate and disabled") {
            scene.named("Transport").config.contains(SemanticsProperties.Disabled) &&
                scene.named("Transport").config[SemanticsProperties.ProgressBarRangeInfo] ==
                    ProgressBarRangeInfo.Indeterminate
        }
        scene.tap(button.boundsInRoot.center.x, button.boundsInRoot.center.y)
        verify("buffering cannot invoke transport") { callbacks == 1 }
        busy.value = false
        scene.frame()
        scene.named("Mode").config[SemanticsActions.OnClick].action!!.invoke()
        scene.frame()
        verify("mode exposes selected state") {
            scene.named("Mode").config[SemanticsProperties.Selected]
        }
        interactive.value = false
        scene.frame()
        scene.tap(button.boundsInRoot.center.x, button.boundsInRoot.center.y)
        verify("outgoing deck cannot receive transport taps") { callbacks == 1 }
        interactive.value = true
        scene.frame()
        val track = scene.named("Position")
        val bounds = track.boundsInRoot
        val y = bounds.center.y
        verify("timeline has at least 48dp touch area") { bounds.height >= 96f }
        scene.tap(bounds.left + bounds.width * .7f, y)
        verify("timeline tap seeks and finishes once") {
            kotlin.math.abs(value - .7f) < .01f && finish == 1 && cancel == 0
        }
        scene.sendPointerEvent(
            PointerEventType.Press,
            Offset(bounds.center.x, y),
            buttons = PointerButtons(isPrimaryPressed = true),
        )
        scene.frame()
        key.value = "track-b"
        scene.frame()
        verify("track change cancels rather than commits") { cancel == 1 && finish == 1 }
        scene.sendPointerEvent(
            PointerEventType.Release,
            Offset(bounds.center.x, y),
            buttons = PointerButtons(),
        )
        scene.frame()
        scene.sendPointerEvent(
            PointerEventType.Press,
            Offset(bounds.center.x, y),
            buttons = PointerButtons(isPrimaryPressed = true),
        )
        scene.frame()
        enabled.value = false
        scene.frame()
        verify("permission lock cancels in-progress seek") { cancel == 2 && finish == 1 }
        scene.sendPointerEvent(
            PointerEventType.Release,
            Offset(bounds.center.x, y),
            buttons = PointerButtons(),
        )
        scene.frame()
        verify("disabled semantic seek is rejected") {
            !scene.named("Position").config[SemanticsActions.SetProgress].action!!.invoke(.8f)
        }
        enabled.value = true
        scene.frame()
        verify("nonfinite semantic seek is rejected") {
            !scene.named("Position").config[SemanticsActions.SetProgress].action!!.invoke(Float.NaN)
        }
        verify("accessible seek clamps and commits once") {
            scene.named("Position").config[SemanticsActions.SetProgress].action!!.invoke(2f) &&
                value == 1f &&
                finish == 2
        }
        scene.sendPointerEvent(
            PointerEventType.Press,
            Offset(bounds.left + bounds.width * .2f, y),
            buttons = PointerButtons(isPrimaryPressed = true),
        )
        scene.frame()
        scene.sendPointerEvent(
            PointerEventType.Release,
            Offset(bounds.left + bounds.width * .8f, y),
            buttons = PointerButtons(),
        )
        scene.frame()
        verify("release position is committed when no move event arrives") {
            kotlin.math.abs(value - .8f) < .01f && finish == 3
        }
        scene.sendPointerEvent(
            PointerEventType.Press,
            Offset(bounds.center.x, y),
            buttons = PointerButtons(isPrimaryPressed = true),
        )
        scene.frame()
        interactive.value = false
        scene.frame()
        verify("hiding the deck cancels an active seek") { cancel == 3 && finish == 3 }
        scene.sendPointerEvent(
            PointerEventType.Release,
            Offset(bounds.center.x, y),
            buttons = PointerButtons(),
        )
        scene.frame()
        scene.close()
        var animatedClicks = 0
        val animated =
            ImageComposeScene(240, 240, density = Density(2f)) {
                PlayerTransportControl(
                    transportAsset("ic_player_play"),
                    "Animated",
                    { animatedClicks++ },
                    68.dp,
                    92.dp,
                    emphasized = true,
                )
            }
        animated.frame(0)
        val fixedBounds = animated.named("Animated").boundsInRoot
        animated.sendPointerEvent(
            PointerEventType.Press,
            fixedBounds.center,
            buttons = PointerButtons(isPrimaryPressed = true),
        )
        animated.frame(16_000_000)
        animated.frame(76_000_000)
        verify("animated press keeps its original hit region") {
            animated.named("Animated").boundsInRoot == fixedBounds
        }
        animated.sendPointerEvent(
            PointerEventType.Release,
            fixedBounds.center,
            buttons = PointerButtons(),
        )
        animated.frame(152_000_000)
        verify("animated press releases to exactly one action") { animatedClicks == 1 }
        animated.close()
        val rtl =
            ImageComposeScene(
                600,
                160,
                density = Density(2f),
                layoutDirection = LayoutDirection.Rtl,
            ) {
                PlayerTrackInput(.5f, true, "RTL", { value = it }, { finish++ }, { cancel++ }, {}) {
                    PlayerProgressTrack({ value }, 7.dp, { Color.White }, Color.Gray)
                }
            }
        rtl.frame(0)
        rtl.tap(60f, 48f)
        verify("RTL seek matches reversed timeline") { kotlin.math.abs(value - .9f) < .01f }
        rtl.close()
        verify("invalid geometry cannot produce a seek") {
            playerPointerFraction(Float.NaN, 100f, false) == null &&
                playerPointerFraction(2f, 0f, false) == null
        }
        verify("reduced motion uses zero-duration transitions") {
            playerMotionMillis(true, 280) == 0 && playerMotionMillis(false, 280) == 280
        }
        val scrub = PlayerScrub()
        var seekCalls = 0
        scrub.drag(.8f)
        scrub.cancel()
        verify("cancel clears drag and pending override") {
            !scrub.scrubbing && scrub.pendingSeek == null && scrub.shown(1000, 10000) == .1f
        }
        scrub.drag(.6f)
        scrub.release { seekCalls++ }
        verify("released seek keeps its target while buffering") {
            seekCalls == 1 && !scrub.scrubbing && scrub.pendingSeek == .6f
        }
        println(
            "$checks player component checks passed; ten native component review scenes rendered."
        )
    }

private data class Quad(val kind: String, val width: Int, val height: Int, val scale: Float)
