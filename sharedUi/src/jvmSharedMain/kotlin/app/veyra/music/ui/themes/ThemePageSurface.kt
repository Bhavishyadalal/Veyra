package app.veyra.music.ui.themes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/** Each navigation slot owns an opaque base and its scene, so entering/exiting text never bleeds through. */
@Composable
fun ThemePageSurface(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.background,
    content: @Composable () -> Unit,
) {
    Surface(modifier.fillMaxSize(), color = color) {
        Box(Modifier.fillMaxSize()) {
            ThemeScene(Modifier.matchParentSize(), prominent = true)
            content()
        }
    }
}

/** Retain tab-local UI and Settings across pushes; transient details do not accumulate saved states. */
@Composable
fun RetainedRootPage(holder: SaveableStateHolder, pageKey: String, content: @Composable () -> Unit) {
    ThemePageSurface {
        if (pageKey.startsWith("tab:") || pageKey == "settings") holder.SaveableStateProvider(pageKey, content)
        else content()
    }
}
