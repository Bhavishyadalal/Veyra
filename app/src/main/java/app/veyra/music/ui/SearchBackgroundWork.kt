package app.veyra.music.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/** Main-thread confined work owned by the visible search, rather than the whole ViewModel. */
internal class SearchBackgroundWork(
    private val scope: CoroutineScope,
    private val onLoadingMore: (Boolean) -> Unit,
) {
    private var continuation: Job? = null
    private var continuationOwner: Any? = null
    private var prefetch: Job? = null

    fun continueSearch(block: suspend () -> Unit) {
        if (continuationOwner != null) return
        val owner = Any()
        continuationOwner = owner
        onLoadingMore(true)
        continuation = scope.launch { block() }.also { job ->
            job.invokeOnCompletion {
                // Also runs if the scope is canceled before the body starts.
                // An old request must not clear its replacement's spinner.
                if (continuationOwner === owner) {
                    continuationOwner = null
                    onLoadingMore(false)
                }
            }
        }
    }

    fun prefetch(block: suspend () -> Unit) {
        prefetch?.cancel()
        prefetch = scope.launch { block() }
    }

    fun cancel() {
        continuationOwner = null
        continuation?.cancel()
        continuation = null
        prefetch?.cancel()
        prefetch = null
        onLoadingMore(false)
    }
}
