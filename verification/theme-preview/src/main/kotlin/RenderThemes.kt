@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
package app.veyra.music.ui.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.*
import app.veyra.music.ui.themes.*
import app.veyra.music.ui.icons.*
import app.veyra.music.ui.screens.SettingsRow
import app.veyra.music.ui.screens.SettingsPresetRow
import java.io.File
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import javax.swing.SwingUtilities

private val root=File(System.getProperty("veyra.root"))
private val dir=File(root,"themes/halloween-nocturne")
private val amber=Color(0xFFFFC17A)
private val pack=ThemeVisuals("halloween-nocturne","Halloween Nocturne",amber,Color(0xFF17120F),Color(0xFF261D17),Color(0xFF6C5038),14,18,26,
    mapOf("header" to File(dir,"artwork/header.webp").path,"player" to File(dir,"artwork/player.webp").path)+
        listOf("nav_home","nav_search","nav_explore","nav_library","play","pause","previous","next","settings").associateWith{File(dir,"icons/$it.png").path}, seekStyle="ember",seekHeight=8)
private val colors=darkColorScheme(primary=amber,onPrimary=Color(0xFF101010),primaryContainer=Color(0xFF54321A),onPrimaryContainer=Color(0xFFFFF4E6),
    background=Color(0xFF0D0B09),surface=pack.surface,surfaceVariant=pack.raised,onSurface=Color(0xFFFFF4E6),onBackground=Color(0xFFFFF4E6),
    onSurfaceVariant=Color(0xFFCFBDAA),outline=pack.outline,outlineVariant=pack.outline)
