package app.veyra.music.ui.player

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow

/** Closing a scrolling panel must release the parent player's gesture suppression. */
@Composable
internal fun ReportPlayerScrolling(listState: LazyListState, onChange: (Boolean) -> Unit) {
    val latestChange by rememberUpdatedState(onChange)
    LaunchedEffect(listState) {
        try {
            snapshotFlow { listState.isScrollInProgress }.collect { latestChange(it) }
        } finally {
            latestChange(false)
        }
    }
}
