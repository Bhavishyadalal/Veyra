package app.veyra.music.ui.player
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
@Composable actual fun ImmersiveWindow(onDismiss:()->Unit,content:@Composable ()->Unit) {
    Dialog(onCloseRequest=onDismiss,title="Veyra · Immersive",content={content()})
}
