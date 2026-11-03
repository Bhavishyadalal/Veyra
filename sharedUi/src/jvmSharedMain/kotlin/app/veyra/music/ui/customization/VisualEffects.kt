package app.veyra.music.ui.customization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp

/** Propagates to sheets/popups and shared player surfaces without changing saved themes. */
val LocalReducedVisualEffects = staticCompositionLocalOf { false }

@Composable
fun Modifier.visualEffectBlur(radius: Dp, edgeTreatment: BlurredEdgeTreatment = BlurredEdgeTreatment.Rectangle): Modifier =
    if (LocalReducedVisualEffects.current) this else blur(radius, edgeTreatment)

/** Remove decorative elevation while retaining the original clipping contract. */
@Composable
fun Modifier.visualEffectShadow(
    elevation: Dp,
    shape: Shape = RectangleShape,
    clip: Boolean = elevation > 0.dp,
    ambientColor: Color = Color.Black,
    spotColor: Color = Color.Black,
): Modifier = if (LocalReducedVisualEffects.current) {
    if (clip) this.clip(shape) else this
} else shadow(elevation, shape, clip, ambientColor, spotColor)
