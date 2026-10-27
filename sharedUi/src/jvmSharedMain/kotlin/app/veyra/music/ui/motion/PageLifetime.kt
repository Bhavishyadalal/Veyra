package app.veyra.music.ui.motion

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner

private class RetainedPageOwner : LifecycleOwner {
    override val lifecycle = LifecycleRegistry.createUnsafe(this)
    fun moveTo(state: Lifecycle.State) {
        if (state == Lifecycle.State.DESTROYED && lifecycle.currentState == Lifecycle.State.INITIALIZED)
            lifecycle.currentState = Lifecycle.State.CREATED
        lifecycle.currentState = state
    }
}

/** Retain UI state while stopping covered-page collectors, drawing and stale input separately. */
@Composable
fun PageLifetime(active: Boolean, visible: Boolean = true, content: @Composable () -> Unit) {
    val parent = LocalLifecycleOwner.current
    val owner = remember(parent) { RetainedPageOwner() }
    DisposableEffect(parent, owner, active) {
        fun update() {
            val state = parent.lifecycle.currentState
            owner.moveTo(if (active || state < Lifecycle.State.CREATED) state else Lifecycle.State.CREATED)
        }
        val observer = LifecycleEventObserver { _, _ -> update() }
        parent.lifecycle.addObserver(observer)
        update()
        onDispose { parent.lifecycle.removeObserver(observer) }
    }
    DisposableEffect(owner) { onDispose { owner.moveTo(Lifecycle.State.DESTROYED) } }
    Box(Modifier.fillMaxSize().graphicsLayer { alpha = if (visible) 1f else 0f }
        .then(if (active) Modifier else Modifier.clearAndSetSemantics { }.pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) awaitPointerEvent(PointerEventPass.Initial).changes.forEach { it.consume() }
            }
        })) {
        CompositionLocalProvider(LocalLifecycleOwner provides owner, content = content)
    }
}
