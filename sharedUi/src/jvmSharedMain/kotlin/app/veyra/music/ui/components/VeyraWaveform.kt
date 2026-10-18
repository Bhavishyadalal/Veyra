package app.veyra.music.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Small native brand accent; animation state is read only during drawing. */
@Composable
fun VeyraWaveform(
    modifier: Modifier = Modifier.size(32.dp),
    color: Color = MaterialTheme.colorScheme.primary,
    progress: () -> Float = { 1f },
) {
    Canvas(modifier) {
        val heights = floatArrayOf(0.27f, 0.62f, 1f, 0.74f, 0.35f)
        val width = size.width / 9f
        val reveal = progress().coerceIn(0.15f, 1f)
        heights.forEachIndexed { index, height ->
            val barHeight = size.height * height * reveal
            drawRoundRect(color.copy(alpha = color.alpha * (0.6f + reveal * 0.4f)),
                Offset(index * width * 2f, (size.height - barHeight) / 2f),
                Size(width, barHeight), CornerRadius(width / 2f))
        }
    }
}
