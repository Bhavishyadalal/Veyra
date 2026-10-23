package app.veyra.music

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import app.veyra.music.data.model.ShelfItem
import app.veyra.music.ui.screens.PersonalizedHomeShortcuts
import app.veyra.music.ui.screens.PersonalizedSpeedDial
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.*
import androidx.lifecycle.compose.LocalLifecycleOwner
import app.veyra.music.data.canvas.CanvasArtwork
import app.veyra.music.data.lyrics.*
import app.veyra.music.data.model.Song
import app.veyra.music.data.settings.*
import app.veyra.music.playback.PlaybackPosition
import app.veyra.music.ui.player.*
import app.veyra.music.ui.themes.*
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Assert.*
import org.junit.Test
import org.jetbrains.skia.EncodedImageFormat
import java.io.File
import java.lang.reflect.Proxy
import javax.swing.SwingUtilities

/** Native offscreen rendering of the production immersive content, not a recreated mock screen. */
@OptIn(ExperimentalComposeUiApi::class)
class ExperienceRenderTest {
    @Test fun personalizedHomeTilesRetainActionsAtNormalAndLargeRtlSizes() {
        org.junit.Assume.assumeTrue(System.getProperty("os.name").contains("Linux") && System.getProperty("os.arch") in listOf("amd64","x86_64"))
        val root=generateSequence(File(System.getProperty("user.dir"))) {it.parentFile}.first {File(it,"themes/matcha-mischief").isDirectory}
        val output=File(root,"themes/previews/experience-0.1.8").apply {mkdirs()}
        val artwork=File(root,"themes/matcha-mischief/artwork/preview.webp").toURI().toString()
        val items=List(9) {ShelfItem(if(it==0)"Blue hour" else "Evening ${it+1}","Veyra",artwork,"fixture-$it",null)}
        for(large in listOf(false,true)) {
            var actions=0
            var scene:ImageComposeScene?=null
            try {
                SwingUtilities.invokeAndWait {
                    scene=ImageComposeScene(390,844,density=Density(1f,if(large)1.8f else 1f)) {
                        CompositionLocalProvider(LocalLayoutDirection provides if(large)LayoutDirection.Rtl else LayoutDirection.Ltr) {
                            MaterialTheme(colorScheme=darkColorScheme()) {
                                androidx.compose.material3.Surface(color=MaterialTheme.colorScheme.background) {
                                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                                    PersonalizedHomeShortcuts(listOf(0,1,2,3),{actions++})
                                    PersonalizedSpeedDial(items,{_,_->actions++},null)
                                }
                                }
                            }
                        }
                    }
                }
                repeat(15) {i->Thread.sleep(20);SwingUtilities.invokeAndWait {scene!!.render(i*50_000_000L).close()}}
                SwingUtilities.invokeAndWait {
                    fun SemanticsNode.all():List<SemanticsNode> = listOf(this)+children.flatMap {it.all()}
                    val nodes=scene!!.semanticsOwners.flatMap {it.rootSemanticsNode.all()}
                    for(label in listOf("Downloads","Blue hour")) {
                        val node=nodes.firstOrNull {it.config.getOrNull(SemanticsProperties.Text)?.any {it.text==label}==true && it.config.getOrNull(SemanticsActions.OnClick)!=null}
                        assertNotNull(label,node)
                        assertTrue(node!!.boundsInRoot.width>0 && node.boundsInRoot.top<844)
                        assertEquals(true,node.config[SemanticsActions.OnClick].action?.invoke())
                    }
                    assertEquals(2,actions)
                    val image=scene!!.render(1_000_000_000L)
                    try {File(output,"home-tiles-${if(large)"large-rtl" else "normal"}.webp").writeBytes(image.encodeToData(EncodedImageFormat.WEBP,85)!!.bytes)} finally {image.close()}
                }
            } finally {SwingUtilities.invokeAndWait {scene?.close()}}
        }
    }

    @Test fun immersiveBackgroundsAndLargeRtlKeepTransportAndExitReachable() {
        org.junit.Assume.assumeTrue(System.getProperty("os.name").contains("Linux") && System.getProperty("os.arch") in listOf("amd64","x86_64"))
        val root=generateSequence(File(System.getProperty("user.dir"))) {it.parentFile}.first {File(it,"themes/matcha-mischief").isDirectory}
        val output=File(root,"themes/previews/immersive-worlds").apply {mkdirs()}
        val artwork=File(root,"themes/matcha-mischief/artwork/preview.webp")
        val decoded=kotlinx.coroutines.runBlocking {
            coil3.SingletonImageLoader.get(coil3.PlatformContext.INSTANCE).execute(coil3.request.ImageRequest.Builder(coil3.PlatformContext.INSTANCE).data(artwork).size(600).build())
        }
        assertTrue("Fixture artwork must decode successfully: $decoded",decoded is coil3.request.SuccessResult)
        val owner=object:LifecycleOwner {override val lifecycle=LifecycleRegistry.createUnsafe(this).apply {currentState=Lifecycle.State.RESUMED}}
        val host=RenderPlayerHost()
        PlayerPlatform.install(host)
        val theme=ThemeVisuals("fixture","Matcha",Color(0xFFD6E9A3),Color(0xFF1C3829),Color(0xFF34513C),Color(0xFF718764),16,18,24,
            mapOf("header" to artwork.path),background=Color(0xFF10241A))
        val lines=listOf(LyricLine(0,"A quiet place to begin"),LyricLine(10000,"Let the evening find you"),LyricLine(30000,"Music makes this space your own"))
        val cases=ImmersiveStyle.entries.map {Triple(it,false,false)} + listOf(
            Triple(ImmersiveStyle.LYRICS_FOCUS,true,false),Triple(ImmersiveStyle.LANDSCAPE_STAGE,false,true))
        for((style,large,wide) in cases) {
            val background=ImmersiveBackground.THEME
            val width=if(wide)900 else if(large)360 else 390
            val height=if(wide)440 else 844
            var actions=0
            var scene:ImageComposeScene?=null
            try {
                SwingUtilities.invokeAndWait {
                    scene=ImageComposeScene(width,height,density=Density(1f,if(large)1.8f else 1f)) {
                        CompositionLocalProvider(LocalLifecycleOwner provides owner,LocalThemeVisuals provides theme,
                            LocalLayoutDirection provides if(large)LayoutDirection.Rtl else LayoutDirection.Ltr) {
                            MaterialTheme(colorScheme=darkColorScheme()) {
                                ImmersivePlayer(song=Song("fixture","Evening listening","Veyra",artwork.toURI().toString()),artwork=artwork.toURI().toString(),canvas=null,isPlaying=false,
                                    position=remember {PlaybackPosition().apply {report(12000)}},durationMs=180000,lines=lines,looking=false,
                                    options=ExperienceOptions(immersive=true,immersiveBackground=background,immersiveStyle=style),onOptions={},onExit={actions++},onPlayPause={actions++},onPrevious={},onNext={actions++},
                                    hasPrevious=true,hasNext=true,locked=false,onBlocked={},onSeek={},onSource={},onTiming={},onSongOptions={},windowed=false)
                            }
                        }
                    }
                }
                repeat(15) {i->Thread.sleep(20);SwingUtilities.invokeAndWait {scene!!.render(i*50_000_000L).close()}}
                SwingUtilities.invokeAndWait {
                    fun SemanticsNode.all():List<SemanticsNode> = listOf(this)+children.flatMap {it.all()}
                    val nodes=scene!!.semanticsOwners.flatMap {it.rootSemanticsNode.all()}
                    for(label in listOf("Return to player","Play","Next song")) {
                        val node=nodes.firstOrNull {it.config.getOrNull(SemanticsProperties.ContentDescription)?.contains(label)==true && it.config.getOrNull(SemanticsActions.OnClick)!=null}
                        assertNotNull("$background: $label is accessible",node)
                        assertTrue("$label remains on screen",node!!.boundsInRoot.top>=0 && node.boundsInRoot.bottom<=height)
                        assertEquals(true,node.config[SemanticsActions.OnClick].action?.invoke())
                    }
                    assertEquals(3,actions)
                    val image=scene!!.render(1_000_000_000L)
                    try {File(output,"immersive-${style.name.lowercase()}${if(large)"-large-rtl" else if(wide)"-wide" else ""}.webp").writeBytes(image.encodeToData(EncodedImageFormat.WEBP,85)!!.bytes)} finally {image.close()}
                }
            } finally {SwingUtilities.invokeAndWait {scene?.close()}}
        }
    }
}

