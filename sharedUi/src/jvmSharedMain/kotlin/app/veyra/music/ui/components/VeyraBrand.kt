package app.veyra.music.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import app.veyra.music.sharedui.resources.Res
import app.veyra.music.sharedui.resources.veyra_brand_icon
import org.jetbrains.compose.resources.painterResource

/** Android supplies the owner's locally bundled brand font; other hosts keep their UI font. */
val LocalVeyraWordmarkFont = staticCompositionLocalOf<FontFamily?> { null }

/** The launcher artwork and a single shaped word, never individually stretched letters. */
@Composable
fun VeyraBrand(
    modifier: Modifier = Modifier,
    prominent: Boolean = false,
    color: Color = MaterialTheme.colorScheme.onBackground,
    reveal: () -> Float = { 1f },
) {
    val density = LocalDensity.current
    val direction = if (LocalLayoutDirection.current == LayoutDirection.Rtl) -1 else 1
    BoxWithConstraints(modifier.fillMaxWidth().clearAndSetSemantics { contentDescription = "Veyra" }) {
        val iconSize = if (maxWidth < 280.dp) 56.dp else if (prominent) 80.dp else 64.dp
        val gap = if (prominent) 18.dp else 14.dp
        // Size the logo to its available width; supporting interface text still follows font scale.
        // Super Malibu's measured advance is 3.214 em. Extra room protects its edge bearings.
        val wordSize = (minOf(if (prominent) 58f else 46f,
            ((maxWidth - iconSize - gap).value / 3.4f).coerceAtLeast(1f)) / density.fontScale).sp
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(gap)) {
            Image(painterResource(Res.drawable.veyra_brand_icon), null,
                Modifier.size(iconSize).graphicsLayer {
                    val progress = reveal().coerceIn(0f, 1f)
                    alpha = progress
                    scaleX = 0.92f + 0.08f * progress
                    scaleY = scaleX
                    translationY = (1f - progress) * 10.dp.toPx()
                })
            Text("Veyra", style = TextStyle(fontFamily = LocalVeyraWordmarkFont.current,
                fontWeight = FontWeight.Normal, fontSynthesis = FontSynthesis.None,
                fontSize = wordSize, lineHeight = wordSize * 1.45f, letterSpacing = 0.sp),
                color = color, maxLines = 1, softWrap = false,
                modifier = Modifier.graphicsLayer {
                    val progress = ((reveal() - 0.15f) / 0.85f).coerceIn(0f, 1f)
                    alpha = progress
                    translationX = direction * (1f - progress) * 10.dp.toPx()
                })
        }
    }
}
