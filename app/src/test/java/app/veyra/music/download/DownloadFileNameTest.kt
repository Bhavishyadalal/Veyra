package app.veyra.music.download

import app.veyra.music.data.model.Song
import org.junit.Assert.*
import org.junit.Test

class DownloadFileNameTest {
    private fun song(id: String, title: String = "Song") = Song(id, title, "Artist", null)

    @Test fun `same display names do not conflate recordings`() {
        assertNotEquals(DownloadStore.fileNameFor(song("one"), "flac"), DownloadStore.fileNameFor(song("two"), "flac"))
        assertEquals(DownloadStore.fileNameFor(song("one"), "flac"), DownloadStore.fileNameFor(song("one"), "flac"))
    }
    @Test fun `unicode names stay within filesystem byte limits without splitting emoji`() {
        val name = DownloadStore.fileNameFor(song("one", "🎵".repeat(150)), "flac")
        assertTrue(name.toByteArray().size < 255)
        assertEquals(name, String(name.toByteArray()))
    }
    @Test fun `unsafe display characters never become path separators`() {
        val name = DownloadStore.fileNameFor(song("../unsafe", "../../title:bad\u0000"), "m4a")
        assertFalse(name.contains('/'))
        assertFalse(name.contains(':'))
        assertFalse(name.contains('\u0000'))
    }
}
