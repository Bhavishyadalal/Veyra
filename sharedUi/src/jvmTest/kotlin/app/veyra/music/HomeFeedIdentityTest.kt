package app.veyra.music

import app.veyra.music.ui.screens.homeShelfKeys
import org.junit.Assert.*
import org.junit.Test
import java.util.Locale

class HomeFeedIdentityTest {
    @Test fun repeatedHeadingsHaveDistinctKeys() {
        val keys = homeShelfKeys(listOf("Quick picks", "Quick picks", "QUICK PICKS", ""))
        assertEquals(4, keys.toSet().size)
    }
    @Test fun lateRecentsInsertionPreservesRecommendationKeys() {
        val initial = homeShelfKeys(listOf("Quick picks", "Albums", "Quick picks"))
        assertEquals(initial, homeShelfKeys(listOf("Recents", "Quick picks", "Albums", "Quick picks")).drop(1))
    }
    @Test fun separatorsInsideTitlesCannotCollideWithOccurrenceSuffixes() {
        val keys = homeShelfKeys(listOf("a", "a:0", "a", "", ":0", "home:shelf:a:0"))
        assertEquals(keys.size, keys.toSet().size)
    }
    @Test fun deviceLocaleDoesNotChangeSavedFeedIdentity() {
        val old = Locale.getDefault()
        try {
            Locale.setDefault(Locale.US)
            val before = homeShelfKeys(listOf("INDIE", "INDIE"))
            Locale.setDefault(Locale.forLanguageTag("tr-TR"))
            assertEquals(before, homeShelfKeys(listOf("INDIE", "INDIE")))
        } finally { Locale.setDefault(old) }
    }
}
