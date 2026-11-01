package app.veyra.music.ui.performance

import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PreparationRunnerTest {
    @Test fun unreadableSavedStateExposesRecoveryInsteadOfRemainingOnBlankScreen() = runTest {
        var fail = true
        var saved = false
        val runner = PreparationRunner(backgroundScope, listOf({}),
            { if (fail) error("Preferences unavailable") else false }, { saved = true })
        runner.start(); runCurrent()
        assertTrue(runner.state.value.failed)
        assertFalse(runner.state.value.checking)
        assertFalse(saved)
        fail = false
        runner.start(); runCurrent()
        assertTrue(runner.state.value.done)
        assertTrue(saved)
    }

    @Test fun progressTracksCompletedWorkAndRepeatedStartsDoNotDuplicateIt() = runTest {
        val finish = CompletableDeferred<Unit>()
        var runs = 0
        var saved = 0
        val runner = PreparationRunner(backgroundScope, listOf(
            { runs++ }, { finish.await() },
        ), { false }, { saved++ })
        runner.start(); runCurrent()
        assertEquals(1, runner.state.value.completed)
        assertFalse(runner.state.value.done)
        assertEquals(0, saved)
        runner.start(); runCurrent()
        assertEquals(1, runs)
        finish.complete(Unit); runCurrent()
        assertTrue(runner.state.value.done)
        assertEquals(1, saved)
        runner.start(); runCurrent()
        assertEquals(1, runs)
    }

    @Test fun preparedBuildSkipsOptionalWarmup() = runTest {
        val runner = PreparationRunner(backgroundScope, listOf({ error("Must not run") }), { true }, { error("Must not save") })
        runner.start(); runCurrent()
        assertTrue(runner.state.value.done)
    }

    @Test fun failedWorkDoesNotMarkPreparedAndCanRetry() = runTest {
        var fail = true
        var saved = false
        val runner = PreparationRunner(backgroundScope, listOf({ if (fail) error("Font unavailable") }),
            { false }, { saved = true })
        runner.start(); runCurrent()
        assertTrue(runner.state.value.failed)
        assertFalse(saved)
        fail = false
        runner.start(); runCurrent()
        assertTrue(runner.state.value.done)
        assertTrue(saved)
    }

    @Test fun cancellationNeverMarksUnfinishedWorkPrepared() = runTest {
        val process = CoroutineScope(SupervisorJob() + StandardTestDispatcher(testScheduler))
        var saved = false
        val runner = PreparationRunner(process, listOf({ awaitCancellation() }), { false }, { saved = true })
        runner.start(); runCurrent()
        process.cancel(); runCurrent()
        assertFalse(saved)
        assertFalse(runner.state.value.done)
    }

    @Test fun persistenceFailureRemainsRetryable() = runTest {
        val runner = PreparationRunner(backgroundScope, emptyList(), { false }, { error("Disk full") })
        runner.start(); runCurrent()
        assertFalse(runner.state.value.done)
        assertTrue(runner.state.value.failed)
    }
}
