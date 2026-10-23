package app.veyra.music.ui.themes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import coil3.compose.AsyncImagePainter
import androidx.compose.ui.unit.dp

val LocalThemeControlPressed = androidx.compose.runtime.staticCompositionLocalOf { false }

/** Preserve authored colors only after decoding; native loading/error placeholders retain tint.
 * Disabled alpha and destructive error tint stay app-owned. State is observed per visible glyph.
 */
@Composable
fun ThemePainterGlyph(painter: Painter, description: String?, modifier: Modifier, tint: Color, preserveColors: Boolean) {
    val ready = preserveColors && painter is AsyncImagePainter && painter.state.collectAsState().value is AsyncImagePainter.State.Success
    val error = MaterialTheme.colorScheme.error
    val destructive = tint != Color.Unspecified && tint.copy(alpha=1f) == error.copy(alpha=1f)
    val original = ready && !destructive
    Image(painter, description, modifier,
        alpha=if(original && tint!=Color.Unspecified) tint.alpha else 1f,
        colorFilter=if(original || tint==Color.Unspecified) null else ColorFilter.tint(tint))
}

@Composable
fun themeHeadingColor(fallback: Color): Color = LocalThemeVisuals.current?.takeIf {it.typeStyle == "punk"}?.accent ?: fallback

/** Cached native finish; no blur, shader, ticker or downloaded executable material. */
fun punkControlFinish(modifier: Modifier, theme: ThemeVisuals): Modifier = modifier.drawWithCache {
    val gloss=Brush.verticalGradient(listOf(theme.text.copy(alpha=.22f),theme.secondary.copy(alpha=.08f),Color.Transparent,theme.background.copy(alpha=.24f)))
    onDrawBehind {
        drawRect(gloss)
        when(theme.materialStyle) {
            "punk_rivet" -> repeat(4) { i ->
                val x=if(i%2==0)size.width*.12f else size.width*.88f
                val y=if(i<2)size.height*.12f else size.height*.88f
                drawCircle(theme.text.copy(alpha=.6f),1.1.dp.toPx(),Offset(x,y))
            }
            "punk_torn" -> repeat(8) {i ->
                val x=size.width*(i+.2f)/8f
                drawLine(theme.text.copy(alpha=.18f),Offset(x,0f),Offset(x+2.dp.toPx(),2.dp.toPx()),1.dp.toPx())
            }
            "punk_chrome" -> drawLine(theme.secondary.copy(alpha=.45f),Offset(size.width*.2f,size.height*.15f),Offset(size.width*.8f,size.height*.15f),1.dp.toPx())
            else -> drawLine(theme.text.copy(alpha=.45f),Offset(size.width*.25f,size.height*.12f),Offset(size.width*.65f,size.height*.12f),1.dp.toPx())
        }
    }
}
