package app.veyra.music.download

import kotlinx.coroutines.Job

/** Guard all calls with Downloads' queue lock. Claims outlive cancellation cleanup. */
internal class DownloadWorkQueue<T>(private val idOf: (T) -> String) {
    class Claim<T>(val id: String, val item: T) {
        internal var cancelled = false
        internal var job: Job? = null
    }
    private val pending = linkedMapOf<String, T>()
    private val running = linkedMapOf<String, Claim<T>>()
    val busy: Boolean get() = pending.isNotEmpty() || running.isNotEmpty()

    fun enqueue(item: T): Boolean {
        val id = idOf(item)
        if (id in pending || running[id]?.cancelled == false) return false
        pending[id] = item
        return true
    }

    fun takeNext(): Claim<T>? {
        val next = pending.entries.firstOrNull { it.key !in running } ?: return null
        pending.remove(next.key)
        return Claim(next.key, next.value).also { running[it.id] = it }
    }

    fun rejectPending(id: String): Boolean = pending.remove(id) != null

    fun accepts(claim: Claim<T>): Boolean = running[claim.id] === claim && !claim.cancelled

    fun attach(claim: Claim<T>, job: Job) {
        if (!accepts(claim)) job.cancel() else claim.job = job
    }

    fun cancel(id: String): Job? {
        pending.remove(id)
        return running[id]?.let { it.cancelled = true; it.job }
    }

    fun finish(claim: Claim<T>) {
        if (running[claim.id] === claim) running.remove(claim.id)
    }

    fun stop(): List<String> {
        val interrupted = (pending.keys + running.values.filterNot { it.cancelled }.map { it.id }).distinct()
        running.values.forEach { it.cancelled = true; it.job?.cancel() }
        pending.clear()
        // Retain cancelled claims until their IO/file cleanup actually ends.
        return interrupted
    }
}
