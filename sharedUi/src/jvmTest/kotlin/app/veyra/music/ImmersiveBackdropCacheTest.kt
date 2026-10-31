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
import app.veyra.music.data.settings.ImmersiveStyle
import app.veyra.music.ui.player.immersiveBackdropMotion
import org.junit.Assert.*
import org.junit.Test
import javax.swing.SwingUtilities

@OptIn(ExperimentalComposeUiApi::class)
class ImmersiveBackdropCacheTest {
    @Test fun movingBackdropLayerDoesNotRerecordItsDrawing() {
        var phase by mutableFloatStateOf(0f)
        var draws = 0
        var scene: ImageComposeScene? = null
        var time = 1_000_000_000L
        fun frame() { scene!!.render(time).close(); time += 40_000_000L }
        try {
            SwingUtilities.invokeAndWait {
                scene = ImageComposeScene(width = 120, height = 120) {
                    val state = remember { derivedStateOf { phase } }
                    Box(Modifier.size(100.dp).immersiveBackdropMotion(ImmersiveStyle.NEON_TUNNEL, state)
                        .drawWithCache { onDrawBehind { draws++; drawRect(Color.Magenta) } })
                }
                frame(); frame()
                val initial = draws
                assertTrue(initial > 0)
                repeat(10) { phase += .01f; frame() }
                assertEquals("scene animation must reuse its vector display list", initial, draws)
            }
        } finally { SwingUtilities.invokeAndWait { scene?.close() } }
    }
}
