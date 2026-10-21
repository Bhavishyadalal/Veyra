package app.veyra.music.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.veyra.music.data.model.ShelfItem
import app.veyra.music.data.model.artworkAt
import app.veyra.music.sharedui.resources.*
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource

@Composable internal fun PersonalizedHomeShortcuts(ids:List<Int>,onClick:(Int)->Unit) {
    val labels=stringArrayResource(Res.array.experience_shortcuts)
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal=24.dp,vertical=8.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
        ids.forEach {id->AssistChip(onClick={onClick(id)},label={Text(labels[id],color=MaterialTheme.colorScheme.onSurface)})}
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable internal fun PersonalizedSpeedDial(items:List<ShelfItem>,onClick:(ShelfItem,String)->Unit,onLongClick:((ShelfItem)->Unit)?) {
    val title=stringResource(Res.string.home_speed_dial)
    val largeText=LocalDensity.current.fontScale>1.3f
    Column(Modifier.fillMaxWidth().padding(horizontal=24.dp,vertical=12.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
        Text(title,style=MaterialTheme.typography.headlineSmall,color=MaterialTheme.colorScheme.onBackground)
        BoxWithConstraints {
            val columns=if(largeText || maxWidth<280.dp)2 else 3
            Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
                items.chunked(columns).forEach {row->
                    Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                        row.forEach {item->
                            Column(Modifier.weight(1f).clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.surfaceContainer)
                                .combinedClickable(onClick={onClick(item,title)},onLongClick=onLongClick?.let {{it(item)}})) {
                                AsyncImage(item.thumbnailUrl.artworkAt(240),null,Modifier.fillMaxWidth().aspectRatio(1.15f),contentScale=ContentScale.Crop)
                                Text(item.title,style=MaterialTheme.typography.labelLarge,color=MaterialTheme.colorScheme.onSurface,maxLines=2,overflow=TextOverflow.Ellipsis,modifier=Modifier.padding(horizontal=8.dp,vertical=10.dp))
                            }
                        }
                        repeat(columns-row.size){Spacer(Modifier.weight(1f))}
                    }
                }
            }
        }
    }
}
