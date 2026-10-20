package app.veyra.music

import org.junit.Assert.*
import org.junit.Test
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class ThemeLauncherTest {
    @Test fun themesMapOnlyToBundledIconsAndUnknownThemesCannotSelectAnAlias() {
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
    @Test fun groupedPickerRetainsEveryExistingIconExactlyOnce() {
        val p=app.veyra.music.data.themes.IconPickerPolicy
        val family=app.veyra.music.data.themes.GlassFamily.entries
        val groups=app.veyra.music.data.themes.IconCollection.entries
        val all=groups.flatMap { g ->
            if(g==app.veyra.music.data.themes.IconCollection.GLASS) family.flatMap {p.options(g,it)}
            else p.options(g,family.first())
        }
        assertEquals(AppIcon.entries.toSet(),all.toSet())
        assertEquals(AppIcon.entries.size,all.size)
        family.forEach {assertEquals(4,p.options(app.veyra.music.data.themes.IconCollection.GLASS,it).size)}
    }

}
