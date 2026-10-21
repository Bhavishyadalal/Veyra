package app.veyra.music.download

import kotlinx.coroutines.Job
import org.junit.Assert.*
import org.junit.Test

class DownloadWorkQueueTest {
    @Test fun `cancel then retry waits for old cleanup without blocking other tracks`() {
        val queue = DownloadWorkQueue<String> { it }
        assertTrue(queue.enqueue("a"))
        val old = queue.takeNext()!!
        val job = Job()
        queue.attach(old, job)
        assertSame(job, queue.cancel("a"))
        job.cancel()
        assertFalse(queue.accepts(old))
        assertTrue(queue.enqueue("a"))
        assertTrue(queue.enqueue("b"))
        val other = queue.takeNext()!!
        assertEquals("b", other.id)
        assertNull(queue.takeNext())
        queue.finish(old)
        val retry = queue.takeNext()!!
        assertEquals("a", retry.id)
        assertTrue(queue.accepts(retry))
        // A stale worker's finalizer cannot release its replacement's slot.
        queue.finish(old)
        assertFalse(queue.enqueue("a"))
        queue.finish(retry)
        queue.finish(other)
        assertFalse(queue.busy)
    }

    @Test fun `cancel before job attachment prevents that job starting`() {
        val queue = DownloadWorkQueue<String> { it }
        queue.enqueue("a")
        val claim = queue.takeNext()!!
        assertNull(queue.cancel("a"))
        val job = Job()
        queue.attach(claim, job)
        assertTrue(job.isCancelled)
        assertTrue(queue.busy)
        queue.finish(claim)
        assertFalse(queue.busy)
    }

    @Test fun `duplicates stay single and queued cancellation leaves no work`() {
        val queue = DownloadWorkQueue<String> { it }
        assertTrue(queue.enqueue("a"))
        assertFalse(queue.enqueue("a"))
        queue.cancel("a")
        assertNull(queue.takeNext())
        assertFalse(queue.busy)
    }

    @Test fun `service stop invalidates claims and reports outstanding retries once`() {
        val queue = DownloadWorkQueue<String> { it }
        queue.enqueue("a")
        val claim = queue.takeNext()!!
        val job = Job()
        queue.attach(claim, job)
        queue.enqueue("b")
        assertEquals(setOf("a", "b"), queue.stop().toSet())
        assertTrue(job.isCancelled)
        assertFalse(queue.accepts(claim))
        assertTrue(queue.busy)
        assertTrue(queue.enqueue("a"))
        assertNull(queue.takeNext())
        queue.finish(claim)
        assertEquals("a", queue.takeNext()!!.id)
    }
}
