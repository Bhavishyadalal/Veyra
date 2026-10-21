package app.veyra.music.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.foundation.lazy.LazyListState
import app.veyra.music.data.model.CARD_ART_PX
import app.veyra.music.data.model.HEADER_ART_PX
import app.veyra.music.data.model.HomeShelf
import app.veyra.music.data.model.UiState
import app.veyra.music.data.model.artworkAt
import coil3.SingletonImageLoader
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest

/** Warm only the next few Home covers; never download the entire feed. */
@Composable
internal fun PrefetchHomeArtwork(state: UiState<List<HomeShelf>>, listState: LazyListState, enabled: Boolean = true) {
    val context = LocalPlatformContext.current
    val shelves = (state as? UiState.Success)?.data.orEmpty()
    val keys = remember(shelves) { homeShelfKeys(shelves.map { it.title }) }
    val firstShelf by remember(keys, listState) { derivedStateOf {
        listState.layoutInfo.visibleItemsInfo.firstNotNullOfOrNull { item ->
            keys.indexOf(item.key).takeIf { it >= 0 }
        } ?: 0
    } }
    val covers = remember(state, enabled, firstShelf) {
        if (!enabled) emptyList() else (state as? UiState.Success)?.data.orEmpty()
            .drop(firstShelf).take(3).flatMapIndexed { index, shelf ->
                val px = if (firstShelf == 0 && index == 0) HEADER_ART_PX else CARD_ART_PX
                shelf.items.take(4).mapNotNull { item -> item.thumbnailUrl.artworkAt(px)?.let { it to px } }
            }.distinctBy { it.first }
    }
    DisposableEffect(context, covers) {
        val loader = SingletonImageLoader.get(context)
        val requests = covers.map { (url, px) ->
            loader.enqueue(ImageRequest.Builder(context).data(url).size(px).build())
        }
        onDispose { requests.forEach { it.dispose() } }
    }
}
