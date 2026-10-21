@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
package app.veyra.music.ui.atelier

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import app.veyra.music.ui.themes.ThemeIconChoice
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import java.io.File
import java.util.Properties
import javax.swing.SwingUtilities

private val root=File(System.getProperty("veyra.root"))
private val art=File(root,"design/launcher/atelier")
private val properties=Properties().apply { File(art,"render.properties").inputStream().use { load(it) } }
private val ids=properties.getProperty("ids").split(',')
private fun name(id:String)=properties.getProperty("$id.name")
@Composable private fun IconArt(id:String,modifier:Modifier) {
    val bitmap=remember(id) { Image.makeFromEncoded(File(art,"$id.webp").readBytes()).toComposeImageBitmap() }
    Image(bitmap,null,modifier)
}
@Composable private fun Masks(secondPage:Boolean) {
    Column(Modifier.fillMaxSize().padding(20.dp),verticalArrangement=Arrangement.spacedBy(18.dp)) {
        Text("Glass Atelier",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
        Text("Standalone icons · 48 dp masks",style=MaterialTheme.typography.bodyMedium)
        ids.drop(if(secondPage)6 else 0).take(6).forEach { id ->
            Row(Modifier.fillMaxWidth().heightIn(min=92.dp),verticalAlignment=Alignment.CenterVertically,
                horizontalArrangement=Arrangement.spacedBy(14.dp)) {
                Text(name(id),Modifier.weight(1f),style=MaterialTheme.typography.titleMedium,fontWeight=FontWeight.Bold)
                IconArt(id,Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)))
                IconArt(id,Modifier.size(48.dp).clip(CircleShape))
            }
        }
        Text("Native mask study · not an Android screenshot",style=MaterialTheme.typography.bodySmall)
    }
}
@Composable private fun Picker(secondPage:Boolean,onSelect:()->Unit) {
    val visible=ids.drop(if(secondPage)6 else 0).take(6)
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement=Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
            Text("App icon",Modifier.weight(1f),style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold)
            TextButton(onClick={}) { Text("Done") }
        }
        FlowRow(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            listOf("Veyra","Glass","Themes","Glass Atelier").forEach { label ->
                FilterChip(selected=label=="Glass Atelier",onClick={},label={Text(label)})
            }
        }
        Column(Modifier.selectableGroup(),verticalArrangement=Arrangement.spacedBy(10.dp)) {
            visible.chunked(2).forEach { row ->
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                    row.forEach { id ->
                        ThemeIconChoice(name(id),id==visible.first(),
                            true,onSelect,Modifier.weight(1f)) {
                            IconArt(id,Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)))
                        }
                    }
                }
            }
        }
        Text("Your launcher choice is independent of the active theme.",style=MaterialTheme.typography.bodyMedium)
    }
}
private fun SemanticsNode.flatten():List<SemanticsNode> = listOf(this)+children.flatMap { it.flatten() }
fun main() {
    val out=File(root,"verification/atelier-preview").apply { mkdirs() }
    val results=mutableListOf<String>()
    for(kind in listOf("masks-one","masks-two","picker-large-text","picker-rtl-large-text")) {
        val large=kind.contains("large-text")
        val width=if(large)320 else 390
        var clicks=0
        lateinit var scene:ImageComposeScene
        SwingUtilities.invokeAndWait {
            scene=ImageComposeScene(width*2,1800,density=Density(2f,if(large)1.5f else 1f)) {
                CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides
                    if(kind.contains("rtl"))LayoutDirection.Rtl else LayoutDirection.Ltr) {
                    MaterialTheme(colorScheme=darkColorScheme(primary=Color(0xFFE2DBFF),
                        background=Color(0xFF0D0F14),surface=Color(0xFF151820))) {
                        Surface(Modifier.fillMaxSize()) {
                            if(kind.startsWith("masks"))Masks(kind.endsWith("two"))
                            else Picker(kind.contains("rtl")) { clicks++ }
                        }
                    }
                }
            }
        }
        repeat(3) { index -> SwingUtilities.invokeAndWait { scene.render(index*100_000_000L) } }
        SwingUtilities.invokeAndWait {
            val nodes=scene.semanticsOwners.flatMap { it.rootSemanticsNode.flatten() }
            check(nodes.isNotEmpty())
            if(large) {
                val radios=nodes.filter { it.config.getOrNull(SemanticsProperties.Role)==Role.RadioButton }
                check(radios.size==6) { "Each visible choice must expose one radio target" }
                radios.forEach { check(it.boundsInRoot.height>=96f && it.boundsInRoot.width>=96f) }
                check(radios.first().config.getOrNull(SemanticsActions.OnClick)?.action?.invoke()==true)
                check(clicks==1)
                results+="$kind: six radio targets >=48 dp; selection callback once; 320 dp / 150% text."
            }
            File(out,"$kind.webp").writeBytes(scene.render(400_000_000L).encodeToData(EncodedImageFormat.WEBP,90)!!.bytes)
            results+="$kind: native component study rendered."
            scene.close()
        }
    }
    File(out,"RENDER-RESULTS.txt").writeText(results.joinToString("\n")+"\nNative shared components with illustrative layout; not Android screenshots or device launcher tests.\n")
    println(results.joinToString("\n"))
}
