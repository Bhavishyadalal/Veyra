package app.veyra.music.download

import android.content.Context
import android.content.Intent
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.net.Uri
import android.os.Build
import androidx.core.content.ContextCompat
import app.veyra.music.data.model.Song
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.ByteBuffer

/** Sequential streaming export: bounded memory, no network, no deletion of saved audio. */
object DownloadExport {
    data class State(
        val running: Boolean = false,
        val processed: Int = 0,
        val total: Int = 0,
        val copied: Int = 0,
        val existing: Int = 0,
        val failed: Int = 0,
        val title: String = "",
        val message: String = "",
    )
    private val mutableState = MutableStateFlow(State())
    val state = mutableState.asStateFlow()

    @Synchronized fun start(context: Context) {
        if (mutableState.value.running) return
        mutableState.value = State(running = true)
        try {
            ContextCompat.startForegroundService(context, Intent(context, DownloadExportService::class.java))
        } catch (e: Exception) {
            mutableState.value = State(message = e.message ?: "Could not start export")
        }
    }

    suspend fun run(context: Context) = withContext(Dispatchers.IO) {
        try {
            val songs = Downloads.getDownloadedSongs(context)
            mutableState.value = State(running = true, total = songs.size)
            if (songs.isEmpty()) mutableState.value = mutableState.value.copy(message = "No saved downloads to export.")
            for (song in songs) {
                currentCoroutineContext().ensureActive()
                mutableState.value = mutableState.value.copy(title = song.title)
                try {
                    val copied = export(context, song)
                    mutableState.value = mutableState.value.let {
                        it.copy(processed = it.processed + 1, copied = it.copied + if (copied) 1 else 0,
                            existing = it.existing + if (copied) 0 else 1)
                    }
                } catch (e: CancellationException) { throw e
                } catch (e: Exception) {
                    mutableState.value = mutableState.value.let {
                        it.copy(processed = it.processed + 1, failed = it.failed + 1,
                            message = "${song.title}: ${e.message ?: "Could not export"}")
                    }
                }
            }
        } catch (e: CancellationException) {
            mutableState.value = mutableState.value.copy(message = "Export cancelled. Completed files are safe; you can retry.")
            throw e
        } catch (e: Exception) {
            mutableState.value = mutableState.value.copy(message = e.message ?: "Export failed")
        } finally {
            mutableState.value = mutableState.value.copy(title = "")
        }
    }

    internal fun finished() {
        mutableState.value = mutableState.value.copy(running = false, title = "")
    }

    internal fun interrupted() {
        if (mutableState.value.running) mutableState.value = mutableState.value.copy(
            running = false, message = "Export interrupted. You can retry.")
    }

    private suspend fun export(context: Context, song: Song): Boolean {
        val source = Uri.parse(song.localUri ?: error("Missing download"))
        // Existing shared downloads are already usable outside Veyra.
        if (downloadLocation(source.toString(), context.filesDir.path) == DownloadLocation.PUBLIC) return false
        val file = File(source.path ?: error("Invalid download path"))
        val scope = currentCoroutineContext()
        val packagePlan = if (file.extension == "m3u8") PortableAudio.inspect(file) else null
        val originalExtension = file.extension.lowercase(java.util.Locale.ROOT)
        val extension = packagePlan?.extension ?: if (originalExtension == "webm" && Build.VERSION.SDK_INT >= 29) "ogg" else originalExtension
        val mime = when (extension) {
            "flac" -> "audio/flac"
            "m4a", "mp4" -> "audio/mp4"
            "mp3" -> "audio/mpeg"
            "ogg", "opus" -> "audio/ogg"
            "wav" -> "audio/x-wav"
            "aac" -> "audio/aac"
            "webm" -> "audio/webm" // Legacy filesystem path; scoped-storage devices remux above.
            else -> error("Unsupported audio format: $extension")
        }
        val name = DownloadStore.fileNameFor(song, extension)
        if (DownloadStore.existing(context, name, public = true) != null) return false
        var remuxed: File? = null
        var pending: DownloadStore.Pending? = null
        try {
            if (originalExtension == "webm" && Build.VERSION.SDK_INT >= 29) {
                remuxed = File.createTempFile("veyra-export-", ".ogg", context.cacheDir)
                remuxOpus(file, remuxed) { scope.ensureActive() }
            }
            val destination = DownloadStore.begin(context, name, mime, public = true)
            pending = destination
            destination.openStream().use { sink ->
                if (packagePlan != null) PortableAudio.write(context.cacheDir, packagePlan, sink) { scope.ensureActive() }
                else (remuxed ?: file).inputStream().use { input ->
                    val buffer = ByteArray(128 * 1024)
                    var total = 0L
                    while (true) {
                        scope.ensureActive()
                        val count = input.read(buffer)
                        if (count < 0) break
                        sink.write(buffer, 0, count)
                        total += count
                    }
                    check(total > 0) { "Download is empty" }
                }
            }
            if (packagePlan != null) {
                val folder = file.parentFile ?: error("Missing offline folder")
                val words = File(folder, "lyrics.lrc").takeIf { it.length() in 1..(2L * 1024 * 1024) }
                    ?.readText()?.let { LyricsTag.Embeddable(it, it) }
                val cover = listOf("jpg", "png").firstNotNullOfOrNull { ext ->
                    File(folder, "cover.$ext").takeIf { it.length() in 1..(16L * 1024 * 1024) }
                        ?.let { MediaTagger.Artwork(it.readBytes(), if (ext == "jpg") "image/jpeg" else "image/png") }
                }
                MediaTagger.embed(context, destination.tagUri, song, extension, words, cover)
            }
            scope.ensureActive()
            destination.commit()
            pending = null
            return true
        } finally {
            pending?.abort()
            remuxed?.delete()
        }
    }

    @androidx.annotation.RequiresApi(29)
    private fun remuxOpus(source: File, target: File, checkpoint: () -> Unit) {
        val extractor = MediaExtractor()
        try {
            extractor.setDataSource(source.path)
            val track = (0 until extractor.trackCount).firstOrNull {
                extractor.getTrackFormat(it).getString(MediaFormat.KEY_MIME) == "audio/opus"
            } ?: error("This WebM download cannot be exported as Opus")
            extractor.selectTrack(track)
            val muxer = MediaMuxer(target.path, MediaMuxer.OutputFormat.MUXER_OUTPUT_OGG)
            try {
                val outputTrack = muxer.addTrack(extractor.getTrackFormat(track))
                muxer.start()
                val buffer = ByteBuffer.allocate(1024 * 1024)
                val info = MediaCodec.BufferInfo()
                var samples = 0
                while (extractor.sampleTime >= 0) {
                    checkpoint()
                    check(extractor.sampleSize in 1..buffer.capacity().toLong()) { "Invalid Opus sample size" }
                    buffer.clear()
                    val size = extractor.readSampleData(buffer, 0)
                    check(size > 0) { "Could not read Opus sample" }
                    info.set(0, size, extractor.sampleTime, if (extractor.sampleFlags and MediaExtractor.SAMPLE_FLAG_SYNC != 0) MediaCodec.BUFFER_FLAG_KEY_FRAME else 0)
                    muxer.writeSampleData(outputTrack, buffer, info)
                    samples++
                    extractor.advance()
                }
                check(samples > 0) { "Download is empty" }
                muxer.stop()
            } finally { muxer.release() }
        } finally { extractor.release() }
    }
}
