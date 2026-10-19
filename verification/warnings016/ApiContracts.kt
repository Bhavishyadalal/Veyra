package verification.warnings016

import android.content.ClipData
import android.content.Context
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.media3.session.MediaSession
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOf

/** Compile-only contracts for replacements used by the app; not an Android runtime test. */
@androidx.annotation.OptIn(markerClass = [UnstableApi::class])
object ApiContracts {
    val clipboardProvider = LocalClipboard

    suspend fun copy(clipboard: Clipboard, text: String) {
        clipboard.setClipEntry(ClipEntry(ClipData.newPlainText("Veyra", text)))
    }

    fun folder() = MediaMetadata.Builder()
        .setIsBrowsable(true)
        .setIsPlayable(false)
        .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_PLAYLISTS)
        .build()

    fun connection(session: MediaSession, controller: MediaSession.ControllerInfo) =
        MediaSession.ConnectionResult.AcceptedResultBuilder(session, controller).build()

    fun sink(context: Context) = DefaultAudioSink.Builder(context)
        .setEnableAudioOutputPlaybackParameters(true)
        .build()

    val resumptionCallback = object : MediaSession.Callback {
        override fun onPlaybackResumption(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            isForPlayback: Boolean,
        ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> =
            Futures.immediateFuture(MediaSession.MediaItemsWithStartPosition(emptyList(), 0, 0L))
    }

    fun debouncedChanges() = run {
        @OptIn(FlowPreview::class)
        val changes = flowOf("configuration").debounce(300)
        changes
    }
}
