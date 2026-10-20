package app.veyra.music.ui.layout

/** Available width in text-sized dp, rather than a device-width-only breakpoint. */
fun stackForReadability(widthDp: Float, fontScale: Float, minimumTextWidthDp: Float): Boolean {
    if (!widthDp.isFinite() || widthDp <= 0f) return true
    val scale = fontScale.takeIf { it.isFinite() && it > 0f } ?: 1f
    return widthDp / scale < minimumTextWidthDp
}
