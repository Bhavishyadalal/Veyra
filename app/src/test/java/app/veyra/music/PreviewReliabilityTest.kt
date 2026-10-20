package app.veyra.music

import app.veyra.music.data.home.HomeDiskStore
import app.veyra.music.data.model.HomeShelf
import app.veyra.music.data.model.ShelfItem
import app.veyra.music.data.model.Song
import app.veyra.music.download.DownloadRequest
import app.veyra.music.playback.QueueUndo
import org.junit.Assert.*
import org.junit.Test
import java.nio.file.Files

class PreviewReliabilityTest {
    @Test fun `saved home is account scoped and an empty refresh cannot erase it`() {
        val dir = Files.createTempDirectory("home-test").toFile()
        try {
            val store = HomeDiskStore(dir) { 1234L }
            val shelves = listOf(HomeShelf("For you", listOf(ShelfItem("Song", "Artist", null, "id", null))))
            assertTrue(store.write(HomeDiskStore.owner("one"), shelves))
            assertNull(store.read(HomeDiskStore.owner("two")))
            assertNull(store.read(HomeDiskStore.owner(null)))
            assertFalse(store.write(HomeDiskStore.owner("one"), emptyList()))
            val restored = store.read(HomeDiskStore.owner("one"))!!
            assertEquals(1234L, restored.savedAt)
            assertEquals("id", restored.shelves.single().items.single().videoId)
            dir.listFiles()!!.single().writeText("interrupted write")
            assertNull(store.read(HomeDiskStore.owner("one")))
        } finally { dir.deleteRecursively() }
    }

    @Test fun `download recovery roundtrips request identity and video conversion metadata`() {
        val request = DownloadRequest.of(Song("id", "Song", "Artist", "https://example.org/art",
            durationText="3:42", albumId="album", albumName="Album", isVideo=true, isExplicit=true), "Playlist")
        val restored = DownloadRequest.decode(DownloadRequest.encode(listOf(request))).single()
        assertEquals(request, restored)
        assertTrue(restored.song().isVideo)
        assertEquals("album", restored.song().albumId)
        assertNull(restored.song().localUri)
        assertTrue(DownloadRequest.decode("broken").isEmpty())
        assertEquals(1, DownloadRequest.decode(DownloadRequest.encode(listOf(request, request))).size)
    }

    @Test fun `host reconnection feedback follows membership and local connection`() {
        val host = app.veyra.music.data.listentogether.PartyMember("host", isHost=true, connected=false)
        val live = app.veyra.music.data.listentogether.ListenTogether.State(
            code="ROOM", members=listOf(host), connection=app.veyra.music.data.listentogether.ListenTogether.Connection.LIVE)
        assertTrue(live.hostReconnecting)
        assertFalse(live.copy(members=listOf(host.copy(connected=true))).hostReconnecting)
        assertFalse(live.copy(connection=app.veyra.music.data.listentogether.ListenTogether.Connection.CONNECTING).hostReconnecting)
    }

    @Test fun `undo restores disjoint removals in original order and refuses changed timelines`() {
        val before = listOf("current", "user-1", "context", "user-2", "auto")
        val removed = listOf(1 to before[1], 3 to before[3])
        val remaining = before.filterIndexed { i, _ -> i != 1 && i != 3 }
        val undo = QueueUndo(removed, remaining, "current")
        assertTrue(undo.canRestore(remaining, "current"))
        val restored = remaining.toMutableList()
        undo.removed.forEach { (index, item) -> restored.add(index, item) }
        assertEquals(before, restored)
        assertFalse(undo.canRestore(remaining.reversed(), "current"))
        assertFalse(undo.canRestore(remaining, "context"))
        assertFalse(undo.canRestore(remaining + "new", "current"))
    }
}
