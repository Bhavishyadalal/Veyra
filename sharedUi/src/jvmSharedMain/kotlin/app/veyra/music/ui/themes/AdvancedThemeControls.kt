package app.veyra.music.ui.themes

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/** Native, bounded control geometry; downloaded packs never contain executable drawing code. */
fun themeControlShape(theme: ThemeVisuals, corner: Int = theme.buttonCorner): CornerBasedShape = when(theme.materialStyle) {
    "punk_gel" -> RoundedCornerShape(corner.dp)
    "punk_rivet" -> CutCornerShape(12.dp)
    "punk_sticker" -> RoundedCornerShape(topStart=18.dp,topEnd=28.dp,bottomEnd=20.dp,bottomStart=12.dp)
    "punk_torn" -> SketchShape
    "punk_chrome" -> RoundedCornerShape(topStart=26.dp,topEnd=6.dp,bottomEnd=26.dp,bottomStart=6.dp)
    "pixel" -> PixelControlShape
    "botanical" -> RoundedCornerShape((corner+8).coerceAtMost(44).dp, (corner/2).dp, (corner+8).coerceAtMost(44).dp, (corner/2).dp)
    "deco" -> CutCornerShape((corner/3).dp)
    "telemetry" -> CutCornerShape(topStart=5.dp, topEnd=16.dp, bottomEnd=5.dp, bottomStart=16.dp)
    "flame" -> RoundedCornerShape(topStart=4.dp,topEnd=corner.dp,bottomEnd=4.dp,bottomStart=corner.dp)
    else -> when(theme.sceneStyle) { "sketch" -> SketchShape; "web" -> CutCornerShape(corner.dp); else -> RoundedCornerShape(corner.dp) }
}

private object PixelControlShape : CornerBasedShape(CornerSize(8.dp),CornerSize(8.dp),CornerSize(8.dp),CornerSize(8.dp)) {
    override fun copy(topStart: CornerSize, topEnd: CornerSize, bottomEnd: CornerSize, bottomStart: CornerSize): CornerBasedShape = this
    override fun createOutline(size: Size, topStart: Float, topEnd: Float, bottomEnd: Float, bottomStart: Float, layoutDirection: LayoutDirection): Outline {
        val w=size.width;val h=size.height;val s=minOf(w,h)*.12f
        return Outline.Generic(Path().apply {
            moveTo(2*s,0f);lineTo(w-2*s,0f);lineTo(w-2*s,s);lineTo(w-s,s);lineTo(w-s,2*s);lineTo(w,2*s)
            lineTo(w,h-2*s);lineTo(w-s,h-2*s);lineTo(w-s,h-s);lineTo(w-2*s,h-s);lineTo(w-2*s,h)
            lineTo(2*s,h);lineTo(2*s,h-s);lineTo(s,h-s);lineTo(s,h-2*s);lineTo(0f,h-2*s)
            lineTo(0f,2*s);lineTo(s,2*s);lineTo(s,s);lineTo(2*s,s);close()
        })
    }
}

/** Shared by real seek, volume and Settings sliders. No timer or independently moving mascot.
 * All ornaments stay within the track. The fixed 24-step ceiling bounds work on large screens.
 */
