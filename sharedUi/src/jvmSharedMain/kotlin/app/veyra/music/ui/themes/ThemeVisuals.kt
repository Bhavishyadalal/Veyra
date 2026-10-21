package app.veyra.music.ui.themes

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.Composable
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.luminance
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.border
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
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import java.io.File

@Immutable
data class ThemeVisuals(
    val id: String, val name: String, val accent: Color, val surface: Color,
    val raised: Color, val outline: Color, val rowCorner: Int, val buttonCorner: Int,
    val navCorner: Int, val assets: Map<String, String>,
    val seekStyle: String = "plain", val seekHeight: Int = 7, val version: Int = 1,
    val background: Color = surface,
    val text: Color = Color(0xFFFFF4E6), val muted: Color = Color(0xFFCFBDAA),
    val secondary: Color = accent, val sceneStyle: String = "mist",
    val tagline: String = "A different kind of night.",
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
            "PlayArrow" -> "play"
            "Pause" -> "pause"
            "SkipPrevious" -> "previous"
            "SkipNext" -> "next"
            "Home" -> "nav_home"
            "Search" -> "nav_search"
            "Explore" -> "nav_explore"
            "LibraryMusic" -> "nav_library"
            "Settings" -> "settings"
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
        // Match Material Icon's default 24 dp footprint; pack bitmaps have much
        // larger intrinsic sizes. Explicit caller sizing still takes precedence.
        Image(painter, contentDescription, modifier.size(24.dp).graphicsLayer { if (key == "back" && rtl) scaleX = -1f },
            colorFilter = if (tint == Color.Unspecified) null else ColorFilter.tint(tint))
    }
}

