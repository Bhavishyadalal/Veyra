package app.veyra.music

import app.veyra.music.ui.searchPageMadeProgress
import app.veyra.music.ui.searchResponseIsCurrent
import app.veyra.music.ui.typeaheadIsCurrent
import org.junit.Assert.*
import org.junit.Test

class SearchPublicationTest {
    @Test fun `repeated continuation without new rows stops automatic paging but terminal and advanced pages are accepted`() {
        assertFalse(searchPageMadeProgress(20, 20, "page-a", "page-a"))
        assertTrue(searchPageMadeProgress(20, 21, "page-a", "page-a"))
        assertTrue(searchPageMadeProgress(20, 20, "page-a", "page-b"))
        assertTrue(searchPageMadeProgress(20, 20, "page-a", null))
    }
    @Test fun `cleared query invalidates first pages and continuations without a replacement call`() {
        assertTrue(searchResponseIsCurrent(4, 4, true))
        assertFalse(searchResponseIsCurrent(4, 5, true))
    }
    @Test fun `same text on another search source cannot accept a YouTube answer`() {
        assertFalse(searchResponseIsCurrent(4, 4, false))
        assertFalse(typeaheadIsCurrent("song", "song", false, false))
    }
    @Test fun `submit during typeahead lookup keeps its late answer hidden`() {
        assertTrue(typeaheadIsCurrent("song", "song", false, true))
        assertFalse(typeaheadIsCurrent("song", "song", true, true))
    }
    @Test fun `edited and blank queries reject obsolete typeahead`() {
        assertFalse(typeaheadIsCurrent("song", "songs", false, true))
        assertFalse(typeaheadIsCurrent("song", "", false, true))
        assertFalse(typeaheadIsCurrent(" ", " ", false, true))
    }
}
