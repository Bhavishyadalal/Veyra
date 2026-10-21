package app.veyra.music

import app.veyra.music.ui.SearchBackgroundWork
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Assert.*
import org.junit.Test

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class SearchBackgroundWorkTest {
    @Test fun duplicatePaginationDoesNotStartAnotherRequestAndCanRetryAfterCompletion() = runTest {
        var loading = false
        var calls = 0
        val gate = CompletableDeferred<Unit>()
        val work = SearchBackgroundWork(this) { loading = it }
        work.continueSearch { calls++; gate.await() }
        work.continueSearch { calls++ }
        runCurrent()
        assertTrue(loading)
        assertEquals(1, calls)
        gate.complete(Unit)
        runCurrent()
        assertFalse(loading)
        work.continueSearch { calls++ }
        runCurrent()
        assertEquals(2, calls)
        assertFalse(loading)
    }

    @Test fun queryInvalidationCancelsBothNetworkJobs() = runTest {
        var loading = false
        var paginationCanceled = false
        var prefetchCanceled = false
        val work = SearchBackgroundWork(this) { loading = it }
        work.continueSearch { try { awaitCancellation() } finally { paginationCanceled = true } }
        work.prefetch { try { awaitCancellation() } finally { prefetchCanceled = true } }
        runCurrent()
        work.cancel()
        runCurrent()
        assertFalse(loading)
        assertTrue(paginationCanceled)
        assertTrue(prefetchCanceled)
    }

    @Test fun lateCleanupCannotHideReplacementPaginationSpinner() = runTest {
        var loading = false
        val cleanup = CompletableDeferred<Unit>()
        val nextPage = CompletableDeferred<Unit>()
        val work = SearchBackgroundWork(this) { loading = it }
        work.continueSearch {
            try { awaitCancellation() } finally { withContext(NonCancellable) { cleanup.await() } }
        }
        runCurrent()
        work.cancel()
        work.continueSearch { nextPage.await() }
        runCurrent()
        cleanup.complete(Unit)
        runCurrent()
        assertTrue(loading)
        nextPage.complete(Unit)
        runCurrent()
        assertFalse(loading)
    }

    @Test fun replacingPrefetchCancelsObsoleteWork() = runTest {
        var oldCanceled = false
        var newCompleted = false
        val work = SearchBackgroundWork(this) {}
        work.prefetch { try { awaitCancellation() } finally { oldCanceled = true } }
        runCurrent()
        work.prefetch { newCompleted = true }
        runCurrent()
        assertTrue(oldCanceled)
        assertTrue(newCompleted)
    }

    @Test fun cancellationBeforeDispatchAndDuringRequestBothReleaseLoading() = runTest {
        var loading = false
        val work = SearchBackgroundWork(this) { loading = it }
        work.continueSearch { fail("Canceled request must not run") }
        work.cancel()
        runCurrent()
        assertFalse(loading)
        work.continueSearch { throw CancellationException("request canceled") }
        runCurrent()
        assertFalse(loading)
        work.continueSearch {}
        runCurrent()
        assertFalse(loading)
    }
}
