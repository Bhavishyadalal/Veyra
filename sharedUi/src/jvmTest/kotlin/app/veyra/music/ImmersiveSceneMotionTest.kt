package app.veyra.music

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.unit.dp
import app.veyra.music.ui.player.rememberImmersiveScenePhase
import org.junit.Assert.*
import org.junit.Test
import javax.swing.SwingUtilities

@OptIn(ExperimentalComposeUiApi::class)
class ImmersiveSceneMotionTest {
    @Test fun sceneMotionDrawsWithoutRecomposingAndStopsWhenInactive() {
        var active by mutableStateOf(true)
        var compositions = 0
        var drawnPhase = 0f
        var scene: ImageComposeScene? = null
        var time = 1_000_000_000L
        fun frame() { scene!!.render(time).close(); time += 40_000_000 }
        try {
            SwingUtilities.invokeAndWait {
                scene = ImageComposeScene(40, 40) {
                    compositions++
                    val phase = rememberImmersiveScenePhase(active)
                    Box(Modifier.size(40.dp).drawBehind { drawnPhase = phase.value })
                }
                frame(); frame()
            }
            repeat(8) { SwingUtilities.invokeAndWait { frame() } }
            SwingUtilities.invokeAndWait {
                assertTrue("active scene never advanced", drawnPhase > 0f)
                assertEquals("drawing must not invalidate the screen's composition", 1, compositions)
                active = false; frame(); frame()
                val stopped = drawnPhase
                repeat(10) { frame() }
                assertEquals("paused/reduced-motion scenes must stop", stopped, drawnPhase, .00001f)
                time += 60_000_000_000L
                active = true; frame(); frame(); frame()
                assertTrue("resume must not pay back background time as a jump", drawnPhase - stopped < .01f)
            }
        } finally { SwingUtilities.invokeAndWait { scene?.close() } }
    }
}
