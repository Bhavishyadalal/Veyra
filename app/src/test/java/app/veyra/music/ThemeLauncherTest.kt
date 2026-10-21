package app.veyra.music

import org.junit.Assert.*
import org.junit.Test
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class ThemeLauncherTest {
    @Test fun themesMapOnlyToBundledIconsAndUnknownThemesCannotSelectAnAlias() {
        assertEquals(AppIcon.StainedGlassSonata,AppIcon.forTheme("stained-glass-sonata"))
        assertEquals(AppIcon.FireflyRailway,AppIcon.forTheme("firefly-railway"))
        assertEquals(AppIcon.PorcelainMoon,AppIcon.forTheme("porcelain-moon"))
        assertEquals(AppIcon.BlackHoney,AppIcon.forTheme("black-honey"))
        assertEquals(AppIcon.SakuraArcade,AppIcon.forTheme("sakura-arcade"))
        assertEquals(AppIcon.CloudPostOffice,AppIcon.forTheme("cloud-post-office"))
        assertEquals(AppIcon.CopperCanyonRadio,AppIcon.forTheme("copper-canyon-radio"))
        assertEquals(AppIcon.MidnightAquarium,AppIcon.forTheme("midnight-aquarium"))
        assertEquals(AppIcon.SundayBakery,AppIcon.forTheme("sunday-bakery"))
        assertEquals(AppIcon.RoseCircuit,AppIcon.forTheme("rose-circuit"))
        assertEquals(AppIcon.ClockworkGarden,AppIcon.forTheme("clockwork-garden"))
        assertEquals(AppIcon.SnowglobeVillage,AppIcon.forTheme("snowglobe-village"))
        assertEquals(AppIcon.IndigoAtelier,AppIcon.forTheme("indigo-atelier"))
        assertEquals(AppIcon.PrismPlayground,AppIcon.forTheme("prism-playground"))
        assertEquals(AppIcon.SilentCinema,AppIcon.forTheme("silent-cinema"))
        assertEquals(AppIcon.RiversidePause,AppIcon.forTheme("riverside-pause"))
        assertEquals(AppIcon.PixelStargazer,AppIcon.forTheme("pixel-stargazer"))
        assertEquals(AppIcon.RoamingCosmos,AppIcon.forTheme("roaming-cosmos"))
        assertEquals(AppIcon.BlueHourSanctuary,AppIcon.forTheme("blue-hour-sanctuary"))
        assertEquals(AppIcon.CelestialCurrent,AppIcon.forTheme("celestial-current"))
        assertEquals(AppIcon.ChromeVesper,AppIcon.forTheme("chrome-vesper"))
        assertEquals(AppIcon.WindowWish,AppIcon.forTheme("window-wish"))
        assertEquals(AppIcon.CassetteCorner,AppIcon.forTheme("cassette-corner"))
        assertEquals(AppIcon.AzureBloom,AppIcon.forTheme("azure-bloom"))
        assertEquals(AppIcon.StrawberryDaydream,AppIcon.forTheme("strawberry-daydream"))
        assertEquals(AppIcon.GoldenAfterglow,AppIcon.forTheme("golden-afterglow"))
        assertEquals(AppIcon.LilypondParade,AppIcon.forTheme("lilypond-parade"))
        assertEquals(AppIcon.DustbyteOutpost,AppIcon.forTheme("dustbyte-outpost"))
        assertEquals(AppIcon.AlpineEmber,AppIcon.forTheme("alpine-ember"))
        assertEquals(AppIcon.StarfallPals,AppIcon.forTheme("starfall-pals"))
        assertEquals(AppIcon.PumpkinPurr,AppIcon.forTheme("pumpkin-purr"))
        assertEquals(AppIcon.SunsetCrew,AppIcon.forTheme("sunset-crew"))
        assertEquals(AppIcon.StarrySpirits,AppIcon.forTheme("starry-spirits"))
        assertEquals(AppIcon.AnalogReverie,AppIcon.forTheme("analog-reverie"))
        assertEquals(AppIcon.PetalPromise,AppIcon.forTheme("petal-promise"))
        assertEquals(AppIcon.MoonlitHerbarium,AppIcon.forTheme("moonlit-herbarium"))
        assertEquals(AppIcon.MidnightExpress,AppIcon.forTheme("midnight-express"))
        assertEquals(AppIcon.AstralTide,AppIcon.forTheme("astral-tide"))
        assertEquals(AppIcon.PlaytimeClub,AppIcon.forTheme("playtime-club"))
        assertEquals(AppIcon.WebSlinger,AppIcon.forTheme("web-slinger"))
        assertEquals(AppIcon.HalloweenNocturne,AppIcon.forTheme("halloween-nocturne"))
        assertEquals(AppIcon.PatchworkPicnic,AppIcon.forTheme("patchwork-picnic"))
        assertEquals(AppIcon.CloverRequiem,AppIcon.forTheme("clover-requiem"))
        assertEquals(AppIcon.SunroomMixtape,AppIcon.forTheme("sunroom-mixtape"))
        assertEquals(AppIcon.ConcreteAfterhours,AppIcon.forTheme("concrete-afterhours"))
        assertEquals(AppIcon.MatchaMischief,AppIcon.forTheme("matcha-mischief"))
        assertNull(AppIcon.forTheme(".MainActivity"))
        assertNull(AppIcon.forTheme(null))
        assertNull(AppIcon.forTheme("untrusted-pack"))
    }
    @Test fun everySelectableAliasExistsAndExactlyTheOriginalDefaultShipsEnabled() {
        val project=generateSequence(File(requireNotNull(System.getProperty("user.dir")))) { it.parentFile }
            .first { File(it,"app/src/main/AndroidManifest.xml").exists() }
        val xml=DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .parse(File(project,"app/src/main/AndroidManifest.xml"))
        val aliases=xml.getElementsByTagName("activity-alias")
        val states=(0 until aliases.length).associate { i ->
            val a=aliases.item(i).attributes
            a.getNamedItem("android:name").nodeValue.removePrefix(".") to a.getNamedItem("android:enabled").nodeValue
        }
        assertEquals(AppIcon.entries.size,AppIcon.entries.map { it.alias }.distinct().size)
        AppIcon.entries.forEach { assertTrue(it.alias,states.containsKey(it.alias)) }
        assertEquals(setOf("LauncherDefault"),states.filterValues { it=="true" }.keys)
    }
    @Test fun atelierAddsTwelveStandaloneChoicesWithoutThemeBindings() {
        val project=generateSequence(File(requireNotNull(System.getProperty("user.dir")))) { it.parentFile }
            .first { File(it,"design/launcher/atelier/catalog.json").exists() }
        val catalog=kotlinx.serialization.json.Json.parseToJsonElement(
            File(project,"design/launcher/atelier/catalog.json").readText()).let {
                it as kotlinx.serialization.json.JsonObject
            }.getValue("icons") as kotlinx.serialization.json.JsonArray
        val choices=app.veyra.music.data.themes.IconPickerPolicy.options(
            app.veyra.music.data.themes.IconCollection.ATELIER,app.veyra.music.data.themes.GlassFamily.LIQUID)
        assertEquals(12,choices.size)
        assertEquals(74,AppIcon.entries.size)
        val expected=catalog.map { row ->
            val id=((row as kotlinx.serialization.json.JsonObject).getValue("id") as kotlinx.serialization.json.JsonPrimitive).content
            assertNull(AppIcon.forTheme(id))
            "Atelier"+id.split('-').joinToString("") { it.replaceFirstChar(Char::uppercase) }
        }.toSet()
        assertEquals(expected,choices.map { it.name }.toSet())
        choices.forEach { assertNull(app.veyra.music.data.themes.IconPickerPolicy.family(it)) }
    }
    @Test fun allIconsAreCompleteAndIndependentOfTheGlassFamily() {
        val p=app.veyra.music.data.themes.IconPickerPolicy
        app.veyra.music.data.themes.GlassFamily.entries.forEach { family ->
            val all=p.options(app.veyra.music.data.themes.IconCollection.ALL,family)
            assertEquals(AppIcon.entries.toList(),all)
            assertEquals(74,all.distinct().size)
        }
    }
    @Test fun groupedPickerRetainsEveryExistingIconExactlyOnce() {
        val p=app.veyra.music.data.themes.IconPickerPolicy
        val family=app.veyra.music.data.themes.GlassFamily.entries
        val groups=app.veyra.music.data.themes.IconCollection.entries.filter {it!=app.veyra.music.data.themes.IconCollection.ALL}
        val all=groups.flatMap { g ->
            if(g==app.veyra.music.data.themes.IconCollection.GLASS) family.flatMap {p.options(g,it)}
            else p.options(g,family.first())
        }
        assertEquals(AppIcon.entries.toSet(),all.toSet())
        assertEquals(AppIcon.entries.size,all.size)
        family.forEach {assertEquals(4,p.options(app.veyra.music.data.themes.IconCollection.GLASS,it).size)}
    }

}
