package app.veyra.music

import app.veyra.music.ui.screens.HomeCarouselPaging as Paging
import org.junit.Assert.*
import org.junit.Test

class HomeCarouselPagingTest {
    @Test fun switchingToTilesAndBackPreservesEveryBannerAnchor() {
        for (count in 1..200) {
            for (item in 0 until count) {
                val page = Paging.tilePageForItem(item, count)
                val start = Paging.firstItemForTilePage(page, count)
                assertTrue(item in start until minOf(start + Paging.TILES_PER_PAGE, count))
                assertEquals(item, Paging.bannerItemForTilePage(page, item, count))
            }
        }
    }
    @Test fun movingToAnotherTilePageSelectsItsFirstBanner() {
        assertEquals(6, Paging.bannerItemForTilePage(2, 1, 20))
        assertEquals(0, Paging.bannerItemForTilePage(0, 8, 20))
    }
    @Test fun emptyFeedsAreSafe() {
        for (count in listOf(0, -1, Int.MIN_VALUE)) {
            assertEquals(0, Paging.pageCount(count))
            assertEquals(0, Paging.tilePageForItem(50, count))
            assertEquals(0, Paging.firstItemForTilePage(50, count))
            assertEquals(0, Paging.bannerItemForTilePage(50, 50, count))
        }
    }
    @Test fun refreshThatShrinksTheFeedClampsTheSavedAnchor() {
        assertEquals(0, Paging.tilePageForItem(999, 2))
        assertEquals(1, Paging.bannerItemForTilePage(999, 999, 2))
        assertEquals(0, Paging.bannerItemForTilePage(-9, -9, 2))
    }
    @Test fun partialLastPagesKeepAllRemainingItems() {
        assertEquals(3, Paging.pageCount(8))
        assertEquals(6, Paging.firstItemForTilePage(2, 8))
        assertEquals(7, Paging.bannerItemForTilePage(2, 7, 8))
        assertEquals(1, Paging.pageCount(1))
        assertEquals(0, Paging.firstItemForTilePage(100, 1))
    }
    @Test fun extremeIndicesDoNotOverflowOrEscapeTheFeed() {
        val count = Int.MAX_VALUE
        val start = Paging.firstItemForTilePage(Int.MAX_VALUE, count)
        assertTrue(start >= 0 && start < count)
        assertEquals(Paging.pageCount(count) - 1, Paging.tilePageForItem(Int.MAX_VALUE, count))
        assertEquals(0, Paging.tilePageForItem(Int.MIN_VALUE, count))
    }
}
