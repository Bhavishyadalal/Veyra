package app.veyra.music.ui.theme

import app.veyra.music.ui.themes.over
import app.veyra.music.ui.customization.*
import app.veyra.music.ui.styles.*
import android.app.Activity
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.veyra.music.data.settings.AppSettings
import app.veyra.music.ui.components.LocalVeyraContrastControls
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import app.veyra.music.R
import app.veyra.music.ui.components.LocalVeyraWordmarkFont

// Apple Music's signature red. No longer the primary accent, but kept for the
// spots (Replay's rank badge) that want that specific red regardless of theme.
val AccentRed = Color(0xFFFA2D48)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFD9C4FF),
    onPrimary = Color(0xFF35204F),
    primaryContainer = Color(0xFF422D5C),
    onPrimaryContainer = Color(0xFFF0E3FF),
    secondary = Color(0xFFE9BEA8),
    onSecondary = Color(0xFF422B20),
    background = Color.Black,
    onBackground = Color.White,
    surface = Color(0xFF0D0D0F),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1C1C1E),
    onSurfaceVariant = Color(0xFFACA8B9),
    outline = Color(0xFF383443),
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF69408C),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF0E3FF),
    onPrimaryContainer = Color(0xFF35204F),
    secondary = Color(0xFF84523B),
    onSecondary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color(0xFFF7F7F9),
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFF2F2F7),
    onSurfaceVariant = Color(0xFF6E6E73),
    outline = Color(0xFFE5E5EA),
)

/**
 * Manrope, a bundled open font used for Veyra’s visual identity. Only the weights the
 * type scale actually asks for are bundled; Compose synthesises nothing, so a
 * missing weight would silently fall back to the nearest one shipped.
 */
val VeyraFontFamily = FontFamily(
    Font(R.font.veyra_regular, FontWeight.W400),
    Font(R.font.veyra_medium, FontWeight.W500),
    Font(R.font.veyra_semibold, FontWeight.W600),
    Font(R.font.veyra_bold, FontWeight.W700),
    Font(R.font.veyra_heavy, FontWeight.W800),
)

private val VeyraWordmarkFont = FontFamily(Font(R.font.super_malibu, FontWeight.Normal))

// Clear, compact type scale for Veyra.
private val VeyraTypography = Typography(
    displayLarge = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 34.sp, lineHeight = 42.sp, letterSpacing = (-0.8).sp),
    displayMedium = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = (-0.6).sp),
    displaySmall = TextStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 36.sp),
    headlineLarge = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 30.sp, lineHeight = 38.sp, letterSpacing = (-0.7).sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 32.sp, letterSpacing = (-0.4).sp),
    headlineSmall = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 30.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 28.sp, letterSpacing = (-0.3).sp),
    titleMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = (-0.2).sp),
    titleSmall = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 18.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 11.sp, lineHeight = 16.sp),
).withFamily(VeyraFontFamily)

private val StrongerTypography = VeyraTypography.copy(
    bodyLarge = VeyraTypography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
    bodyMedium = VeyraTypography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
    bodySmall = VeyraTypography.bodySmall.copy(fontWeight = FontWeight.Medium),
    titleMedium = VeyraTypography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
    titleSmall = VeyraTypography.titleSmall.copy(fontWeight = FontWeight.Bold),
)

/** Applies [family] to every style in the scale, so nothing is left on Roboto. */
internal fun Typography.withFamily(family: FontFamily) = Typography(
    displayLarge = displayLarge.copy(fontFamily = family),
    displayMedium = displayMedium.copy(fontFamily = family),
    displaySmall = displaySmall.copy(fontFamily = family),
    headlineLarge = headlineLarge.copy(fontFamily = family),
    headlineMedium = headlineMedium.copy(fontFamily = family),
    headlineSmall = headlineSmall.copy(fontFamily = family),
    titleLarge = titleLarge.copy(fontFamily = family),
    titleMedium = titleMedium.copy(fontFamily = family),
    titleSmall = titleSmall.copy(fontFamily = family),
    bodyLarge = bodyLarge.copy(fontFamily = family),
    bodyMedium = bodyMedium.copy(fontFamily = family),
    bodySmall = bodySmall.copy(fontFamily = family),
    labelLarge = labelLarge.copy(fontFamily = family),
    labelMedium = labelMedium.copy(fontFamily = family),
    labelSmall = labelSmall.copy(fontFamily = family),
)

internal fun defaultAppearanceTypography(strongerText: Boolean): Typography =
    if (strongerText) StrongerTypography else VeyraTypography