private val heading=FontFamily(Font(File(dir,"fonts/GrenzeGotisch.ttf"),FontWeight.Bold))
private val body=FontFamily(Font(File(dir,"fonts/Manrope.ttf")))
private var capturedScroll: androidx.compose.foundation.ScrollState? = null
private var transportClicks = 0
private val typography=Typography(
    headlineLarge=TextStyle(fontFamily=heading,fontSize=32.sp,lineHeight=40.sp),
    headlineMedium=TextStyle(fontFamily=heading,fontSize=28.sp,lineHeight=36.sp),
    headlineSmall=TextStyle(fontFamily=heading,fontSize=24.sp,lineHeight=30.sp),
    titleLarge=TextStyle(fontFamily=body,fontWeight=FontWeight.Bold,fontSize=20.sp,lineHeight=28.sp),
    titleMedium=TextStyle(fontFamily=body,fontWeight=FontWeight.SemiBold,fontSize=16.sp,lineHeight=24.sp),
    bodyLarge=TextStyle(fontFamily=body,fontSize=16.sp,lineHeight=24.sp),
    bodyMedium=TextStyle(fontFamily=body,fontSize=14.sp,lineHeight=20.sp),
    bodySmall=TextStyle(fontFamily=body,fontSize=12.sp,lineHeight=18.sp),
    labelLarge=TextStyle(fontFamily=body,fontWeight=FontWeight.Bold,fontSize=14.sp,lineHeight=20.sp),
    labelMedium=TextStyle(fontFamily=body,fontSize=12.sp,lineHeight=16.sp),
)
private fun bitmap(path:String)=Image.makeFromEncoded(File(dir,path).readBytes()).toComposeImageBitmap()
@Composable private fun Nav(selected:Int) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(26.dp)).background(pack.surface).border(1.dp,pack.outline,RoundedCornerShape(26.dp)).padding(6.dp)) {
        listOf("Home","Search","Explore","Library").forEachIndexed { i,name ->
            Column(Modifier.weight(1f).clip(RoundedCornerShape(20.dp)).background(if(i==selected)colors.primaryContainer else Color.Transparent)
                .padding(vertical=10.dp),horizontalAlignment=Alignment.CenterHorizontally) {
                ThemeIcon(listOf(VeyraIcons.Home,VeyraIcons.Search,VeyraIcons.Explore,VeyraIcons.Library)[i],name,Modifier.size(24.dp),
                    tint=if(i==selected)amber else colors.onSurfaceVariant,themeKey=listOf("nav_home","nav_search","nav_explore","nav_library")[i])
                Spacer(Modifier.height(4.dp));Text(name,style=MaterialTheme.typography.labelMedium,color=if(i==selected)amber else colors.onSurfaceVariant)
            }
        }
    }
}
@Composable private fun Header(title:String) {
    if (androidx.compose.ui.platform.LocalDensity.current.fontScale > 1.2f) {
        Column(Modifier.fillMaxWidth()) {
            Text(title,style=MaterialTheme.typography.headlineLarge)
            Text("VEYRA / 01",style=MaterialTheme.typography.labelMedium,color=amber)
        }
    } else Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
        Text(title,Modifier.weight(1f),style=MaterialTheme.typography.headlineLarge)
        Text("VEYRA / 01",style=MaterialTheme.typography.labelMedium,color=amber)
    }
}
@Composable private fun Home() {
    Column(Modifier.fillMaxSize().padding(20.dp),verticalArrangement=Arrangement.spacedBy(18.dp)) {
        Header("Listen now")
        ThemeAtmosphere("header")
        Text("After dark, all yours.",style=MaterialTheme.typography.headlineMedium)
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(12.dp)) {
            listOf("Moonlit sessions","Midnight stories").forEachIndexed { i,name ->
                Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                    Image(bitmap(if(i==0)"artwork/player.webp" else "artwork/header.webp"),null,
                        Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(14.dp)),contentScale=ContentScale.Crop)
                    Text(name,style=MaterialTheme.typography.titleMedium)
                    Text("Find your frequency",style=MaterialTheme.typography.bodySmall,color=colors.onSurfaceVariant)
                }
            }
        }
        Text("Your familiar favorites",style=MaterialTheme.typography.headlineSmall)
        SettingsRow(icon=VeyraIcons.Play,title="The night is young",subtitle="Your music stays yours",onClick={})
        Spacer(Modifier.weight(1f));Nav(0)
    }
}
@Composable private fun Player() {
    Box(Modifier.fillMaxSize()) {
        ThemeBackdrop("player",Modifier.fillMaxSize())
        Column(Modifier.fillMaxSize().padding(24.dp),verticalArrangement=Arrangement.spacedBy(18.dp)) {
            Header("Now playing")
            Image(bitmap("artwork/player.webp"),null,Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(18.dp)),contentScale=ContentScale.Crop)
            Text("A different kind of night",style=MaterialTheme.typography.headlineMedium)
            Text("Halloween Nocturne · Theme preview",style=MaterialTheme.typography.bodyMedium,color=colors.onSurfaceVariant)
            PlayerProgressTrack(fraction={0.34f},height=7.dp,activeColor={Color.White},inactiveColor=Color.Gray)
            PlayerTimelineLabels("1:12","−2:24",amber) { Text("High quality",style=MaterialTheme.typography.labelMedium,color=amber) }
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceAround,verticalAlignment=Alignment.CenterVertically) {
                PlayerTransportControl(rememberVectorPainter(VeyraIcons.Play),themeKey="previous",description="Previous",onClick={},iconSize=30.dp,touchSize=56.dp,reducedMotion=true)
                PlayerTransportControl(rememberVectorPainter(VeyraIcons.Play),themeKey="pause",description="Pause",onClick={transportClicks++},iconSize=56.dp,touchSize=88.dp,emphasized=true,reducedMotion=true)
                PlayerTransportControl(rememberVectorPainter(VeyraIcons.Play),themeKey="next",description="Next",onClick={},iconSize=30.dp,touchSize=56.dp,reducedMotion=true)
            }
            SettingsRow(icon=VeyraIcons.Queue,title="Up next",subtitle="All of your queue actions remain",onClick={})
            Spacer(Modifier.weight(1f))
        }
    }
}
@Composable private fun Settings() {
    val scroll = rememberScrollState()
    SideEffect { capturedScroll = scroll }
    Column(Modifier.fillMaxSize().verticalScroll(scroll).padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        Header("Appearance")
        ThemeAtmosphere("header",compact=true)
        Text("Theme studio",style=MaterialTheme.typography.headlineMedium)
        Text("Halloween Nocturne is active",style=MaterialTheme.typography.bodyMedium,color=amber)
        SettingsRow(icon=SettingsIcons.Appearance,title="Theme studio",subtitle="Fonts, artwork, icons and player styling",onClick={})
        SettingsPresetRow(SettingsIcons.Appearance,"Accent palette","Saved for Original Veyra",listOf("Original Veyra","Amber"),0,{})
        SettingsPresetRow(SettingsIcons.Icons,"Artwork corners","Album covers keep their identity",listOf("Original","Soft","Rounded"),0,{})
        SettingsRow(icon=SettingsIcons.Performance,title="Reduce animation",subtitle="Respect the pace that suits you",checked=true,onClick={})
        SettingsRow(icon=SettingsIcons.Sound,title="Higher-contrast player controls",checked=true,onClick={})
        OutlinedButton(onClick={},modifier=Modifier.fillMaxWidth()) {Text("Return to Original Veyra")}
        Text("Your accounts, music and saved preferences stay unchanged.",style=MaterialTheme.typography.bodySmall,color=colors.onSurfaceVariant)
    }
}
private fun SemanticsNode.flatten():List<SemanticsNode> = listOf(this)+children.flatMap{it.flatten()}
fun main() {
    val output=File(root,"themes/previews").apply{mkdirs()}
    val results=mutableListOf<String>()
    for(kind in listOf("home","player","settings","settings-large-text")) {
        val width=if(kind.endsWith("large-text"))320 else 390
        lateinit var scene: ImageComposeScene
        SwingUtilities.invokeAndWait { scene=ImageComposeScene(width*2,1800,density=Density(2f,if(kind.endsWith("large-text"))1.5f else 1f)) {
            CompositionLocalProvider(LocalThemeVisuals provides pack) {
                MaterialTheme(colorScheme=colors,typography=typography) {
                    Surface(Modifier.fillMaxSize(),color=colors.background) {
                        when(kind){"home"->Home();"player"->Player();else->Settings()}
                    }
                }
            }
        }
        }
        SwingUtilities.invokeAndWait { scene.render(0) }
        repeat(12) { frame ->
            Thread.sleep(30)
            SwingUtilities.invokeAndWait { scene.render((frame+1)*100_000_000L) }
        }
        SwingUtilities.invokeAndWait {
        if (kind.startsWith("settings")) {
            capturedScroll?.let { it.dispatchRawDelta(-it.value.toFloat()) }
            scene.render(1_300_000_000L)
        }
        val nodes=scene.semanticsOwners.flatMap{it.rootSemanticsNode.flatten()}
        check(nodes.isNotEmpty())
        if (kind == "player") {
            val pause = nodes.first { it.config.getOrNull(SemanticsProperties.ContentDescription)?.contains("Pause") == true }
            check(pause.config.getOrNull(SemanticsActions.OnClick)?.action?.invoke() == true)
            check(transportClicks == 1) { "Themed transport callback changed" }
            check(pause.boundsInRoot.width >= 96f && pause.boundsInRoot.height >= 96f) { "Transport hit target shrank" }
        }
        val image=scene.render(1_000_000_000L)
        File(output,"$kind.png").writeBytes(image.encodeToData(EncodedImageFormat.PNG)!!.bytes)
        results+="$kind: native Compose components rendered at $width dp"
        if(kind == "player") results+="Themed transport delivered its native callback once; hit target at least 48 dp."
        scene.close()
        }
    }
    File(output,"RENDER-RESULTS.txt").writeText(results.joinToString("\n")+"\nNative component studies, not full-device screenshots. Album artwork in the sample player is demonstration art.\n")
    println(results.joinToString("\n"))
}
