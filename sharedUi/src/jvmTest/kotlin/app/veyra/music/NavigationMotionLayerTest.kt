package app.veyra.music

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.veyra.music.ui.themes.navigationPillMotion
import org.junit.Assert.*
import org.junit.Test
import javax.swing.SwingUtilities

@OptIn(ExperimentalComposeUiApi::class)
class NavigationMotionLayerTest {
    @Test fun pillTravelDoesNotRecomposeOrRerecordTheBar() {
        var offset by mutableFloatStateOf(0f)
        var compositions = 0
        var draws = 0
        var scene: ImageComposeScene? = null
        var time = 1_000_000_000L
        fun frame() { scene!!.render(time).close(); time += 40_000_000L }
        try {
            SwingUtilities.invokeAndWait {
                scene = ImageComposeScene(width = 240, height = 100) {
                    SideEffect { compositions++ }
                    Box(Modifier.size(40.dp).navigationPillMotion({ offset }, 120f, 60f, .2f, .3f)
                        .drawWithCache { onDrawBehind { draws++; drawRect(Color.Magenta) } })
                }
                frame(); frame()
                val initialDraws = draws
                val initialCompositions = compositions
                assertTrue(initialDraws > 0)
                repeat(12) { offset += 10f; frame() }
                assertEquals(initialCompositions, compositions)
                assertEquals(initialDraws, draws)
            }
        } finally { SwingUtilities.invokeAndWait { scene?.close() } }
    }
}