@Composable
fun VeyraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val appStyle by AppSettings.appStyle.collectAsStateWithLifecycle()
    val fontSelection = remember {AppSettings.experience.map {it.appFont}.distinctUntilChanged()}
    val importedHash by fontSelection.collectAsStateWithLifecycle(initialValue=AppSettings.experience.value.appFont)
    val assetPack by app.veyra.music.data.themes.AssetRepository.active.collectAsStateWithLifecycle()
    val assetOptions by app.veyra.music.data.themes.AssetRepository.options.collectAsStateWithLifecycle()
    val importedFont = app.veyra.music.data.settings.rememberImportedFont(if(appStyle.custom && (assetPack == null || !assetOptions.fonts)) "" else importedHash)
    val accent by AppSettings.accentPalette.collectAsStateWithLifecycle()
    val appearanceFont by AppSettings.appearanceFont.collectAsStateWithLifecycle()
    val themeArtwork by AppSettings.themeArtwork.collectAsStateWithLifecycle()
    val corners by AppSettings.artworkCorners.collectAsStateWithLifecycle()
    val spacing by AppSettings.listSpacing.collectAsStateWithLifecycle()
    val subtle by AppSettings.subtleMotion.collectAsStateWithLifecycle()
    val strongerText by AppSettings.strongerText.collectAsStateWithLifecycle()
    val contrastControls by AppSettings.contrastPlayerControls.collectAsStateWithLifecycle()
    val packState by app.veyra.music.data.themes.ThemeRepository.active.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    val styleAssets by androidx.compose.runtime.produceState<Map<String,String>>(emptyMap(),appStyle) {
        value = if (appStyle != AppStyle.LISTENING_ROOM) emptyMap() else kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            runCatching {
                val file=java.io.File(context.filesDir,"app-style-listening-room-v1.webp")
                if(!file.exists()) {
                    val staging=java.io.File(context.filesDir,"app-style-listening-room-v1.tmp")
                    context.assets.open("app-styles/listening-room.webp").use {input->staging.outputStream().use {input.copyTo(it)}}
                    check(staging.renameTo(file))
                }
                mapOf("header" to file.path)
            }.getOrDefault(emptyMap())
        }
    }
    val basePack = remember(appStyle,packState,styleAssets) { effectiveStyleVisuals(appStyle,packState,styleAssets) }
    val fallbackPalette = remember(appStyle,accent,darkTheme) {if(appStyle.custom) appStyle.colorScheme() else paletteFor(accent,darkTheme)}
    val pack = remember(basePack,assetPack,assetOptions,fallbackPalette) {
        if(assetPack == null) basePack else assetPack!!.over(basePack ?: app.veyra.music.ui.themes.ThemeVisuals(
            "@original","Original",fallbackPalette.primary,fallbackPalette.surface,fallbackPalette.surfaceContainerHigh,
            fallbackPalette.outline,20,24,32,emptyMap(),background=fallbackPalette.background,
            text=fallbackPalette.onSurface,muted=fallbackPalette.onSurfaceVariant,secondary=fallbackPalette.secondary),assetOptions)
    }
    // Keep fonts and palette on the same activation snapshot during live updates.
    val manifest = remember(packState,appStyle) { pack?.takeUnless {appStyle.custom}?.let { app.veyra.music.data.themes.ThemeRepository.activeManifest() } }
    val packFonts=app.veyra.music.data.themes.rememberPackFonts(pack)
    val selectedTypography = remember(pack, strongerText, appearanceFont, appStyle, importedFont, assetPack, assetOptions, packFonts) {
        val base = if (strongerText) StrongerTypography else VeyraTypography
        if (assetPack != null && importedFont != null) base.withFamily(importedFont)
        else if (assetPack != null && assetOptions.fonts) runCatching {appearanceTypography(base,appearanceFont,pack,packFonts)}.getOrDefault(base)
        else if (appStyle.custom) {
            val heading = if(appStyle==AppStyle.AFTER_DARK) FontFamily(Font(R.font.appearance_barlow_condensed,FontWeight.Bold))
                else FontFamily(Font(R.font.app_style_serif,FontWeight.Bold))
            appStyleTypography(base, heading, FontFamily(Font(R.font.appearance_manrope)))
        } else if (importedFont != null) base.withFamily(importedFont)
        else runCatching { appearanceTypography(base, appearanceFont, pack, packFonts) }.getOrDefault(base)
    }
    val colorScheme = remember(appStyle, manifest, accent, darkTheme) {
        if (appStyle.custom) appStyle.colorScheme() else manifest?.let { packPalette(it.palette) } ?: paletteFor(accent, darkTheme)
    }
    val shapes = remember(pack?.materialStyle, pack?.sceneStyle, pack?.buttonCorner, pack?.rowCorner, pack?.navCorner) {
        if (pack == null) androidx.compose.material3.Shapes()
        // Keep large lists/cards on native rounded outlines. The pack-specific
        // cut/sketch outline belongs to actual controls, not every container.
        else androidx.compose.material3.Shapes(
            small = androidx.compose.foundation.shape.RoundedCornerShape(androidx.compose.ui.unit.Dp(pack.buttonCorner.toFloat())),
            medium = androidx.compose.foundation.shape.RoundedCornerShape(androidx.compose.ui.unit.Dp(pack.rowCorner.toFloat())),
            large = androidx.compose.foundation.shape.RoundedCornerShape(androidx.compose.ui.unit.Dp(pack.buttonCorner.toFloat())),
            extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(androidx.compose.ui.unit.Dp(pack.navCorner.toFloat())))
    }
    CompositionLocalProvider(
        app.veyra.music.ui.themes.LocalThemeArtworkOptions provides
            (if (appStyle.custom) app.veyra.music.ui.themes.ThemeArtworkOptions() else themeArtwork),
        LocalAppStyle provides appStyle,
        app.veyra.music.ui.themes.LocalThemeVisuals provides pack,
        LocalArtworkCorners provides (if(appStyle.custom) ArtworkCorners.DEFAULT else corners),
        LocalListSpacing provides spacing,
        LocalSubtleMotion provides subtle,
        LocalVeyraWordmarkFont provides (if (pack?.asset("brandIcon") != null) selectedTypography.headlineLarge.fontFamily else VeyraWordmarkFont),
        LocalVeyraContrastControls provides contrastControls,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = if(pack?.headingColor != null || pack?.typeStyle == "punk") selectedTypography.copy(
                displayLarge=selectedTypography.displayLarge.copy(color=pack.headingColor ?: pack.accent),
                displayMedium=selectedTypography.displayMedium.copy(color=pack.headingColor ?: pack.secondary),
                displaySmall=selectedTypography.displaySmall.copy(color=pack.headingColor ?: pack.accent),
                headlineLarge=selectedTypography.headlineLarge.copy(color=pack.headingColor ?: pack.accent),
                headlineMedium=selectedTypography.headlineMedium.copy(color=pack.headingColor ?: pack.secondary),
                headlineSmall=selectedTypography.headlineSmall.copy(color=pack.headingColor ?: pack.accent)) else selectedTypography,
            shapes = shapes,
            content = content,
        )
    }
}

