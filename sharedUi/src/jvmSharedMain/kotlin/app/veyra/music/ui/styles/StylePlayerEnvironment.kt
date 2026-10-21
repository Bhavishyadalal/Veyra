package app.veyra.music.ui.styles

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import app.veyra.music.ui.themes.LocalThemeVisuals

/** The artwork-led player stays dark for cover/video contrast, using the same style's warm ink/cream palette. */
@Composable
fun StylePlayerEnvironment(content:@Composable ()->Unit) {
    if(LocalAppStyle.current!=AppStyle.MUSIC_EDITORIAL) {content();return}
    val pack=remember {requireNotNull(AppStyle.MUSIC_EDITORIAL.visuals()).copy(
        background=Color(0xFF211116),surface=Color(0xFF321B22),raised=Color(0xFF48232E),
        text=Color(0xFFFFF1DB),muted=Color(0xFFD5BDAC),accent=Color(0xFFF0D5B4),outline=Color(0xFF96726D))}
    CompositionLocalProvider(LocalThemeVisuals provides pack) {
        MaterialTheme(colorScheme=darkColorScheme(primary=pack.accent,onPrimary=pack.background,background=pack.background,
            onBackground=pack.text,surface=pack.surface,onSurface=pack.text,surfaceVariant=pack.raised,onSurfaceVariant=pack.muted,
            surfaceContainerLow=pack.surface,surfaceContainer=pack.surface,surfaceContainerHigh=pack.raised,
            surfaceContainerHighest=pack.raised,outline=pack.outline,secondary=pack.accent,onSecondary=pack.background,
            secondaryContainer=pack.raised,onSecondaryContainer=pack.text,tertiary=pack.accent,onTertiary=pack.background,
            tertiaryContainer=pack.raised,onTertiaryContainer=pack.text,primaryContainer=pack.raised,onPrimaryContainer=pack.text,
            surfaceContainerLowest=pack.background,surfaceBright=pack.raised,surfaceDim=pack.background),typography=MaterialTheme.typography,shapes=MaterialTheme.shapes,content=content)
    }
}
