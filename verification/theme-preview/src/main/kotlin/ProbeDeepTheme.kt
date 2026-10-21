@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
package app.veyra.music.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.AllInclusive
import androidx.compose.runtime.*
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.*
import app.veyra.music.ui.themes.*
import java.io.File
import java.util.Properties
import javax.imageio.ImageIO
import javax.swing.SwingUtilities
import org.jetbrains.skia.EncodedImageFormat

private fun SemanticsNode.deepNodes():List<SemanticsNode> = listOf(this)+children.flatMap { it.deepNodes() }
fun main() {
    val root=File(System.getProperty("veyra.root"))
    val out=File(root,"themes/previews/deep-polish-0.1.8").apply {mkdirs()}
    val p=Properties().apply {File(root,"themes/render-config/matcha-mischief.properties").inputStream().use {load(it)}}
    fun c(k:String)=Color(0xFF000000L or p.getProperty(k).removePrefix("#").toLong(16))
    val pack=ThemeVisuals("matcha-mischief","Matcha Mischief",c("accent"),c("surface"),c("raised"),c("outline"),20,20,32,
        mapOf("header" to File(root,"themes/matcha-mischief/${p.getProperty("asset.header")}").path),
        background=c("background"),text=c("text"),muted=c("muted"))
    val results=mutableListOf<String>()
    for(kind in listOf("round-output","round-modes-rtl","cut-modes","default-output","selected-modes","disabled-output")) {
        val count=if(kind.contains("modes"))3 else 2
        val theme=when {kind.startsWith("default")->null;kind.startsWith("cut")->pack.copy(sceneStyle="web",buttonCorner=10);else->pack}
        var clicks=0
        lateinit var scene:ImageComposeScene
        SwingUtilities.invokeAndWait {
            scene=ImageComposeScene(480,240,density=Density(2f,1.5f)) {
                CompositionLocalProvider(LocalThemeVisuals provides theme,
                    LocalLayoutDirection provides if(kind.contains("rtl"))LayoutDirection.Rtl else LayoutDirection.Ltr) {
                    MaterialTheme(colorScheme=darkColorScheme(background=pack.background)) {
                        Surface(Modifier.fillMaxSize(),color=pack.background) {
                            Column(Modifier.padding(16.dp)) {
                                PlayerControlGroup(pack.accent,Modifier.height(48.dp)) {
                                    repeat(count) {i ->
                                        if(i>0)PlayerControlGroupDivider(pack.accent)
                                        PlayerControlSurface("Action $i",{clicks++},Modifier.width(64.dp).height(48.dp),grouped=true,
                                            enabled=!kind.startsWith("disabled"),selected=i==count-1,
                                            reducedMotion=true,tint=pack.accent,
                                            containerColor=if(kind.startsWith("selected") && i==count-1)pack.accent.copy(alpha=.16f) else Color.Transparent) {
                                            Icon(if(count==2)listOf(Icons.Rounded.Headphones,Icons.Rounded.Person)[i]
                                                else listOf(Icons.Rounded.Shuffle,Icons.Rounded.Repeat,Icons.Rounded.AllInclusive)[i],
                                                contentDescription=null,tint=pack.accent,modifier=Modifier.size(24.dp))
                                        }
                                    }
                                }
                                Text("Native shared controls",style=MaterialTheme.typography.labelSmall,color=pack.text)
                            }
                        }
                    }
                }
            }
        }
        repeat(5) {frame -> SwingUtilities.invokeAndWait {scene.render(frame*100_000_000L)}}
        SwingUtilities.invokeAndWait {
            val buttons=scene.semanticsOwners.flatMap {it.rootSemanticsNode.deepNodes()}
                .filter {it.config.getOrNull(SemanticsProperties.Role)==Role.Button}
            check(buttons.size==count)
            check(buttons.all {it.boundsInRoot.width>=128 && it.boundsInRoot.height>=96})
            check(buttons.count {it.config.getOrNull(SemanticsProperties.Selected)==true}==1)
            if(kind.startsWith("disabled")) check(buttons.all {it.config.contains(SemanticsProperties.Disabled)})
            else buttons.forEach {check(it.config.getOrNull(SemanticsActions.OnClick)!!.action!!.invoke())}
            check(clicks==if(kind.startsWith("disabled"))0 else count)
            val image=scene.render(600_000_000L)
            val pixels=ImageIO.read(image.encodeToData(EncodedImageFormat.PNG)!!.bytes.inputStream())
            if(!kind.startsWith("selected")) {
                // Separators stop before the outer edge; no full-height divider or individual rounded outlines at the joins.
                for(i in 1 until count) for(y in listOf(40,120)) {
                    val x=32+i*128+(i-1)*2
                    val a=pixels.getRGB(x,y);val b=pixels.getRGB(x-6,y)
                    check(listOf(0,8,16).maxOf {k->kotlin.math.abs((a shr k and 255)-(b shr k and 255))}<=3) {"$kind protruding seam at $x,$y"}
                }
            }
            File(out,"$kind.webp").writeBytes(image.encodeToData(EncodedImageFormat.WEBP,90)!!.bytes)
            scene.close()
        }
        results += "$kind: $count controls, >=48 dp targets, selection/disabled semantics, callbacks and inset seams passed."
    }
    // Exercise the same fade modifier as the real Android artist photo and the wrap-content sheet surface.
    fun capture(name:String,theme:ThemeVisuals?,photo:Boolean):java.awt.image.BufferedImage {
        lateinit var scene:ImageComposeScene
        SwingUtilities.invokeAndWait {
            scene=ImageComposeScene(640,800,density=Density(2f)) {
                CompositionLocalProvider(LocalThemeVisuals provides theme) {
                    MaterialTheme(colorScheme=darkColorScheme(background=pack.background)) {
                        ThemeComponentSurface(Modifier.fillMaxWidth()) {
                            Box(Modifier.fillMaxWidth().height(400.dp)) {
                                if(photo) Box(Modifier.fillMaxWidth().height(300.dp).themeArtistArtworkFade(theme!=null).background(Color.White))
                            }
                        }
                    }
                }
            }
        }
        repeat(16) {frame->Thread.sleep(25);SwingUtilities.invokeAndWait {scene.render(frame*50_000_000L)}}
        lateinit var pixels:java.awt.image.BufferedImage
        SwingUtilities.invokeAndWait {
            val image=scene.render(900_000_000L)
            pixels=ImageIO.read(image.encodeToData(EncodedImageFormat.PNG)!!.bytes.inputStream())
            File(out,"$name.webp").writeBytes(image.encodeToData(EncodedImageFormat.WEBP,90)!!.bytes)
            scene.close()
        }
        return pixels
    }
    val baseline=capture("sheet-artwork",pack,false)
    val faded=capture("artist-fade",pack,true)
    val plain=capture("default-photo",null,true)
    check(faded.getRGB(320,100)==plain.getRGB(320,100)) {"Photo top must remain unchanged"}
    val a=faded.getRGB(320,599);val b=baseline.getRGB(320,599)
    check(listOf(0,8,16).maxOf {k->kotlin.math.abs((a shr k and 255)-(b shr k and 255))}<=3) {"Artist foot must reveal scene"}
    check(plain.getRGB(320,599)==-1) {"No-theme photograph must not fade"}
    check(baseline.getRGB(320,700)==faded.getRGB(320,700)) {"Artwork remains below photo"}
    results += "Artist mask: unchanged photo top, theme scene revealed at foot/below photo, no-pack photo unchanged."
    results += "Wrap-content sheet backdrop rendered with actual shared scene; captures are native component studies, not Android screenshots."
    File(out,"RESULTS.txt").writeText(results.joinToString("\n")+"\n")
    println(results.joinToString("\n"))
}
