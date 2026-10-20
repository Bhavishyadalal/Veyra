package app.veyra.music.ui.customization

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class AccentPalette { DEFAULT, TEAL, AMBER, ROSE }
enum class ArtworkCorners { DEFAULT, SOFT, ROUNDED, SQUARE }
enum class ListSpacing { COMFORTABLE, COMPACT }
enum class MotionLevel { FULL, SUBTLE, OFF }
enum class PlayerShortcut { NONE, DOWNLOAD, FAVORITE, SLEEP_TIMER, PLAYLIST }

/** Unknown future/invalid backup values fall back without crashing settings. */
inline fun <reified T : Enum<T>> savedChoice(raw: String?, fallback: T): T =
    enumValues<T>().firstOrNull { it.name == raw } ?: fallback

val LocalArtworkCorners = staticCompositionLocalOf { ArtworkCorners.DEFAULT }
val LocalListSpacing = staticCompositionLocalOf { ListSpacing.COMFORTABLE }
val LocalSubtleMotion = staticCompositionLocalOf { false }

@Composable
fun artworkCorner(default: Dp): Dp = when (LocalArtworkCorners.current) {
    ArtworkCorners.DEFAULT -> default
    ArtworkCorners.SOFT -> 8.dp
    ArtworkCorners.ROUNDED -> 24.dp
    ArtworkCorners.SQUARE -> 0.dp
}

@Composable
fun artworkShape(default: Shape): Shape = when (LocalArtworkCorners.current) {
    ArtworkCorners.DEFAULT -> default
    ArtworkCorners.SOFT -> RoundedCornerShape(8.dp)
    ArtworkCorners.ROUNDED -> RoundedCornerShape(24.dp)
    ArtworkCorners.SQUARE -> RoundedCornerShape(0.dp)
}

/** Compact presentation never reduces the content below accessible artwork/touch sizes. */
fun songRowPadding(spacing: ListSpacing, fontScale: Float): Int =
    if (spacing == ListSpacing.COMPACT && fontScale.isFinite() && fontScale in 0.5f..1.15f) 0 else 4
