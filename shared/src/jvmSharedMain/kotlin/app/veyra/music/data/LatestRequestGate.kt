package app.veyra.music.data

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

/** Publishes only the latest uncancelled result; invalidation also covers account switches. */
class LatestRequestGate {
    private val lock=Any()
    private var revision=0L

    fun invalidate() { synchronized(lock) { revision++ } }

    suspend fun <T> load(fetch:suspend ()->T,publish:(T)->Unit) {
        val request=synchronized(lock) {++revision}
        val result=fetch()
        val context=currentCoroutineContext()
        synchronized(lock) {
            context.ensureActive()
            if(request==revision)publish(result)
        }
    }
}
