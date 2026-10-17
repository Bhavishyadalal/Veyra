package app.veyra.music.ui.components

/** Logical tab position for a physical horizontal drag, clamped at both ends. */
internal fun navigationDragPosition(
    startIndex: Int,
    deltaPx: Float,
    stepPx: Float,
    tabCount: Int,
    rightToLeft: Boolean,
): Float {
    val last = (tabCount - 1).coerceAtLeast(0)
    val start = startIndex.coerceIn(0, last).toFloat()
    if (stepPx <= 0f || !stepPx.isFinite() || !deltaPx.isFinite()) return start
    val direction = if (rightToLeft) -1f else 1f
    return (start + deltaPx * direction / stepPx).coerceIn(0f, last.toFloat())
}

/** The indicator is placed from the physical left; tab indices follow reading order. */
internal fun navigationIndicatorOffset(index: Int, tabCount: Int, stepPx: Float, rightToLeft: Boolean): Float {
    if (tabCount <= 0 || stepPx <= 0f || !stepPx.isFinite()) return 0f
    val clamped = index.coerceIn(0, tabCount - 1)
    return (if (rightToLeft) tabCount - 1 - clamped else clamped) * stepPx
}
