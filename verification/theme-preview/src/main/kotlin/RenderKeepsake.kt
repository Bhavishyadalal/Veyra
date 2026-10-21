@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
package app.veyra.music.ui.keepsake

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.*
import app.veyra.music.ui.themes.ThemeStudioRow
import app.veyra.music.ui.themes.ThemeIconChoice
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import java.io.File
import javax.swing.SwingUtilities

private val root=File(System.getProperty("veyra.root"))
private val ids=listOf("patchwork-picnic","clover-requiem","sunroom-mixtape","concrete-afterhours","matcha-mischief")
private fun title(id:String)=id.split('-').joinToString(" ") { it.replaceFirstChar(Char::uppercase) }
private fun SemanticsNode.flatten():List<SemanticsNode> = listOf(this)+children.flatMap { it.flatten() }

fun main() {
    val out=File(root,"themes/previews/keepsake-ui").apply {mkdirs()}
    val results=mutableListOf<String>()
    for(kind in listOf("studio","icons-large-text","icons-rtl-large-text")) {
        val large=kind.contains("large-text")
        var clicks=0
        lateinit var scene:ImageComposeScene
        SwingUtilities.invokeAndWait {
            scene=ImageComposeScene(if(large)640 else 780,1800,density=Density(2f,if(large)1.5f else 1f)) {
                CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides
                    if(kind.contains("rtl"))LayoutDirection.Rtl else LayoutDirection.Ltr) {
                    MaterialTheme(colorScheme=darkColorScheme(primary=Color(0xFFD8EBA6),primaryContainer=Color(0xFF42552F),
                        onPrimaryContainer=Color(0xFFFFF9E7),background=Color(0xFF11251C),surface=Color(0xFF20392B),
                        surfaceContainerLow=Color(0xFF20392B),surfaceContainerHigh=Color(0xFF34503A),
                        onSurface=Color(0xFFFFF9E7),onSurfaceVariant=Color(0xFFD7E2C9))) {
                        Surface(Modifier.fillMaxSize()) {
                            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
                                Text(if(large)"App icon" else "Theme studio",style=MaterialTheme.typography.titleLarge)
                                Text("Native component study · illustrative layout",style=MaterialTheme.typography.bodySmall)
                                OutlinedTextField("",{},singleLine=true,label={Text(if(large)"Search icons" else "Search themes")},
                                    shape=androidx.compose.foundation.shape.RoundedCornerShape(20.dp),modifier=Modifier.fillMaxWidth())
                                if(!large) {
                                    ids.forEach { id -> ThemeStudioRow(title(id),if(id==ids.first())"Downloaded · Ready to apply" else "Keepsake collection",
                                        File(root,"themes/$id/artwork/preview.webp"),id==ids.first(),{clicks++}) }
                                } else Column(Modifier.selectableGroup(),verticalArrangement=Arrangement.spacedBy(10.dp)) {
                                    ids.chunked(2).forEach { row ->
                                        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                                            row.forEach { id ->
                                                ThemeIconChoice(title(id),id==ids.first(),true,{clicks++},Modifier.weight(1f)) {
                                                    val bitmap=remember(id) { Image.makeFromEncoded(File(root,"themes/launcher-art/$id.webp").readBytes()).toComposeImageBitmap() }
                                                    Image(bitmap,null,Modifier.fillMaxSize())
                                                }
                                            }
                                            if(row.size==1) Spacer(Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        repeat(12) { frame ->
            Thread.sleep(30)
            SwingUtilities.invokeAndWait {scene.render(frame*100_000_000L)}
        }
        SwingUtilities.invokeAndWait {
            val role=if(large)Role.RadioButton else Role.Button
            val nodes=scene.semanticsOwners.flatMap {it.rootSemanticsNode.flatten()}.filter {it.config.getOrNull(SemanticsProperties.Role)==role}
            check(nodes.size==5)
            nodes.forEach {check(it.boundsInRoot.width>=96f && it.boundsInRoot.height>=96f)}
            check(nodes.first().config.getOrNull(SemanticsActions.OnClick)?.action?.invoke()==true)
            check(clicks==1)
            File(out,"$kind.webp").writeBytes(scene.render(1_300_000_000L).encodeToData(EncodedImageFormat.WEBP,90)!!.bytes)
            results+="$kind: five native selection targets >=48 dp; callback once; font scale ${if(large)1.5 else 1.0}."
            scene.close()
        }
    }
    File(out,"RENDER-RESULTS.txt").writeText(results.joinToString("\n")+"\nShared components in illustrative layouts, not Android screenshots or full-screen/device validation.\n")
    println(results.joinToString("\n"))
}
