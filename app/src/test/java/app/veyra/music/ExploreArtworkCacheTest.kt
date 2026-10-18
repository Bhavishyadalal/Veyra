package app.veyra.music

import app.veyra.music.data.artwork.ExploreArtworkCache
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ExploreArtworkCacheTest {
    @get:Rule val temporary = TemporaryFolder()
    private val url = "https://lh3.googleusercontent.com/album=w400"
    private val key = ExploreArtworkCache.key("mood", "params", "IN", "en")

    @Test fun survivesNewCacheInstanceWithoutAnotherLookup() = runBlocking {
        val directory = temporary.newFolder()
        val first = ExploreArtworkCache(directory) { 1_000L }
        assertEquals(url, first.resolve(key) { url })
        val reopened = ExploreArtworkCache(directory) { 2_000L }
        assertEquals(url, reopened.cached(key))
        assertEquals(url, reopened.resolve(key) { error("fresh cache must not use network") })
    }

    @Test fun expiredArtworkRefreshes() = runBlocking {
        val directory = temporary.newFolder()
        ExploreArtworkCache(directory) { 0L }.resolve(key) { url }
        val next = "$url-new"
        val reopened = ExploreArtworkCache(directory) { 8L * 24 * 60 * 60 * 1000 }
        assertEquals(next, reopened.resolve(key) { next })
        assertEquals(next, reopened.cached(key))
    }

    @Test fun staleArtworkRemainsAvailableOffline() = runBlocking {
        val directory = temporary.newFolder()
        ExploreArtworkCache(directory) { 0L }.resolve(key) { url }
        val stale = ExploreArtworkCache(directory) { 8L * 24 * 60 * 60 * 1000 }
        assertEquals(url, stale.resolve(key) { throw IOException("offline") })
        assertEquals(url, stale.cached(key))
    }

    @Test fun explicitRefreshBypassesFreshEntry() = runBlocking {
        val cache = ExploreArtworkCache(temporary.newFolder()) { 1_000L }
        cache.resolve(key) { url }
        val newer = "$url-new"
        assertEquals(newer, cache.resolve(key, forceRefresh = true) { newer })
    }

    @Test fun negativeAnswerIsCachedBrieflyThenRetried() = runBlocking {
        val directory = temporary.newFolder()
        var calls = 0
        val cache = ExploreArtworkCache(directory) { 0L }
        assertNull(cache.resolve(key) { calls++; null })
        assertNull(cache.resolve(key) { calls++; url })
        assertEquals(1, calls)
        val later = ExploreArtworkCache(directory) { 7L * 60 * 60 * 1000 }
        assertEquals(url, later.resolve(key) { calls++; url })
        assertEquals(2, calls)
    }

    @Test fun cancellationIsPropagatedAndDoesNotCacheFailure() = runBlocking {
        val cache = ExploreArtworkCache(temporary.newFolder())
        try {
            cache.resolve(key) { throw CancellationException("screen replaced") }
            fail("cancellation must propagate")
        } catch (_: CancellationException) { }
        assertNull(cache.cached(key))
        assertEquals(url, cache.resolve(key) { url })
    }

    @Test fun simultaneousSameKeyLookupsShareThePersistedAnswer() = runBlocking {
        val cache = ExploreArtworkCache(temporary.newFolder())
        val started = CompletableDeferred<Unit>()
        val release = CompletableDeferred<Unit>()
        var calls = 0
        val one = async { cache.resolve(key) { calls++; started.complete(Unit); release.await(); url } }
        started.await()
        val two = async { cache.resolve(key) { calls++; url } }
        release.complete(Unit)
        assertEquals(url, one.await())
        assertEquals(url, two.await())
        assertEquals(1, calls)
    }

    @Test fun corruptCacheIsReplacedAndTemporaryFilesAreRemoved() = runBlocking {
        val directory = temporary.newFolder()
        val cache = ExploreArtworkCache(directory)
        cache.resolve(key) { url }
        directory.listFiles()!!.single().writeText("not=a valid cache entry")
        assertNull(cache.cached(key))
        assertEquals(url, cache.resolve(key) { url })
        assertTrue(directory.listFiles()!!.all { it.extension == "properties" })
    }

    @Test fun paramsRegionLanguageAndNullAreDistinctKeys() {
        val keys = listOf(
            ExploreArtworkCache.key("a", "b:c", "IN", "en"),
            ExploreArtworkCache.key("a:b", "c", "IN", "en"),
            ExploreArtworkCache.key("a", null, "IN", "en"),
            ExploreArtworkCache.key("a", "", "IN", "en"),
            ExploreArtworkCache.key("a", "", "US", "en"),
            ExploreArtworkCache.key("a", "", "US", "hi"),
        )
        assertEquals(keys.size, keys.toSet().size)
    }

    @Test fun invalidNewUrlCannotReplaceGoodArtwork() = runBlocking {
        val cache = ExploreArtworkCache(temporary.newFolder())
        cache.resolve(key) { url }
        for (invalid in listOf("file:///private", "javascript:alert(1)", "https://user:password@example.com/a")) {
            assertEquals(url, cache.resolve(key, forceRefresh = true) { invalid })
        }
        assertEquals(url, cache.cached(key))
    }

    @Test fun emptyRefreshKeepsTheLastGoodCover() = runBlocking {
        val cache = ExploreArtworkCache(temporary.newFolder())
        cache.resolve(key) { url }
        assertEquals(url, cache.resolve(key, forceRefresh = true) { null })
        assertEquals(url, cache.cached(key))
    }

    @Test fun storageFailureDoesNotPreventNetworkArtwork() = runBlocking {
        val file = temporary.newFile()
        val cache = ExploreArtworkCache(file)
        assertEquals(url, cache.resolve(key) { url })
    }
    @Test fun cacheStaysBoundedAndKeepsTheNewestWrite() = runBlocking {
        val directory = temporary.newFolder()
        val cache = ExploreArtworkCache(directory)
        repeat(260) { cache.resolve("category-$it") { "$url-$it" } }
        assertTrue(directory.listFiles()!!.size <= 256)
        assertEquals("$url-259", cache.cached("category-259"))
    }

}
