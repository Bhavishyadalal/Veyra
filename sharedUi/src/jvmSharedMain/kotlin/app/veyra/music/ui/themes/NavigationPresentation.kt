package app.veyra.music.ui.themes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import app.veyra.music.data.settings.NavigationDesign

/** Shared geometry for both native navigation paths and their settings previews. */
fun NavigationDesign.navigationShape(themeCorner:Int=28):RoundedCornerShape = when(this) {
    NavigationDesign.DOCKED -> RoundedCornerShape(topStart=20.dp,topEnd=20.dp)
    NavigationDesign.MINIMAL -> RoundedCornerShape(12.dp)
    NavigationDesign.RIBBON -> RoundedCornerShape(18.dp)
    NavigationDesign.CAPSULE,NavigationDesign.ORBIT -> RoundedCornerShape(50)
    else -> if(themeCorner<0)RoundedCornerShape(50) else RoundedCornerShape(themeCorner.dp)
}

fun NavigationDesign.hasSelectionPill() = this!=NavigationDesign.RIBBON && this!=NavigationDesign.ORBIT

/** Decorative icon treatment only: one existing bar owns its backdrop and input targets. */
@Composable fun NavigationGlyphFrame(design:NavigationDesign,selected:Boolean,modifier:Modifier=Modifier,
    content:@Composable ()->Unit) {
    val scheme=MaterialTheme.colorScheme
    val orbit=design==NavigationDesign.ORBIT && selected
    Box(modifier.size(if(design==NavigationDesign.ORBIT || design==NavigationDesign.RIBBON)40.dp else 24.dp)
        .then(if(orbit)Modifier.background(scheme.primaryContainer,CircleShape)
            .border(1.dp,scheme.primary.copy(alpha=.4f),CircleShape) else Modifier)
        .drawWithContent {
            drawContent()
            if(design==NavigationDesign.RIBBON && selected) {
                drawLine(scheme.primary,Offset(size.width*.25f,size.height-2.dp.toPx()),
                    Offset(size.width*.75f,size.height-2.dp.toPx()),3.dp.toPx(),cap=androidx.compose.ui.graphics.StrokeCap.Round)
            }
        },contentAlignment=Alignment.Center) {content()}
}
