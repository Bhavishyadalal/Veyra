@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
package app.veyra.music.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.*
import androidx.compose.ui.semantics.*
import app.veyra.music.ui.styles.*
import app.veyra.music.ui.themes.*
import java.io.File
import javax.swing.SwingUtilities
import org.jetbrains.skia.EncodedImageFormat

private fun SemanticsNode.nodes():List<SemanticsNode> = listOf(this)+children.flatMap {it.nodes()}
fun main() {
    val root=File(System.getProperty("veyra.root"))
    val output=File(root,"themes/previews/app-styles-0.1.8").apply {mkdirs()}
    val results=mutableListOf<String>()
    val art=listOf("golden-afterglow","blue-hour-sanctuary","roaming-cosmos","alpine-ember").map { id ->
        val properties=java.util.Properties().apply {File(root,"themes/render-config/$id.properties").inputStream().use {load(it)}}
        File(root,"themes/$id/${properties.getProperty("asset.header")}")
    }
    for(style in AppStyle.entries.filter {it.custom}) for(kind in listOf("home","secondary","large-rtl","shelves")) {
        val large=kind=="large-rtl"
        val theme=requireNotNull(style.visuals(mapOf("header" to File(root,"app/src/main/assets/app-styles/listening-room.webp").path)))
        val heading=FontFamily(Font(File(root,if(style==AppStyle.AFTER_DARK)"themes/web-slinger/fonts/BarlowCondensed.ttf" else "design/app-styles/CormorantGaramond.ttf"),FontWeight.Bold))
        val body=FontFamily(Font(File(root,"themes/midnight-express/fonts/Manrope.ttf")))
        val typography=appStyleTypography(Typography(),heading,body)
        var callbacks=0
        val liveCover=mutableStateOf<Any?>(art[1])
        val liveTitle=mutableStateOf<String?>("Blue hour")
        val items=List(4) {i->StyleMusicItem("track-$i",listOf("Blue hour","Evening listening","A slower kind of day","On the way home")[i],"Native artwork fixture",art[i],{callbacks++},{callbacks++})}
        lateinit var scene:ImageComposeScene
        SwingUtilities.invokeAndWait {
            scene=ImageComposeScene(if(large)640 else 780,1800,density=Density(2f,if(large)2f else 1f)) {
                CompositionLocalProvider(LocalAppStyle provides style,LocalThemeVisuals provides theme,
                    LocalLayoutDirection provides if(large)LayoutDirection.Rtl else LayoutDirection.Ltr) {
                    MaterialTheme(colorScheme=style.colorScheme(),typography=typography) {
                        ThemePageSurface {
                            Column(Modifier.fillMaxSize()) {
                                LazyColumn(Modifier.weight(1f)) {
                                    if(kind=="shelves") {
                                        item {StyledMusicShelf(style,"Quick picks",items)}
                                    } else if(kind!="secondary") {
                                        item { StyledHomeOpening(style,items[0],liveCover.value,if(style==AppStyle.AFTER_DARK)liveTitle.value else null,
                                            if(style==AppStyle.AFTER_DARK && liveTitle.value!=null)"Native artwork fixture" else null,{callbacks++},"Open player") }
                                        item {StyledMusicShelf(style,"Continue listening",items,compact=true)}
                                        item {StyledMusicShelf(style,"Quick picks",items.reversed())}
                                    } else {
                                        item {
                                            Column(Modifier.padding(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
                                                StyleBrand()
                                                Text("Search",style=MaterialTheme.typography.headlineLarge)
                                                OutlinedTextField("",{},label={Text("Artists, songs and more")},leadingIcon={Icon(Icons.Rounded.Search,null)},modifier=Modifier.fillMaxWidth())
                                                Text("Explore",style=MaterialTheme.typography.headlineLarge)
                                                Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {AssistChip({},label={Text("Focus")});AssistChip({},label={Text("Evening")})}
                                                Text("Library",style=MaterialTheme.typography.headlineLarge)
                                                ListItem(headlineContent={Text("Downloads")},supportingContent={Text("Your offline music")},leadingContent={Icon(Icons.Rounded.Download,null)})
                                                Text("Settings",style=MaterialTheme.typography.headlineLarge)
                                                Button({callbacks++}) {Text("App style · ${style.title}")}
                                                Text("Native shared components; illustrative content.",style=MaterialTheme.typography.bodySmall)
                                            }
                                        }
                                    }
                                }
                                Surface(color=theme.surface,shape=androidx.compose.foundation.shape.RoundedCornerShape(12.dp),modifier=Modifier.padding(horizontal=16.dp)) {
                                    Row(Modifier.fillMaxWidth().heightIn(min=64.dp).padding(horizontal=14.dp),verticalAlignment=Alignment.CenterVertically) {
                                        Column(Modifier.weight(1f)) {Text("Blue hour",style=MaterialTheme.typography.titleSmall);Text("Native fixture",style=MaterialTheme.typography.bodySmall)}
                                        PlayerControlSurface("Play",{callbacks++},Modifier.size(48.dp),tint=theme.accent,reducedMotion=true) {Icon(Icons.Rounded.PlayArrow,null,tint=theme.accent)}
                                    }
                                }
                                Row(Modifier.fillMaxWidth().padding(horizontal=12.dp,vertical=8.dp)) {
                                    listOf("Home","Search","Explore","Library").forEachIndexed {i,label->
                                        ThemeNavigationItem(label,i==0,if(i==0)theme.accent else theme.muted,{callbacks++},Modifier.weight(1f)) {
                                            Icon(listOf(Icons.Rounded.Home,Icons.Rounded.Search,Icons.Rounded.Explore,Icons.Rounded.LibraryMusic)[i],null,tint=if(i==0)theme.accent else theme.muted)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        repeat(18) {frame->Thread.sleep(20);SwingUtilities.invokeAndWait {scene.render(frame*50_000_000L)}}
        SwingUtilities.invokeAndWait {
            check(callbacks==0) {"Rendering must not trigger playback/actions"}
            val nodes=scene.semanticsOwners.flatMap {it.rootSemanticsNode.nodes()}
            val tabs=nodes.filter {it.config.getOrNull(SemanticsProperties.Role)==Role.Tab}
            check(tabs.size==4)
            check(tabs.all {it.boundsInRoot.height>=96f})
            var expectedCallbacks=0
            if(kind=="shelves") {
                val cards=nodes.filter {it.config.getOrNull(SemanticsActions.OnLongClick)!=null}
                check(cards.size==4) {"$style must retain every card/menu: ${cards.size}"}
                cards.forEach {check(it.config.getOrNull(SemanticsActions.OnClick)!!.action!!.invoke())}
                check(cards.first().config.getOrNull(SemanticsActions.OnLongClick)!!.action!!.invoke())
                expectedCallbacks=5
            }
            val play=nodes.first {it.config.getOrNull(SemanticsProperties.ContentDescription)?.contains("Play")==true}
            check(play.boundsInRoot.width>=96f && play.boundsInRoot.height>=96f)
            check(play.config.getOrNull(SemanticsActions.OnClick)!!.action!!.invoke())
            check(callbacks==expectedCallbacks+1)
            File(output,"${style.name.lowercase()}-$kind.webp").writeBytes(scene.render(1_100_000_000L).encodeToData(EncodedImageFormat.WEBP,90)!!.bytes)
        }
        if(style==AppStyle.AFTER_DARK && kind=="home") {
            fun frame(label:String):java.awt.image.BufferedImage {
                repeat(20) {i->Thread.sleep(20);SwingUtilities.invokeAndWait {scene.render(1_200_000_000L+i*60_000_000L)}}
                lateinit var pixels:java.awt.image.BufferedImage
                SwingUtilities.invokeAndWait {
                    val shot=scene.render(2_500_000_000L)
                    File(output,"after_dark-$label.webp").writeBytes(shot.encodeToData(EncodedImageFormat.WEBP,90)!!.bytes)
                    pixels=javax.imageio.ImageIO.read(shot.encodeToData(EncodedImageFormat.PNG)!!.bytes.inputStream())
                }
                return pixels
            }
            val before=frame("cover-a")
            SwingUtilities.invokeAndWait {liveCover.value=art[2]}
            val after=frame("cover-b")
            var changed=0
            for(y in 580..860 step 10)for(x in 80..400 step 10)if(before.getRGB(x,y)!=after.getRGB(x,y))changed++
            check(changed>50) {"Live cover change did not reach the record image: $changed pixels"}
            SwingUtilities.invokeAndWait {liveCover.value=null;liveTitle.value=null}
            val idle=frame("idle")
            SwingUtilities.invokeAndWait {liveCover.value=File(output,"missing-cover.jpg");liveTitle.value="Blue hour"}
            val failed=frame("failed-cover")
            var fallbackChanged=0
            for(y in 620..820 step 10)for(x in 130..350 step 10)if(idle.getRGB(x,y)!=failed.getRGB(x,y))fallbackChanged++
            check(fallbackChanged==0) {"Missing-cover and idle record fallbacks differ: $fallbackChanged"}
            check(callbacks==1) {"Artwork changes must not trigger playback"}
            results+="AFTER_DARK live artwork: changed $changed sampled pixels on song-cover replacement; identical idle/error abstract record; no automatic playback callbacks."
        }
        SwingUtilities.invokeAndWait {scene.close()}
        results+="${style.name}/$kind: native shared style/header/shelf/surface components; four navigation tabs, >=48 dp controls, no automatic actions; explicit Play callback once."
    }
    results += "These are native component studies with theme-art fixtures, not complete Android screenshots or real music-data/device tests."
    File(output,"RESULTS.txt").writeText(results.joinToString("\n")+"\n")
    println(results.joinToString("\n"))
}
