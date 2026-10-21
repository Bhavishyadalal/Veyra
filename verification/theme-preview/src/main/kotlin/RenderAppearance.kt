@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
package app.veyra.music.ui.appearance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.*
import app.veyra.music.ui.components.VeyraBrandContent
import app.veyra.music.ui.themes.*
import java.io.File
import java.util.Properties
import org.jetbrains.skia.EncodedImageFormat
import javax.swing.SwingUtilities

private val root=File(System.getProperty("veyra.root"))
private fun SemanticsNode.flatten():List<SemanticsNode> = listOf(this)+children.flatMap {it.flatten()}
private fun pack(id:String):ThemeVisuals {
    val p=Properties().apply {File(root,"themes/render-config/$id.properties").inputStream().use {load(it)}}
    fun c(key:String)=Color(0xFF000000L or p.getProperty(key).removePrefix("#").toLong(16))
    return ThemeVisuals(id,p.getProperty("name"),c("accent"),c("surface"),c("raised"),c("outline"),
        p.getProperty("rowCorner").toInt(),p.getProperty("buttonCorner").toInt(),p.getProperty("navCorner").toInt(),
        p.stringPropertyNames().filter {it.startsWith("asset.")}.associate {
            it.removePrefix("asset.") to File(root,"themes/$id/${p.getProperty(it)}").path},
        background=c("background"),text=c("text"),muted=c("muted"),sceneStyle=p.getProperty("sceneStyle"),tagline=p.getProperty("tagline"))
}

