package app.veyra.music.data.themes

import app.veyra.music.AppIcon

enum class IconCollection { ALL, VEYRA, GLASS, THEMES, ATELIER, ASSETS }
enum class GlassFamily { LIQUID, SAMSUNG, PIXEL }

/** Every existing icon belongs to one small, browsable collection. */
object IconPickerPolicy {
    fun collection(icon: AppIcon): IconCollection = when {
        icon == AppIcon.CollectionCathedralStatic -> IconCollection.ASSETS
        icon == AppIcon.CollectionVelvetHex -> IconCollection.ASSETS
        icon == AppIcon.CollectionRazorBloom -> IconCollection.ASSETS
        icon == AppIcon.CollectionNeonSeance -> IconCollection.ASSETS
        icon == AppIcon.CollectionBlackoutZine -> IconCollection.ASSETS
        icon == AppIcon.CollectionChromeFang -> IconCollection.ASSETS
        icon == AppIcon.CollectionCandyCrypt -> IconCollection.ASSETS
        icon == AppIcon.CollectionRoyalRuin -> IconCollection.ASSETS
        icon == AppIcon.CollectionAcidGargoyle -> IconCollection.ASSETS
        icon == AppIcon.CollectionMidnightStitch -> IconCollection.ASSETS
        icon == AppIcon.CollectionRiotCandy -> IconCollection.THEMES
        icon == AppIcon.CollectionChromeVenom -> IconCollection.THEMES
        icon == AppIcon.CollectionBubbleRiot -> IconCollection.THEMES
        icon == AppIcon.CollectionXeroxAfterparty -> IconCollection.THEMES
        icon == AppIcon.CollectionCosmicVandal -> IconCollection.THEMES
        icon == AppIcon.CollectionSwitchbackRadio -> IconCollection.THEMES
        icon == AppIcon.CollectionKomorebiHour -> IconCollection.THEMES
        icon == AppIcon.CollectionRooftopReverie -> IconCollection.THEMES
        icon == AppIcon.CollectionApexTelemetry -> IconCollection.THEMES
        icon == AppIcon.CollectionCinderKata -> IconCollection.THEMES
        icon == AppIcon.CollectionLanternLetters -> IconCollection.THEMES
        icon == AppIcon.CollectionVelvetOrbit -> IconCollection.THEMES
        icon == AppIcon.CollectionMossMechanism -> IconCollection.THEMES
        icon == AppIcon.CollectionSugarStatic -> IconCollection.THEMES
        icon == AppIcon.CollectionTidalOrigami -> IconCollection.THEMES
        icon == AppIcon.CollectionMarginNotes -> IconCollection.THEMES
        icon == AppIcon.CollectionSprayParade -> IconCollection.ASSETS
        icon == AppIcon.CollectionPaperPop -> IconCollection.ASSETS
        icon == AppIcon.CollectionInkRebel -> IconCollection.ASSETS
        icon == AppIcon.CollectionLaserClub -> IconCollection.ASSETS
        icon == AppIcon.CollectionThreadRiot -> IconCollection.ASSETS

        icon.name.startsWith("Atelier") -> IconCollection.ATELIER
        icon in setOf(AppIcon.PatchworkPicnic, AppIcon.CloverRequiem, AppIcon.SunroomMixtape,
            AppIcon.ConcreteAfterhours, AppIcon.MatchaMischief) -> IconCollection.THEMES
        icon == AppIcon.StainedGlassSonata || icon == AppIcon.FireflyRailway || icon == AppIcon.PorcelainMoon || icon == AppIcon.BlackHoney || icon == AppIcon.SakuraArcade || icon == AppIcon.CloudPostOffice || icon == AppIcon.CopperCanyonRadio || icon == AppIcon.MidnightAquarium || icon == AppIcon.SundayBakery || icon == AppIcon.RoseCircuit || icon == AppIcon.ClockworkGarden || icon == AppIcon.SnowglobeVillage || icon == AppIcon.IndigoAtelier || icon == AppIcon.PrismPlayground || icon == AppIcon.SilentCinema || icon == AppIcon.RiversidePause || icon == AppIcon.PixelStargazer || icon == AppIcon.RoamingCosmos || icon == AppIcon.BlueHourSanctuary || icon == AppIcon.CelestialCurrent || icon == AppIcon.ChromeVesper || icon == AppIcon.WindowWish || icon == AppIcon.CassetteCorner || icon == AppIcon.AzureBloom || icon == AppIcon.StrawberryDaydream || icon == AppIcon.GoldenAfterglow || icon == AppIcon.LilypondParade || icon == AppIcon.DustbyteOutpost || icon == AppIcon.AlpineEmber || icon == AppIcon.StarfallPals || icon == AppIcon.PumpkinPurr || icon == AppIcon.SunsetCrew || icon == AppIcon.StarrySpirits || icon == AppIcon.AnalogReverie || icon == AppIcon.PetalPromise || icon == AppIcon.MoonlitHerbarium || icon == AppIcon.MidnightExpress || icon == AppIcon.AstralTide || icon == AppIcon.PlaytimeClub || icon == AppIcon.WebSlinger || icon == AppIcon.HalloweenNocturne -> IconCollection.THEMES
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
        collection == IconCollection.ALL ||
            (collection(it) == collection && (collection != IconCollection.GLASS || family(it) == family))
    }
}