/** Native text overlays original artwork; images never replace the functional interface. */
@Composable
fun ThemeAtmosphere(key: String, modifier: Modifier = Modifier, compact: Boolean = false) {
    val theme = LocalThemeVisuals.current ?: return
    val asset = theme.asset(key) ?: return
    Box(modifier.fillMaxWidth().heightIn(min=if (compact) 128.dp else 196.dp)
        .clip(RoundedCornerShape(theme.rowCorner.dp)).background(theme.surface)) {
        AsyncImage(File(asset), null,
            Modifier.matchParentSize().clearAndSetSemantics {}, contentScale = ContentScale.Crop)
        // The entire text column needs contrast, including bright artwork and large text.
        val textFraction = if (LocalDensity.current.fontScale > 1.2f) .85f else .64f
        val rtl = androidx.compose.ui.platform.LocalLayoutDirection.current == androidx.compose.ui.unit.LayoutDirection.Rtl
        val scrim = if (rtl) arrayOf(0f to theme.surface.copy(alpha=.1f),
            (1f-textFraction) to theme.surface.copy(alpha=.9f), 1f to theme.surface.copy(alpha=.96f))
        else arrayOf(0f to theme.surface.copy(alpha=.96f),
            textFraction to theme.surface.copy(alpha=.9f), 1f to theme.surface.copy(alpha=.1f))
        Box(Modifier.matchParentSize().background(Brush.horizontalGradient(*scrim)))
        Column(Modifier.fillMaxWidth(textFraction).padding(20.dp)) {
            Text(theme.name, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(6.dp))
            Text(theme.tagline, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ThemeBackdrop(key: String, modifier: Modifier = Modifier, visible: Boolean = true) {
    val theme = LocalThemeVisuals.current ?: return
    val path = theme.asset(key) ?: return
    if (visible) AsyncImage(File(path), null,
        modifier.clearAndSetSemantics {}, contentScale=ContentScale.Crop, alpha=if(theme.sceneStyle == "playful") .08f else .16f)
}

/** Painter resources such as service logos keep their original identity and behavior. */
@Composable
fun ThemeIcon(
    painter: androidx.compose.ui.graphics.painter.Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) { Icon(painter,contentDescription,modifier,tint) }


/** Static local artwork and cached ornaments: no timers, shaders or touch interception. */
fun themeSceneScrimAlpha(theme: ThemeVisuals): Float {
    // Even the brightest possible artwork must leave body/status text readable.
    fun readable(scrim: Float): Boolean {
        val artwork=.70f*(1f-scrim)
        val background=Color(
            theme.background.red*(1f-artwork)+artwork,
            theme.background.green*(1f-artwork)+artwork,
            theme.background.blue*(1f-artwork)+artwork).luminance()
        return listOf(theme.text,theme.muted).all { color ->
            val foreground=color.luminance()
            (maxOf(foreground,background)+.05f)/(minOf(foreground,background)+.05f)>=4.5f
        }
    }
    var low=.50f; var high=1f
    repeat(12) {val mid=(low+high)/2f;if(readable(mid))high=mid else low=mid}
    return high
}

@Composable
fun ThemeScene(modifier: Modifier = Modifier, prominent: Boolean = false) {
    val theme = LocalThemeVisuals.current ?: return
    if (theme.sceneStyle.startsWith("app_")) {
        app.veyra.music.ui.styles.StyleBackdrop(app.veyra.music.ui.styles.LocalAppStyle.current,theme.asset("header"),modifier)
        return
    }
    val scrim=remember(theme) {themeSceneScrimAlpha(theme)}
    Box(modifier.background(theme.background).clearAndSetSemantics {}) {
        theme.asset("header")?.let { path ->
            AsyncImage(File(path), null, Modifier.matchParentSize(),
                contentScale = ContentScale.Crop, alignment = androidx.compose.ui.Alignment.CenterEnd,
                alpha = if(prominent) .70f else if(theme.sceneStyle == "playful") .22f else .34f)
        }
        Box(Modifier.matchParentSize().background(Brush.verticalGradient(listOf(
            theme.background.copy(alpha = if(prominent) scrim else .35f),
            theme.background.copy(alpha = if(prominent) maxOf(scrim,.72f) else .50f),
            theme.background.copy(alpha = if(prominent) maxOf(scrim,.65f) else .35f)))))
        androidx.compose.foundation.Canvas(Modifier.matchParentSize()) {
            if (theme.sceneStyle == "web") {
                val origin = androidx.compose.ui.geometry.Offset(size.width * 1.04f, -size.height * .04f)
                val reach = maxOf(size.width,size.height) * 1.4f
                for (ray in 0..10) {
                    val angle = ray * Math.PI / 20 + Math.PI / 2
                    drawLine(theme.secondary.copy(alpha=.12f),origin,
                        origin + androidx.compose.ui.geometry.Offset(kotlin.math.cos(angle).toFloat()*reach,kotlin.math.sin(angle).toFloat()*reach), .7.dp.toPx())
                }
                for (ring in 1..9) {
                    val radius = ring * reach / 10
                    drawArc(theme.accent.copy(alpha=.10f),90f,90f,false,
                        topLeft=origin-androidx.compose.ui.geometry.Offset(radius,radius),
                        size=androidx.compose.ui.geometry.Size(radius*2,radius*2),
                        style=androidx.compose.ui.graphics.drawscope.Stroke(.7.dp.toPx()))
                }
            }
            if (theme.sceneStyle == "playful") {
                // Quiet sticker-like bubbles keep the theme present deep in every screen.
                // Fixed geometry: no background ticker, bitmap churn or invisible touch targets.
                repeat(12) { i ->
                    val center = androidx.compose.ui.geometry.Offset(
                        size.width * ((i * 31 % 97) / 96f), size.height * ((i * 43 % 101) / 100f))
                    val radius = (10 + i % 4 * 7).dp.toPx()
                    val color = if (i % 2 == 0) theme.secondary else theme.accent
                    drawCircle(color.copy(alpha=.055f),radius,center)
                    drawCircle(color.copy(alpha=.16f),radius,center,
                        style=androidx.compose.ui.graphics.drawscope.Stroke(1.2.dp.toPx()))
                    drawCircle(theme.text.copy(alpha=.18f),2.dp.toPx(),
                        center + androidx.compose.ui.geometry.Offset(-radius*.35f,-radius*.35f))
                }
            }
            // A restrained constellation continues beyond the hero, even deep in a feed.
            repeat(24) { i ->
                val x = size.width * ((i * 37 % 101) / 100f)
                val y = size.height * ((i * 61 % 103) / 102f)
                drawCircle(theme.accent.copy(alpha = if (i % 3 == 0) .26f else .12f),
                    (if (i % 3 == 0) 1.3f else .7f).dp.toPx(), androidx.compose.ui.geometry.Offset(x,y))
            }
        }
    }
}

/** Small decorative rule used by real section headings, without extra spoken content. */
@Composable
fun ThemeSectionRule(modifier: Modifier = Modifier) {
    val theme = LocalThemeVisuals.current ?: return
    androidx.compose.foundation.Canvas(modifier.fillMaxWidth().height(12.dp).clearAndSetSemantics {}) {
        val y = size.height / 2
        val c = androidx.compose.ui.geometry.Offset(4.dp.toPx(),y)
        if (theme.sceneStyle == "web") {
            val d=3.dp.toPx()
            val path=androidx.compose.ui.graphics.Path().apply {
                moveTo(c.x,c.y-d);lineTo(c.x+d,c.y);lineTo(c.x,c.y+d);lineTo(c.x-d,c.y);close()
            }
            drawPath(path,theme.accent)
        } else if (theme.sceneStyle == "playful") {
            drawCircle(theme.accent,3.dp.toPx(),c)
            drawCircle(theme.secondary,2.dp.toPx(),c+androidx.compose.ui.geometry.Offset(9.dp.toPx(),0f))
        } else drawCircle(theme.accent,2.dp.toPx(),c)
        drawLine(Brush.horizontalGradient(listOf(theme.accent.copy(alpha=.55f),Color.Transparent)),
            androidx.compose.ui.geometry.Offset(14.dp.toPx(),y),
            androidx.compose.ui.geometry.Offset(size.width,y), .8.dp.toPx())
    }
}

/** Decorative icon housing only: the containing row owns its single button semantics. */
@Composable
fun ThemeActionEmblem(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val theme = LocalThemeVisuals.current
    if (theme == null) { content(); return }
    val shape = if (theme.sceneStyle == "web") androidx.compose.foundation.shape.CutCornerShape(9.dp) else RoundedCornerShape(if(theme.sceneStyle == "playful") 19.dp else 12.dp)
    Box(modifier.size(38.dp).clip(shape)
        .background(Brush.linearGradient(listOf(theme.raised,theme.surface)))
        .border(.75.dp, theme.outline.copy(alpha=.65f), shape),
        contentAlignment=androidx.compose.ui.Alignment.Center) { content() }
}
