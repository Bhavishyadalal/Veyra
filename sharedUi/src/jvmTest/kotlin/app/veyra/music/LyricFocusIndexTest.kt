package app.veyra.music

import app.veyra.music.data.lyrics.LyricLine
import app.veyra.music.ui.player.LyricFocusIndex
import app.veyra.music.ui.player.activeLyricRows
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.random.Random

class LyricFocusIndexTest {
    @Test fun indexedFocusPreservesOverlapsGapsEqualTimesAndSeeks() {
        val random = Random(34)
        val lines = List(600) { i ->
            val time = (i / 2) * 400L
            LyricLine(time, "line $i", sungUntilMs = if (i % 7 == 0) null else time + random.nextLong(10, 8000))
        }
        val index = LyricFocusIndex(lines)
        repeat(1000) {
            val now = random.nextLong(-1000, 140000)
            assertEquals("at $now", activeLyricRows(lines, now), index.activeRows(now))
            assertEquals(lines.indexOfLast { it.timeMs <= now }, index.latest(now))
        }
    }
    @Test fun handlesUnorderedProviderDataWithoutChangingFocusSemantics() {
        val lines = listOf(LyricLine(2000, "a", sungUntilMs = 5000), LyricLine(1000, "b", sungUntilMs = 4000))
        val index = LyricFocusIndex(lines)
        listOf(-1L, 0L, 1500L, 3000L, 5000L).forEach { now ->
            assertEquals(activeLyricRows(lines, now), index.activeRows(now))
        }
    }
    @Test fun emptyDocumentAndBeforeFirstLineHaveNoFocus() {
        assertEquals(emptyList<Int>(), LyricFocusIndex(emptyList()).activeRows(0))
        assertEquals(emptyList<Int>(), LyricFocusIndex(listOf(LyricLine(1000, "intro"))).activeRows(0))
    }
}
