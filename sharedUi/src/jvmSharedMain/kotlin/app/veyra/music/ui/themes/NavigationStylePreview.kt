package app.veyra.music.ui.themes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import app.veyra.music.data.settings.NavigationDesign

/** Static settings specimen: same shapes, glyph frames and theme assets, no backdrop sampling. */
@Composable fun NavigationStylePreview(design:NavigationDesign,title:String,selected:Boolean,onClick:()->Unit,
    modifier:Modifier=Modifier) {
    Surface(onClick=onClick,modifier=modifier.semantics {this.selected=selected;role=Role.RadioButton},
        shape=RoundedCornerShape(20.dp),color=MaterialTheme.colorScheme.surfaceContainerLow,
        border=BorderStroke(if(selected)2.dp else 1.dp,
            if(selected)MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)) {
        Column(Modifier.padding(12.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
            val background=MaterialTheme.colorScheme.surfaceContainerHigh
            val shape=design.navigationShape()
            val icons=listOf(Icons.Rounded.Home,Icons.Rounded.Search,Icons.Rounded.Explore)
            val iconCell:@Composable (Int)->Unit={index->
                Box(Modifier.background(if(index==0 && design.hasSelectionPill())MaterialTheme.colorScheme.primaryContainer else androidx.compose.ui.graphics.Color.Transparent,RoundedCornerShape(14.dp)).padding(2.dp)) {
                    NavigationGlyphFrame(design,index==0) {
                        ThemeIcon(icons[index],null,Modifier.size(18.dp),if(index==0)MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            if(design==NavigationDesign.FLOATING) Row(Modifier.fillMaxWidth().height(52.dp),horizontalArrangement=Arrangement.spacedBy(6.dp),verticalAlignment=Alignment.CenterVertically) {
                Row(Modifier.weight(1f).fillMaxHeight().background(background,shape).themeNavigationSurface(shape),
                    horizontalArrangement=Arrangement.SpaceEvenly,verticalAlignment=Alignment.CenterVertically) {iconCell(0);iconCell(1)}
                Box(Modifier.size(38.dp).background(background,androidx.compose.foundation.shape.CircleShape),contentAlignment=Alignment.Center) {iconCell(2)}
            } else Row(Modifier.fillMaxWidth().height(52.dp).background(background,shape)
                .themeNavigationSurface(shape,preserveShape=true).padding(horizontal=4.dp),verticalAlignment=Alignment.CenterVertically) {
                icons.indices.forEach {index->Box(Modifier.weight(1f),contentAlignment=Alignment.Center) {iconCell(index)}}
            }

            Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                Text(title,Modifier.weight(1f),style=MaterialTheme.typography.labelLarge)
                if(selected)Icon(Icons.Rounded.CheckCircle,null,Modifier.size(16.dp),tint=MaterialTheme.colorScheme.primary)
            }
        }
    }
}
