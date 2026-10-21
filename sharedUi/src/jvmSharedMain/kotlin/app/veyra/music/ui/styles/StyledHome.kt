@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
package app.veyra.music.ui.styles

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage

/** UI-only adapter: all playback, account/offline decisions and menus stay in the existing Home host. */
data class StyleMusicItem(val key:String,val title:String,val artist:String,val artwork:Any?,val onClick:()->Unit,val onLongPress:(()->Unit)?=null,val current:Boolean=false,val playing:Boolean=false)

@Composable
fun StyleBrand(modifier:Modifier=Modifier,brandPainter:androidx.compose.ui.graphics.painter.Painter?=null) {
    Row(modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
        Box(Modifier.size(30.dp).clip(CircleShape).background(Color(0xFF111111)),contentAlignment=Alignment.Center) {
            if(brandPainter!=null) androidx.compose.foundation.Image(brandPainter,null,Modifier.fillMaxSize())
            else Icon(Icons.Rounded.GraphicEq,null,Modifier.size(22.dp),tint=Color.White)
        }
        Text("VEYRA",style=MaterialTheme.typography.titleMedium,fontWeight=FontWeight.Bold,letterSpacing=4.sp)
        HorizontalDivider(Modifier.weight(1f),color=MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
fun StyledHomeOpening(style:AppStyle,featured:StyleMusicItem?,currentArtwork:Any?,songTitle:String?,songArtist:String?,
    onOpenCurrent:(()->Unit)?,openCurrentLabel:String,modifier:Modifier=Modifier,brandPainter:androidx.compose.ui.graphics.painter.Painter?=null,
    onCustomize:(()->Unit)?=null,customizeLabel:String="") {
    Column(modifier.fillMaxWidth().padding(horizontal=20.dp,vertical=16.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
        Row(verticalAlignment=Alignment.CenterVertically) {
            Box(Modifier.weight(1f)){StyleBrand(brandPainter=brandPainter)}
            onCustomize?.let {IconButton(it,Modifier.size(48.dp)) {Icon(Icons.Rounded.Tune,customizeLabel)}}
        }
        if(style==AppStyle.MUSIC_EDITORIAL) {
            Text("MUSIC  /  PEOPLE  /  PLACES",style=MaterialTheme.typography.labelSmall,
                letterSpacing=2.sp,color=MaterialTheme.colorScheme.onSurfaceVariant,modifier=Modifier.clearAndSetSemantics {})
        }
        val primary=MaterialTheme.colorScheme.primary
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val density=LocalDensity.current
            val rtl=androidx.compose.ui.platform.LocalLayoutDirection.current==LayoutDirection.Rtl
            val wideHeader=maxWidth>=300.dp
            val headlineSize=(maxWidth.value/(if(style==AppStyle.AFTER_DARK)3.9f else 4.6f)/density.fontScale).coerceIn(32f,100f).sp
            Box(Modifier.fillMaxWidth()) {
            Column {
                Text(buildAnnotatedString {
                    when(style) {
                        AppStyle.MUSIC_EDITORIAL -> {append("On\nre");withStyle(SpanStyle(color=primary)){append("peat.")}}
                        AppStyle.AFTER_DARK -> append("AFTER\nHOURS")
                        else -> append("Good music\nlives here.")
                    }
                },
                    style=MaterialTheme.typography.displayLarge.copy(fontSize=headlineSize,lineHeight=headlineSize*.87f,
                        fontWeight=FontWeight.Bold,letterSpacing=(-1.5).sp,
                        textDirection=androidx.compose.ui.text.style.TextDirection.Ltr,
                        textAlign=if(rtl)androidx.compose.ui.text.style.TextAlign.End else androidx.compose.ui.text.style.TextAlign.Start),
                    modifier=Modifier.fillMaxWidth().padding(bottom=6.dp).semantics {heading()})
                if(style==AppStyle.LISTENING_ROOM) Text("A little evening escape",style=MaterialTheme.typography.headlineSmall.copy(fontStyle=FontStyle.Italic),
                    color=MaterialTheme.colorScheme.primary)
            }
            if(style==AppStyle.AFTER_DARK && density.fontScale<=1.3f && wideHeader && !rtl) {
                Column(Modifier.align(Alignment.TopEnd).padding(top=12.dp).width(76.dp).clearAndSetSemantics {},verticalArrangement=Arrangement.spacedBy(12.dp)) {
                    Text("MUSIC\nKEEPS\nUS CLOSE",style=MaterialTheme.typography.labelSmall,letterSpacing=1.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
                    Box(Modifier.width(1.dp).height(64.dp).background(Color(0xFF2F80ED)))
                }
            }
            }
        }
        when(style) {
            AppStyle.MUSIC_EDITORIAL -> EditorialFeature(featured)
            AppStyle.AFTER_DARK -> NightFeature(currentArtwork,songTitle,songArtist,onOpenCurrent,openCurrentLabel)
            else -> HorizontalDivider(color=MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Composable
private fun EditorialFeature(item:StyleMusicItem?) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val stacked=LocalDensity.current.fontScale>1.3f || maxWidth<300.dp
        val detail:@Composable ()->Unit = {
            Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary).padding(16.dp),
                verticalArrangement=Arrangement.spacedBy(10.dp)) {
                Text("FEATURED",style=MaterialTheme.typography.labelSmall,letterSpacing=2.sp,color=Color(0xFFFFF0D9))
                Text(item?.title ?: "Your next favourite",style=MaterialTheme.typography.headlineMedium,color=Color(0xFFFFF0D9),maxLines=3,overflow=TextOverflow.Ellipsis)
                if(item!=null) {
                    Text(item.artist,style=MaterialTheme.typography.bodySmall,color=Color(0xFFFFE2C9),maxLines=2,overflow=TextOverflow.Ellipsis)
                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.End) {
                        IconButton(onClick=item.onClick,modifier=Modifier.size(48.dp).background(Color(0xFFFFF0D9),CircleShape)) {
                            Icon(Icons.Rounded.PlayArrow,item.title,tint=MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
        if(stacked) Column {
            StyleArtwork(item?.artwork,Modifier.fillMaxWidth().aspectRatio(1.4f))
            detail()
        } else Row(Modifier.fillMaxWidth().heightIn(min=240.dp).background(MaterialTheme.colorScheme.primary)) {
            StyleArtwork(item?.artwork,Modifier.weight(1.25f).height(250.dp))
            Box(Modifier.weight(1f)){detail()}
        }
    }
}

@Composable
private fun NightFeature(artwork:Any?,title:String?,artist:String?,open:(()->Unit)?,description:String) {
    BoxWithConstraints {
        val narrow=maxWidth<300.dp || LocalDensity.current.fontScale>1.3f
        val record:@Composable ()->Unit = {
            Box(Modifier.fillMaxWidth().aspectRatio(1f).border(1.dp,Color(0xFF2F80ED).copy(alpha=.30f),CircleShape)
                .padding(6.dp).clip(CircleShape).border(2.dp,Color(0xFF2F80ED),CircleShape)
                .then(if(open!=null && title!=null)Modifier.combinedClickable(role=Role.Button,onClickLabel=description,onClick=open) else Modifier)) {
                StyleArtwork(if(title!=null)artwork else null,Modifier.fillMaxSize())
            }
        }
        val caption:@Composable ()->Unit = {
            Column(verticalArrangement=Arrangement.spacedBy(8.dp)) {
                Text(if(title!=null)description else "NIGHT RADIO",style=MaterialTheme.typography.labelSmall,letterSpacing=2.sp,color=MaterialTheme.colorScheme.primary)
                Text(title ?: "Room for\nyour music.",style=MaterialTheme.typography.headlineSmall,maxLines=3,overflow=TextOverflow.Ellipsis)
                if(artist!=null) Text(artist,style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant,maxLines=2,overflow=TextOverflow.Ellipsis)
                if(open!=null && title!=null) IconButton(open,Modifier.size(48.dp).border(1.dp,MaterialTheme.colorScheme.outline,CircleShape)) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowForward,description,tint=MaterialTheme.colorScheme.primary)
                }
            }
        }
        if(narrow) Column(verticalArrangement=Arrangement.spacedBy(16.dp)) {Box(Modifier.widthIn(max=250.dp)){record()};caption()}
        else Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(18.dp)) {
            Box(Modifier.weight(1.5f)){record()};Box(Modifier.weight(1f)){caption()}
        }
    }
}

@Composable
private fun StyleArtwork(model:Any?,modifier:Modifier=Modifier) {
    // An absent, failed or loading cover shows the designed abstract record, never a stock portrait.
    SubcomposeAsyncImage(model,contentDescription=null,modifier=modifier,contentScale=ContentScale.Crop,
        loading={NightRecordFallback(Modifier.fillMaxSize())},error={NightRecordFallback(Modifier.fillMaxSize())})
}

@Composable
fun StyledMusicShelf(style:AppStyle,title:String,items:List<StyleMusicItem>,modifier:Modifier=Modifier,compact:Boolean=false,trailing:(@Composable ()->Unit)?=null) {
    Column(modifier.fillMaxWidth().padding(vertical=12.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
        Column(Modifier.padding(horizontal=20.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
            if(style==AppStyle.MUSIC_EDITORIAL) Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                Box(Modifier.width(28.dp).height(3.dp).background(MaterialTheme.colorScheme.primary))
                HorizontalDivider(Modifier.weight(1f),color=MaterialTheme.colorScheme.outlineVariant)
            }
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                if(style==AppStyle.AFTER_DARK) Box(Modifier.width(3.dp).height(24.dp).background(Color(0xFF2F80ED)))
                Box(Modifier.weight(1f)) {
                    Text(title,style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold,
                        modifier=if(style==AppStyle.LISTENING_ROOM)Modifier
                            .background(Color(0xFFE4D0A5),androidx.compose.foundation.shape.CutCornerShape(topEnd=8.dp,bottomStart=3.dp))
                            .padding(horizontal=14.dp,vertical=6.dp) else Modifier,
                        color=if(style==AppStyle.LISTENING_ROOM)Color(0xFF261F18) else MaterialTheme.colorScheme.onBackground)
                }
                trailing?.invoke()
            }
        }
        if(compact) {
            LazyRow(contentPadding=PaddingValues(horizontal=20.dp),horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                itemsIndexed(items,key={i,item->"$i:${item.key}"}) {i,item->StyleCoverCard(style,item,i,Modifier.width(82.dp))}
            }
        } else BoxWithConstraints {
            val pageWidth=(maxWidth-40.dp).coerceAtMost(520.dp)
            LazyRow(contentPadding=PaddingValues(horizontal=20.dp),horizontalArrangement=Arrangement.spacedBy(20.dp)) {
                itemsIndexed(items.chunked(4),key={index,_->index}) {pageIndex,page ->
                    StyleShelfPage(style,page,Modifier.width(pageWidth),startIndex=pageIndex*4)
                }
            }
        }
    }
}

@Composable
private fun StyleCoverCard(style:AppStyle,item:StyleMusicItem,index:Int,modifier:Modifier,aspect:Float=1f) {
    Column(modifier.combinedClickable(role=Role.Button,onClick=item.onClick,onLongClick=item.onLongPress)
        .semantics(mergeDescendants=true) {if(item.current) selected=true},verticalArrangement=Arrangement.spacedBy(5.dp)) {
        Box(Modifier.fillMaxWidth().padding(if(style==AppStyle.LISTENING_ROOM)5.dp else 0.dp)
            .graphicsLayer {rotationZ=if(style==AppStyle.LISTENING_ROOM)if(index%2==0)-2f else 2f else 0f}
            .then(if(style==AppStyle.LISTENING_ROOM)Modifier.border(5.dp,Color(0xFFDED1AA)) else Modifier)) {
            StyleArtwork(item.artwork,Modifier.fillMaxWidth().aspectRatio(aspect))
            if(style==AppStyle.LISTENING_ROOM) Canvas(Modifier.matchParentSize().clearAndSetSemantics {}) {
                val tapeWidth=size.width*.30f
                drawRect(Color(0xFFE8D8AF).copy(alpha=.78f),
                    topLeft=androidx.compose.ui.geometry.Offset(size.width*.07f,0f),
                    size=androidx.compose.ui.geometry.Size(tapeWidth,8.dp.toPx()))
            }
            if(item.current) Icon(if(item.playing)Icons.Rounded.GraphicEq else Icons.Rounded.PlayArrow,null,
                Modifier.align(Alignment.BottomEnd).padding(6.dp).size(28.dp).background(MaterialTheme.colorScheme.surface,CircleShape).padding(4.dp),
                tint=MaterialTheme.colorScheme.primary)
        }
        Text(item.title,style=MaterialTheme.typography.titleSmall,maxLines=2,overflow=TextOverflow.Ellipsis)
        Text(item.artist,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant,maxLines=1,overflow=TextOverflow.Ellipsis)
    }
}


/** Each style has its own composition; large text uses a simple ordered grid. */
@Composable
private fun StyleShelfPage(style:AppStyle,items:List<StyleMusicItem>,modifier:Modifier,startIndex:Int=0) {
    val largeText=LocalDensity.current.fontScale>1.3f
    if(style==AppStyle.AFTER_DARK && !largeText) {
        Column(modifier,verticalArrangement=Arrangement.spacedBy(10.dp)) {
            items.firstOrNull()?.let {item ->
                Box(Modifier.fillMaxWidth().aspectRatio(1.65f).clip(RoundedCornerShape(8.dp))
                    .combinedClickable(role=Role.Button,onClick=item.onClick,onLongClick=item.onLongPress)
                    .semantics(mergeDescendants=true) {if(item.current)selected=true}) {
                    StyleArtwork(item.artwork,Modifier.fillMaxSize())
                    Box(Modifier.matchParentSize().background(Brush.verticalGradient(listOf(Color.Transparent,Color(0xFF030B17).copy(alpha=.92f)))))
                    if(item.current) Icon(if(item.playing)Icons.Rounded.GraphicEq else Icons.Rounded.PlayArrow,null,
                        Modifier.align(Alignment.TopEnd).padding(10.dp).size(28.dp).background(Color(0xFF091729),CircleShape).padding(4.dp),
                        tint=Color(0xFF9BCDFF))
                    Column(Modifier.align(Alignment.BottomStart).padding(14.dp)) {
                        Text(item.title,style=MaterialTheme.typography.headlineSmall,color=Color(0xFFDDEFFF),maxLines=2,overflow=TextOverflow.Ellipsis)
                        Text(item.artist,style=MaterialTheme.typography.bodySmall,color=Color(0xFFB7CDE7),maxLines=1,overflow=TextOverflow.Ellipsis)
                    }
                }
            }
            items.drop(1).forEachIndexed {index,item ->
                Row(Modifier.fillMaxWidth().heightIn(min=64.dp)
                    .combinedClickable(role=Role.Button,onClick=item.onClick,onLongClick=item.onLongPress)
                    .semantics(mergeDescendants=true) {if(item.current)selected=true}
                    .padding(vertical=6.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                    Text((startIndex+index+2).toString().padStart(2,'0'),style=MaterialTheme.typography.labelMedium,color=MaterialTheme.colorScheme.primary,
                        modifier=Modifier.clearAndSetSemantics {})
                    Box(Modifier.size(48.dp).clip(RoundedCornerShape(4.dp))) {StyleArtwork(item.artwork,Modifier.fillMaxSize())}
                    Column(Modifier.weight(1f)) {
                        Text(item.title,style=MaterialTheme.typography.titleSmall,maxLines=2,overflow=TextOverflow.Ellipsis)
                        Text(item.artist,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant,maxLines=1,overflow=TextOverflow.Ellipsis)
                    }
                    Icon(if(item.current && item.playing)Icons.Rounded.GraphicEq else if(item.current)Icons.Rounded.PlayArrow else Icons.AutoMirrored.Rounded.ArrowForward,null,
                        Modifier.size(20.dp),tint=MaterialTheme.colorScheme.primary)
                }
                HorizontalDivider(color=MaterialTheme.colorScheme.outlineVariant)
            }
        }
    } else Column(modifier,verticalArrangement=Arrangement.spacedBy(16.dp)) {
        items.chunked(2).forEachIndexed {rowIndex,row ->
            Row(horizontalArrangement=Arrangement.spacedBy(14.dp),verticalAlignment=Alignment.Bottom) {
                row.forEachIndexed {colIndex,item ->
                    val portrait=style==AppStyle.MUSIC_EDITORIAL && !largeText && (rowIndex+colIndex)%2==0
                    StyleCoverCard(style,item,rowIndex*2+colIndex,Modifier.weight(if(portrait)1.15f else 1f),
                        aspect=if(portrait).82f else 1f)
                }
                if(row.size==1)Spacer(Modifier.weight(1f))
            }
        }
    }
}
