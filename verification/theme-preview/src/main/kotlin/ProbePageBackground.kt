@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
package app.veyra.music.ui.appearance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.*
import app.veyra.music.ui.themes.*
import java.io.File
import java.util.Properties
import javax.imageio.ImageIO
import javax.swing.SwingUtilities
import org.jetbrains.skia.EncodedImageFormat

/** This uses the exact destination wrapper called by MainActivity. Layout content is illustrative. */
fun main() {
    val root = File(System.getProperty("veyra.root"))
    val out = File(root, "themes/previews/background-0.1.8").apply { mkdirs() }
    val p = Properties().apply { File(root,"themes/render-config/matcha-mischief.properties").inputStream().use { load(it) } }
    fun color(k: String) = Color(0xFF000000L or p.getProperty(k).removePrefix("#").toLong(16))
    val theme = ThemeVisuals("matcha-mischief", "Matcha Mischief", color("accent"),color("surface"),color("raised"),color("outline"),20,20,32,
        mapOf("header" to File(root,"themes/matcha-mischief/${p.getProperty("asset.header")}").path),
        background=color("background"),text=color("text"),muted=color("muted"))
    fun capture(key: String, pack: ThemeVisuals?, nested: Boolean = false, opaqueRegression: Boolean = false): java.awt.image.BufferedImage {
        lateinit var scene: ImageComposeScene
        SwingUtilities.invokeAndWait {
            scene = ImageComposeScene(640,1280,density=Density(2f,1.5f)) {
                CompositionLocalProvider(LocalThemeVisuals provides pack, LocalLayoutDirection provides LayoutDirection.Rtl) {
                    MaterialTheme(colorScheme=darkColorScheme(background=theme.background,onSurface=theme.text)) {
                        RetainedRootPage(rememberSaveableStateHolder(),key) {
                            val content: @Composable () -> Unit = {
                                Column(Modifier.fillMaxSize().padding(20.dp)) {
                                    Text("Native destination wrapper",style=MaterialTheme.typography.titleLarge)
                                    Text(key,style=MaterialTheme.typography.bodyMedium)
                                    Text("Illustrative content · not an Android screenshot",style=MaterialTheme.typography.labelSmall)
                                }
                            }
                            if(opaqueRegression) Box(Modifier.fillMaxSize().background(theme.background)) { content() }
                            else if(nested) ThemePageSurface { content() }
                            else content()
                        }
                    }
                }
            }
        }
        repeat(20) { frame ->
            Thread.sleep(25)
            SwingUtilities.invokeAndWait { scene.render(frame*50_000_000L) }
        }
        lateinit var png: ByteArray
        SwingUtilities.invokeAndWait {
            val image=scene.render(1_100_000_000L)
            png=image.encodeToData(EncodedImageFormat.PNG)!!.bytes
            if(pack != null && !opaqueRegression && pack.assets.isNotEmpty())
                File(out,key.replace(':','-')+".webp").writeBytes(image.encodeToData(EncodedImageFormat.WEBP,90)!!.bytes)
            scene.close()
        }
        return ImageIO.read(png.inputStream())
    }
    val results=mutableListOf<String>()
    val withoutArt=theme.copy(assets=emptyMap())
    val reference=capture("tab:0",withoutArt)
    for((key,nested) in listOf("tab:0" to false,"tab:1" to false,"tab:2" to true,"tab:3" to false,
        "settings" to false,"artist-detail" to true,"settings-overlay" to true)) {
        val image=capture(key,theme,nested)
        var changed=0
        // Exclude illustrative labels; compare actual artwork with the identical scene minus its header asset.
        for(y in 400 until 1200 step 4) for(x in 20 until 620 step 4) {
            val a=image.getRGB(x,y); val b=reference.getRGB(x,y)
            if(listOf(0,8,16).maxOf { shift -> kotlin.math.abs((a shr shift and 255)-(b shr shift and 255)) }>=3) changed++
        }
        check(changed>500) { "$key hides/misses theme artwork: $changed changed samples" }
        results += "$key: artwork visible at $changed/30000 sampled pixels; actual retained wrapper, RTL and 150% font scale."
    }
    val plain=capture("tab:0",null)
    check(plain.getRGB(320,1100)==theme.background.toArgb()) { "Default background changed" }
    val regression=capture("tab:0",theme,opaqueRegression=true)
    check(regression.getRGB(320,1100)==plain.getRGB(320,1100)) { "Opaque cover control must hide artwork" }
    results += "Default/no-pack fallback remains opaque; old opaque-cover regression control hides the image."
    results += "Native shared-wrapper checks; not full Android screenshots or device tests."
    File(out,"RESULTS.txt").writeText(results.joinToString("\n")+"\n")
    println(results.joinToString("\n"))
}
