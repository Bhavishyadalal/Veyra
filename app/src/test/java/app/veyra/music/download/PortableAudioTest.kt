package app.veyra.music.download

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.nio.ByteBuffer

class PortableAudioTest {
    @get:Rule val tmp = TemporaryFolder()
    private fun box(type: String, bytes: ByteArray) =
        ByteBuffer.allocate(bytes.size + 8).putInt(bytes.size + 8).put(type.toByteArray()).put(bytes).array()

    private fun packageFor(codec: String = "fLaC", segment: String = "segment.m4s"): File {
        val entry = box(codec, ByteArray(28))
        var bytes = box("stsd", ByteArray(4) + byteArrayOf(0, 0, 0, 1) + entry)
        listOf("stbl", "minf", "mdia", "trak", "moov").forEach { bytes = box(it, bytes) }
        File(tmp.root, "init.mp4").writeBytes(bytes)
        File(tmp.root, "segment.m4s").writeBytes(byteArrayOf(1))
        return File(tmp.root, "playlist.m3u8").apply {
            writeText("#EXTM3U\n#EXT-X-MAP:URI=\"init.mp4\"\n#EXTINF:1,\n$segment\n#EXT-X-ENDLIST\n")
        }
    }

    @Test fun `recognizes lossless and ordinary audio packages`() {
        assertEquals("flac", PortableAudio.inspect(packageFor()).extension)
        assertEquals("m4a", PortableAudio.inspect(packageFor("alac")).extension)
        assertEquals("m4a", PortableAudio.inspect(packageFor("ec-3")).extension)
    }
    @Test fun `refuses unsupported codecs without exporting a false extension`() {
        assertThrows(IllegalStateException::class.java) { PortableAudio.inspect(packageFor("Opus")) }
    }
    @Test fun `refuses escaped paths and network references`() {
        listOf("../other.m4s", "https://example.org/a.m4s", "/etc/passwd").forEach { segment ->
            assertThrows(IllegalArgumentException::class.java) { PortableAudio.inspect(packageFor(segment = segment)) }
        }
    }
    @Test fun `refuses missing and empty fragments`() {
        assertThrows(IllegalArgumentException::class.java) { PortableAudio.inspect(packageFor(segment = "missing.m4s")) }
        val playlist = packageFor()
        File(tmp.root, "segment.m4s").writeBytes(byteArrayOf())
        assertThrows(IllegalArgumentException::class.java) { PortableAudio.inspect(playlist) }
    }
}
