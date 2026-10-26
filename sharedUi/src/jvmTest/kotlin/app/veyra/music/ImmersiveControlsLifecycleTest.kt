package app.veyra.music

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerButtons
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.platform.AccessibilityManager
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.unit.Density
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner
import app.veyra.music.data.model.Song
import app.veyra.music.data.settings.ExperienceOptions
import app.veyra.music.data.settings.ImmersiveStyle
import app.veyra.music.playback.PlaybackPosition
import app.veyra.music.ui.player.ImmersivePlayer
import app.veyra.music.ui.player.ImmersiveTimeline
import app.veyra.music.ui.player.PlayerPlatform
import org.junit.Assert.*
import org.junit.Test
import javax.swing.SwingUtilities

/** Exercises the production footer, rather than a duplicate of its visibility logic. */
@OptIn(ExperimentalComposeUiApi::class)
class ImmersiveControlsLifecycleTest {
    @Test fun shortLandscapeWithLargeTextKeepsAReachableScrollableControlDeck() {
        PlayerPlatform.install(RenderPlayerHost())
        var scene: ImageComposeScene? = null
        fun SemanticsNode.all(): List<SemanticsNode> = listOf(this) + children.flatMap { it.all() }
        try {
            SwingUtilities.invokeAndWait {
                scene = ImageComposeScene(600, 320, density = Density(1f, 1.8f)) {
                    MaterialTheme(colorScheme = darkColorScheme()) {
                        ImmersivePlayer(
                            song = Song("short", "A long song title for a small landscape screen", "Veyra", null),
                            artwork = null, canvas = null, isPlaying = false,
                            position = PlaybackPosition(), durationMs = 180_000, lines = emptyList(), looking = false,
                            options = ExperienceOptions(), onOptions = {}, onExit = {}, onPlayPause = {},
                            onPrevious = {}, onNext = {}, hasPrevious = true, hasNext = true,
                            locked = false, onBlocked = {}, onSeek = {}, onSource = {}, onTiming = {},
                            onSongOptions = {}, windowed = false,
                        )
                    }
                }
                repeat(3) { scene!!.render(it * 40_000_000L).close() }
                val nodes = scene!!.semanticsOwners.flatMap { it.rootSemanticsNode.all() }
                val scrollable = nodes.firstOrNull {
                    it.config.getOrNull(SemanticsProperties.VerticalScrollAxisRange)?.maxValue?.invoke()?.let { max -> max > 0f } == true
                }
                assertNotNull("large controls must scroll instead of overflowing the window", scrollable)
                assertTrue(scrollable!!.boundsInRoot.top >= 0f)
                assertTrue(scrollable.boundsInRoot.bottom <= 320f)
            }
        } finally { SwingUtilities.invokeAndWait { scene?.close() } }
    }

    @Test fun autoHideDoesNotRevealItsOwnFooterAgain() {
        for (style in listOf(ImmersiveStyle.CLASSIC, ImmersiveStyle.MINIMAL_NIGHT)) {
            checkAutoHide(style, reduced = false)
        }
    }

    @Test fun reducedMotionKeepsTheSameAutoHideAndTapBehavior() {
        checkAutoHide(ImmersiveStyle.CLASSIC, reduced = true)
    }

    @Test fun retiringADragCleansUpWithoutLosingTheNextValidSeek() {
        var scene: ImageComposeScene? = null
        var showing by mutableStateOf(true)
        var callbackTag by mutableStateOf("before")
        val events = mutableListOf<Pair<String, Boolean>>()
        var seeks = 0
        val position = PlaybackPosition().apply { report(20_000) }
        var frameTime = System.nanoTime()
        fun frame() { scene!!.render(frameTime).close(); frameTime += 40_000_000 }
        try {
            SwingUtilities.invokeAndWait {
                scene = ImageComposeScene(390, 100) {
                    MaterialTheme {
                        if (showing) {
                            val tag = callbackTag
                            ImmersiveTimeline("drag", position, 180_000, false, Color.White,
                                { seeks++ }, { events += tag to it })
                        }
                    }
                }
                frame(); frame()
                scene!!.sendPointerEvent(PointerEventType.Press, Offset(60f,16f), buttons = PointerButtons(isPrimaryPressed=true))
                frame()
                scene!!.sendPointerEvent(PointerEventType.Move, Offset(190f,16f), buttons = PointerButtons(isPrimaryPressed=true))
                frame(); frame()
                assertTrue("seek gesture never started", events.any { it.second })
                assertEquals("seek finished before cancellation", 0, seeks)
                callbackTag = "after"; frame(); frame()
                showing = false; frame(); frame()
                assertEquals("cleanup retained a stale callback", "after" to false, events.last())
                assertEquals("cancelled seek must not jump playback", 0, seeks)
                scene!!.sendPointerEvent(PointerEventType.Release, Offset(190f,16f), buttons = PointerButtons())
                frame()
            }
            SwingUtilities.invokeAndWait {
                showing = true; frame(); frame()
                scene!!.sendPointerEvent(PointerEventType.Press, Offset(60f,16f), buttons = PointerButtons(isPrimaryPressed=true))
                frame()
                scene!!.sendPointerEvent(PointerEventType.Move, Offset(190f,16f), buttons = PointerButtons(isPrimaryPressed=true))
                frame()
                scene!!.sendPointerEvent(PointerEventType.Release, Offset(190f,16f), buttons = PointerButtons())
                frame(); frame()
            }
            repeat(3) { Thread.sleep(20); SwingUtilities.invokeAndWait { frame() } }
            SwingUtilities.invokeAndWait { assertEquals("a completed seek must commit once", 1, seeks) }
        } finally { SwingUtilities.invokeAndWait { scene?.close() } }
    }

