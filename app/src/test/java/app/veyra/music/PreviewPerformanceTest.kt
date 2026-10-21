package app.veyra.music

import app.veyra.music.util.ConflatedSnapshotWriter
import app.veyra.music.util.loadOrderedBatches
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class PreviewPerformanceTest {
    @Test fun `independent shelves overlap but publish in provider order`() = runTest {
        val published = mutableListOf<Int>()
        loadOrderedBatches(listOf(1, 2), 2, load = {
            delay(if (it == 1) 8000 else 1000)
            it
        }, publish = published::add)
        assertEquals(listOf(1, 2), published)
        assertEquals(8000L, testScheduler.currentTime)
    }

    @Test fun `larger feeds never exceed the request limit`() = runTest {
        var active = 0
        var peak = 0
        val published = mutableListOf<Int>()
        loadOrderedBatches((1..7).toList(), 2, load = {
            active++
            peak = maxOf(peak, active)
            try { delay(10); it } finally { active-- }
        }, publish = published::add)
        assertEquals(2, peak)
        assertEquals((1..7).toList(), published)
    }

    @Test fun `leaving a feed cancels every in flight request and publishes nothing`() = runTest {
        var cancelled = 0
        val published = mutableListOf<Int>()
        val job = launch {
            loadOrderedBatches(listOf(1, 2, 3), 2, load = {
                try { awaitCancellation() } finally { cancelled++ }
                @Suppress("UNREACHABLE_CODE") it
            }, publish = published::add)
        }
        runCurrent()
        job.cancel()
        advanceUntilIdle()
        assertEquals(2, cancelled)
        assertTrue(published.isEmpty())
    }

    @Test fun `a bulk queue mutation serializes only its latest snapshot`() = runTest {
        val written = mutableListOf<List<Int>>()
        val writer = ConflatedSnapshotWriter<List<Int>>(backgroundScope, { throw AssertionError(it) }) { written.add(it) }
        repeat(500) { writer.submit((0..it).toList()) }
        runCurrent()
        assertEquals(1, written.size)
        assertEquals((0..499).toList(), written.single())
    }

    @Test fun `changes during a write cannot be overtaken by stale recovery state`() = runTest {
        val gate = CompletableDeferred<Unit>()
        val written = mutableListOf<Int>()
        val writer = ConflatedSnapshotWriter<Int>(backgroundScope, { throw AssertionError(it) }) {
            if (it == 1) gate.await()
            written.add(it)
        }
        writer.submit(1)
        runCurrent()
        writer.submit(2)
        writer.submit(3)
        gate.complete(Unit)
        runCurrent()
        assertEquals(listOf(1, 3), written)
    }

    @Test fun `one failed write does not disable later recovery persistence`() = runTest {
        val written = mutableListOf<Int>()
        var failures = 0
        val writer = ConflatedSnapshotWriter<Int>(backgroundScope, { failures++ }) {
            if (it == 1) throw java.io.IOException("storage unavailable")
            written.add(it)
        }
        writer.submit(1)
        runCurrent()
        writer.submit(2)
        runCurrent()
        assertEquals(1, failures)
        assertEquals(listOf(2), written)
    }
}
