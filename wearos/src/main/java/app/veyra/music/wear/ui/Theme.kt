package app.veyra.music.wear.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Typography
import app.veyra.music.wear.R

/**
 * SF Pro Display, as on the phone — subset to Latin for the watch (the phone's
 * files are 2.3 MB a weight; these are ~50 KB). Anything outside the subset, a
 * Hindi or Japanese title, falls back to the system face glyph by glyph.
 */
val VeyraFontFamily = FontFamily(
    Font(R.font.veyra_regular, FontWeight.W400),
    Font(R.font.veyra_medium, FontWeight.W500),
    Font(R.font.veyra_semibold, FontWeight.W600),
    Font(R.font.veyra_bold, FontWeight.W700),
)

/** The phone's AccentRed: Love, and nothing much else. */
val AccentRed = Color(0xFFFA2D48)

/** Secondary text on the mesh and on black: the phone's onSurfaceVariant. */
val Secondary = Color(0xFFACA8B9)

/** Grouped cells on black, iOS's secondarySystemBackground in dark. */
val Cell = Color(0xFF1C1C1E)

object WearType {
    val title = TextStyle(fontFamily = VeyraFontFamily, fontWeight = FontWeight.W600, fontSize = 15.sp, letterSpacing = (-0.2).sp)
    val subtitle = TextStyle(fontFamily = VeyraFontFamily, fontWeight = FontWeight.W400, fontSize = 13.sp, letterSpacing = (-0.1).sp)
    val header = TextStyle(fontFamily = VeyraFontFamily, fontWeight = FontWeight.W700, fontSize = 17.sp, letterSpacing = (-0.3).sp)
    val row = TextStyle(fontFamily = VeyraFontFamily, fontWeight = FontWeight.W500, fontSize = 14.sp, letterSpacing = (-0.15).sp)
    val rowSub = TextStyle(fontFamily = VeyraFontFamily, fontWeight = FontWeight.W400, fontSize = 12.sp)
    val button = TextStyle(fontFamily = VeyraFontFamily, fontWeight = FontWeight.W600, fontSize = 14.sp)
    val caption = TextStyle(fontFamily = VeyraFontFamily, fontWeight = FontWeight.W500, fontSize = 11.sp, letterSpacing = 0.2.sp)
}

@Composable
fun VeyraWearTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme(
            primary = Color.White,
            onPrimary = Color.Black,
            background = Color.Black,
            onBackground = Color.White,
            onSurface = Color.White,
            onSurfaceVariant = Secondary,
            surfaceContainer = Cell,
        ),
        typography = Typography(defaultFontFamily = VeyraFontFamily),
        content = content,
    )
}
