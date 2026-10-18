package app.veyra.music.ui.screens

/** Anchor conversion for switching the same collection between banners and three-tile pages. */
object HomeCarouselPaging {
    const val TILES_PER_PAGE = 3
    fun pageCount(itemCount: Int): Int = if (itemCount <= 0) 0 else (itemCount - 1) / TILES_PER_PAGE + 1
    fun tilePageForItem(index: Int, itemCount: Int): Int =
        if (itemCount <= 0) 0 else index.coerceIn(0, itemCount - 1) / TILES_PER_PAGE
    fun bannerItemForTilePage(page: Int, preferredItem: Int, itemCount: Int): Int {
        if (itemCount <= 0) return 0
        val preferred = preferredItem.coerceIn(0, itemCount - 1)
        return if (tilePageForItem(preferred, itemCount) == page.coerceIn(0, pageCount(itemCount) - 1)) preferred
            else firstItemForTilePage(page, itemCount)
    }
    fun firstItemForTilePage(page: Int, itemCount: Int): Int =
        if (itemCount <= 0) 0 else (page.toLong().coerceIn(0, (pageCount(itemCount) - 1).toLong()) * TILES_PER_PAGE).toInt()
}
