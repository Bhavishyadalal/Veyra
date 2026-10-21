package app.veyra.music.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import app.veyra.music.R
import app.veyra.music.data.themes.AppearanceFont
import app.veyra.music.ui.themes.ThemeVisuals
import java.io.File

/** The picker previews exactly the same heading/body pair used by the app. */
internal fun appearanceTypography(base: Typography, choice: AppearanceFont, pack: ThemeVisuals?): Typography {
    val resolved = choice.resolved(pack != null)
    if (resolved == AppearanceFont.VEYRA_DEFAULT) return base
    val (heading, body) = if (resolved == AppearanceFont.FOLLOW_THEME) {
        val theme = requireNotNull(pack)
        FontFamily(Font(File(theme.assets.getValue("headingFont")), FontWeight.Bold)) to
            FontFamily(Font(File(theme.assets.getValue("bodyFont"))))
    } else {
        val (resource, weight) = when (resolved) {
            AppearanceFont.FREDOKA -> R.font.appearance_fredoka to FontWeight(650)
            AppearanceFont.SPACE_GROTESK -> R.font.appearance_space_grotesk to FontWeight.Bold
            AppearanceFont.RAJDHANI -> R.font.appearance_rajdhani to FontWeight.Bold
            AppearanceFont.BARLOW_CONDENSED -> R.font.appearance_barlow_condensed to FontWeight.Bold
            AppearanceFont.GRENZE_GOTISCH -> R.font.appearance_grenze_gotisch to FontWeight.Normal
            else -> R.font.appearance_manrope to FontWeight.Normal
        }
        FontFamily(Font(resource, weight)) to FontFamily(Font(R.font.appearance_manrope))
    }
    val t = base.copy(
        bodyLarge=base.bodyLarge.copy(fontFamily=body), bodyMedium=base.bodyMedium.copy(fontFamily=body),
        bodySmall=base.bodySmall.copy(fontFamily=body), titleLarge=base.titleLarge.copy(fontFamily=body),
        titleMedium=base.titleMedium.copy(fontFamily=body), titleSmall=base.titleSmall.copy(fontFamily=body),
        labelLarge=base.labelLarge.copy(fontFamily=body), labelMedium=base.labelMedium.copy(fontFamily=body),
        labelSmall=base.labelSmall.copy(fontFamily=body),
    )
    return t.copy(displayLarge=t.displayLarge.copy(fontFamily=heading), displayMedium=t.displayMedium.copy(fontFamily=heading),
        displaySmall=t.displaySmall.copy(fontFamily=heading), headlineLarge=t.headlineLarge.copy(fontFamily=heading),
        headlineMedium=t.headlineMedium.copy(fontFamily=heading), headlineSmall=t.headlineSmall.copy(fontFamily=heading))
}
