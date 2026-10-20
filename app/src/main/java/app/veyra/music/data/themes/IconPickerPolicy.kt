package app.veyra.music.data.themes

import app.veyra.music.AppIcon

enum class IconCollection { VEYRA, GLASS, THEMES, ATELIER }
enum class GlassFamily { LIQUID, SAMSUNG, PIXEL }

/** Every existing icon belongs to one small, browsable collection. */
object IconPickerPolicy {
    fun collection(icon: AppIcon): IconCollection = when {
        icon.name.startsWith("Atelier") -> IconCollection.ATELIER
        icon == AppIcon.GoldenAfterglow || icon == AppIcon.LilypondParade || icon == AppIcon.DustbyteOutpost || icon == AppIcon.AlpineEmber || icon == AppIcon.StarfallPals || icon == AppIcon.PumpkinPurr || icon == AppIcon.SunsetCrew || icon == AppIcon.StarrySpirits || icon == AppIcon.AnalogReverie || icon == AppIcon.PetalPromise || icon == AppIcon.MoonlitHerbarium || icon == AppIcon.MidnightExpress || icon == AppIcon.AstralTide || icon == AppIcon.PlaytimeClub || icon == AppIcon.WebSlinger || icon == AppIcon.HalloweenNocturne -> IconCollection.THEMES
        family(icon) != null -> IconCollection.GLASS
        else -> IconCollection.VEYRA
    }
    fun family(icon: AppIcon): GlassFamily? = when {
        icon.name.startsWith("LiquidGlass") -> GlassFamily.LIQUID
        icon.name.startsWith("SamsungGlass") -> GlassFamily.SAMSUNG
        icon.name.startsWith("PixelGlass") -> GlassFamily.PIXEL
        else -> null
    }
    fun options(collection: IconCollection, family: GlassFamily): List<AppIcon> = AppIcon.entries.filter {
        collection(it) == collection && (collection != IconCollection.GLASS || family(it) == family)
    }
}
