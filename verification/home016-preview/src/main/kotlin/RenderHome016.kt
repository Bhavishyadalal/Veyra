@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
package app.veyra.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.*
import app.veyra.music.data.home.*
import app.veyra.music.data.model.*
import org.jetbrains.skia.EncodedImageFormat
import java.io.File
import javax.swing.SwingUtilities

private val font = FontFamily(Font(File(System.getProperty("veyra.root"), "app/src/main/res/font/veyra_regular.ttf")))
private val typography = Typography(
    bodyMedium = TextStyle(fontFamily = font, fontSize = 14.sp, lineHeight = 21.sp),
    bodySmall = TextStyle(fontFamily = font, fontSize = 12.sp, lineHeight = 18.sp),
    titleSmall = TextStyle(fontFamily = font, fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
    headlineSmall = TextStyle(fontFamily = font, fontSize = 23.sp, fontWeight = FontWeight.Bold),
    labelLarge = TextStyle(fontFamily = font, fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
)
private fun SemanticsNode.flatten(): List<SemanticsNode> = listOf(this) + children.flatMap { it.flatten() }
private fun ImageComposeScene.nodes() = semanticsOwners.flatMap { it.rootSemanticsNode.flatten() }
private fun ImageComposeScene.action(description: String) = nodes().first { node ->
    node.config.getOrNull(SemanticsProperties.ContentDescription)?.contains(description) == true
}
fun main() = SwingUtilities.invokeAndWait {
    val out = File(System.getProperty("veyra.root"), "design/home016-preview").apply { mkdirs() }
    val feed = listOf("Quick picks", "Hindi Hits", "Albums & singles", "Recents").map {
        HomeShelf(it, listOf(ShelfItem("Song", "Artist", null, "id", null)))
    }
    for (kind in listOf("offline", "saved", "editor", "large-text", "rtl", "light")) {
        val width = if (kind == "large-text") 320 else 390
        val scene = ImageComposeScene(width * 2, 2100,
            density = Density(2f, if (kind == "large-text") 1.6f else 1f),
            layoutDirection = if (kind == "rtl") LayoutDirection.Rtl else LayoutDirection.Ltr) {
            MaterialTheme(colorScheme = if (kind == "light") lightColorScheme() else darkColorScheme(
                background = Color.Black, surface = Color(0xff101014), primary = Color(0xffd9c4ff)), typography = typography) {
                Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(top = 36.dp)) {
                    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
                    if (kind == "offline" || kind == "saved") {
                        Text("Veyra", Modifier.padding(16.dp), style = MaterialTheme.typography.headlineLarge)
                        HomeExperienceHeader(kind == "offline", true, kind == "saved", null,
                            HomeLayout().options(feed), {}, {}, {}, reducedMotion = true)
                        Text("Home section artwork appears below this header", Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodySmall)
                    } else HomeSectionEditorContent(HomeLayout().options(feed), { _, _ -> }, { _, _ -> }, {}, {}, true)
                    }
                }
            }
        }
        scene.render(0).close()
        val bitmap = scene.render(1_000_000_000L)
        File(out, "$kind.png").writeBytes(bitmap.encodeToData(EncodedImageFormat.PNG)!!.bytes)
        bitmap.close(); scene.close()
    }
    var layout by mutableStateOf(HomeLayout())
    var downloads = 0; var local = 0; var edits = 0; var reset = 0
    val scene = ImageComposeScene(780, 2200, density = Density(2f)) {
        MaterialTheme {
            Column {
                HomeExperienceHeader(true, true, false, null, layout.options(feed), { edits++ }, { downloads++ }, { local++ }, true)
                HomeSectionEditorContent(layout.options(feed),
                    { id, offset -> layout = layout.move(id, offset, feed) },
                    { id, visible -> layout = layout.setVisible(id, visible) },
                    { reset++; layout = HomeLayout() }, {}, true)
            }
        }
    }
    var time = 2_000_000_000L
    fun frame() { time += 50_000_000; scene.render(time).close() }
    fun click(node: SemanticsNode) { check(node.config[SemanticsActions.OnClick].action!!.invoke()); frame() }
    frame()
    click(scene.action("Move Hindi Hits up"))
    check(layout.apply(feed).first().title == "Hindi Hits")
    click(scene.action("Show Quick picks")); check(layout.apply(feed).none { it.title == "Quick picks" })
    fun textNode(label: String) = scene.nodes().first { it.config.getOrNull(SemanticsProperties.Text)?.any { text -> text.text == label } == true && it.config.getOrNull(SemanticsActions.OnClick) != null }
    click(textNode("Downloads")); check(downloads == 1)
    click(textNode("Local Music")); check(local == 1)
    click(textNode("Customize Home")); check(edits == 1)
    click(textNode("Restore default layout")); check(reset == 1 && layout.apply(feed) == feed)
    check(scene.action("Move Quick picks up").config.getOrNull(SemanticsProperties.Disabled) != null)
    scene.close()
    println("7 real component interaction checks passed; six review boards rendered. Android host wiring not exercised.")
}