private class RenderPlayerHost:PlayerHost {
    private val flows=mutableMapOf<String,StateFlow<*>>()
    override val settings=Proxy.newProxyInstance(PlayerSettingsSource::class.java.classLoader,arrayOf(PlayerSettingsSource::class.java)) {proxy,method,args->
        when(method.name) {
            "toString"->"RenderSettings"
            "hashCode"->System.identityHashCode(proxy)
            "equals"->proxy===args?.firstOrNull()
            else->if(!method.name.startsWith("get"))null else flows.getOrPut(method.name) {MutableStateFlow(when(method.name) {
                "getExperience"->ExperienceOptions()
                "getAudioQualityWifi","getAudioQualityCellular"->AudioQuality.LOSSLESS
                "getLyricsOffsetMs"->0
                "getLastPlayerScreen"->LastPlayerScreen.MAIN
                "getLyricsSourceOrder"->LyricsSource.offered
                "getSmartAnalysis"->SmartAnalysis()
                "getSmartMixBlend","getSmartTransitionWindow"->null
                "getTranslationLanguage"->""
                "getReduceAnimation","getSyncedLyrics"->true
                else->false
            })}
        }
    } as PlayerSettingsSource
    override fun cachedCanvas(song:Song):CanvasArtwork?=null
    override suspend fun canvasFor(song:Song):CanvasArtwork?=null
    @Composable override fun CanvasVideo(spec:CanvasVideoSpec,modifier:Modifier)=Unit
    @Composable override fun rememberRemoteArtworkUrl(song:Song?)=song?.thumbnailUrl
    override val volume=object:SystemVolume {override val level=MutableStateFlow(.5f);override fun set(level:Float)=Unit;override fun refresh()=Unit}
    @Composable override fun rememberAudioOutputs()=emptyList<AudioOutputDevice>()
    @Composable override fun rememberOutputPicker(onOpen:()->Unit)=onOpen
    override fun selectAudioOutput(id:Int?)=Unit
    override val outputFormat=MutableStateFlow(OutputFormatUi())
    override val party=MutableStateFlow(PartyUi())
    override suspend fun translateLyrics(trackId:String,lines:List<LyricLine>,targetLanguageTag:String)=LyricsTranslationResult.Unavailable
    override suspend fun romanizeLyrics(trackId:String,lines:List<LyricLine>,targetLanguageTag:String)=LyricsRomanizationResult.Unavailable
    override fun showMessage(message:String)=Unit
    @Composable override fun AudioPipelineDialog(hazeState:HazeState,isPlaying:Boolean,onDismiss:()->Unit)=Unit
}