/**
 * Draws the status and navigation bar glyphs dark or light.
 *
 * `enableEdgeToEdge()` decides this from the *system* dark-mode setting, which
 * is the wrong input the moment the in-app theme disagrees with it: Light theme
 * on a phone in dark mode left white icons on a white bar, invisible. The bars
 * have to follow the content the app is actually painting. The player supplies
 * its own stable light-icon value and paints contrast behind it; every other
 * surface follows the theme. Hence a parameter rather than reading it here.
 */
@Composable
fun SystemBarIcons(dark: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return
    val window = findWindow(view) ?: return
    SideEffect {
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = dark
            isAppearanceLightNavigationBars = dark
        }
    }
}

/** Dark/light foregrounds are chosen together with their accent containers. */
private fun paletteFor(accent: AccentPalette, dark: Boolean): androidx.compose.material3.ColorScheme {
    val base = if (dark) DarkColors else LightColors
    if (accent == AccentPalette.DEFAULT) return base
    val pair = when (accent) {
        AccentPalette.TEAL -> Color(0xFF9BE1D5) to Color(0xFF005047)
        AccentPalette.AMBER -> Color(0xFFFFD58A) to Color(0xFF654000)
        AccentPalette.ROSE -> Color(0xFFFFB1C4) to Color(0xFF7B2947)
        AccentPalette.DEFAULT -> return base
    }
    return base.copy(primary = if (dark) pair.first else pair.second,
        onPrimary = if (dark) Color(0xFF101715) else Color.White,
        primaryContainer = if (dark) pair.second else pair.first,
        onPrimaryContainer = if (dark) Color.White else Color(0xFF171313))
}

/** Halloween is an intentionally dark art direction, independent of the saved base theme. */
private fun packPalette(p: app.veyra.music.data.themes.ThemePalette): androidx.compose.material3.ColorScheme {
    fun c(value:String) = app.veyra.music.data.themes.ThemeRepository.color(value)
    return darkColorScheme(primary=c(p.accent),onPrimary=Color(0xFF101010),
        primaryContainer=c(p.accentContainer),onPrimaryContainer=c(p.text),
        secondary=c(p.secondary),onSecondary=Color(0xFF101010),secondaryContainer=c(p.raised),onSecondaryContainer=c(p.text),
        tertiary=c(p.accent),onTertiary=Color(0xFF101010),tertiaryContainer=c(p.raised),onTertiaryContainer=c(p.text),
        background=c(p.background),onBackground=c(p.text),surface=c(p.surface),onSurface=c(p.text),
        surfaceVariant=c(p.raised),onSurfaceVariant=c(p.muted),surfaceTint=c(p.accent),
        outline=c(p.outline),outlineVariant=c(p.outline),inverseSurface=c(p.text),inverseOnSurface=c(p.background),
        inversePrimary=c(p.accentContainer),surfaceBright=c(p.raised),surfaceDim=c(p.background),
        surfaceContainerLowest=c(p.background),surfaceContainerLow=c(p.surface),surfaceContainer=c(p.surface),
        surfaceContainerHigh=c(p.raised),surfaceContainerHighest=c(p.raised))
}
