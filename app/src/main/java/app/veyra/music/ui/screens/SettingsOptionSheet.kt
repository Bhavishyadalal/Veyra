package app.veyra.music.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch

/** Finish dismissal before a selection changes layout, motion or storage policy. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsOptionSheet(onDismiss: () -> Unit, content: @Composable ((() -> Unit) -> Unit) -> Unit) {
    val sheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val latestDismiss by rememberUpdatedState(onDismiss)
    var committing by remember { mutableStateOf(false) }
    ModalBottomSheet(sheetState = sheet, onDismissRequest = { latestDismiss() },
        containerColor = MaterialTheme.colorScheme.background) {
        content { action ->
            if (!committing) {
                committing = true
                scope.launch {
                    try {
                        sheet.hide()
                        if (!sheet.isVisible) { action(); latestDismiss() }
                    } finally { committing = false }
                }
            }
        }
    }
}
