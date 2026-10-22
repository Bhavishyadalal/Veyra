package app.veyra.music.ui.themes

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

/** Intentional pencil irregularity stays inside the original layout/touch bounds. */
object SketchShape : CornerBasedShape(CornerSize(18.dp), CornerSize(18.dp), CornerSize(18.dp), CornerSize(18.dp)) {
    override fun copy(topStart: CornerSize, topEnd: CornerSize, bottomEnd: CornerSize, bottomStart: CornerSize): CornerBasedShape = this
    override fun createOutline(size: Size, topStart: Float, topEnd: Float, bottomEnd: Float, bottomStart: Float, layoutDirection: LayoutDirection): Outline {
        val w = size.width; val h = size.height
        return Outline.Generic(Path().apply {
            moveTo(w*.18f, h*.04f)
            cubicTo(w*.41f, 0f, w*.72f, h*.065f, w*.85f, h*.025f)
            cubicTo(w*.97f, h*.05f, w*.985f, h*.23f, w*.965f, h*.48f)
            cubicTo(w, h*.77f, w*.97f, h*.94f, w*.82f, h*.96f)
            cubicTo(w*.58f, h*.93f, w*.34f, h, w*.16f, h*.96f)
            cubicTo(w*.035f, h*.96f, w*.015f, h*.76f, w*.04f, h*.48f)
            cubicTo(w*.01f, h*.23f, w*.055f, h*.04f, w*.18f, h*.04f)
            close()
        })
    }
}
