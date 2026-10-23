package app.veyra.music.data.home

import app.veyra.music.data.model.HomeShelf

/** An offline/empty success must not block the next useful first-page or supplement result. */
fun hasUsableHomeContent(shelves:List<HomeShelf>):Boolean = shelves.any { shelf -> shelf.items.any {it.title.isNotBlank()} }

/** Upstream placeholder-only rows cannot become permanently blank cards or empty carousels. */
fun usableHomeShelves(shelves:List<HomeShelf>):List<HomeShelf> = shelves.map { shelf ->
    shelf.copy(items=shelf.items.filter {it.title.isNotBlank()})
}.filter {it.items.isNotEmpty()}
