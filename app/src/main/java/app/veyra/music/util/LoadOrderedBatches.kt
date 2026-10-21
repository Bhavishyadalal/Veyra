package app.veyra.music.util

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/** Bounded independent fetches, stable publication order, and structured cancellation. */
internal suspend fun <T, R> loadOrderedBatches(
    items: List<T>,
    parallelism: Int,
    load: suspend (T) -> R,
    publish: (R) -> Unit,
) = coroutineScope {
    require(parallelism > 0)
    for (batch in items.chunked(parallelism)) {
        val pending = batch.map { item -> async { load(item) } }
        pending.forEach { publish(it.await()) }
    }
}
