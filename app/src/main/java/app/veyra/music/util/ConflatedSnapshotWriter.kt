package app.veyra.music.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException

/** Serial writes with one pending snapshot: bursts cannot build an unbounded disk-work queue. */
internal class ConflatedSnapshotWriter<T>(
    scope: CoroutineScope,
    private val onFailure: (Exception) -> Unit,
    write: suspend (T) -> Unit,
) {
    private val pending = Channel<T>(Channel.CONFLATED)
    init {
        scope.launch {
            for (snapshot in pending) {
                try { write(snapshot) }
                catch (cancelled: CancellationException) { throw cancelled }
                catch (failure: Exception) { onFailure(failure) }
            }
        }
    }

    fun submit(snapshot: T) { check(pending.trySend(snapshot).isSuccess) }
}
