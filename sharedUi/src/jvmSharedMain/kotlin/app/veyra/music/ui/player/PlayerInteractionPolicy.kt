package app.veyra.music.ui.player

/** Defensive fractions shared by gesture, drawing and accessibility paths. */
internal fun playerFraction(value: Float): Float =
    if (value.isFinite()) value.coerceIn(0f, 1f) else 0f

internal fun playerPointerFraction(x: Float, width: Float, rtl: Boolean): Float? {
    if (!x.isFinite() || !width.isFinite() || width <= 0f) return null
    val fraction = (x / width).coerceIn(0f, 1f)
    return if (rtl) 1f - fraction else fraction
}

internal fun playerMotionMillis(reduced: Boolean, duration: Int): Int =
    if (reduced) 0 else duration.coerceAtLeast(0)
