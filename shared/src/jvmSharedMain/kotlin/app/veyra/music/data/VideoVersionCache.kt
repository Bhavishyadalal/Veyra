package app.veyra.music.data

import app.veyra.music.data.model.Song
import java.util.concurrent.TimeUnit

/** A wrapper distinguishes a known miss from an absent entry without putting null in a map. */
internal class VideoVersionCache(
    private val capacity: Int = 128,
    private val missTtlNanos: Long = TimeUnit.MINUTES.toNanos(10),
    private val now: () -> Long = System::nanoTime,
) {
    data class Lookup(val song: Song?, val savedAt: Long)
    private val entries = object : LinkedHashMap<String, Lookup>(16, .75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Lookup>?) = size > capacity
    }

    @Synchronized fun get(id: String): Lookup? {
        val entry = entries[id] ?: return null
        if (entry.song == null && now() - entry.savedAt !in 0 until missTtlNanos) {
            entries.remove(id)
            return null
        }
        return entry
    }

    @Synchronized fun put(id: String, song: Song?) {
        entries[id] = Lookup(song, now())
    }
}
