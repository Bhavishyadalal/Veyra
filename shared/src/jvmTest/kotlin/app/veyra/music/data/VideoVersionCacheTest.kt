package app.veyra.music.data

import app.veyra.music.data.model.Song
import kotlin.test.*

class VideoVersionCacheTest {
    @Test fun noVideoIsACacheableMissThatExpires() {
        var time = 10L
        val cache = VideoVersionCache(missTtlNanos = 100L, now = { time })
        assertNull(cache.get("track"))
        cache.put("track", null) // ConcurrentHashMap's nullable value previously threw here.
        assertNotNull(cache.get("track"))
        assertNull(cache.get("track")!!.song)
        time += 100L
        assertNull(cache.get("track"))
    }

    @Test fun boundedCacheEvictsLeastRecentlyUsedAndRetainsSuccessfulMatches() {
        var time = 10L
        val cache = VideoVersionCache(capacity = 2, missTtlNanos = 100L, now = { time })
        val song = Song("video", "Song", "Artist", null)
        cache.put("first", song)
        cache.put("second", null)
        assertEquals(song, cache.get("first")!!.song)
        cache.put("third", null)
        assertNull(cache.get("second"))
        time += 1_000L
        assertEquals(song, cache.get("first")!!.song)
        assertNull(cache.get("third"))
    }
}
