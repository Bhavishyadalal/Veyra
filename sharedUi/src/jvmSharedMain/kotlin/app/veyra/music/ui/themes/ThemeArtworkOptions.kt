package app.veyra.music.ui.themes

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

/** User presentation overrides; installed packs remain untouched. */
@Immutable
data class ThemeArtworkOptions(
    val visibility: Float = 1f,
    val dim: Float = 0f,
    val blur: Float = 0f,
    val controlOpacity: Float = 1f,
) {
    fun normalized() = copy(
        visibility = visibility.safe(0f, 1f, 1f),
        dim = dim.safe(0f, .6f, 0f),
        blur = blur.safe(0f, 16f, 0f),
        controlOpacity = controlOpacity.safe(.55f, 1f, 1f),
    )
}
private fun Float.safe(min: Float, max: Float, fallback: Float) =
    if (isFinite()) coerceIn(min, max) else fallback
val LocalThemeArtworkOptions = staticCompositionLocalOf { ThemeArtworkOptions() }
