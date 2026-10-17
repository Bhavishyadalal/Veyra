package app.veyra.music.playback

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

/** Warm metadata early; leave later lookups and audio bytes outside the startup burst. */
internal suspend fun warmQueueUrls(
    ids: List<String>,
    skip: (String) -> Boolean,
    resolve: suspend (String) -> Unit,
    onFailure: (String, Exception) -> Unit,
    wait: suspend (Long) -> Unit = { delay(it) },
) {
    ids.distinct().forEachIndexed { index, id ->
        wait(if (index == 0) 1_500L else if (index == 1) 6_500L else 500L)
        if (!skip(id)) {
            try {
                resolve(id)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (failure: Exception) {
                onFailure(id, failure)
            }
        }
    }
}
