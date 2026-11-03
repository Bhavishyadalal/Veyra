package app.veyra.music

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.veyra.music.ui.customization.LocalReducedVisualEffects
import app.veyra.music.ui.customization.visualEffectShadow
import androidx.compose.foundation.shape.CircleShape
import app.veyra.music.ui.customization.visualEffectBlur
import app.veyra.music.ui.components.optimizedHazeEffect
import app.veyra.music.ui.themes.*
import dev.chrisbanes.haze.HazeState
import org.jetbrains.skia.EncodedImageFormat
import org.junit.Assert.*
import org.junit.Test
import javax.imageio.ImageIO
import javax.swing.SwingUtilities

@OptIn(ExperimentalComposeUiApi::class)
class ReducedVisualEffectsRenderTest {
    private fun pixels(content: @Composable () -> Unit): java.awt.image.BufferedImage {
        var scene: ImageComposeScene? = null
        var image: java.awt.image.BufferedImage? = null
        try {
            SwingUtilities.invokeAndWait {
                scene = ImageComposeScene(width=160,height=160) { content() }
                repeat(4) { scene!!.render(it * 50_000_000L).close() }
                val frame=scene!!.render(250_000_000L)
                try {image=ImageIO.read(frame.encodeToData(EncodedImageFormat.PNG)!!.bytes.inputStream())}
                finally {frame.close()}
            }
        } finally {SwingUtilities.invokeAndWait {scene?.close()}}
        return image!!
    }

    @Test fun reducedHazeBecomesAnOpaqueSurfaceWithoutARequiredBackdropSource() {
        val image=pixels {
            CompositionLocalProvider(LocalReducedVisualEffects provides true) {
                MaterialTheme(colorScheme=darkColorScheme(surface=Color(0xFF123456))) {
                    Box(Modifier.fillMaxSize().background(Color.Magenta)) {
                        Box(Modifier.fillMaxSize().optimizedHazeEffect(remember {HazeState()}))
                    }
                }
            }
        }
        assertEquals(0xFF123456.toInt(), image.getRGB(80,80))
        assertEquals(255, image.getRGB(80,80) ushr 24)
    }

    @Test fun reducedThemeSceneUsesOnlyItsOpaquePaletteFloor() {
        val theme=ThemeVisuals("test","test",Color.Cyan,Color(0xFF345678),Color.Gray,Color.White,
            20,20,20,emptyMap(),sceneStyle="web")
        val image=pixels {
            CompositionLocalProvider(LocalReducedVisualEffects provides true,LocalThemeVisuals provides theme) {
                ThemeScene(Modifier.fillMaxSize(),prominent=true)
            }
        }
        for (point in listOf(4 to 4,80 to 80,150 to 150))
            assertEquals(0xFF345678.toInt(),image.getRGB(point.first,point.second))
    }

    @Test fun liveToggleRestoresBlurThenRemovesItAgain() {
        var reduced by mutableStateOf(true)
        var scene: ImageComposeScene? = null
        var time = 1_000_000_000L
        fun sample(): Int {
            repeat(3) {scene!!.render(time).close();time+=50_000_000L}
            val frame=scene!!.render(time);time+=50_000_000L
            try {return ImageIO.read(frame.encodeToData(EncodedImageFormat.PNG)!!.bytes.inputStream()).getRGB(55,20)}
            finally {frame.close()}
        }
        try {
            SwingUtilities.invokeAndWait {
                scene=ImageComposeScene(width=160,height=160) {
                    CompositionLocalProvider(LocalReducedVisualEffects provides reduced) {
                        Box(Modifier.fillMaxSize().background(Color.Black)) {
                            Box(Modifier.size(80.dp).visualEffectBlur(24.dp)) {
                                Box(Modifier.size(40.dp).background(Color.Red))
                            }
                        }
                    }
                }
                assertEquals(0xFF000000.toInt(),sample())
                reduced=false
                assertNotEquals(0xFF000000.toInt(),sample())
                reduced=true
                assertEquals(0xFF000000.toInt(),sample())
            }
        } finally {SwingUtilities.invokeAndWait {scene?.close()}}
    }

    @Test fun removingDecorativeShadowRetainsShapeClipping() {
        val image=pixels {
            CompositionLocalProvider(LocalReducedVisualEffects provides true) {
                Box(Modifier.fillMaxSize().background(Color.Black)) {
                    Box(Modifier.size(80.dp).visualEffectShadow(12.dp,CircleShape).background(Color.Red))
                }
            }
        }
        assertEquals(0xFF000000.toInt(),image.getRGB(0,0))
        assertEquals(0xFFFF0000.toInt(),image.getRGB(40,40))
        assertEquals(0xFF000000.toInt(),image.getRGB(90,40))
    }

    @Test fun reducedBlurKeepsContentSharpAndOpaque() {
        val image=pixels {
            CompositionLocalProvider(LocalReducedVisualEffects provides true) {
                Box(Modifier.fillMaxSize().background(Color.Black)) {
                    Box(Modifier.size(80.dp).visualEffectBlur(24.dp).background(Color.Red))
                }
            }
        }
        assertEquals(0xFFFF0000.toInt(),image.getRGB(4,4))
        assertEquals(0xFF000000.toInt(),image.getRGB(90,40))
    }
}
