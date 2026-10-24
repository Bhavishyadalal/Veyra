package app.veyra.music

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import app.veyra.music.ui.player.PlayerControlSurface
import app.veyra.music.ui.themes.*
import org.jetbrains.skia.EncodedImageFormat
import org.junit.Assert.*
import org.junit.Test
import javax.imageio.ImageIO
import javax.swing.SwingUtilities

@OptIn(ExperimentalComposeUiApi::class)
class ThemeControlShapeRefreshTest {
    @Test fun sceneOnlyThemeChangeRefreshesExistingPlayerButtonShape() {
        val theme=ThemeVisuals("fixture","Fixture",Color.White,Color.Black,Color.Black,Color.White,16,24,24,emptyMap())
        var sceneStyle by mutableStateOf("mist")
        var scene:ImageComposeScene?=null
        try {
            SwingUtilities.invokeAndWait {
                scene=ImageComposeScene(64,64,density=Density(1f)) {
                    CompositionLocalProvider(LocalThemeVisuals provides theme.copy(sceneStyle=sceneStyle)) {
                        MaterialTheme {
                            Box(Modifier.fillMaxSize().background(Color.Black)) {
                                PlayerControlSurface("Control",{},Modifier.size(64.dp),visualSize=64.dp) {
                                    Box(Modifier.fillMaxSize().background(Color.White))
                                }
                            }
                        }
                    }
                }
                fun pixel(time:Long):Int {
                    scene!!.render(time).close()
                    val frame=scene!!.render(time+100_000_000L)
                    try {return ImageIO.read(frame.encodeToData(EncodedImageFormat.PNG)!!.bytes.inputStream()).getRGB(8,10)}
                    finally {frame.close()}
                }
                val round=pixel(1_000_000_000L)
                sceneStyle="web"
                val cut=pixel(2_000_000_000L)
                assertNotEquals(round,cut)
                sceneStyle="mist"
                assertEquals(round,pixel(3_000_000_000L))
            }
        } finally {SwingUtilities.invokeAndWait {scene?.close()}}
    }
}
