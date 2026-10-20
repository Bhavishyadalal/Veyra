package app.veyra.music.ui.themes

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.Composable
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.clearAndSetSemantics
import coil3.compose.rememberAsyncImagePainter
import java.io.File

@Immutable
data class ThemeVisuals(
    val id: String, val name: String, val accent: Color, val surface: Color,
    val raised: Color, val outline: Color, val rowCorner: Int, val buttonCorner: Int,
    val navCorner: Int, val assets: Map<String, String>,
    val seekStyle: String = "plain", val seekHeight: Int = 7, val version: Int = 1,
) {
    fun asset(key: String): String? = assets[key]
}
val LocalThemeVisuals = staticCompositionLocalOf<ThemeVisuals?> { null }

/** Theme images are local, prevalidated assets. Missing/failed icons retain the native glyph. */
@Composable
fun ThemeIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
    themeKey: String = imageVector.name,
) {
    val key = when (themeKey) {
        "veyra_appearance" -> "settings"
        "bc_queue" -> "queue"
        "bc_lyrics_quote" -> "lyrics"
        else -> when (themeKey.substringAfterLast('.')) {
            "ArrowBack" -> "back"
            "Download", "DownloadForOffline", "FileDownload" -> "download"
            "QueueMusic" -> "queue"
            else -> themeKey
        }
    }
    val path = LocalThemeVisuals.current?.asset(key)
    if (path == null) Icon(imageVector, contentDescription, modifier, tint)
    else {
        val painter = rememberAsyncImagePainter(File(path), placeholder = androidx.compose.ui.graphics.vector.rememberVectorPainter(imageVector), error = androidx.compose.ui.graphics.vector.rememberVectorPainter(imageVector))
        val rtl = androidx.compose.ui.platform.LocalLayoutDirection.current == androidx.compose.ui.unit.LayoutDirection.Rtl
        Image(painter, contentDescription, modifier.graphicsLayer { if (key == "back" && rtl) scaleX = -1f },
            colorFilter = if (tint == Color.Unspecified) null else ColorFilter.tint(tint))
    }
}

/** Native text overlays original artwork; images never replace the functional interface. */
@Composable
fun ThemeAtmosphere(key: String, modifier: Modifier = Modifier, compact: Boolean = false) {
    val theme = LocalThemeVisuals.current ?: return
    val asset = theme.asset(key) ?: return
    Box(modifier.fillMaxWidth().heightIn(min=if (compact) 112.dp else 168.dp)
        .clip(RoundedCornerShape(theme.rowCorner.dp)).background(theme.surface)) {
        Image(rememberAsyncImagePainter(File(asset)), null,
            Modifier.matchParentSize().clearAndSetSemantics {}, contentScale = ContentScale.Crop)
        Box(Modifier.matchParentSize().background(Brush.horizontalGradient(listOf(theme.surface.copy(alpha=.65f),Color.Transparent))))
        Column(Modifier.fillMaxWidth(if(LocalDensity.current.fontScale > 1.2f) .85f else .64f).padding(20.dp)) {
            Text(theme.name, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(6.dp))
            Text("A different kind of night.", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ThemeBackdrop(key: String, modifier: Modifier = Modifier, visible: Boolean = true) {
    val path = LocalThemeVisuals.current?.asset(key) ?: return
    if (visible) Image(rememberAsyncImagePainter(File(path)), null,
        modifier.clearAndSetSemantics {}, contentScale=ContentScale.Crop, alpha=0.16f)
}

/** Painter resources such as service logos keep their original identity and behavior. */
@Composable
fun ThemeIcon(
    painter: androidx.compose.ui.graphics.painter.Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) { Icon(painter,contentDescription,modifier,tint) }
