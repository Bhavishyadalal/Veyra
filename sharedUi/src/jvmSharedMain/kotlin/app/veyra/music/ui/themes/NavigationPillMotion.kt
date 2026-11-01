package app.veyra.music.ui.themes

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.abs

/** Read travel/squash at the layer, keeping the bar's composition out of each frame. */
fun Modifier.navigationPillMotion(
    offset: () -> Float,
    targetOffset: Float,
    tabStep: Float,
    stretch: Float,
    squash: Float,
): Modifier = graphicsLayer {
    val current = offset()
    val lag = if (tabStep > 0f) (abs(targetOffset - current) / tabStep).coerceIn(0f, 1f) else 0f
    translationX = current
    scaleX = 1f + lag * stretch
    scaleY = 1f - lag * stretch * squash
}
