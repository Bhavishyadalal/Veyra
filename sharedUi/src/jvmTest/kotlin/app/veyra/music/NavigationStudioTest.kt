package app.veyra.music

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.*
import app.veyra.music.data.settings.*
import app.veyra.music.ui.themes.*
import org.jetbrains.skia.EncodedImageFormat
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import javax.swing.SwingUtilities

@OptIn(ExperimentalComposeUiApi::class)
class NavigationStudioTest {
    @Test fun persistedChoicesRoundTripAndOlderOrFutureSettingsKeepSafeDefaults() {
        val legacy=ExperienceOptions.decode("{\"navigationDesign\":\"DOCKED\",\"navigation\":[0,3,1],\"startupTab\":3}")
        assertEquals(ScrollPlayerMotion.FLOW,legacy.scrollPlayerMotion)
        assertEquals(NavigationDesign.DOCKED,legacy.navigationDesign)
        for(design in NavigationDesign.entries)for(motion in ScrollPlayerMotion.entries) {
            val value=legacy.copy(navigationDesign=design,scrollPlayerMotion=motion,appMotionStyle=AppMotionStyle.SHUTTER,motionTempo=MotionTempo.QUICK)
            assertEquals(value,ExperienceOptions.decode(value.encode()))
        }
        val future=ExperienceOptions.decode("{\"navigationDesign\":\"FUTURE\",\"scrollPlayerMotion\":\"FUTURE\"}")
        assertEquals(AppMotionStyle.DEFAULT,ExperienceOptions.decode("{\"appMotionStyle\":\"FUTURE\",\"motionTempo\":\"FUTURE\"}").appMotionStyle)
        assertEquals(MotionTempo.BALANCED,ExperienceOptions.decode(null).motionTempo)
        assertEquals(NavigationDesign.FLOATING,future.navigationDesign)
        assertEquals(ScrollPlayerMotion.FLOW,future.scrollPlayerMotion)
    }

    @Test fun stylePreviewsRetainSelectionAndActionsWithThemeAssetsLargeTextAndRtl() {
        val root=generateSequence(File(System.getProperty("user.dir"))) {it.parentFile}.first {File(it,"themes").isDirectory}
        val output=File(root,"themes/previews/navigation-studio").apply {mkdirs()}
        val theme=ThemeVisuals("fixture","Matcha",Color(0xFFD6E9A3),Color(0xFF1C3829),Color(0xFF34513C),Color(0xFF718764),16,18,24,emptyMap())
        val overlay=AssetVisuals("fixture","Chrome",emptyMap(),"punk_chrome","plasma",8,20,24,32,"#CAF7E8","punk_chrome")
        for(case in 0..2) {
            val large=case==2
            var scene:ImageComposeScene?=null
            val choices=mutableSetOf<NavigationDesign>()
            try {
                SwingUtilities.invokeAndWait {
                    scene=ImageComposeScene(390,844,density=Density(1f,if(large)1.8f else 1f)) {
                        CompositionLocalProvider(LocalThemeVisuals provides when(case){0->null;1->theme;else->overlay.over(theme,AssetOptions())},
                            LocalLayoutDirection provides if(large)LayoutDirection.Rtl else LayoutDirection.Ltr) {
                            MaterialTheme(colorScheme=darkColorScheme(primary=theme.accent)) {
                                Surface {
                                    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
                                        Text("Navigation studio",style=MaterialTheme.typography.headlineSmall)
                                        NavigationDesign.entries.chunked(if(large)1 else 2).forEach {row->
                                            Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                                                row.forEach {design->NavigationStylePreview(design,design.name.lowercase().replaceFirstChar {it.uppercase()},design==NavigationDesign.ORBIT,{choices+=design},Modifier.weight(1f))}
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    val frame=scene!!.render(1_000_000_000L)
                    try {File(output,"styles-$case.webp").writeBytes(frame.encodeToData(EncodedImageFormat.WEBP,90)!!.bytes)} finally {frame.close()}
                    fun SemanticsNode.all():List<SemanticsNode> = listOf(this)+children.flatMap {it.all()}
                    val nodes=scene!!.semanticsOwners.flatMap {it.rootSemanticsNode.all()}.filter {it.config.getOrNull(SemanticsProperties.Role)==Role.RadioButton}
                    assertEquals(NavigationDesign.entries.size,nodes.size)
                    assertEquals(1,nodes.count {it.config.getOrNull(SemanticsProperties.Selected)==true})
                    nodes.forEach {assertTrue(it.config[SemanticsActions.OnClick].action!!.invoke())}
                    assertEquals(NavigationDesign.entries.toSet(),choices)
                }
            } finally {SwingUtilities.invokeAndWait {scene?.close()}}
        }
    }
}
