package app.veyra.music.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import app.veyra.music.ui.themes.*
import org.jetbrains.skia.EncodedImageFormat
import org.junit.Assert.*
import org.junit.Test
import javax.imageio.ImageIO
import javax.swing.SwingUtilities

/** Production overlay compositing; Android GPU refraction requires a real device. */
@OptIn(ExperimentalComposeUiApi::class)
class ThemeNavigationGlassTest {
    @Test fun themeAndAssetDecorationsLeaveGlassVisibleWhileOffKeepsThemedFill() {
        val theme=ThemeVisuals("fixture","Matcha",Color(0xFFD6E9A3),Color(0xFF1C3829),
            Color(0xFF34513C),Color(0xFF718764),16,18,24,emptyMap())
        val original=theme.copy(id="@original",assets=emptyMap())
        val assets=AssetVisuals("fixture","Chrome",emptyMap(),"punk_chrome","plasma",8,20,24,32,"#CAF7E8","punk_chrome")
        val cases=listOf(theme,theme.copy(sceneStyle="sketch"),assets.over(theme,AssetOptions()),assets.over(original,AssetOptions()))
        val left=0xFF196FD4.toInt()
        val right=0xFFE540A4.toInt()
        for(pack in cases) for(glass in listOf(false,true)) {
            var scene:ImageComposeScene?=null
            try {
                SwingUtilities.invokeAndWait {
                    scene=ImageComposeScene(208,96,density=Density(1f)) {
                        CompositionLocalProvider(LocalThemeVisuals provides pack) {
                            MaterialTheme {
                                Box(Modifier.fillMaxSize()) {
                                    Canvas(Modifier.matchParentSize()) {
                                        drawRect(Color(left),size=Size(size.width/2,size.height))
                                        drawRect(Color(right),topLeft=Offset(size.width/2,0f),size=Size(size.width/2,size.height))
                                    }
                                    Box(Modifier.matchParentSize().themeNavigationSurface(RoundedCornerShape(24.dp),glass=glass))
                                }
                            }
                        }
                    }
                    val frame=scene!!.render(1_000_000_000L)
                    try {
                        val pixels=ImageIO.read(frame.encodeToData(EncodedImageFormat.PNG)!!.bytes.inputStream())
                        if(glass) {
                            assertEquals("${pack.id}: left glass obscured",left,pixels.getRGB(52,40))
                            assertEquals("${pack.id}: right glass obscured",right,pixels.getRGB(156,40))
                        } else {
                            assertNotEquals(left,pixels.getRGB(52,40))
                            assertNotEquals(right,pixels.getRGB(156,40))
                        }
                    } finally {frame.close()}
                }
            } finally {SwingUtilities.invokeAndWait {scene?.close()}}
        }
    }
}
