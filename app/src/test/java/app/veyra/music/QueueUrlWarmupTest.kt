package app.veyra.music

import app.veyra.music.playback.warmQueueUrls
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.io.IOException

class QueueUrlWarmupTest {
    @Test fun nextUrlStartsBeforeFartherLookups() = runBlocking {
        val events = mutableListOf<String>()
        warmQueueUrls(listOf("next", "later"), { false },
            { events += "resolve:$it" }, { _, _ -> fail("unexpected failure") },
            { events += "wait:$it" })
        assertEquals(listOf("wait:1500", "resolve:next", "wait:6500", "resolve:later"), events)
    }

    @Test fun duplicateIdsAndPinnedSourcesDoNotResolveAgain() = runBlocking {
        val resolved = mutableListOf<String>()
        warmQueueUrls(listOf("pinned", "next", "next"), { it == "pinned" },
            { resolved += it }, { _, _ -> fail("unexpected failure") }, {})
        assertEquals(listOf("next"), resolved)
    }

    @Test fun failedNextDoesNotPreventLaterWarmup() = runBlocking {
        val resolved = mutableListOf<String>()
        val failed = mutableListOf<String>()
        warmQueueUrls(listOf("bad", "good"), { false },
            { if (it == "bad") throw IOException("offline") else resolved += it },
            { id, _ -> failed += id }, {})
        assertEquals(listOf("bad"), failed)
        assertEquals(listOf("good"), resolved)
    }

    @Test fun cancellationIsNotReportedAsNetworkFailure() = runBlocking {
        val resolved = mutableListOf<String>()
        try {
            warmQueueUrls(listOf("next", "later"), { false },
                { resolved += it; throw CancellationException("queue changed") },
                { _, _ -> fail("cancellation was swallowed") }, {})
            fail("cancellation must propagate")
        } catch (_: CancellationException) {
            assertEquals(listOf("next"), resolved)
        }
    }

    @Test fun queueReplacementCancelsPendingWarmupBeforeAnyRequest() = runBlocking {
        val gate = CompletableDeferred<Unit>()
        var resolved = false
        val job = launch(start = CoroutineStart.UNDISPATCHED) {
            warmQueueUrls(listOf("old"), { false }, { resolved = true },
                { _, _ -> fail("unexpected failure") }, { gate.await() })
        }
        job.cancel()
        job.join()
        assertFalse(resolved)
    }
}
