package app.veyra.music.ui.styles

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import app.veyra.music.ui.themes.ThemeVisuals

/** Stored names are stable; downloadable themes are suspended, never overwritten, by a custom style. */
enum class AppStyle(val title: String) {
    ORIGINAL("Original"), MUSIC_EDITORIAL("Music Editorial"), LISTENING_ROOM("Listening Room"), AFTER_DARK("After Dark");
    companion object { fun fromStoredName(value:String?) = entries.firstOrNull {it.name==value} ?: ORIGINAL }
    val custom get() = this != ORIGINAL
    val dark get() = this != MUSIC_EDITORIAL
}
val LocalAppStyle = staticCompositionLocalOf { AppStyle.ORIGINAL }

fun AppStyle.visuals(assets:Map<String,String> = emptyMap()):ThemeVisuals? {
    if(!custom)return null
    val editorial=this==AppStyle.MUSIC_EDITORIAL
    val room=this==AppStyle.LISTENING_ROOM
    return ThemeVisuals("app-style-${name.lowercase()}",title,
        accent=if(editorial)Color(0xFF772D39) else if(room)Color(0xFFE8D4A2) else Color(0xFF9BCDFF),
        surface=if(editorial)Color(0xFFF5ECDD) else if(room)Color(0xFF193D2C) else Color(0xFF091729),
        raised=if(editorial)Color(0xFFE8DAC5) else if(room)Color(0xFF254A36) else Color(0xFF102641),
        outline=if(editorial)Color(0xFFAB947C) else if(room)Color(0xFF7D8C68) else Color(0xFF365C84),
        rowCorner=if(editorial)3 else 12,buttonCorner=if(editorial)8 else 18,navCorner=if(editorial)10 else 22,
        assets=if(room)assets else emptyMap(),background=if(editorial)Color(0xFFF9F1E4) else if(room)Color(0xFF10271C) else Color(0xFF040C17),
        text=if(editorial)Color(0xFF261A18) else if(room)Color(0xFFFFF2D6) else Color(0xFFDDEFFF),
        muted=if(editorial)Color(0xFF64554D) else if(room)Color(0xFFCBCCA9) else Color(0xFF9EB5D0),
        sceneStyle="app_${name.lowercase()}",tagline="")
}

/** All semantic roles follow the style, including sheets and disabled/secondary content. */
fun AppStyle.colorScheme():ColorScheme {
    val p=requireNotNull(visuals())
    val base=if(dark)darkColorScheme() else lightColorScheme()
    return base.copy(primary=p.accent,onPrimary=p.background,primaryContainer=p.raised,onPrimaryContainer=p.text,
        secondary=p.accent,onSecondary=p.background,secondaryContainer=p.raised,onSecondaryContainer=p.text,
        tertiary=p.accent,onTertiary=p.background,tertiaryContainer=p.raised,onTertiaryContainer=p.text,
        background=p.background,onBackground=p.text,surface=p.surface,onSurface=p.text,
        surfaceVariant=p.raised,onSurfaceVariant=p.muted,outline=p.outline,outlineVariant=p.outline.copy(alpha=.45f),
        surfaceTint=p.accent,surfaceBright=p.raised,surfaceDim=p.background,
        surfaceContainerLowest=p.background,surfaceContainerLow=p.surface,surfaceContainer=p.surface,
        surfaceContainerHigh=p.raised,surfaceContainerHighest=p.raised,inverseSurface=p.text,inverseOnSurface=p.background)
}

fun appStyleTypography(base:Typography, heading:androidx.compose.ui.text.font.FontFamily, body:androidx.compose.ui.text.font.FontFamily):Typography = base.copy(
    displayLarge=base.displayLarge.copy(fontFamily=heading),displayMedium=base.displayMedium.copy(fontFamily=heading),displaySmall=base.displaySmall.copy(fontFamily=heading),
    headlineLarge=base.headlineLarge.copy(fontFamily=heading),headlineMedium=base.headlineMedium.copy(fontFamily=heading),headlineSmall=base.headlineSmall.copy(fontFamily=heading),
    titleLarge=base.titleLarge.copy(fontFamily=body),titleMedium=base.titleMedium.copy(fontFamily=body),titleSmall=base.titleSmall.copy(fontFamily=body),
    bodyLarge=base.bodyLarge.copy(fontFamily=body),bodyMedium=base.bodyMedium.copy(fontFamily=body),bodySmall=base.bodySmall.copy(fontFamily=body),
    labelLarge=base.labelLarge.copy(fontFamily=body),labelMedium=base.labelMedium.copy(fontFamily=body),labelSmall=base.labelSmall.copy(fontFamily=body))

fun effectiveStyleVisuals(style:AppStyle, downloaded:ThemeVisuals?, assets:Map<String,String> = emptyMap()):ThemeVisuals? =
    if(style.custom)style.visuals(assets) else downloaded

@androidx.compose.runtime.Composable
fun rememberStyleHomeListState(): androidx.compose.foundation.lazy.LazyListState {
    val original=androidx.compose.foundation.lazy.rememberLazyListState()
    val editorial=androidx.compose.foundation.lazy.rememberLazyListState()
    val room=androidx.compose.foundation.lazy.rememberLazyListState()
    val night=androidx.compose.foundation.lazy.rememberLazyListState()
    return when(LocalAppStyle.current) {
        AppStyle.ORIGINAL->original;AppStyle.MUSIC_EDITORIAL->editorial;AppStyle.LISTENING_ROOM->room;AppStyle.AFTER_DARK->night
    }
}

/** Four navigation labels remain legible on a narrow bar; body and content text retain full system scaling. */
@androidx.compose.runtime.Composable
fun styleNavigationLabelStyle():androidx.compose.ui.text.TextStyle {
    val base=MaterialTheme.typography.labelMedium
    val scale=androidx.compose.ui.platform.LocalDensity.current.fontScale
    return if(LocalAppStyle.current.custom && scale>1.5f)base.copy(fontSize=base.fontSize*(1.5f/scale),lineHeight=base.lineHeight*(1.5f/scale)) else base
}
