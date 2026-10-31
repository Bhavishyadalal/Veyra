package app.veyra.music.download

import java.io.File
import java.io.OutputStream

/** Offline packages are local, bounded inputs; never follow playlist URLs or escapes. */
internal object PortableAudio {
    data class Package(val extension: String, val init: File, val media: List<File>)

    fun inspect(playlist: File): Package {
        require(playlist.length() in 1..(2L * 1024 * 1024)) { "Invalid offline playlist size" }
        val (initName, names) = ManifestFile.hlsSegments(playlist.readText())
        val root = playlist.canonicalFile.parentFile ?: error("Missing offline folder")
        fun local(name: String): File {
            require(!name.contains(':') && !File(name).isAbsolute) { "Non-local offline fragment" }
            return File(root, name).canonicalFile.also {
                require(it.parentFile == root && it.isFile && it.length() in 1..MAX_SEGMENT) {
                    "Missing or invalid offline fragment"
                }
            }
        }
        val init = local(initName)
        val bytes = init.readBytes()
        val stsd = Mp4Boxes.find(bytes, "moov", "trak", "mdia", "minf", "stbl", "stsd")
            ?: error("Unsupported offline audio container")
        val codec = Mp4Boxes.boxes(bytes, stsd.content + 8, stsd.end).firstOrNull()?.type
        val extension = when (codec) {
            "fLaC" -> "flac"
            "mp4a", "alac", "ec-3", "ac-3" -> "m4a"
            else -> error("Unsupported offline codec: $codec")
        }
        return Package(extension, init, names.map(::local))
    }

    fun write(cache: File, plan: Package, sink: OutputStream, checkpoint: () -> Unit) {
        val cancellableSink = object : java.io.FilterOutputStream(sink) {
            override fun write(bytes: ByteArray, offset: Int, length: Int) {
                checkpoint()
                out.write(bytes, offset, length)
            }
            override fun write(value: Int) {
                checkpoint()
                out.write(value)
            }
        }
        ManifestFile.assemble(cache, plan.extension, plan.init.readBytes(), plan.media.asSequence().map {
            checkpoint()
            require(it.length() in 1..MAX_SEGMENT) { "Invalid offline fragment size" }
            it.readBytes()
        }, cancellableSink)
    }

    private const val MAX_SEGMENT = 32L * 1024 * 1024
}
