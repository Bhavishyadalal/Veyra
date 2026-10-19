package app.veyra.music.ui.player

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
internal class PlayerScrub {
    var scrubbing by mutableStateOf(false)
        private set

    var value by mutableFloatStateOf(0f)
        private set

    /**
     * After releasing the scrubber the player needs to buffer before it reports the new position.
     * Kept showing where the user dropped it so the handle doesn't snap back and then jump forward
     * once loading finishes.
     */
    internal var pendingSeek by mutableStateOf<Float?>(null)

    /** Where the handle is drawn. */
    fun shown(positionMs: Long, durationMs: Long): Float {
        val fraction = if (durationMs > 0) positionMs.toFloat() / durationMs else 0f
        return when {
            scrubbing -> value
            pendingSeek != null -> pendingSeek!!
            else -> fraction.coerceIn(0f, 1f)
        }
    }

    fun drag(to: Float) {
        scrubbing = true
        value = playerFraction(to)
    }

    fun cancel() {
        scrubbing = false
        pendingSeek = null
    }

    fun release(seekTo: (Float) -> Unit) {
        pendingSeek = value
        seekTo(value)
        scrubbing = false
    }
}
