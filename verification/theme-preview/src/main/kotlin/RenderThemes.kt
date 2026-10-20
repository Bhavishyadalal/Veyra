@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
package app.veyra.music.ui.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.automirrored.rounded.*
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
import app.veyra.music.ui.components.VeyraBrandContent
import app.veyra.music.ui.components.LocalVeyraWordmarkFont
import app.veyra.music.ui.screens.HomeArtworkFrame
import app.veyra.music.ui.screens.SettingsRow
import app.veyra.music.ui.screens.SettingsPresetRow
import java.io.File
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import javax.swing.SwingUtilities

private val root=File(System.getProperty("veyra.root"))
private val themeId=System.getProperty("veyra.theme","halloween-nocturne")
private val midnight=themeId=="midnight-express"
private val astral=themeId=="astral-tide"
private val playful=themeId=="playtime-club"
private val web=themeId=="web-slinger"
private val dir=File(root,"themes/$themeId")
private val amber=if(midnight)Color(0xFFFFC778) else if(astral)Color(0xFFA0E8D5) else if(playful)Color(0xFFFFE18A) else if(web)Color(0xFFFF4F66) else Color(0xFFFFC17A)
private val pack=if(midnight)ThemeVisuals("midnight-express","Midnight Express",Color(0xFFFFC778),
    Color(0xFF17212E),Color(0xFF263548),Color(0xFF627C98),12,16,24,
    mapOf("header" to File(dir,"artwork/header.webp").path,"player" to File(dir,"artwork/player.webp").path,"brandIcon" to File(dir,"artwork/brand.webp").path)+
        listOf("nav_home","nav_search","nav_explore","nav_library","play","pause","previous","next","settings","download","queue").associateWith{File(dir,"icons/$it.png").path},
    seekStyle="web",seekHeight=8,background=Color(0xFF0C121A),text=Color(0xFFFFF7EA),muted=Color(0xFFC8D4E2),
    secondary=Color(0xFF9CCBE8),sceneStyle="mist",tagline="Your next stop. Another song.") else if(astral)ThemeVisuals("astral-tide","Astral Tide",Color(0xFFA0E8D5),
    Color(0xFF0D2730),Color(0xFF183A43),Color(0xFF547F84),20,24,30,
    mapOf("header" to File(dir,"artwork/header.webp").path,"player" to File(dir,"artwork/player.webp").path,"brandIcon" to File(dir,"artwork/brand.webp").path)+
        listOf("nav_home","nav_search","nav_explore","nav_library","play","pause","previous","next","settings","download","queue").associateWith{File(dir,"icons/$it.png").path},
    seekStyle="ember",seekHeight=8,background=Color(0xFF06171E),text=Color(0xFFF1FCF9),muted=Color(0xFFB8D4D4),
    secondary=Color(0xFFF2D39C),sceneStyle="mist",tagline="Drift deeper. Hear everything.") else ThemeVisuals(themeId,if(playful)"Playtime Club" else if(web)"Web Slinger" else "Halloween Nocturne",amber,
    if(playful)Color(0xFF112342) else if(web)Color(0xFF101A2C) else Color(0xFF17120F),if(playful)Color(0xFF203453) else if(web)Color(0xFF1B2A42) else Color(0xFF261D17),
    if(playful)Color(0xFF526F99) else if(web)Color(0xFF4D6384) else Color(0xFF6C5038),if(playful)26 else if(web)12 else 14,if(playful)32 else if(web)16 else 18,if(playful)36 else if(web)24 else 26,
    mapOf("header" to File(dir,"artwork/header.webp").path,"player" to File(dir,"artwork/player.webp").path,"brandIcon" to File(dir,"artwork/brand.webp").path)+
        listOf("nav_home","nav_search","nav_explore","nav_library","play","pause","previous","next","settings","download","queue").associateWith{File(dir,"icons/$it.png").path},
    seekStyle=if(playful)"bubbles" else if(web)"web" else "ember",seekHeight=if(playful)10 else if(web)7 else 8,
    background=if(playful)Color(0xFF09152C) else if(web)Color(0xFF070C18) else Color(0xFF0D0B09),
    text=if(playful)Color(0xFFFAFCFF) else if(web)Color(0xFFF5F8FF) else Color(0xFFFFF4E6),muted=if(playful)Color(0xFFC4D5EE) else if(web)Color(0xFFBAC9DF) else Color(0xFFCFBDAA),
    secondary=if(playful)Color(0xFF8DE8E0) else if(web)Color(0xFF82B9FF) else Color(0xFFC6D2AE),sceneStyle=if(playful)"playful" else if(web)"web" else "mist",
    tagline=if(playful)"A little joy. Every play." else if(web)"Your city. Your soundtrack." else "A different kind of night.")
