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
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.graphics.painter.Painter

val LocalVeyraSelectedIcon = androidx.compose.runtime.compositionLocalOf<Painter?> {null}

val LocalVeyraWordmarkFont = staticCompositionLocalOf<FontFamily?> { null }

/** The real brand layout, with the host's original icon supplied as a fallback. */
@Composable
fun VeyraBrandContent(
    fallbackIcon: Painter,
    wordmarkFont: FontFamily? = LocalVeyraWordmarkFont.current,
    modifier: Modifier = Modifier,
    prominent: Boolean = false,
    color: Color = MaterialTheme.colorScheme.onBackground,
    reveal: () -> Float = { 1f },
) {
    val pack = app.veyra.music.ui.themes.LocalThemeVisuals.current
    val selectedIcon=LocalVeyraSelectedIcon.current
    val framedIcon=selectedIcon != null || pack?.asset("brandIcon") != null
    val density = LocalDensity.current
    val direction = if (LocalLayoutDirection.current == LayoutDirection.Rtl) -1 else 1
    BoxWithConstraints(modifier.fillMaxWidth().clearAndSetSemantics { contentDescription = "Veyra" }) {
        val iconSize = if (maxWidth < 280.dp) 56.dp else if (prominent) 80.dp else 64.dp
        val gap = if (prominent) 18.dp else 14.dp
        // Size the logo to its available width; supporting interface text still follows font scale.
        // Super Malibu's measured advance is 3.214 em. Extra room protects its edge bearings.
        val wordSize = (minOf(if (pack?.asset("brandIcon") != null) { if (prominent) 68f else 60f } else if (prominent) 58f else 46f,
            ((maxWidth - iconSize - gap).value / 3.4f).coerceAtLeast(1f)) / density.fontScale).sp
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(gap)) {
            val nativeIcon = fallbackIcon
            val brandPainter = selectedIcon ?: pack?.asset("brandIcon")?.let {
                app.veyra.music.ui.themes.rememberThemeGlyphPainter(it,nativeIcon,256)
            } ?: nativeIcon
            Image(brandPainter, null,
                Modifier.size(iconSize).then(if(framedIcon) Modifier.clip(androidx.compose.foundation.shape.RoundedCornerShape(18.dp)) else Modifier).graphicsLayer {
                    val progress = reveal().coerceIn(0f, 1f)
                    alpha = progress
                    scaleX = 0.92f + 0.08f * progress
                    scaleY = scaleX
                    translationY = (1f - progress) * 10.dp.toPx()
                })
            Text(if (pack?.sceneStyle == "web") "VEYRA" else "Veyra", style = TextStyle(fontFamily = wordmarkFont,
                fontWeight = if (pack?.asset("brandIcon") != null) FontWeight.Bold else FontWeight.Normal, fontSynthesis = FontSynthesis.None,
                fontSize = wordSize, lineHeight = wordSize * 1.10f, letterSpacing = 0.sp,
                lineHeightStyle = androidx.compose.ui.text.style.LineHeightStyle(
                    androidx.compose.ui.text.style.LineHeightStyle.Alignment.Center,
                    androidx.compose.ui.text.style.LineHeightStyle.Trim.Both)),
                color = color, maxLines = 1, softWrap = false,
                modifier = Modifier.graphicsLayer {
                    val progress = ((reveal() - 0.15f) / 0.85f).coerceIn(0f, 1f)
                    alpha = progress
                    translationX = direction * (1f - progress) * 10.dp.toPx()
                })
        }
    }
}
