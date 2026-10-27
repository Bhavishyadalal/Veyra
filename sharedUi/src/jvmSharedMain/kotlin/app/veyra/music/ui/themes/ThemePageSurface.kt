package app.veyra.music.ui.themes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.drawWithCache

/** Each navigation slot owns an opaque base and its scene, so entering/exiting text never bleeds through. */
@Composable
fun ThemePageSurface(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.background,
    content: @Composable () -> Unit,
) {
    Surface(modifier.fillMaxSize(), color = color) {
        Box(Modifier.fillMaxSize()) {
            ThemeScene(Modifier.matchParentSize(), prominent = true)
            content()
        }
    }
}

/** Retain tab-local UI and Settings across pushes; transient details do not accumulate saved states. */
@Composable
fun RetainedRootPage(holder: SaveableStateHolder, pageKey: String, active: Boolean = true,
    visible: Boolean = true, content: @Composable () -> Unit) {
    app.veyra.music.ui.motion.PageLifetime(active, visible) {
    ThemePageSurface {
        if (pageKey.startsWith("tab:") || pageKey == "settings") holder.SaveableStateProvider(pageKey, content)
        else content()
    }
    }
}

/** Wrap-content counterpart for sheets: artwork never changes their measured height or scroll ownership. */
@Composable
fun ThemeComponentSurface(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier) {
        ThemeScene(Modifier.matchParentSize(), prominent = true)
        content()
    }
}

/** Fade an artist photograph into the themed scene instead of an opaque flat-color foot. */
fun Modifier.themeArtistArtworkFade(enabled: Boolean): Modifier = if (!enabled) this else
    graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        .drawWithCache {
            val mask = Brush.verticalGradient(
                .45f to Color.Black, .65f to Color.Black.copy(alpha=.70f),
                .82f to Color.Black.copy(alpha=.28f), .94f to Color.Black.copy(alpha=.05f),
                1f to Color.Transparent,
            )
            onDrawWithContent {
                drawContent()
                drawRect(mask, blendMode=BlendMode.DstIn)
            }
        }
