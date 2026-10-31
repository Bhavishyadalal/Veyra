package app.veyra.music.ui.onboarding

import kotlin.math.abs

internal const val WELCOME_PREVIEW_COUNT = 3
internal fun welcomeNameInput(raw: String, limit: Int): String {
    val clean = raw.replace(Regex("[\\u0000-\\u001F\\u007F]"), " ")
    val end = clean.offsetByCodePoints(0, minOf(clean.codePointCount(0, clean.length), limit.coerceAtLeast(0)))
    return clean.substring(0, end)
}
internal fun nextWelcomePreview(current: Int, forward: Boolean = true): Int {
    val normalized = ((current % WELCOME_PREVIEW_COUNT) + WELCOME_PREVIEW_COUNT) % WELCOME_PREVIEW_COUNT
    return (normalized + if (forward) 1 else WELCOME_PREVIEW_COUNT - 1) % WELCOME_PREVIEW_COUNT
}
internal fun welcomePreviewAfterSwipe(current: Int, distance: Float, threshold: Float, rtl: Boolean): Int {
    if (!distance.isFinite() || !threshold.isFinite() || threshold <= 0f || abs(distance) < threshold) return current
    return nextWelcomePreview(current, if (rtl) distance > 0f else distance < 0f)
}
