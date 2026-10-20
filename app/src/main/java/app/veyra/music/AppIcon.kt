package app.veyra.music

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

/**
 * The home-screen icon choices. Each is an `<activity-alias>` in the manifest
 * carrying the launcher intent filter; exactly one is enabled at a time.
 *
 * The glass icons are adaptive icons whose foreground already holds the finished
 * icon in its own shape (Apple squircle, Samsung squircle or Pixel circle), so
 * [previewRes] — that foreground — shows the variant as designed rather than
 * through the device's launcher mask. The first two keep yesterday's alias
 * names so a choice made before the update stays enabled.
 */
enum class AppIcon(val alias: String, val iconRes: Int, val labelRes: Int, val previewRes: Int? = null) {
    WebSlinger("LauncherWebSlinger", R.mipmap.ic_theme_web, R.string.app_icon_web_slinger, R.mipmap.ic_theme_web_foreground),
    HalloweenNocturne("LauncherHalloween", R.mipmap.ic_theme_halloween, R.string.app_icon_halloween, R.mipmap.ic_theme_halloween_foreground),
    Default("LauncherDefault", R.mipmap.ic_wave_glass, R.string.app_icon_wave_default, R.mipmap.ic_wave_glass_foreground),
    WhiteFrost("LauncherWaveFrost", R.mipmap.ic_wave_frost, R.string.app_icon_wave_frost, R.mipmap.ic_wave_frost_foreground),
    GlassOrbit("LauncherWaveOrbit", R.mipmap.ic_wave_orbit, R.string.app_icon_wave_orbit, R.mipmap.ic_wave_orbit_foreground),
    Original("LauncherOriginal", R.mipmap.ic_launcher, R.string.app_icon_original),
    LiquidGlass("LauncherGlassLight", R.mipmap.ic_liquid_glass, R.string.app_icon_liquid_glass, R.mipmap.ic_liquid_glass_foreground),
    LiquidGlassDark("LauncherGlassDark", R.mipmap.ic_liquid_glass_dark, R.string.app_icon_liquid_glass_dark, R.mipmap.ic_liquid_glass_dark_foreground),
    LiquidGlassClear("LauncherGlassClear", R.mipmap.ic_liquid_glass_clear, R.string.app_icon_liquid_glass_clear, R.mipmap.ic_liquid_glass_clear_foreground),
    LiquidGlassClearDark("LauncherGlassClearDark", R.mipmap.ic_liquid_glass_clear_dark, R.string.app_icon_liquid_glass_clear_dark, R.mipmap.ic_liquid_glass_clear_dark_foreground),
    SamsungGlass("LauncherSamsungGlass", R.mipmap.ic_samsung_glass, R.string.app_icon_samsung_glass, R.mipmap.ic_samsung_glass_foreground),
    SamsungGlassDark("LauncherSamsungGlassDark", R.mipmap.ic_samsung_glass_dark, R.string.app_icon_samsung_glass_dark, R.mipmap.ic_samsung_glass_dark_foreground),
    SamsungGlassClear("LauncherSamsungGlassClear", R.mipmap.ic_samsung_glass_clear, R.string.app_icon_samsung_glass_clear, R.mipmap.ic_samsung_glass_clear_foreground),
    SamsungGlassClearDark("LauncherSamsungGlassClearDark", R.mipmap.ic_samsung_glass_clear_dark, R.string.app_icon_samsung_glass_clear_dark, R.mipmap.ic_samsung_glass_clear_dark_foreground),
    PixelGlass("LauncherPixelGlass", R.mipmap.ic_pixel_glass, R.string.app_icon_pixel_glass, R.mipmap.ic_pixel_glass_foreground),
    PixelGlassDark("LauncherPixelGlassDark", R.mipmap.ic_pixel_glass_dark, R.string.app_icon_pixel_glass_dark, R.mipmap.ic_pixel_glass_dark_foreground),
    PixelGlassClear("LauncherPixelGlassClear", R.mipmap.ic_pixel_glass_clear, R.string.app_icon_pixel_glass_clear, R.mipmap.ic_pixel_glass_clear_foreground),
    PixelGlassClearDark("LauncherPixelGlassClearDark", R.mipmap.ic_pixel_glass_clear_dark, R.string.app_icon_pixel_glass_clear_dark, R.mipmap.ic_pixel_glass_clear_dark_foreground);

    private fun component(context: Context) =
        ComponentName(context.packageName, "app.veyra.music.$alias")

    companion object {
        /** A data-only theme cannot supply a component name or an arbitrary launcher alias. */
        fun forTheme(id: String?): AppIcon? = when (id) {
            "web-slinger" -> WebSlinger
            "halloween-nocturne" -> HalloweenNocturne
            else -> null
        }

        @Synchronized
        fun current(context: Context): AppIcon {
            val pm = context.packageManager
            return entries.firstOrNull {
                val state = pm.getComponentEnabledSetting(it.component(context))
                state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED ||
                    (state == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT && it == Default)
            } ?: Default
        }

        /** Atomic on Android 13+, and enable-first on older Android versions. */
        @Synchronized
        fun apply(context: Context, icon: AppIcon) {
            val pm = context.packageManager
            if (android.os.Build.VERSION.SDK_INT >= 33) {
                pm.setComponentEnabledSettings(entries.map {
                    PackageManager.ComponentEnabledSetting(
                        it.component(context),
                        if (it == icon) PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                        else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP,
                    )
                })
            } else {
                val previous = current(context)
                try {
                    pm.setComponentEnabledSetting(icon.component(context), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
                    entries.filter { it != icon }.forEach {
                        pm.setComponentEnabledSetting(it.component(context), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                    }
                } catch (error: Exception) {
                    // Best effort rollback, with the previously working launcher enabled first.
                    runCatching { pm.setComponentEnabledSetting(previous.component(context), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP) }
                    if (icon != previous) runCatching { pm.setComponentEnabledSetting(icon.component(context), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP) }
                    throw error
                }
            }
        }
    }
}
