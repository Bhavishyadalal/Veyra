package app.veyra.music.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BlurOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.veyra.music.R

/** One-time, explicit choice after welcome. Playback and saved themes remain intact. */
@Composable
internal fun VisualEffectsChoiceDialog(onChoose: (Boolean) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Rounded.BlurOff, null) },
        title = { Text(stringResource(R.string.visual_effects_choice_title)) },
        text = {
            Column(Modifier.heightIn(max = 320.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(stringResource(R.string.visual_effects_choice_hint))
                Text(stringResource(R.string.visual_effects_reduced_hint), style = MaterialTheme.typography.bodyMedium)
                Text(stringResource(R.string.visual_effects_full_hint), style = MaterialTheme.typography.bodyMedium)
                Text(stringResource(R.string.visual_effects_change_later), style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        confirmButton = {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onChoose(true) }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.visual_effects_use_reduced))
                }
                OutlinedButton(onClick = { onChoose(false) }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.visual_effects_use_full))
                }
            }
        },
        // Full-width actions wrap safely with large text instead of competing for a row.
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 1f),
    )
}
