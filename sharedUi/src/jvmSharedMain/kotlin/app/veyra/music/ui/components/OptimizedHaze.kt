package app.veyra.music.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import app.veyra.music.ui.customization.LocalReducedVisualEffects
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeEffectScope
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeEffect

/**
 * Keeps Haze's visual style while allowing it to reduce its sampling resolution.
 * Haze 1.x otherwise processes every effect at full resolution, even when a
 * large blur makes those extra source pixels invisible.
 *
 * A third rather than [HazeInputScale.Auto]. Auto only steps the resolution down
 * once the blur radius is large enough for it to be confident, which left the
 * mini player and the nav bar sampling at full resolution on every frame of
 * every scroll. A third is what the liquid glass surfaces already sample at, and
 * what [app.veyra.music.ui.components.TopFadeBlur] was moved to for the same
 * reason: the blur is what hides the upscale, so the pixels that were being paid
 * for could not be seen either way. [block] can still override it per call site.
 */
@Composable
@OptIn(ExperimentalHazeApi::class)
fun Modifier.optimizedHazeEffect(
    state: HazeState,
    style: HazeStyle = HazeStyle.Unspecified,
    block: (HazeEffectScope.() -> Unit)? = null,
): Modifier = if (LocalReducedVisualEffects.current) {
    background(MaterialTheme.colorScheme.surface.copy(alpha = 1f))
} else hazeEffect(state, style) {
    inputScale = HazeInputScale.Fixed(0.33f)
    block?.invoke(this)
}

@Composable
fun Modifier.visualEffectHazeSource(state: HazeState): Modifier =
    if (LocalReducedVisualEffects.current) this else hazeSource(state)