    private fun checkAutoHide(style: ImmersiveStyle, reduced: Boolean) {
        PlayerPlatform.install(RenderPlayerHost(reduced))
        val owner = object : LifecycleOwner {
            override val lifecycle = LifecycleRegistry.createUnsafe(this).apply {
                currentState = Lifecycle.State.RESUMED
            }
        }
        val timeout = object : AccessibilityManager {
            override fun calculateRecommendedTimeoutMillis(
                originalTimeoutMillis: Long, containsIcons: Boolean,
                containsText: Boolean, containsControls: Boolean,
            ) = 120L
        }
        val position = PlaybackPosition().apply { report(12_000) }
        var scene: ImageComposeScene? = null
        var frameTime = System.nanoTime()
        fun frame() { scene!!.render(frameTime).close(); frameTime += 40_000_000 }
        fun SemanticsNode.all(): List<SemanticsNode> = listOf(this) + children.flatMap { it.all() }
        fun sliderCount(): Int = scene!!.semanticsOwners.flatMap { it.rootSemanticsNode.all() }
            .count { it.config.getOrNull(SemanticsProperties.ProgressBarRangeInfo) != null }
        fun settle(frames: Int) {
            repeat(frames) { Thread.sleep(20); SwingUtilities.invokeAndWait { frame() } }
        }
        try {
            SwingUtilities.invokeAndWait {
                scene = ImageComposeScene(390, 844) {
                    CompositionLocalProvider(LocalLifecycleOwner provides owner,
                        LocalAccessibilityManager provides timeout) {
                        MaterialTheme(colorScheme = darkColorScheme()) {
                            ImmersivePlayer(
                                song = Song("lifecycle", "A quiet evening", "Veyra", null),
                                artwork = null, canvas = null, isPlaying = true,
                                position = position, durationMs = 180_000, lines = emptyList(), looking = false,
                                options = ExperienceOptions(immersiveStyle = style), onOptions = {},
                                onExit = {}, onPlayPause = {}, onPrevious = {}, onNext = {},
                                hasPrevious = true, hasNext = true, locked = false, onBlocked = {},
                                onSeek = {}, onSource = {}, onTiming = {}, onSongOptions = {}, windowed = false,
                            )
                        }
                    }
                }
                frame(); frame()
                assertEquals("footer was not initially visible", 1, sliderCount())
            }
            settle(25)
            repeat(8) { i ->
                SwingUtilities.invokeAndWait {
                    position.report(13_000L + i * 500)
                    frame(); frame()
                    assertEquals("$style footer reappeared after disposal", 0, sliderCount())
                }
                Thread.sleep(35)
            }
            SwingUtilities.invokeAndWait {
                scene!!.sendPointerEvent(PointerEventType.Press, Offset(10f,400f), buttons = PointerButtons(isPrimaryPressed=true))
                frame()
                scene!!.sendPointerEvent(PointerEventType.Release, Offset(10f,400f), buttons = PointerButtons())
                frame(); frame()
                assertEquals("background tap must reveal controls", 1, sliderCount())
            }
            settle(25)
            SwingUtilities.invokeAndWait { assertEquals("second hide must stay hidden", 0, sliderCount()) }
        } finally { SwingUtilities.invokeAndWait { scene?.close() } }
    }
}
