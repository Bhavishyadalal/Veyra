package app.veyra.music

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.unit.dp
import app.veyra.music.ui.player.PlayerControlSurface
import org.junit.Assert.*
import org.junit.Test
import javax.swing.SwingUtilities

@OptIn(ExperimentalComposeUiApi::class)
class PlayerHoldLifecycleTest {
    @Test fun cancelledHoldDoesNotSuppressATapAfterHoldSupportIsRemoved() = checkHold(cancel=true)
    @Test fun completedHoldDoesNotAlsoDispatchAShortClick() = checkHold(cancel=false)
    private fun checkHold(cancel:Boolean) {
        var scene:ImageComposeScene?=null
        var holds=0;var clicks=0
        var holdEnabled by mutableStateOf(true)
        var time=1_000_000_000L
        fun frame() {scene!!.render(time).close();time+=40_000_000L}
        fun event(type:PointerEventType,position:Offset,down:Boolean) {
            scene!!.sendPointerEvent(type,position,buttons=PointerButtons(isPrimaryPressed=down));frame()
        }
        try {
            SwingUtilities.invokeAndWait {
                scene=ImageComposeScene(64,64) {
                    val base=LocalViewConfiguration.current
                    val config=remember(base) {object:ViewConfiguration by base {override val longPressTimeoutMillis=30L}}
                    CompositionLocalProvider(LocalViewConfiguration provides config) {
                        MaterialTheme {PlayerControlSurface("Seek",{clicks++},Modifier.size(64.dp),onHold=if(holdEnabled)({holds++}) else null) {}}
                    }
                }
                frame();frame();event(PointerEventType.Press,Offset(32f,32f),true)
            }
            repeat(10) {Thread.sleep(20);SwingUtilities.invokeAndWait {frame()}}
            SwingUtilities.invokeAndWait {
                assertTrue("hold callback did not run",holds>0)
                if(cancel) {
                    event(PointerEventType.Move,Offset(-100f,-100f),true)
                    event(PointerEventType.Release,Offset(-100f,-100f),false)
                    holdEnabled=false;frame();frame()
                    event(PointerEventType.Press,Offset(32f,32f),true)
                    event(PointerEventType.Release,Offset(32f,32f),false)
                    frame();assertEquals(1,clicks)
                } else {
                    event(PointerEventType.Release,Offset(32f,32f),false)
                    frame();assertEquals(0,clicks)
                }
            }
        } finally {SwingUtilities.invokeAndWait {scene?.close()}}
    }
}
