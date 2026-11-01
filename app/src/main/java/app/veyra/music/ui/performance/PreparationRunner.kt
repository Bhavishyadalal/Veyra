package app.veyra.music.ui.performance

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Process-owned work: leaving the preparation screen does not cancel warming. */
internal class PreparationRunner(
    private val scope: CoroutineScope,
    private val tasks: List<suspend () -> Unit>,
    private val isPrepared: () -> Boolean,
    private val savePrepared: () -> Unit,
) {
    data class State(val completed: Int = 0, val total: Int, val done: Boolean = false, val failed: Boolean = false, val checking: Boolean = false)
    private val _state = MutableStateFlow(State(total = tasks.size, checking = true))
    val state = _state.asStateFlow()
    private var job: Job? = null

    @Synchronized fun start() {
        if (job?.isActive == true || _state.value.done) return
        job = scope.launch {
            try {
                if (!isPrepared()) {
                    _state.value = State(total = tasks.size)
                    tasks.forEachIndexed { index, task ->
                        ensureActive()
                        task()
                        _state.value = State(completed = index + 1, total = tasks.size)
                    }
                    ensureActive()
                    savePrepared()
                }
                _state.value = State(tasks.size, tasks.size, done = true)
            } catch (cancel: CancellationException) {
                throw cancel
            } catch (_: Exception) {
                _state.value = _state.value.copy(failed = true, checking = false)
            }
        }
    }
}
