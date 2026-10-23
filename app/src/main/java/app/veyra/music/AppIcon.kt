package app.veyra.music

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import kotlinx.coroutines.flow.asStateFlow

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
    CollectionCathedralStatic("LauncherCollectionCathedralStatic", R.mipmap.ic_collection_cathedral_static, R.string.app_icon_collection_cathedral_static, R.mipmap.ic_collection_cathedral_static_foreground),
    CollectionVelvetHex("LauncherCollectionVelvetHex", R.mipmap.ic_collection_velvet_hex, R.string.app_icon_collection_velvet_hex, R.mipmap.ic_collection_velvet_hex_foreground),
    CollectionRazorBloom("LauncherCollectionRazorBloom", R.mipmap.ic_collection_razor_bloom, R.string.app_icon_collection_razor_bloom, R.mipmap.ic_collection_razor_bloom_foreground),
    CollectionNeonSeance("LauncherCollectionNeonSeance", R.mipmap.ic_collection_neon_seance, R.string.app_icon_collection_neon_seance, R.mipmap.ic_collection_neon_seance_foreground),
    CollectionBlackoutZine("LauncherCollectionBlackoutZine", R.mipmap.ic_collection_blackout_zine, R.string.app_icon_collection_blackout_zine, R.mipmap.ic_collection_blackout_zine_foreground),
    CollectionChromeFang("LauncherCollectionChromeFang", R.mipmap.ic_collection_chrome_fang, R.string.app_icon_collection_chrome_fang, R.mipmap.ic_collection_chrome_fang_foreground),
    CollectionCandyCrypt("LauncherCollectionCandyCrypt", R.mipmap.ic_collection_candy_crypt, R.string.app_icon_collection_candy_crypt, R.mipmap.ic_collection_candy_crypt_foreground),
    CollectionRoyalRuin("LauncherCollectionRoyalRuin", R.mipmap.ic_collection_royal_ruin, R.string.app_icon_collection_royal_ruin, R.mipmap.ic_collection_royal_ruin_foreground),
    CollectionAcidGargoyle("LauncherCollectionAcidGargoyle", R.mipmap.ic_collection_acid_gargoyle, R.string.app_icon_collection_acid_gargoyle, R.mipmap.ic_collection_acid_gargoyle_foreground),
    CollectionMidnightStitch("LauncherCollectionMidnightStitch", R.mipmap.ic_collection_midnight_stitch, R.string.app_icon_collection_midnight_stitch, R.mipmap.ic_collection_midnight_stitch_foreground),
    CollectionRiotCandy("LauncherCollectionRiotCandy", R.mipmap.ic_collection_riot_candy, R.string.app_icon_collection_riot_candy, R.mipmap.ic_collection_riot_candy_foreground),
    CollectionChromeVenom("LauncherCollectionChromeVenom", R.mipmap.ic_collection_chrome_venom, R.string.app_icon_collection_chrome_venom, R.mipmap.ic_collection_chrome_venom_foreground),
    CollectionBubbleRiot("LauncherCollectionBubbleRiot", R.mipmap.ic_collection_bubble_riot, R.string.app_icon_collection_bubble_riot, R.mipmap.ic_collection_bubble_riot_foreground),
    CollectionXeroxAfterparty("LauncherCollectionXeroxAfterparty", R.mipmap.ic_collection_xerox_afterparty, R.string.app_icon_collection_xerox_afterparty, R.mipmap.ic_collection_xerox_afterparty_foreground),
    CollectionCosmicVandal("LauncherCollectionCosmicVandal", R.mipmap.ic_collection_cosmic_vandal, R.string.app_icon_collection_cosmic_vandal, R.mipmap.ic_collection_cosmic_vandal_foreground),
    CollectionSwitchbackRadio("LauncherCollectionSwitchbackRadio", R.mipmap.ic_collection_switchback_radio, R.string.app_icon_collection_switchback_radio, R.mipmap.ic_collection_switchback_radio_foreground),
    CollectionKomorebiHour("LauncherCollectionKomorebiHour", R.mipmap.ic_collection_komorebi_hour, R.string.app_icon_collection_komorebi_hour, R.mipmap.ic_collection_komorebi_hour_foreground),
    CollectionRooftopReverie("LauncherCollectionRooftopReverie", R.mipmap.ic_collection_rooftop_reverie, R.string.app_icon_collection_rooftop_reverie, R.mipmap.ic_collection_rooftop_reverie_foreground),
    CollectionApexTelemetry("LauncherCollectionApexTelemetry", R.mipmap.ic_collection_apex_telemetry, R.string.app_icon_collection_apex_telemetry, R.mipmap.ic_collection_apex_telemetry_foreground),
    CollectionCinderKata("LauncherCollectionCinderKata", R.mipmap.ic_collection_cinder_kata, R.string.app_icon_collection_cinder_kata, R.mipmap.ic_collection_cinder_kata_foreground),
    CollectionLanternLetters("LauncherCollectionLanternLetters", R.mipmap.ic_collection_lantern_letters, R.string.app_icon_collection_lantern_letters, R.mipmap.ic_collection_lantern_letters_foreground),
    CollectionVelvetOrbit("LauncherCollectionVelvetOrbit", R.mipmap.ic_collection_velvet_orbit, R.string.app_icon_collection_velvet_orbit, R.mipmap.ic_collection_velvet_orbit_foreground),
    CollectionMossMechanism("LauncherCollectionMossMechanism", R.mipmap.ic_collection_moss_mechanism, R.string.app_icon_collection_moss_mechanism, R.mipmap.ic_collection_moss_mechanism_foreground),
    CollectionSugarStatic("LauncherCollectionSugarStatic", R.mipmap.ic_collection_sugar_static, R.string.app_icon_collection_sugar_static, R.mipmap.ic_collection_sugar_static_foreground),
    CollectionTidalOrigami("LauncherCollectionTidalOrigami", R.mipmap.ic_collection_tidal_origami, R.string.app_icon_collection_tidal_origami, R.mipmap.ic_collection_tidal_origami_foreground),
    CollectionMarginNotes("LauncherCollectionMarginNotes", R.mipmap.ic_collection_margin_notes, R.string.app_icon_collection_margin_notes, R.mipmap.ic_collection_margin_notes_foreground),
    CollectionSprayParade("LauncherCollectionSprayParade", R.mipmap.ic_collection_spray_parade, R.string.app_icon_collection_spray_parade, R.mipmap.ic_collection_spray_parade_foreground),
    CollectionPaperPop("LauncherCollectionPaperPop", R.mipmap.ic_collection_paper_pop, R.string.app_icon_collection_paper_pop, R.mipmap.ic_collection_paper_pop_foreground),
    CollectionInkRebel("LauncherCollectionInkRebel", R.mipmap.ic_collection_ink_rebel, R.string.app_icon_collection_ink_rebel, R.mipmap.ic_collection_ink_rebel_foreground),
    CollectionLaserClub("LauncherCollectionLaserClub", R.mipmap.ic_collection_laser_club, R.string.app_icon_collection_laser_club, R.mipmap.ic_collection_laser_club_foreground),
    CollectionThreadRiot("LauncherCollectionThreadRiot", R.mipmap.ic_collection_thread_riot, R.string.app_icon_collection_thread_riot, R.mipmap.ic_collection_thread_riot_foreground),
    PatchworkPicnic("LauncherPatchworkPicnic", R.mipmap.ic_theme_patchworkpicnic, R.string.app_icon_patchworkpicnic, R.mipmap.ic_theme_patchworkpicnic_foreground),
    CloverRequiem("LauncherCloverRequiem", R.mipmap.ic_theme_cloverrequiem, R.string.app_icon_cloverrequiem, R.mipmap.ic_theme_cloverrequiem_foreground),
    SunroomMixtape("LauncherSunroomMixtape", R.mipmap.ic_theme_sunroommixtape, R.string.app_icon_sunroommixtape, R.mipmap.ic_theme_sunroommixtape_foreground),
    ConcreteAfterhours("LauncherConcreteAfterhours", R.mipmap.ic_theme_concreteafterhours, R.string.app_icon_concreteafterhours, R.mipmap.ic_theme_concreteafterhours_foreground),
    MatchaMischief("LauncherMatchaMischief", R.mipmap.ic_theme_matchamischief, R.string.app_icon_matchamischief, R.mipmap.ic_theme_matchamischief_foreground),
    AtelierAuroraVeil("LauncherAtelierAuroraVeil", R.mipmap.ic_atelier_aurora_veil, R.string.app_icon_atelier_aurora_veil, R.mipmap.ic_atelier_aurora_veil_foreground),
    AtelierSakuraPearl("LauncherAtelierSakuraPearl", R.mipmap.ic_atelier_sakura_pearl, R.string.app_icon_atelier_sakura_pearl, R.mipmap.ic_atelier_sakura_pearl_foreground),
    AtelierObsidianGold("LauncherAtelierObsidianGold", R.mipmap.ic_atelier_obsidian_gold, R.string.app_icon_atelier_obsidian_gold, R.mipmap.ic_atelier_obsidian_gold_foreground),
    AtelierGlacierBlue("LauncherAtelierGlacierBlue", R.mipmap.ic_atelier_glacier_blue, R.string.app_icon_atelier_glacier_blue, R.mipmap.ic_atelier_glacier_blue_foreground),
    AtelierRubyVelvet("LauncherAtelierRubyVelvet", R.mipmap.ic_atelier_ruby_velvet, R.string.app_icon_atelier_ruby_velvet, R.mipmap.ic_atelier_ruby_velvet_foreground),
    AtelierEmeraldDew("LauncherAtelierEmeraldDew", R.mipmap.ic_atelier_emerald_dew, R.string.app_icon_atelier_emerald_dew, R.mipmap.ic_atelier_emerald_dew_foreground),
    AtelierLunarEclipse("LauncherAtelierLunarEclipse", R.mipmap.ic_atelier_lunar_eclipse, R.string.app_icon_atelier_lunar_eclipse, R.mipmap.ic_atelier_lunar_eclipse_foreground),
    AtelierCosmicPrism("LauncherAtelierCosmicPrism", R.mipmap.ic_atelier_cosmic_prism, R.string.app_icon_atelier_cosmic_prism, R.mipmap.ic_atelier_cosmic_prism_foreground),
    AtelierRoseQuartz("LauncherAtelierRoseQuartz", R.mipmap.ic_atelier_rose_quartz, R.string.app_icon_atelier_rose_quartz, R.mipmap.ic_atelier_rose_quartz_foreground),
    AtelierEmberForge("LauncherAtelierEmberForge", R.mipmap.ic_atelier_ember_forge, R.string.app_icon_atelier_ember_forge, R.mipmap.ic_atelier_ember_forge_foreground),
    AtelierOceanOpal("LauncherAtelierOceanOpal", R.mipmap.ic_atelier_ocean_opal, R.string.app_icon_atelier_ocean_opal, R.mipmap.ic_atelier_ocean_opal_foreground),
    AtelierRoyalSapphire("LauncherAtelierRoyalSapphire", R.mipmap.ic_atelier_royal_sapphire, R.string.app_icon_atelier_royal_sapphire, R.mipmap.ic_atelier_royal_sapphire_foreground),
    StainedGlassSonata("LauncherWorldStainedGlassSonata", R.mipmap.ic_theme_worldstainedglasssonata, R.string.app_icon_worldstainedglasssonata, R.mipmap.ic_theme_worldstainedglasssonata_foreground),
    FireflyRailway("LauncherWorldFireflyRailway", R.mipmap.ic_theme_worldfireflyrailway, R.string.app_icon_worldfireflyrailway, R.mipmap.ic_theme_worldfireflyrailway_foreground),
    PorcelainMoon("LauncherWorldPorcelainMoon", R.mipmap.ic_theme_worldporcelainmoon, R.string.app_icon_worldporcelainmoon, R.mipmap.ic_theme_worldporcelainmoon_foreground),
    BlackHoney("LauncherWorldBlackHoney", R.mipmap.ic_theme_worldblackhoney, R.string.app_icon_worldblackhoney, R.mipmap.ic_theme_worldblackhoney_foreground),
    SakuraArcade("LauncherWorldSakuraArcade", R.mipmap.ic_theme_worldsakuraarcade, R.string.app_icon_worldsakuraarcade, R.mipmap.ic_theme_worldsakuraarcade_foreground),
    CloudPostOffice("LauncherWorldCloudPostOffice", R.mipmap.ic_theme_worldcloudpostoffice, R.string.app_icon_worldcloudpostoffice, R.mipmap.ic_theme_worldcloudpostoffice_foreground),
    CopperCanyonRadio("LauncherWorldCopperCanyonRadio", R.mipmap.ic_theme_worldcoppercanyonradio, R.string.app_icon_worldcoppercanyonradio, R.mipmap.ic_theme_worldcoppercanyonradio_foreground),
    MidnightAquarium("LauncherWorldMidnightAquarium", R.mipmap.ic_theme_worldmidnightaquarium, R.string.app_icon_worldmidnightaquarium, R.mipmap.ic_theme_worldmidnightaquarium_foreground),
    SundayBakery("LauncherWorldSundayBakery", R.mipmap.ic_theme_worldsundaybakery, R.string.app_icon_worldsundaybakery, R.mipmap.ic_theme_worldsundaybakery_foreground),
    RoseCircuit("LauncherWorldRoseCircuit", R.mipmap.ic_theme_worldrosecircuit, R.string.app_icon_worldrosecircuit, R.mipmap.ic_theme_worldrosecircuit_foreground),
    ClockworkGarden("LauncherWorldClockworkGarden", R.mipmap.ic_theme_worldclockworkgarden, R.string.app_icon_worldclockworkgarden, R.mipmap.ic_theme_worldclockworkgarden_foreground),
    SnowglobeVillage("LauncherWorldSnowglobeVillage", R.mipmap.ic_theme_worldsnowglobevillage, R.string.app_icon_worldsnowglobevillage, R.mipmap.ic_theme_worldsnowglobevillage_foreground),
    IndigoAtelier("LauncherWorldIndigoAtelier", R.mipmap.ic_theme_worldindigoatelier, R.string.app_icon_worldindigoatelier, R.mipmap.ic_theme_worldindigoatelier_foreground),
    PrismPlayground("LauncherWorldPrismPlayground", R.mipmap.ic_theme_worldprismplayground, R.string.app_icon_worldprismplayground, R.mipmap.ic_theme_worldprismplayground_foreground),
    SilentCinema("LauncherWorldSilentCinema", R.mipmap.ic_theme_worldsilentcinema, R.string.app_icon_worldsilentcinema, R.mipmap.ic_theme_worldsilentcinema_foreground),
    RiversidePause("LauncherWorldRiversidePause", R.mipmap.ic_theme_worldriversidepause, R.string.app_icon_worldriversidepause, R.mipmap.ic_theme_worldriversidepause_foreground),
    PixelStargazer("LauncherWorldPixelStargazer", R.mipmap.ic_theme_worldpixelstargazer, R.string.app_icon_worldpixelstargazer, R.mipmap.ic_theme_worldpixelstargazer_foreground),
    RoamingCosmos("LauncherWorldRoamingCosmos", R.mipmap.ic_theme_worldroamingcosmos, R.string.app_icon_worldroamingcosmos, R.mipmap.ic_theme_worldroamingcosmos_foreground),
    BlueHourSanctuary("LauncherWorldBlueHourSanctuary", R.mipmap.ic_theme_worldbluehoursanctuary, R.string.app_icon_worldbluehoursanctuary, R.mipmap.ic_theme_worldbluehoursanctuary_foreground),
    CelestialCurrent("LauncherWorldCelestialCurrent", R.mipmap.ic_theme_worldcelestialcurrent, R.string.app_icon_worldcelestialcurrent, R.mipmap.ic_theme_worldcelestialcurrent_foreground),
    ChromeVesper("LauncherVesper", R.mipmap.ic_theme_vesper, R.string.app_icon_vesper, R.mipmap.ic_theme_vesper_foreground),
    WindowWish("LauncherWish", R.mipmap.ic_theme_wish, R.string.app_icon_wish, R.mipmap.ic_theme_wish_foreground),
    CassetteCorner("LauncherCassette", R.mipmap.ic_theme_cassette, R.string.app_icon_cassette, R.mipmap.ic_theme_cassette_foreground),
    AzureBloom("LauncherAzure", R.mipmap.ic_theme_azure, R.string.app_icon_azure, R.mipmap.ic_theme_azure_foreground),
    StrawberryDaydream("LauncherDaydream", R.mipmap.ic_theme_daydream, R.string.app_icon_daydream, R.mipmap.ic_theme_daydream_foreground),
    GoldenAfterglow("LauncherAfterglow", R.mipmap.ic_theme_afterglow, R.string.app_icon_afterglow, R.mipmap.ic_theme_afterglow_foreground),
    LilypondParade("LauncherLilypond", R.mipmap.ic_theme_lilypond, R.string.app_icon_lilypond, R.mipmap.ic_theme_lilypond_foreground),
    DustbyteOutpost("LauncherDustbyte", R.mipmap.ic_theme_dustbyte, R.string.app_icon_dustbyte, R.mipmap.ic_theme_dustbyte_foreground),
    AlpineEmber("LauncherAlpine", R.mipmap.ic_theme_alpine, R.string.app_icon_alpine, R.mipmap.ic_theme_alpine_foreground),
    StarfallPals("LauncherStarfall", R.mipmap.ic_theme_starfall, R.string.app_icon_starfall, R.mipmap.ic_theme_starfall_foreground),
    PumpkinPurr("LauncherPurr", R.mipmap.ic_theme_purr, R.string.app_icon_purr, R.mipmap.ic_theme_purr_foreground),
    SunsetCrew("LauncherSunset", R.mipmap.ic_theme_sunset, R.string.app_icon_sunset, R.mipmap.ic_theme_sunset_foreground),
    StarrySpirits("LauncherSpirits", R.mipmap.ic_theme_spirits, R.string.app_icon_spirits, R.mipmap.ic_theme_spirits_foreground),
    AnalogReverie("LauncherAnalog", R.mipmap.ic_theme_analog, R.string.app_icon_analog, R.mipmap.ic_theme_analog_foreground),
    PetalPromise("LauncherPetal", R.mipmap.ic_theme_petal, R.string.app_icon_petal, R.mipmap.ic_theme_petal_foreground),
    MoonlitHerbarium("LauncherHerbarium", R.mipmap.ic_theme_herbarium, R.string.app_icon_herbarium, R.mipmap.ic_theme_herbarium_foreground),
    MidnightExpress("LauncherMidnight", R.mipmap.ic_theme_midnight, R.string.app_icon_midnight, R.mipmap.ic_theme_midnight_foreground),
    AstralTide("LauncherAstral", R.mipmap.ic_theme_astral, R.string.app_icon_astral, R.mipmap.ic_theme_astral_foreground),
    PlaytimeClub("LauncherPlaytime", R.mipmap.ic_theme_playtime, R.string.app_icon_playtime, R.mipmap.ic_theme_playtime_foreground),
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
        private val _selected=kotlinx.coroutines.flow.MutableStateFlow<AppIcon?>(null)
        val selected=_selected.asStateFlow()

        /** A data-only theme cannot supply a component name or an arbitrary launcher alias. */
        fun forTheme(id: String?): AppIcon? = when (id) {
            "riot-candy" -> CollectionRiotCandy
            "chrome-venom" -> CollectionChromeVenom
            "bubble-riot" -> CollectionBubbleRiot
            "xerox-afterparty" -> CollectionXeroxAfterparty
            "cosmic-vandal" -> CollectionCosmicVandal
            "switchback-radio" -> CollectionSwitchbackRadio
            "komorebi-hour" -> CollectionKomorebiHour
            "rooftop-reverie" -> CollectionRooftopReverie
            "apex-telemetry" -> CollectionApexTelemetry
            "cinder-kata" -> CollectionCinderKata
            "lantern-letters" -> CollectionLanternLetters
            "velvet-orbit" -> CollectionVelvetOrbit
            "moss-mechanism" -> CollectionMossMechanism
            "sugar-static" -> CollectionSugarStatic
            "tidal-origami" -> CollectionTidalOrigami
            "margin-notes" -> CollectionMarginNotes

            "patchwork-picnic" -> PatchworkPicnic
            "clover-requiem" -> CloverRequiem
            "sunroom-mixtape" -> SunroomMixtape
            "concrete-afterhours" -> ConcreteAfterhours
            "matcha-mischief" -> MatchaMischief
            "stained-glass-sonata" -> StainedGlassSonata
            "firefly-railway" -> FireflyRailway
            "porcelain-moon" -> PorcelainMoon
            "black-honey" -> BlackHoney
            "sakura-arcade" -> SakuraArcade
            "cloud-post-office" -> CloudPostOffice
            "copper-canyon-radio" -> CopperCanyonRadio
            "midnight-aquarium" -> MidnightAquarium
            "sunday-bakery" -> SundayBakery
            "rose-circuit" -> RoseCircuit
            "clockwork-garden" -> ClockworkGarden
            "snowglobe-village" -> SnowglobeVillage
            "indigo-atelier" -> IndigoAtelier
            "prism-playground" -> PrismPlayground
            "silent-cinema" -> SilentCinema
            "riverside-pause" -> RiversidePause
            "pixel-stargazer" -> PixelStargazer
            "roaming-cosmos" -> RoamingCosmos
            "blue-hour-sanctuary" -> BlueHourSanctuary
            "celestial-current" -> CelestialCurrent
            "chrome-vesper" -> ChromeVesper
            "window-wish" -> WindowWish
            "cassette-corner" -> CassetteCorner
            "azure-bloom" -> AzureBloom
            "strawberry-daydream" -> StrawberryDaydream
            "golden-afterglow" -> GoldenAfterglow
            "lilypond-parade" -> LilypondParade
            "dustbyte-outpost" -> DustbyteOutpost
            "alpine-ember" -> AlpineEmber
            "starfall-pals" -> StarfallPals
            "pumpkin-purr" -> PumpkinPurr
            "sunset-crew" -> SunsetCrew
            "starry-spirits" -> StarrySpirits
            "analog-reverie" -> AnalogReverie
            "petal-promise" -> PetalPromise
            "moonlit-herbarium" -> MoonlitHerbarium
            "midnight-express" -> MidnightExpress
            "astral-tide" -> AstralTide
            "playtime-club" -> PlaytimeClub
            "web-slinger" -> WebSlinger
            "halloween-nocturne" -> HalloweenNocturne
            else -> null
        }

        /** Asset packs cannot name arbitrary components. Only these bundled choices are eligible. */
        fun forAssetPack(id:String?):AppIcon? = when(id) {
            "cathedral-static" -> CollectionCathedralStatic
            "velvet-hex" -> CollectionVelvetHex
            "razor-bloom" -> CollectionRazorBloom
            "neon-seance" -> CollectionNeonSeance
            "blackout-zine" -> CollectionBlackoutZine
            "chrome-fang" -> CollectionChromeFang
            "candy-crypt" -> CollectionCandyCrypt
            "royal-ruin" -> CollectionRoyalRuin
            "acid-gargoyle" -> CollectionAcidGargoyle
            "midnight-stitch" -> CollectionMidnightStitch
            "spray-parade" -> CollectionSprayParade
            "paper-pop" -> CollectionPaperPop
            "ink-rebel" -> CollectionInkRebel
            "laser-club" -> CollectionLaserClub
            "thread-riot" -> CollectionThreadRiot
            else -> null
        }

        @Synchronized
        fun current(context: Context): AppIcon {
            val pm = context.packageManager
            return entries.firstOrNull {
                val state = pm.getComponentEnabledSetting(it.component(context))
                state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED ||
                    (state == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT && it == Default)
            }?.also {_selected.value=it} ?: Default.also {_selected.value=it}
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
            _selected.value=icon
        }
    }
}
