package app.veyra.music.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
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
internal fun PrefetchHomeArtwork(state: UiState<List<HomeShelf>>, enabled: Boolean = true) {
    val context = LocalPlatformContext.current
    val covers = remember(state, enabled) {
        if (!enabled) emptyList() else (state as? UiState.Success)?.data.orEmpty()
            .take(3).flatMapIndexed { index, shelf ->
                val px = if (index == 0) HEADER_ART_PX else CARD_ART_PX
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
