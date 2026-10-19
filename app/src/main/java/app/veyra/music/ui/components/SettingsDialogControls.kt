package app.veyra.music.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Bounded by each host's screen gutters; action height is a minimum, never a text crop. */
internal val ALERT_WIDTH = 340.dp
internal val ALERT_CORNER = 24.dp
internal val ACTION_HEIGHT = 56.dp

/**
 * Full-bleed action row. Tinted rather than filled, so the two read as equals in weight and only
 * the font differentiates the default action — the alert's whole point is that neither choice is a
 * trap.
 *
 * [destructive] is the one exception to that: an action that deletes something is red in this
 * lineage, because the point there *is* to break the symmetry. It stays a plain row rather than a
 * filled button, so it reads as a warning and not as the thing to press.
 */
@Composable
internal fun AlertAction(
    label: String,
    emphasised: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true,
    destructive: Boolean = false,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    Box(
        modifier =
            Modifier.fillMaxWidth()
                .heightIn(min = ACTION_HEIGHT)
                // iOS washes the whole row instead of drawing a ripple inside it.
                .background(
                    if (pressed) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.09f)
                    else Color.Transparent
                )
                .clickable(
                    enabled = enabled,
                    indication = null,
                    interactionSource = interactionSource,
                    onClick = onClick,
                    role = Role.Button,
                )
                .padding(horizontal = 20.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            textAlign = TextAlign.Center,
            style =
                MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 17.sp,
                    fontWeight = if (emphasised) FontWeight.W700 else FontWeight.W500,
                ),
            color =
                (if (destructive) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary)
                    .copy(alpha = if (enabled) 1f else 0.4f),
        )
    }
}

/**
 * Hairline separator — [HorizontalDivider][androidx.compose.material3.HorizontalDivider]'s 1dp
 * reads as a bar at this scale.
 */
@Composable
internal fun AlertRule(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.14f))
    )
}
