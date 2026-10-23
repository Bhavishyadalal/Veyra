package app.veyra.music.ui.motion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder

/** Covered pages leave composition: their effects/animations stop, while saveable UI returns on exit. */
@Composable fun CoveredAppPages(covered:Boolean,content:@Composable ()->Unit) {
    val savedPages=rememberSaveableStateHolder()
    if(!covered) savedPages.SaveableStateProvider("app-pages",content)
}