fun drawAdvancedTrackDetails(scope: DrawScope, theme: ThemeVisuals, fraction: Float, showHead: Boolean) = with(scope) {
    clipRect {
    val f=if(fraction.isFinite())fraction.coerceIn(0f,1f) else 0f
    val w=size.width;val h=size.height
    if(w<=0f || h<=0f)return@clipRect
    val x=w*f;val cy=h/2f
    when(theme.seekStyle) {
        "gel", "plasma" -> repeat(18) {i ->
            val p=w*(i+.5f)/18f
            val r=h*(if(i%3==0).33f else .17f)
            drawCircle(if(p<x)theme.secondary else theme.text.copy(alpha=.32f),r,Offset(p,cy))
        }
        "cable" -> repeat(24) {i ->
            val p=w*i/24f
            drawLine(if(p<x)theme.background.copy(alpha=.75f) else theme.secondary.copy(alpha=.6f),Offset(p,0f),Offset(p+h,h),1.dp.toPx())
        }
        "beads" -> repeat(20) {i ->
            val p=w*(i+.5f)/20f
            drawCircle(if(p<x)theme.secondary else theme.text.copy(alpha=.5f),h*.37f,Offset(p,cy))
        }
        "tape" -> repeat(24) {i ->
            val p=w*i/24f
            drawRect(if(p<x)theme.background.copy(alpha=.7f) else theme.text.copy(alpha=.45f),Offset(p,cy-h*.15f),Size(w/48f,h*.3f))
        }
        "road" -> repeat(20) { i ->
            val p=w*(i+.25f)/20f
            drawRect(if(p<x)theme.background.copy(alpha=.65f) else theme.text.copy(alpha=.6f),Offset(p,cy-h*.10f),Size(minOf(w/40f,w-p),h*.20f))
        }
        "vine" -> repeat(18) { i ->
            val p=w*(i+.4f)/18f;val a=if(i%2==0) -1f else 1f
            val leaf=Path().apply {moveTo(p,cy);quadraticTo(p+h*.6f,cy-a*h*.5f,p+h,cy);quadraticTo(p+h*.4f,cy+a*h*.4f,p,cy);close()}
            drawPath(leaf,if(p<x)theme.background.copy(alpha=.65f) else theme.secondary)
        }
        "skyline" -> repeat(24) { i ->
            val p=w*i/24f;val bh=h*(.25f+(i*7%5)*.14f)
            drawRect(if(p<x)theme.background.copy(alpha=.6f) else theme.secondary.copy(alpha=.6f),Offset(p,h-bh),Size(w/36f,bh))
        }
        "telemetry" -> repeat(24) { i ->
            val p=w*i/24f
            drawLine(if(p<x)theme.background.copy(alpha=.8f) else theme.text.copy(alpha=.65f),Offset(p,h),Offset((p+h*.35f).coerceAtMost(w),if(i%4==0)0f else h*.55f),.8.dp.toPx())
        }
        "flame" -> repeat(20) { i ->
            val p=w*(i+.2f)/20f
            val spark=Path().apply {moveTo(p,h*.85f);quadraticTo(p-h*.15f,h*.5f,p+h*.35f,h*.08f);quadraticTo(p+h*.2f,h*.6f,p+h*.7f,h*.85f);close()}
            drawPath(spark,if(p<x)theme.background.copy(alpha=.55f) else theme.secondary.copy(alpha=.75f))
        }
    }
    if(showHead && f>0f && theme.materialStyle!="classic") {
        val r=minOf(h*.45f,w/2f);val cx=x.coerceIn(r,w-r)
        if(theme.materialStyle=="pixel" || theme.materialStyle=="telemetry")
            drawRect(theme.text,Offset(cx-r,cy-r),Size(r*2,r*2))
        else if(theme.materialStyle.startsWith("punk_") && theme.seekStyle != "tape") {
            val star=Path().apply {
                repeat(10) { i ->
                    val a=(i*36-90)*kotlin.math.PI/180
                    val radius=if(i%2==0)r else r*.45f
                    val px=cx+kotlin.math.cos(a).toFloat()*radius
                    val py=cy+kotlin.math.sin(a).toFloat()*radius
                    if(i==0)moveTo(px,py) else lineTo(px,py)
                };close()
            }
            drawPath(star,theme.text)
        } else drawCircle(theme.text,r,Offset(cx,cy))
    }
    }
}

/** Subtle edge ornaments keep each world present without competing with content. */
fun drawAdvancedSceneDetails(scope: DrawScope, theme: ThemeVisuals) = with(scope) {
    repeat(12) { i ->
        val x=if(i%2==0)size.width*.035f else size.width*.965f
        val y=size.height*(i+.5f)/12f
        val ink=theme.secondary.copy(alpha=.14f)
        when(theme.materialStyle) {
            "pixel" -> drawRect(ink,Offset(x,y),Size(3.dp.toPx(),3.dp.toPx()))
            "botanical" -> {
                val r=5.dp.toPx()
                drawPath(Path().apply {moveTo(x,y);quadraticTo(x+r*2,y-r*2,x+r*2,y);quadraticTo(x+r,y+r*2,x,y);close()},ink)
            }
            "deco" -> drawLine(ink,Offset(x,y),Offset(x,y+18.dp.toPx()),.7.dp.toPx())
            "telemetry" -> {
                drawLine(ink,Offset(x,y),Offset(x+7.dp.toPx(),y),.8.dp.toPx())
                drawLine(ink,Offset(x,y),Offset(x,y+4.dp.toPx()),.8.dp.toPx())
            }
            "flame" -> {
                val r=3.dp.toPx()
                drawPath(Path().apply {moveTo(x,y+r);quadraticTo(x-r,y,x+r,y-r*2);quadraticTo(x,y,x+r,y+r);close()},ink)
            }
        }
    }
}
