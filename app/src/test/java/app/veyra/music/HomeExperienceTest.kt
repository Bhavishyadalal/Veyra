package app.veyra.music

import app.veyra.music.data.home.*
import app.veyra.music.data.model.HomeShelf
import app.veyra.music.data.model.ShelfItem
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.util.Locale

class HomeExperienceTest {
    private fun shelf(title: String, count: Int = 2) = HomeShelf(title, List(count) {
        ShelfItem("Song $it", "Artist", "https://example.com/$it.webp", "id$it", null)
    }, "subtitle", "VL123", "params")
    private fun disk(block: (File, HomeDiskStore) -> Unit) {
        val dir = Files.createTempDirectory("veyra-home-").toFile()
        try { block(dir, HomeDiskStore(dir) { 100_000L }) } finally { dir.deleteRecursively() }
    }
    @Test fun snapshotSurvivesNewStoreInstanceWithCompleteCardMetadata() = disk { dir, store ->
        val shelves = listOf(shelf("Quick picks"), shelf("Recents"))
        assertTrue(store.write("a", shelves))
        val result = HomeDiskStore(dir) { 100_000L }.read("a")!!
        assertEquals(shelves, result.shelves)
        assertEquals(100_000L, result.savedAt)
    }
    @Test fun accountChannelAndGuestCachesNeverFallBackToEachOther() = disk { _, store ->
        val a = HomeDiskStore.owner("account:channelA")
        val b = HomeDiskStore.owner("account:channelB")
        val guest = HomeDiskStore.owner(null)
        assertTrue(store.write(a, listOf(shelf("Private A"))))
        assertNull(store.read(b)); assertNull(store.read(guest))
        assertTrue(store.write(guest, listOf(shelf("Guest"))))
        assertEquals("Private A", store.read(a)!!.shelves.single().title)
        assertEquals("Guest", store.read(guest)!!.shelves.single().title)
        assertNotEquals(guest, HomeDiskStore.owner("guest"))
    }
    @Test fun filenamesDoNotContainRawAccountIdentity() = disk { dir, store ->
        store.write("listener:person@example.com:channel", listOf(shelf("Music")))
        val name = dir.listFiles()!!.single().name
        assertTrue(name.matches(Regex("[a-f0-9]{64}\\.feed")))
    }
    @Test fun missingTruncatedAndUnknownSchemaSnapshotsAreIgnored() = disk { dir, store ->
        assertNull(store.read("a"))
        store.write("a", listOf(shelf("A")))
        val file = dir.listFiles()!!.single()
        val valid = file.readBytes()
        file.writeBytes(valid.copyOf(14)); assertNull(store.read("a"))
        val future = valid.copyOf(); future[7] = 42
        file.writeBytes(future); assertNull(store.read("a"))
    }
    @Test fun emptyRefreshDoesNotDestroyUsefulSavedFeed() = disk { _, store ->
        store.write("a", listOf(shelf("Saved")))
        assertFalse(store.write("a", emptyList()))
        assertFalse(store.write("a", listOf(shelf("Empty", 0))))
        assertEquals("Saved", store.read("a")!!.shelves.single().title)
    }
    @Test fun oversizeFieldCannotReplacePreviousAtomicSnapshot() = disk { dir, store ->
        store.write("a", listOf(shelf("Saved")))
        assertFalse(store.write("a", listOf(shelf("x".repeat(5000)))))
        assertEquals("Saved", store.read("a")!!.shelves.single().title)
        assertFalse(dir.listFiles()!!.any { it.extension == "tmp" })
    }
    @Test fun oversizePayloadIsStoppedBeforeReplacingSavedContent() = disk { dir, store ->
        store.write("a", listOf(shelf("Saved")))
        val huge = List(24) { n -> HomeShelf("Section $n", List(20) {
            ShelfItem("t".repeat(4096), "a".repeat(4096), null, "id", null)
        }) }
        assertFalse(store.write("a", huge))
        assertEquals("Saved", store.read("a")!!.shelves.single().title)
        assertFalse(dir.listFiles()!!.any { it.extension == "tmp" })
    }
    @Test fun diskFootprintAndSnapshotLengthAreBounded() = disk { dir, store ->
        repeat(12) { store.write("owner$it", List(40) { n -> shelf("Shelf $n", 50) }) }
        val feeds = dir.listFiles()!!.filter { it.extension == "feed" }
        assertEquals(8, feeds.size)
        for (file in feeds) assertTrue(file.length() <= 1_048_576L)
        val cached = store.read("owner11")!!
        assertEquals(24, cached.shelves.size)
        assertTrue(cached.shelves.all { it.items.size == 20 })
    }
    @Test fun layoutsSurviveRestartAndAreScopedIndependently() = disk { dir, store ->
        val feed = listOf(shelf("A"), shelf("B"), shelf("C"))
        val layout = HomeLayout().move(homeSectionId("C"), -1, feed).setVisible(homeSectionId("A"), false)
        assertTrue(store.writeLayout("a", layout))
        assertEquals(layout, HomeDiskStore(dir).readLayout("a"))
        assertEquals(HomeLayout(), store.readLayout("b"))
        assertEquals(listOf("C", "B"), layout.apply(feed).map { it.title })
    }
    @Test fun hideAllCanBeRecoveredWithDefaultLayoutWithoutLosingContent() {
        val feed = listOf(shelf("A"), shelf("B"))
        val hidden = feed.fold(HomeLayout()) { layout, section -> layout.setVisible(homeSectionId(section.title), false) }
        assertTrue(hidden.apply(feed).isEmpty())
        assertEquals(2, hidden.options(feed).size)
        assertEquals(feed, HomeLayout().apply(feed))
    }
    @Test fun missingSectionsRetainTheirOrderWhenTheyReturn() {
        val feed = listOf(shelf("A"), shelf("B"), shelf("C"))
        val layout = HomeLayout().move(homeSectionId("C"), -1, feed)
        val moved = layout.move(homeSectionId("C"), -1, feed.filterNot { it.title == "B" })
        assertEquals(listOf("C", "A", "B"), moved.apply(feed).map { it.title })
    }
    @Test fun newProviderSectionsAppendWithoutResettingHiddenChoices() {
        val feed = listOf(shelf("A"), shelf("B"))
        val layout = HomeLayout().move(homeSectionId("B"), -1, feed).setVisible(homeSectionId("A"), false)
        assertEquals(listOf("B", "C"), layout.apply(feed + shelf("C")).map { it.title })
    }
    @Test fun movingAtAnEdgeOrUnknownSectionDoesNothing() {
        val feed = listOf(shelf("A"), shelf("B"))
        val layout = HomeLayout()
        assertEquals(layout, layout.move(homeSectionId("A"), -1, feed))
        assertEquals(layout, layout.move("missing", 1, feed))
        assertEquals(layout, layout.move(homeSectionId("A"), 5, feed))
    }
    @Test fun sectionIdentityIsStableUnderTurkishDeviceLocale() {
        val previous = Locale.getDefault()
        try {
            val id = homeSectionId("INDIE")
            Locale.setDefault(Locale.forLanguageTag("tr-TR"))
            assertEquals(id, homeSectionId("INDIE"))
        } finally { Locale.setDefault(previous) }
    }
    @Test fun changingTheDeviceClockDoesNotHideSavedHome() = disk { dir, store ->
        store.write("a", listOf(shelf("Saved")))
        assertEquals("Saved", HomeDiskStore(dir) { 1L }.read("a")!!.shelves.single().title)
    }
    @Test fun invalidLayoutDoesNotMakeHomeUnavailable() = disk { dir, store ->
        store.write("a", listOf(shelf("Saved")))
        store.writeLayout("a", HomeLayout(hidden = setOf(homeSectionId("Saved"))))
        dir.listFiles()!!.first { it.extension == "layout" }.writeBytes(byteArrayOf(1, 2, 3))
        assertEquals(HomeLayout(), store.readLayout("a"))
        assertEquals("Saved", store.read("a")!!.shelves.single().title)
    }
    @Test fun unicodeTitlesAndNullCardFieldsRoundTrip() = disk { _, store ->
        val feed = listOf(HomeShelf("हिन्दी 🎵", listOf(ShelfItem("夜", "", null, null, "MP123"))))
        assertTrue(store.write("a", feed))
        assertEquals(feed, store.read("a")!!.shelves)
    }
}
