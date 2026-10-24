package app.veyra.music.ui.themes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import app.veyra.music.data.settings.NavigationDesign

/** Shared by both navigation paths and settings. Geometry is never a downloaded executable. */
fun NavigationDesign.navigationShape(themeCorner:Int=28): CornerBasedShape = when(this) {
    NavigationDesign.DOCKED -> RoundedCornerShape(topStart=20.dp,topEnd=20.dp)
    NavigationDesign.MINIMAL -> RoundedCornerShape(12.dp)
    NavigationDesign.RIBBON -> RoundedCornerShape(18.dp)
    NavigationDesign.CAPSULE,NavigationDesign.ORBIT,NavigationDesign.PEBBLE,NavigationDesign.HALO -> RoundedCornerShape(50)
    NavigationDesign.MONORAIL -> RoundedCornerShape(8.dp)
    NavigationDesign.HARBOR -> RoundedCornerShape(topStart=32.dp,topEnd=32.dp,bottomStart=12.dp,bottomEnd=12.dp)
    NavigationDesign.TICKET -> CutCornerShape(12.dp)
    NavigationDesign.PRISM -> CutCornerShape(topStart=24.dp,bottomEnd=24.dp)
    NavigationDesign.BRACKET -> RoundedCornerShape(4.dp)
    NavigationDesign.STITCH -> RoundedCornerShape(22.dp)
    NavigationDesign.PIXEL -> CutCornerShape(5.dp)
    NavigationDesign.LOZENGE -> RoundedCornerShape(topStart=36.dp,bottomEnd=36.dp,topEnd=8.dp,bottomStart=8.dp)
    else -> if(themeCorner<0)RoundedCornerShape(50) else RoundedCornerShape(themeCorner.dp)
}

fun NavigationDesign.hasSelectionPill() = ordinal < NavigationDesign.RIBBON.ordinal

/** Static accents inside the existing target, with no ticker or additional backdrop capture. */
@Composable fun NavigationGlyphFrame(design:NavigationDesign,selected:Boolean,modifier:Modifier=Modifier,
    content:@Composable ()->Unit) {
    val scheme=MaterialTheme.colorScheme
    val framed=design.ordinal>=NavigationDesign.RIBBON.ordinal
    val fill=design in setOf(NavigationDesign.ORBIT,NavigationDesign.PEBBLE,NavigationDesign.LOZENGE)
    val frame=if(design==NavigationDesign.LOZENGE)RoundedCornerShape(topStart=14.dp,bottomEnd=14.dp,topEnd=4.dp,bottomStart=4.dp) else if(design==NavigationDesign.PEBBLE)RoundedCornerShape(topStart=18.dp,topEnd=10.dp,bottomStart=10.dp,bottomEnd=18.dp) else CircleShape
    Box(modifier.size(if(framed)40.dp else 24.dp)
        .then(if(fill && selected)Modifier.background(scheme.primaryContainer,frame)
            .border(1.dp,scheme.primary.copy(alpha=.4f),frame) else Modifier)
        .drawWithContent {
            drawContent()
            if(selected) {
                val c=scheme.primary;val w=size.width;val h=size.height;val stroke=1.5.dp.toPx()
                fun line(x:Float,y:Float,x2:Float,y2:Float)=drawLine(c,Offset(w*x,h*y),Offset(w*x2,h*y2),stroke,StrokeCap.Round)
                when(design) {
                    NavigationDesign.RIBBON -> line(.25f,.95f,.75f,.95f)
                    NavigationDesign.MONORAIL -> {line(.05f,.95f,.95f,.95f);drawCircle(c,2.dp.toPx(),Offset(w*.5f,h*.95f))}
                    NavigationDesign.HARBOR -> {line(.2f,.93f,.8f,.93f);line(.2f,.93f,.2f,.8f);line(.8f,.93f,.8f,.8f)}
                    NavigationDesign.TICKET -> {line(.1f,.12f,.9f,.12f);repeat(4){drawCircle(c,1.dp.toPx(),Offset(w*(.2f+it*.2f),h*.92f))}}
                    NavigationDesign.PRISM -> {line(.5f,0f,.68f,.13f);line(.68f,.13f,.5f,.26f);line(.5f,.26f,.32f,.13f);line(.32f,.13f,.5f,0f)}
                    NavigationDesign.BRACKET -> {line(.05f,.2f,.05f,.8f);line(.05f,.2f,.2f,.2f);line(.05f,.8f,.2f,.8f);line(.95f,.2f,.95f,.8f);line(.8f,.2f,.95f,.2f);line(.8f,.8f,.95f,.8f)}
                    NavigationDesign.HALO -> drawCircle(c,w*.47f,style=Stroke(stroke))
                    NavigationDesign.STITCH -> repeat(6){line(.1f+it*.14f,.94f,.16f+it*.14f,.94f)}
                    NavigationDesign.PIXEL -> {drawRect(c,Offset(w*.1f,h*.86f),androidx.compose.ui.geometry.Size(w*.8f,h*.08f));drawRect(c,Offset(w*.1f,h*.7f),androidx.compose.ui.geometry.Size(w*.08f,h*.16f))}
                    else -> Unit
                }
            }
        },contentAlignment=Alignment.Center) {content()}
}
