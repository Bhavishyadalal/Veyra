package app.veyra.music.ui.themes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/** A legible, static theme material for both navigation implementations.
 * No backdrop sampling, image decoding, timers or extra input/semantics nodes.
 */
@Composable
fun Modifier.themeNavigationSurface(shape: Shape): Modifier {
    val theme = LocalThemeVisuals.current ?: return this
    val opacity = LocalThemeArtworkOptions.current.controlOpacity
    return clip(shape)
        .background(Brush.verticalGradient(listOf(theme.raised.copy(alpha = opacity), theme.surface.copy(alpha = opacity))))
        .drawWithCache {
            val line = 1.dp.toPx()
            onDrawBehind {
                // Ornament stays below glyphs/labels; never put artwork behind text.
                val y = size.height - 3.dp.toPx()
                when (theme.seekStyle) {
                    "bubbles" -> repeat(9) { i ->
                        drawCircle(theme.secondary.copy(alpha = .25f), (1 + i % 2).dp.toPx(),
                            Offset(size.width * (i + .5f) / 9f, y))
                    }
                    "web" -> repeat(8) { i ->
                        val x = size.width * i / 8f
                        drawLine(theme.secondary.copy(alpha = .28f), Offset(x, y),
                            Offset(x + size.width / 14f, y), line)
                    }
                    else -> drawLine(Brush.horizontalGradient(listOf(
                        theme.surface, theme.secondary.copy(alpha = .45f), theme.surface)),
                        Offset(size.width * .12f, y), Offset(size.width * .88f, y), line)
                }
            }
        }
        .border(1.dp, theme.outline.copy(alpha = .75f), shape)
}

/** Shared with the actual floating bar: one spoken/selectable tab and a wrapping label. */
@Composable
fun ThemeNavigationItem(
    label: String, selected: Boolean, tint: Color, onClick: () -> Unit,
    modifier: Modifier = Modifier, showLabel: Boolean = true, icon: @Composable () -> Unit,
) {
    Column(modifier.clip(RoundedCornerShape(22.dp)).heightIn(min = 48.dp)
        .semantics {contentDescription=label}.selectable(selected = selected, role = Role.Tab, onClick = onClick)
        .padding(vertical = 9.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        icon()
        if(showLabel)Spacer(Modifier.height(3.dp))
        if(showLabel)Text(label, style = app.veyra.music.ui.styles.styleNavigationLabelStyle(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.fillMaxWidth(),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = tint, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}
