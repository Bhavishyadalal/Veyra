package app.veyra.music

import androidx.compose.animation.*
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerButtons
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.lifecycle.*
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.veyra.music.data.settings.AppMotionStyle
import app.veyra.music.data.settings.MotionTempo
import app.veyra.music.ui.motion.PageLifetime
import app.veyra.music.ui.motion.pageOverlayTransition
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.skia.EncodedImageFormat
import org.junit.Assert.*
import org.junit.Test
import javax.imageio.ImageIO
import javax.swing.SwingUtilities

@OptIn(ExperimentalComposeUiApi::class)
class PageOverlayLifecycleTest {
    private fun owner() = object : LifecycleOwner {
        override val lifecycle = LifecycleRegistry.createUnsafe(this).apply { currentState = Lifecycle.State.RESUMED }
    }

    @Test fun outgoingPagesCannotDispatchClicksAndTheRetainedPageCanBeUsedAgain() {
        var active by mutableStateOf(false)
        var clicks = 0
        var scene: ImageComposeScene? = null
        val parent = owner()
        var time = 1_000_000_000L
        fun frame() { scene!!.render(time).close(); time += 40_000_000 }
        fun tap() {
            scene!!.sendPointerEvent(PointerEventType.Press, Offset(50f, 50f), buttons = PointerButtons(isPrimaryPressed = true))
            frame()
            scene!!.sendPointerEvent(PointerEventType.Release, Offset(50f, 50f), buttons = PointerButtons())
            frame(); frame()
        }
        try {
            SwingUtilities.invokeAndWait {
                scene = ImageComposeScene(100, 100) {
                    CompositionLocalProvider(LocalLifecycleOwner provides parent) {
                        PageLifetime(active) { Box(Modifier.fillMaxSize().clickable { clicks++ }) }
                    }
                }
                frame(); frame(); tap()
                assertEquals(0, clicks)
                active = true; frame(); frame(); tap()
                assertEquals(1, clicks)
                active = false; frame(); frame(); tap()
                assertEquals(1, clicks)
            }
        } finally { SwingUtilities.invokeAndWait { scene?.close() } }
    }

    @Test fun everyDismissalAnimatesTheOutgoingPanelInsteadOfAnEmptyIncomingSlot() {
        for (style in AppMotionStyle.entries) for (tempo in MotionTempo.entries)
            for (rtl in listOf(false, true)) for (material in listOf("classic", "punk_gel", "botanical", "telemetry")) {
                val back = pageOverlayTransition(false, false, rtl, style = style, tempo = tempo, materialStyle = material)
                assertEquals(EnterTransition.None, back.targetContentEnter)
                assertNotEquals(ExitTransition.None, back.initialContentExit)
                assertEquals(-1f, back.targetContentZIndex)
                val reduced = pageOverlayTransition(false, true, rtl, style = style, tempo = tempo, materialStyle = material)
                assertEquals(ExitTransition.None, reduced.initialContentExit)
            }
    }

    @Test fun returningFromSourcesRevealsSettingsGraduallyWithoutRemountingIt() {
        for (style in listOf(AppMotionStyle.DEFAULT, AppMotionStyle.SOFT_ZOOM, AppMotionStyle.VELVET)) {
            var visible by mutableStateOf(true)
            var mounted = 0
            var scene: ImageComposeScene? = null
            val parent = owner()
            var time = 1_000_000_000L
            fun pixel(): Int {
                val image = scene!!.render(time)
                time += 40_000_000
                try { return ImageIO.read(image.encodeToData(EncodedImageFormat.PNG)!!.bytes.inputStream()).getRGB(80, 80) }
                finally { image.close() }
            }
            try {
                SwingUtilities.invokeAndWait {
                    scene = ImageComposeScene(100, 100) {
                        CompositionLocalProvider(LocalLifecycleOwner provides parent) {
                            val transition = updateTransition(visible)
                            val covered = visible && transition.currentState && !transition.isRunning
                            Box(Modifier.fillMaxSize()) {
                                PageLifetime(!visible, !covered) {
                                    remember { mounted++ }
                                    Box(Modifier.fillMaxSize().background(Color.Blue))
                                }
                                transition.AnimatedContent(modifier = Modifier.fillMaxSize(), transitionSpec = {
                                    pageOverlayTransition(targetState, false, false, style = style, materialStyle = "punk_gel")
                                }) { source -> if (source) PageLifetime(visible) {
                                    Box(Modifier.fillMaxSize().background(Color.Red))
                                } }
                            }
                        }
                    }
                    pixel(); pixel()
                    assertEquals(0xFFFF0000.toInt(), pixel())
                    visible = false
                    pixel(); pixel()
                    val midway = pixel()
                    assertNotEquals("Sources must start fading before its removal", 0xFFFF0000.toInt(), midway)
                    assertNotEquals("Back must not abruptly replace Sources", 0xFF0000FF.toInt(), midway)
                    time += 1_000_000_000L
                    pixel(); pixel()
                    assertEquals(0xFF0000FF.toInt(), pixel())
                    assertEquals("Settings must retain its state across the overlay", 1, mounted)
                }
            } finally { SwingUtilities.invokeAndWait { scene?.close() } }
        }
    }

    @Test fun coveredCollectorsPauseAndResumeWithoutLosingRememberedState() {
        var active by mutableStateOf(true)
        var shown by mutableStateOf(true)
        val values = MutableStateFlow(1)
        var observed = -1
        var pageOwner: LifecycleOwner? = null
        var scene: ImageComposeScene? = null
        val parent = owner()
        var time = 1_000_000_000L
        fun frame() { scene!!.render(time).close(); time += 40_000_000 }
        fun settle() { repeat(4) { Thread.sleep(20); SwingUtilities.invokeAndWait { frame() } } }
        try {
            SwingUtilities.invokeAndWait {
                scene = ImageComposeScene(100, 100) {
                    CompositionLocalProvider(LocalLifecycleOwner provides parent) {
                        if (shown) PageLifetime(active) {
                            pageOwner = LocalLifecycleOwner.current
                            val value by values.collectAsStateWithLifecycle()
                            observed = value
                            Text("$value")
                        }
                    }
                }
                frame(); frame()
            }
            settle()
            SwingUtilities.invokeAndWait {
                assertEquals(1, observed)
                active = false; frame(); frame()
                assertEquals(Lifecycle.State.CREATED, pageOwner!!.lifecycle.currentState)
                values.value = 42
            }
            settle()
            SwingUtilities.invokeAndWait { assertEquals(1, observed); active = true; frame(); frame() }
            settle()
            SwingUtilities.invokeAndWait {
                assertEquals(42, observed)
                assertEquals(Lifecycle.State.RESUMED, pageOwner!!.lifecycle.currentState)
                shown = false; frame(); frame()
                assertEquals(Lifecycle.State.DESTROYED, pageOwner!!.lifecycle.currentState)
            }
        } finally { SwingUtilities.invokeAndWait { scene?.close() } }
    }
}
