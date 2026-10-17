package app.veyra.music

import app.veyra.music.ui.screens.SettingsSearch
import org.junit.Assert.*
import org.junit.Test

class SettingsSearchTest {
    @Test fun overviewDoesNotComposeCategories() {
        val search = SettingsSearch<String>("", null)
        assertFalse(search.includes("sound"))
        assertFalse(search.anyMatch)
    }

    @Test fun categoryLimitsControlsWhenNotSearching() {
        val search = SettingsSearch("  ", "sound")
        assertTrue(search.includes("sound"))
        assertFalse(search.includes("appearance"))
    }

    @Test fun searchSpansCategoriesAndMatchesWordsAcrossLabels() {
        val search = SettingsSearch("  AUDIO   wifi ", "appearance")
        assertTrue(search.includes("sound"))
        assertTrue(search.matches("Audio quality", "WiFi"))
        assertTrue(search.anyMatch)
    }

    @Test fun allSearchWordsMustMatch() {
        val search = SettingsSearch<String>("audio banana", null)
        assertFalse(search.matches("Audio quality", "WiFi", null))
        assertFalse(search.anyMatch)
    }

    @Test fun noMatchDoesNotEraseEarlierMatch() {
        val search = SettingsSearch<String>("blur", null)
        assertTrue(search.matches("Reduce dynamic blur"))
        assertFalse(search.matches("Playback"))
        assertTrue(search.anyMatch)
        assertFalse(SettingsSearch<String>("missing", null).anyMatch)
    }
}
