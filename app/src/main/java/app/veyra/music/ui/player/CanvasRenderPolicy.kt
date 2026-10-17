package app.veyra.music.ui.player

/** Keep decoder work and GPU readbacks limited to visible, active playback. */
internal object CanvasRenderPolicy {
    fun shouldPlay(isPlaying: Boolean, foreground: Boolean, transitioning: Boolean): Boolean =
        isPlaying && foreground && !transitioning

    fun shouldRefresh(active: Boolean, rendered: Boolean, intervalMs: Long?): Boolean =
        active && rendered && intervalMs != null && intervalMs > 0
}