fun main() {
    val output=File(root,"themes/previews/appearance-0.1.8").apply {mkdirs()}
    val results=mutableListOf<String>()
    val selected=System.getProperty("veyra.appearance.study").orEmpty()
    for(kind in listOf("home","search-large-text","library-rtl-large-text","fonts-rtl-large-text","fonts-light-large-text","brand-gothic-large-text")
        .filter {selected.isEmpty() || it in selected.split(',')}) {
        val large=kind.contains("large-text")
        val light=kind.contains("light")
        val id=when {kind.startsWith("search")->"clover-requiem";kind.startsWith("brand")->"chrome-vesper";else->"matcha-mischief"}
        val theme=pack(id)
        val heading=FontFamily(Font(File(theme.assets.getValue("headingFont")),FontWeight.Bold))
        val body=FontFamily(Font(File(root,"themes/midnight-express/fonts/Manrope.ttf")))
        val typography=Typography().let { t -> t.copy(
            headlineLarge=t.headlineLarge.copy(fontFamily=heading,fontWeight=FontWeight.Bold),
            headlineSmall=t.headlineSmall.copy(fontFamily=heading,fontWeight=FontWeight.Bold),
            titleLarge=t.titleLarge.copy(fontFamily=body),bodyLarge=t.bodyLarge.copy(fontFamily=body),
            bodyMedium=t.bodyMedium.copy(fontFamily=body)) }
        var clicks=0
        lateinit var scene:ImageComposeScene
        SwingUtilities.invokeAndWait {
            scene=ImageComposeScene(if(large)640 else 780,1800,density=Density(2f,if(large)1.5f else 1f)) {
                CompositionLocalProvider(LocalThemeVisuals provides if(light)null else theme,
                    androidx.compose.ui.platform.LocalLayoutDirection provides if(kind.contains("rtl"))LayoutDirection.Rtl else LayoutDirection.Ltr) {
                    MaterialTheme(typography=typography,colorScheme=if(light)lightColorScheme() else darkColorScheme(primary=theme.accent,
                        background=theme.background,surface=theme.surface,surfaceContainerLow=theme.surface,
                        surfaceContainerHigh=theme.raised,onSurface=theme.text,onBackground=theme.text,onSurfaceVariant=theme.muted)) {
                        Surface(Modifier.fillMaxSize(),color=MaterialTheme.colorScheme.background,contentColor=MaterialTheme.colorScheme.onBackground) {
                        Box(Modifier.fillMaxSize()) {
                            ThemeScene(Modifier.matchParentSize(),prominent=true)
                            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
                                verticalArrangement=Arrangement.spacedBy(16.dp)) {
                                Text("Native component study",style=MaterialTheme.typography.labelSmall)
                                if(kind.startsWith("fonts")) {
                                    Text("Fonts",style=MaterialTheme.typography.headlineSmall)
                                    ThemeFontPreview("Your music, your style.","Clear song titles. Comfortable reading.",typography)
                                    Column(Modifier.selectableGroup()) {
                                        listOf("Follow theme","Veyra default","Manrope","Fredoka","Space Grotesk","Rajdhani","Barlow Condensed","Grenze Gotisch").forEachIndexed {i,name ->
                                            ThemeFontChoice(name,i==0) {clicks++}
                                        }
                                    }
                                } else {
                                    IconButton(onClick={clicks++},modifier=Modifier.size(48.dp)) {ThemeIcon(Icons.AutoMirrored.Rounded.ArrowBack,"Back")}
                                    VeyraBrandContent(ColorPainter(theme.accent),heading)
                                    Text(when {kind.startsWith("search")->"Search";kind.startsWith("library")->"Library";else->"Listen Now"},
                                        style=MaterialTheme.typography.headlineLarge)
                                    Text("Find your next favourite.",style=MaterialTheme.typography.bodyMedium,color=theme.muted)
                                    if(kind.startsWith("home")) ThemeAtmosphere("header")
                                    if(kind.startsWith("search")) OutlinedTextField("Evening",{},singleLine=true,
                                        modifier=Modifier.fillMaxWidth(),label={Text("Search music")},leadingIcon={ThemeIcon(Icons.Rounded.Search,null)})
                                    if(!kind.startsWith("brand")) {
                                        repeat(3) {i ->
                                            Row(Modifier.fillMaxWidth().heightIn(min=64.dp),verticalAlignment=Alignment.CenterVertically) {
                                                Column(Modifier.weight(1f)) {
                                                    Text(listOf("Your favourites","Evening listening","Recently played")[i],style=MaterialTheme.typography.titleLarge)
                                                    Text("Your music and artwork stay yours",style=MaterialTheme.typography.bodyMedium,color=theme.muted)
                                                }
                                                IconButton(onClick={clicks++}) {ThemeIcon(Icons.Rounded.PlayArrow,"Play")}
                                            }
                                        }
                                    }
                                }
                                Text("Illustrative layout · not an Android screenshot",style=MaterialTheme.typography.labelSmall)
                            }
                        }
                        }
                    }
                }
            }
        }
        repeat(12) {frame ->Thread.sleep(30);SwingUtilities.invokeAndWait {scene.render(frame*100_000_000L)}}
        SwingUtilities.invokeAndWait {
            val nodes=scene.semanticsOwners.flatMap {it.rootSemanticsNode.flatten()}
            if(kind.startsWith("fonts")) {
                val choices=nodes.filter {it.config.getOrNull(SemanticsProperties.Role)==Role.RadioButton}
                check(choices.size==8)
                choices.take(3).forEach {check(it.boundsInRoot.height>=96f)}
                check(choices.first().config.getOrNull(SemanticsActions.OnClick)?.action?.invoke()==true)
            } else {
                val back=nodes.first {it.config.getOrNull(SemanticsProperties.Role)==Role.Button}
                check(back.boundsInRoot.height>=96f && back.boundsInRoot.width>=96f) {"$kind Back bounds: ${back.boundsInRoot}"}
                val unmerged=scene.semanticsOwners.flatMap {it.unmergedRootSemanticsNode.flatten()}
                val glyph=unmerged.first {it.config.getOrNull(SemanticsProperties.Role)==Role.Image &&
                    it.config.getOrNull(SemanticsProperties.ContentDescription)?.contains("Back")==true}
                check(glyph.boundsInRoot.width<=48f && glyph.boundsInRoot.height<=48f) {"Default glyph must stay within 24 dp"}
                check(back.config.getOrNull(SemanticsActions.OnClick)?.action?.invoke()==true)
            }
            check(clicks==1)
            File(output,"$kind.webp").writeBytes(scene.render(1_300_000_000L).encodeToData(EncodedImageFormat.WEBP,90)!!.bytes)
            results+="$kind: native components rendered; one callback; checked targets >=48 dp."
            scene.close()
        }
    }
    val record=File(output,"RENDER-RESULTS.txt")
    val previous=if(record.exists())record.readLines().filter {it.contains(": native components")} else emptyList()
    record.writeText((previous+results).associateBy {it.substringBefore(':')}.values.joinToString("\n")+
        "\nShared components in illustrative layouts, not Android screenshots or device behavior tests.\n")
    println(results.joinToString("\n"))
}
