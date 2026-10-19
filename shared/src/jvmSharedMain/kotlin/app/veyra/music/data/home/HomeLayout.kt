package app.veyra.music.data.home

import app.veyra.music.data.model.HomeShelf
import java.util.Locale

/** Section identity follows the same title deduplication as the provider's Home feed. */
fun homeSectionId(title: String): String = title.trim().lowercase(Locale.ROOT).let { "section:${it.length}:$it" }

data class HomeSectionOption(val id: String, val title: String, val visible: Boolean)

data class HomeLayout(val order: List<String> = emptyList(), val hidden: Set<String> = emptySet()) {
    fun options(shelves: List<HomeShelf>): List<HomeSectionOption> {
        val available = shelves.filter { it.items.isNotEmpty() }.distinctBy { homeSectionId(it.title) }
        val positions = order.withIndex().associate { it.value to it.index }
        return available.sortedBy { positions[homeSectionId(it.title)] ?: Int.MAX_VALUE }
            .map { HomeSectionOption(homeSectionId(it.title), it.title, homeSectionId(it.title) !in hidden) }
    }

    fun apply(shelves: List<HomeShelf>): List<HomeShelf> {
        val byId = shelves.associateBy { homeSectionId(it.title) }
        return options(shelves).filter { it.visible }.mapNotNull { byId[it.id] }
    }

    /** Only reorder available sections; retain preferences for temporarily absent sections. */
    fun move(id: String, offset: Int, shelves: List<HomeShelf>): HomeLayout {
        if (offset != -1 && offset != 1) return this
        val visibleOrder = options(shelves).map { it.id }.toMutableList()
        val from = visibleOrder.indexOf(id)
        if (from < 0 || from + offset !in visibleOrder.indices) return this
        java.util.Collections.swap(visibleOrder, from, from + offset)
        return copy(order = visibleOrder + order.filterNot { it in visibleOrder })
    }

    fun setVisible(id: String, visible: Boolean): HomeLayout =
        copy(hidden = if (visible) hidden - id else hidden + id)
}
