package app.veyra.music.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/** Original SVG paths compiled to native vector geometry. Sources live in design/settings-svg. */
object SettingsIcons {
    private fun glyph(name: String, data: String) = ImageVector.Builder(
        name = name, defaultWidth = 24.dp, defaultHeight = 24.dp,
        viewportWidth = 24f, viewportHeight = 24f,
    ).addPath(
        pathData = addPathNodes(data), fill = null,
        stroke = SolidColor(Color.Black), strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round,
    ).build()
    val Account: ImageVector by lazy { glyph("veyra_account", "M20 21v-2a7 7 0 0 0-14 0v2 M12 12a4 4 0 1 0 0-8a4 4 0 0 0 0 8") }
    val Sound: ImageVector by lazy { glyph("veyra_sound", "M4 7h16 M4 17h16 M9 4v6 M15 14v6") }
    val Appearance: ImageVector by lazy { glyph("veyra_appearance", "M12 3a9 9 0 1 0 0 18h2a2 2 0 0 0 1-4a1.5 1.5 0 0 1 1-3h1a4 4 0 0 0 4-4a9 9 0 0 0-9-7 M7 10h0.01 M10 6h0.01 M15 6h0.01 M18 10h0.01") }
    val Library: ImageVector by lazy { glyph("veyra_library", "M4 4v16 M9 4v16 M14 5l4-1l4 15l-4 1z") }
    val Performance: ImageVector by lazy { glyph("veyra_performance", "M4 17a9 9 0 1 1 16 0 M12 13l5-5 M5 17h14 M12 13h0.01") }
    val Advanced: ImageVector by lazy { glyph("veyra_advanced", "M4 4h6v6H4z M14 4h6v6h-6z M4 14h6v6H4z M14 17h6 M17 14v6") }
    val Instagram: ImageVector by lazy { glyph("veyra_instagram", "M7 3h10a4 4 0 0 1 4 4v10a4 4 0 0 1-4 4H7a4 4 0 0 1-4-4V7a4 4 0 0 1 4-4z M16 12a4 4 0 1 1-8 0a4 4 0 0 1 8 0 M17.5 6.5h0.01") }
    val Source: ImageVector by lazy { glyph("veyra_source", "M8 7l-5 5l5 5 M16 7l5 5l-5 5 M14 4l-4 16") }
    val Chevron: ImageVector by lazy { glyph("veyra_chevron", "M9 5l7 7l-7 7") }
    val Icons: ImageVector by lazy { glyph("veyra_icons", "M8 3H5a2 2 0 0 0-2 2v3 M16 3h3a2 2 0 0 1 2 2v3 M21 16v3a2 2 0 0 1-2 2h-3 M8 21H5a2 2 0 0 1-2-2v-3 M8 12l3 3l5-6") }
}
