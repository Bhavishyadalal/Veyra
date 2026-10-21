package app.veyra.music.ui.player

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import app.veyra.music.ui.theme.findWindow

@Composable
actual fun ImmersiveWindow(onDismiss: () -> Unit, content: @Composable () -> Unit) {
    Dialog(onDismissRequest=onDismiss,properties=DialogProperties(usePlatformDefaultWidth=false,decorFitsSystemWindows=false)) {
        val view=LocalView.current
        val lifecycle=LocalLifecycleOwner.current.lifecycle
        DisposableEffect(view,lifecycle) {
            val window=findWindow(view)
            val controller=window?.let {WindowCompat.getInsetsController(it,view)}
            val behavior=controller?.systemBarsBehavior
            val wasScreenOn=view.keepScreenOn
            fun update() {view.keepScreenOn=wasScreenOn || lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)}
            val observer=LifecycleEventObserver {_,_->update()}
            lifecycle.addObserver(observer)
            update()
            controller?.systemBarsBehavior=WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller?.hide(WindowInsetsCompat.Type.systemBars())
            onDispose {
                lifecycle.removeObserver(observer)
                view.keepScreenOn=wasScreenOn
                controller?.show(WindowInsetsCompat.Type.systemBars())
                if(behavior!=null)controller?.systemBarsBehavior=behavior
            }
        }
        content()
    }
}
