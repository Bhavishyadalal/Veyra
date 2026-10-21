package app.veyra.music.ui.styles

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.clearAndSetSemantics
import coil3.compose.AsyncImage
import java.io.File

/** Static, offline decorative art. No animation clock or network request. */
@Composable
fun StyleBackdrop(style:AppStyle, artwork:String?, modifier:Modifier=Modifier) {
    val p=app.veyra.music.ui.themes.LocalThemeVisuals.current ?: style.visuals() ?: return
    Box(modifier.background(p.background).clearAndSetSemantics {}) {
        if(style==AppStyle.LISTENING_ROOM && artwork!=null) {
            AsyncImage(File(artwork),null,Modifier.matchParentSize(),contentScale=ContentScale.Crop,alpha=.50f)
            Box(Modifier.matchParentSize().background(p.background.copy(alpha=.30f)))
        }
        Canvas(Modifier.matchParentSize()) {
            if(style==AppStyle.AFTER_DARK) {
                drawRect(Brush.radialGradient(listOf(Color(0xFF10325A).copy(alpha=.35f),Color.Transparent),
                    Offset(size.width*.8f,size.height*.22f),size.maxDimension*.65f))
                repeat(4) {i->val x=size.width*(.16f+i*.24f);drawLine(p.outline.copy(alpha=.12f),Offset(x,0f),Offset(x,size.height),1f)}
            } else if(style==AppStyle.MUSIC_EDITORIAL) {
                repeat(160) {i->drawCircle(p.outline.copy(alpha=.07f),.8f,Offset(size.width*(i*37%163)/163f,size.height*(i*61%167)/167f))}
            }
        }
    }
}

/** Abstract fallback for After Dark: a record halo, with no stock portrait or implied song. */
@Composable
fun NightRecordFallback(modifier:Modifier=Modifier) {
    Canvas(modifier.background(Color(0xFF081528))) {
        drawRect(Brush.radialGradient(listOf(Color(0xFF2966B2),Color(0xFF09172C)),center,size.minDimension*.7f))
        repeat(8) {i->drawCircle(Color(0xFF90C8FF).copy(alpha=.16f),size.minDimension*(.12f+i*.045f),style=androidx.compose.ui.graphics.drawscope.Stroke(1.5f))}
        drawCircle(Color(0xFFDDEFFF),size.minDimension*.025f)
    }
}
