package app.veyra.music.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.compositeOver

/** Explicit off/disabled colours: the theme's dark outline is not a visible thumb. */
@Composable
fun veyraSwitchColors(): SwitchColors {
    val scheme = MaterialTheme.colorScheme
    return SwitchDefaults.colors(
        checkedThumbColor = scheme.onPrimary,
        checkedTrackColor = scheme.primary,
        checkedBorderColor = scheme.primary,
        checkedIconColor = scheme.primary,
        uncheckedThumbColor = scheme.onSurface,
        uncheckedTrackColor = scheme.surfaceVariant,
        uncheckedBorderColor = scheme.onSurfaceVariant,
        uncheckedIconColor = scheme.surfaceVariant,
        disabledCheckedThumbColor = scheme.onPrimary.copy(alpha = 0.8f).compositeOver(scheme.primary),
        disabledCheckedTrackColor = scheme.primary.copy(alpha = 0.45f).compositeOver(scheme.surface),
        disabledCheckedBorderColor = scheme.primary.copy(alpha = 0.45f).compositeOver(scheme.surface),
        disabledCheckedIconColor = scheme.primary,
        disabledUncheckedThumbColor = scheme.onSurface.copy(alpha = 0.55f).compositeOver(scheme.surface),
        disabledUncheckedTrackColor = scheme.surfaceVariant,
        disabledUncheckedBorderColor = scheme.onSurfaceVariant.copy(alpha = 0.55f).compositeOver(scheme.surface),
        disabledUncheckedIconColor = scheme.surfaceVariant,
    )
}