private val colors=darkColorScheme(primary=amber,onPrimary=Color(0xFF101010),primaryContainer=if(midnight)Color(0xFF543920) else if(astral)Color(0xFF184C50) else if(playful)Color(0xFF244B77) else if(web)Color(0xFF67172B) else Color(0xFF54321A),onPrimaryContainer=Color(0xFFFFF4E6),
    background=pack.background,surface=pack.surface,surfaceVariant=pack.raised,onSurface=pack.text,onBackground=pack.text,
    onSurfaceVariant=pack.muted,outline=pack.outline,outlineVariant=pack.outline).let { scheme ->
    if(!astral && !midnight) scheme else scheme.copy(onPrimaryContainer=pack.text,
        secondary=pack.secondary,onSecondary=Color(0xFF101010),secondaryContainer=pack.raised,onSecondaryContainer=pack.text,
        tertiary=pack.accent,onTertiary=Color(0xFF101010),tertiaryContainer=pack.raised,onTertiaryContainer=pack.text,
        surfaceTint=pack.accent,inverseSurface=pack.text,inverseOnSurface=pack.background,inversePrimary=if(midnight)Color(0xFF543920) else Color(0xFF184C50),
        surfaceBright=pack.raised,surfaceDim=pack.background,surfaceContainerLowest=pack.background,
        surfaceContainerLow=pack.surface,surfaceContainer=pack.surface,surfaceContainerHigh=pack.raised,surfaceContainerHighest=pack.raised)
}
private val heading=FontFamily(Font(File(dir,if(midnight)"fonts/Rajdhani.ttf" else if(astral)"fonts/SpaceGrotesk.ttf" else if(playful)"fonts/Fredoka.ttf" else if(web)"fonts/BarlowCondensed.ttf" else "fonts/GrenzeGotisch.ttf"),FontWeight.Bold))
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
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(18.dp)) {
        VeyraBrandContent(rememberVectorPainter(VeyraIcons.MusicNote),wordmarkFont=heading)
        Text("Listen now",style=MaterialTheme.typography.headlineMedium)
        ThemeAtmosphere("header")
        Column { ThemeSectionRule(); Text(if(midnight)"Take the scenic route." else if(astral)"Find your current." else if(playful)"Let the good times play." else if(web)"The city is listening." else "After dark, all yours.",style=MaterialTheme.typography.headlineMedium) }
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(12.dp)) {
            (if(midnight)listOf("Window seat","Last departure") else if(astral)listOf("Moon currents","Deep listening") else if(playful)listOf("Pocket full of joy","Cartoon daydreams") else if(web)listOf("Rooftop rhythms","Night swing") else listOf("Moonlit sessions","Midnight stories")).forEachIndexed { i,name ->
                Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                    HomeArtworkFrame(name,"Find your frequency",compact=true,current=false,currentLabel="Playing",accent=amber,
                        modifier=Modifier.fillMaxWidth().aspectRatio(.78f),
                        artwork={ Image(bitmap(if(i==0)"artwork/player.webp" else "artwork/header.webp"),null,
                            Modifier.fillMaxSize(),contentScale=ContentScale.Crop) },action={})
                }
            }
        }
        Text("Your familiar favorites",style=MaterialTheme.typography.headlineSmall)
        SettingsRow(icon=VeyraIcons.Play,title="The night is young",subtitle="Your music stays yours",onClick={})
        }
        Spacer(Modifier.height(12.dp));Nav(0)
    }
}
@Composable private fun Player() {
    Box(Modifier.fillMaxSize()) {
        ThemeBackdrop("player",Modifier.fillMaxSize())
        Column(Modifier.fillMaxSize().padding(24.dp),verticalArrangement=Arrangement.spacedBy(18.dp)) {
            Header("Now playing")
            Image(bitmap("artwork/player.webp"),null,Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(18.dp)),contentScale=ContentScale.Crop)
            Text(if(midnight)"Between stations" else if(astral)"Below the surface" else if(playful)"A little joy" else if(web)"Midnight skyline" else "A different kind of night",style=MaterialTheme.typography.headlineMedium)
            Text(pack.name+" · Theme preview",style=MaterialTheme.typography.bodyMedium,color=colors.onSurfaceVariant)
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
        Text(pack.name+" is active",style=MaterialTheme.typography.bodyMedium,color=amber)
        SettingsRow(icon=SettingsIcons.Appearance,title="Theme studio",subtitle="Fonts, artwork, icons and player styling",onClick={})
        SettingsPresetRow(SettingsIcons.Appearance,"Accent palette","Saved for Original Veyra",listOf("Original Veyra","Amber"),0,{})
        SettingsPresetRow(SettingsIcons.Icons,"Artwork corners","Album covers keep their identity",listOf("Original","Soft","Rounded"),0,{})
        SettingsRow(icon=SettingsIcons.Performance,title="Reduce animation",subtitle="Respect the pace that suits you",checked=true,onClick={})
        SettingsRow(icon=SettingsIcons.Sound,title="Higher-contrast player controls",checked=true,onClick={})
        OutlinedButton(onClick={},modifier=Modifier.fillMaxWidth()) {Text("Return to Original Veyra")}
        Text("Your accounts, music and saved preferences stay unchanged.",style=MaterialTheme.typography.bodySmall,color=colors.onSurfaceVariant)
    }
}
@Composable private fun Actions() {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal=20.dp),verticalArrangement=Arrangement.spacedBy(4.dp)) {
        Spacer(Modifier.height(12.dp))
        Box(Modifier.align(Alignment.CenterHorizontally).size(34.dp,4.dp).background(amber.copy(alpha=.45f),RoundedCornerShape(4.dp)))
        ThemeSectionRule()
        Row(Modifier.padding(vertical=16.dp),verticalAlignment=Alignment.CenterVertically) {
            Image(bitmap("artwork/player.webp"),null,Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)),contentScale=ContentScale.Crop)
            Column(Modifier.weight(1f).padding(start=14.dp)) {
                Text(if(midnight)"Between stations" else if(astral)"Below the surface" else if(playful)"A little joy" else if(web)"Midnight skyline" else "A different kind of night",style=MaterialTheme.typography.titleMedium)
                Text("Your music · "+pack.name,style=MaterialTheme.typography.bodySmall,color=colors.onSurfaceVariant)
            }
        }
        val glyphs=listOf(Icons.AutoMirrored.Rounded.Undo,Icons.Rounded.Download,Icons.Rounded.Radio,Icons.AutoMirrored.Rounded.PlaylistPlay,Icons.AutoMirrored.Rounded.QueueMusic,Icons.Rounded.Album,Icons.Rounded.Person,Icons.Rounded.Bedtime,Icons.Rounded.Tune,Icons.Rounded.Share,Icons.Rounded.BugReport)
        val labels=listOf("Revert to original","Download","Start radio","Play next","Add to queue","Open album","Open artist","Sleep timer","Lyrics offset","Share","Copy log")
        labels.forEachIndexed { i,label ->
            if(i in listOf(1,5,7)) HorizontalDivider(color=pack.outline.copy(alpha=.4f),modifier=Modifier.padding(vertical=6.dp))
            Row(Modifier.fillMaxWidth().heightIn(min=60.dp).padding(vertical=10.dp),verticalAlignment=Alignment.CenterVertically) {
                ThemeActionEmblem { ThemeIcon(glyphs[i],null,Modifier.size(22.dp),tint=amber) }
                Text(label,Modifier.weight(1f).padding(start=14.dp),style=MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
@Composable private fun Studio() {
    Column(Modifier.fillMaxSize().background(pack.background).padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
            Text("Theme studio",Modifier.weight(1f),style=MaterialTheme.typography.titleLarge)
            TextButton(onClick={}) {Text("Done")}
        }
        androidx.compose.foundation.layout.FlowRow(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            FilterChip(selected=true,onClick={},label={Text("All themes")})
            FilterChip(selected=false,onClick={},label={Text("Downloaded")})
        }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(8.dp)) {
            ThemeStudioRow("Original Veyra","Your saved style",null,false,{})
            if(midnight) ThemeStudioRow("Midnight Express","Currently active",File(root,"themes/midnight-express/artwork/preview.webp"),true,{})
            ThemeStudioRow("Web Slinger",if(web)"Currently active" else "Downloaded",File(root,"themes/web-slinger/artwork/preview.webp"),web,{})
            ThemeStudioRow("Halloween Nocturne",if(web || playful || astral || midnight)"Downloaded" else "Currently active",File(root,"themes/halloween-nocturne/artwork/preview.webp"),!web && !playful && !astral && !midnight,{})
            ThemeStudioRow("Playtime Club",if(playful)"Currently active" else "Available",File(root,"themes/playtime-club/artwork/preview.webp"),playful,{})
            ThemeStudioRow("Astral Tide",if(astral)"Currently active" else "Available",File(root,"themes/astral-tide/artwork/preview.webp"),astral,{})
            SettingsRow(icon=SettingsIcons.Icons,title="App icon",subtitle=pack.name,onClick={})
        }
    }
}
@Composable private fun IconsStudy() {
    Column(Modifier.fillMaxSize().background(pack.background).padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        Text("App icon",style=MaterialTheme.typography.titleLarge)
        androidx.compose.foundation.layout.FlowRow(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            listOf("Veyra","Glass","Themes").forEach {name->FilterChip(selected=name=="Themes",onClick={},label={Text(name)})}
        }
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)) {
            listOf("web-slinger","halloween-nocturne").forEach {id->
                ThemeIconChoice(if(id=="web-slinger")"Web Slinger" else "Halloween Nocturne",id==themeId,true,{},Modifier.weight(1f)) {
                    val image=Image.makeFromEncoded(File(root,"themes/$id/artwork/brand.webp").readBytes()).toComposeImageBitmap()
                    androidx.compose.foundation.Image(image,null,Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)))
                }
            }
        }
        Text("Choose an icon above. Your launcher may take a moment to refresh.",style=MaterialTheme.typography.bodySmall,color=pack.muted)
    }
}
private fun SemanticsNode.flatten():List<SemanticsNode> = listOf(this)+children.flatMap{it.flatten()}
fun main() {
    val performance=System.getProperty("veyra.performance")=="true"
    val reliability=System.getProperty("veyra.reliability")=="true"
    val output=File(root,if(performance)"verification/performance-preview" else if(reliability)"verification/reliability-preview" else if(midnight)"themes/previews/midnight-express" else if(astral)"themes/previews/astral-tide" else if(playful)"themes/previews/playtime-club" else if(web)"themes/previews/web-slinger" else "themes/previews").apply{mkdirs()}
    val study=System.getProperty("veyra.study").orEmpty()
    val results=mutableListOf<String>()
    for(kind in (if(performance)listOf("studio","studio-large-text","studio-rtl-large-text","atmosphere-rtl-large-text") else if(reliability)listOf("apply","apply-large-text","apply-rtl-large-text") else if(astral || midnight)listOf("home","home-large-text","player","studio","studio-large-text","settings") else if(playful) listOf("home","home-large-text","player","studio","studio-large-text","settings","actions") else listOf("home","home-large-text","player","settings","settings-large-text","actions","actions-large-text","studio","studio-large-text","icons","icons-large-text"))) {
        if(study.isNotEmpty() && kind != study) continue
        val width=if(kind.endsWith("large-text"))320 else 390
        var matchingIconCallbacks=0
        lateinit var scene: ImageComposeScene
        SwingUtilities.invokeAndWait { scene=ImageComposeScene(width*2,1800,density=Density(2f,if(kind.endsWith("large-text"))1.5f else 1f)) {
            CompositionLocalProvider(LocalThemeVisuals provides pack,LocalVeyraWordmarkFont provides heading,
                androidx.compose.ui.platform.LocalLayoutDirection provides if(kind.contains("rtl")) LayoutDirection.Rtl else LayoutDirection.Ltr) {
                MaterialTheme(colorScheme=colors,typography=typography) {
                    Surface(Modifier.fillMaxSize(),color=colors.background) {
                        Box(Modifier.fillMaxSize()) {
                            ThemeScene(Modifier.matchParentSize())
                            when { kind.startsWith("atmosphere")->Column(Modifier.fillMaxWidth().padding(20.dp)) { ThemeAtmosphere("header") }; kind.startsWith("apply")->Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(24.dp)) {
                                Text("Apply "+pack.name, style=MaterialTheme.typography.titleLarge)
                                Spacer(Modifier.height(16.dp))
                                ThemeApplyOptions("Your music keeps playing. Original Veyra settings stay saved.",
                                    "Also refresh the interface",false,{},"Also use the matching launcher icon",false,{matchingIconCallbacks++})
                                Spacer(Modifier.height(16.dp))
                                Button(onClick={},modifier=Modifier.fillMaxWidth()) {Text("Apply theme")}
                            };kind.startsWith("home")->Home();kind=="player"->Player();kind.startsWith("actions")->Actions();kind.startsWith("studio")->Studio();kind.startsWith("icons")->IconsStudy();else->Settings() }
                        }
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
        if (kind.startsWith("apply")) {
            val options = nodes.filter { it.config.getOrNull(SemanticsProperties.Role) == Role.Checkbox }
            check(options.size == 2) { "Expected one accessible checkbox per option" }
            options.forEach { check(it.boundsInRoot.height >= 96f) { "Apply option target below 48 dp" } }
            check(options.first().config.getOrNull(SemanticsActions.OnClick)?.action?.invoke() == true)
            check(matchingIconCallbacks == 1) { "Matching icon callback was not delivered exactly once" }
            results+="$kind: two checkbox targets >=48 dp; matching-icon callback delivered once."
        }
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
