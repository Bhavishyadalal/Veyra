package app.veyra.music.ui.motion

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.veyra.music.ui.player.PlayerSettings

/** One finite timing scale for pages, drawers and controls. No permanent animation. */
object VeyraMotion {
    const val Press = 100
    const val Feedback = 140
    const val Enter = 240
    const val Exit = 180
    const val Section = 180
    fun duration(reduced: Boolean, millis: Int): Int = if (reduced) 0 else millis.coerceAtLeast(0)
}

/** Honor both Veyra's preference and Android's animation-duration scale. */
@Composable
fun rememberVeyraReducedMotion(): Boolean {
    val preference by PlayerSettings.reduceAnimation.collectAsStateWithLifecycle()
    val system = rememberCoroutineScope().coroutineContext[MotionDurationScale]?.scaleFactor ?: 1f
    return preference || system == 0f
}

/** Tonal feedback inside the caller's clip; layout and touch targets never move. */
@Composable
fun Modifier.veyraPressFeedback(
    interaction: MutableInteractionSource,
    color: Color,
    enabled: Boolean = true,
): Modifier {
    val pressed by interaction.collectIsPressedAsState()
    // A direct tonal response also works with reduced motion and avoids a
    // separate animation/flow collector for every row in a scrolling list.
    return drawBehind { if (pressed && enabled) drawRect(color.copy(alpha = 0.09f)) }
}
