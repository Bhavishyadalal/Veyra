package app.veyra.music

import androidx.compose.runtime.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner
import app.veyra.music.data.canvas.CanvasArtwork
import app.veyra.music.data.model.Song
import app.veyra.music.playback.PlaybackPosition
import app.veyra.music.ui.player.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import org.junit.Assert.*
import org.junit.Test
import javax.swing.SwingUtilities

/** Actual remembered player state, including effects and cancellation, on the UI dispatcher. */
@OptIn(ExperimentalComposeUiApi::class)
class PlayerStateLifecycleTest {
    @Test fun closingAScrollingPanelReleasesSuppressionThroughTheLatestCallback() {
        var visible by mutableStateOf(true)
        var tag by mutableStateOf("old")
        val events = mutableListOf<Pair<String, Boolean>>()
        var scope: CoroutineScope? = null
        var list: androidx.compose.foundation.lazy.LazyListState? = null
        var scrolling: Job? = null
        scene({
            scope = rememberCoroutineScope()
            val state = rememberLazyListState()
            list = state
            LazyColumn(state = state) { items(30) { Text("Row $it") } }
            if (visible) {
                val callbackTag = tag
                ReportPlayerScrolling(state) { events += callbackTag to it }
            }
        }) { frame ->
            SwingUtilities.invokeAndWait {
                scrolling = scope!!.launch { list!!.scroll { awaitCancellation() } }
                frame(); frame()
            }
            settle(frame)
            SwingUtilities.invokeAndWait {
                assertTrue(events.any { it.second })
                tag = "new"; frame(); frame()
                visible = false; frame(); frame()
            }
            settle(frame)
            SwingUtilities.invokeAndWait {
                assertEquals("closing must release the latest parent callback", "new" to false, events.last())
                scrolling?.cancel()
            }
        }
    }

    @Test fun releasingVolumeReconcilesToTheQuantizedSystemLevel() {
        val base = RenderPlayerHost()
        val system = object : SystemVolume {
            override val level = MutableStateFlow(.5f)
            override fun set(level: Float) { this.level.value = (level * 4).toInt() / 4f }
            override fun refresh() = Unit
        }
        PlayerPlatform.install(object : PlayerHost by base { override val volume = system })
        var volume: PlayerVolume? = null
        scene({ volume = rememberPlayerVolume() }) { frame ->
            SwingUtilities.invokeAndWait { volume!!.drag(.63f); frame() }
            settle(frame)
            SwingUtilities.invokeAndWait {
                assertEquals(.63f, volume!!.level.value, .001f)
                volume!!.release(); frame()
            }
            settle(frame)
            SwingUtilities.invokeAndWait {
                assertFalse(volume!!.dragging)
                assertEquals("release must follow the real device step", .5f, volume!!.level.value, .001f)
                system.level.value = .75f; frame()
            }
            settle(frame)
            SwingUtilities.invokeAndWait { assertEquals(.75f, volume!!.level.value, .001f) }
        }
    }

    @Test fun newTrackNeverInheritsThePreviousTracksPendingSeek() {
        PlayerPlatform.install(RenderPlayerHost())
        var track by mutableStateOf("first")
        val position = PlaybackPosition().apply { report(12_000) }
        var scrub: PlayerScrub? = null
        val secondTrackFirstValues = mutableListOf<Float?>()
        scene({
            val state = rememberPlayerScrub(track, position, 180_000)
            scrub = state
            if (track == "second") secondTrackFirstValues += state.pendingSeek
        }) { frame ->
            SwingUtilities.invokeAndWait {
                scrub!!.drag(.8f); scrub!!.release {}; frame()
                assertEquals(.8f, scrub!!.pendingSeek!!, .001f)
                track = "second"; frame(); frame()
                assertTrue(secondTrackFirstValues.isNotEmpty())
                assertNull("even the first composition must use fresh track state", secondTrackFirstValues.first())
            }
        }
    }

    @Test fun seekSettlementFollowsAReplacementPlaybackPosition() {
        PlayerPlatform.install(RenderPlayerHost())
        var position by mutableStateOf(PlaybackPosition().apply { report(12_000) })
        var scrub: PlayerScrub? = null
        scene({ scrub = rememberPlayerScrub("same-track", position, 180_000) }) { frame ->
            SwingUtilities.invokeAndWait {
                scrub!!.drag(.8f); scrub!!.release {}; frame(); frame()
                assertNotNull(scrub!!.pendingSeek)
                position = PlaybackPosition().apply { report(144_000) }
                frame(); frame()
            }
            settle(frame)
            SwingUtilities.invokeAndWait { assertNull("seek must observe the new position source", scrub!!.pendingSeek) }
        }
    }

    @Test fun cancelledCanvasLookupCannotReenableDisabledArtwork() {
        val base = RenderPlayerHost()
        val enabled = MutableStateFlow(true)
        val answer = CompletableDeferred<CanvasArtwork>()
        var started = false
        val settings = object : PlayerSettingsSource by base.settings { override val animatedCanvas = enabled }
        PlayerPlatform.install(object : PlayerHost by base {
            override val settings = settings
            override suspend fun canvasFor(song: Song): CanvasArtwork = withContext(NonCancellable) {
                started = true
                answer.await()
            }
        })
        var canvas: CanvasArtwork? = null
        scene({ canvas = rememberCanvasArtwork(Song("canvas", "Song", "Artist", null, albumName = "Album")) }) { frame ->
            settle(frame)
            SwingUtilities.invokeAndWait {
                assertTrue("fixture must reach the real lookup", started)
                enabled.value = false; frame(); frame()
            }
            settle(frame)
            SwingUtilities.invokeAndWait {
                assertNull(canvas)
                answer.complete(CanvasArtwork("https://example.test/late.mp4"))
            }
            settle(frame)
            SwingUtilities.invokeAndWait { assertNull("late result must stay disabled", canvas) }
        }
    }

    private fun settle(frame: () -> Unit) {
        repeat(4) { Thread.sleep(20); SwingUtilities.invokeAndWait { frame() } }
    }

    private fun scene(content: @Composable () -> Unit, check: (() -> Unit) -> Unit) {
        val owner = object : LifecycleOwner {
            override val lifecycle = LifecycleRegistry.createUnsafe(this).apply { currentState = Lifecycle.State.RESUMED }
        }
        var scene: ImageComposeScene? = null
        var time = System.nanoTime()
        val frame: () -> Unit = { scene!!.render(time).close(); time += 40_000_000 }
        try {
            SwingUtilities.invokeAndWait {
                scene = ImageComposeScene(390, 100) {
                    CompositionLocalProvider(LocalLifecycleOwner provides owner) { content() }
                }
                frame(); frame()
            }
            check(frame)
        } finally { SwingUtilities.invokeAndWait { scene?.close() } }
    }
}
