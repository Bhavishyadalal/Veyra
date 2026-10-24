package app.veyra.music

import app.veyra.music.data.themes.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.File

class ChaosStudioPackTest {
    @get:Rule val temp=TemporaryFolder()
    private val root get()=generateSequence(File(requireNotNull(System.getProperty("user.dir")))) {it.parentFile}.first {File(it,"themes/catalog.json").exists()}
    private fun validatePng(key:String,file:File) {
        val image=Class.forName("javax.imageio.ImageIO").getMethod("read",File::class.java).invoke(null,file)
        assertNotNull(image)
        val width=image.javaClass.getMethod("getWidth").invoke(image) as Int
        val height=image.javaClass.getMethod("getHeight").invoke(image) as Int
        ThemePack.validateImageBounds(key,width,height)
    }
    @Test fun allNewThemesInstallThroughProductionChecksumAndManifestValidation() {
        val ids=setOf("soda-sabotage","velvet-vandal","acid-daydream","peach-panic","ink-and-ivory","holo-havoc","denim-disorder","cherry-voltage","mint-mutiny","lunar-lollipop")
        val catalog=ThemePack.json.decodeFromString(ThemeCatalog.serializer(),File(root,"themes/catalog.json").readText())
        val entries=catalog.themes.filter {it.id in ids};assertEquals(10,entries.size)
        entries.forEach {entry->
            val (m,dir)=ThemePack.install(File(root,"themes/packs/${entry.id}-v1.zip"),temp.newFolder(),entry,45)
            assertEquals(123,m.assets.size)
            m.assets.forEach {(key,path)->
                val file=File(dir,path)
                if(key.endsWith("Font"))assertNotNull(Class.forName("java.awt.Font").getMethod("createFont",Int::class.javaPrimitiveType,File::class.java).invoke(null,0,file))
                else if(path.endsWith(".png")) {
                    validatePng(key,file)
                }
            }
            for(key in listOf("play","pause","previous","next"))assertNotEquals(m.files[m.assets[key]],m.files[m.assets[key+"_pressed"]])
        }
    }
    @Test fun allNewOverlaysInstallWithoutReplacingThemeArtworkOrPalette() {
        val ids=setOf("gummy-signal","razor-petal","disco-chrome","paper-revolt","velvet-sigil","patch-party","pixel-fizz")
        val catalog=ThemePack.json.decodeFromString(AssetCatalog.serializer(),File(root,"asset-packs/catalog.json").readText())
        assertEquals(22,AssetRepository.parseCatalog(File(root,"asset-packs/catalog.json").readBytes()).size)
        val entries=catalog.packs.filter {it.id in ids};assertEquals(7,entries.size)
        entries.forEach {entry->
            val (m,dir)=AssetPack.install(File(root,"asset-packs/packs/${entry.id}-v1.zip"),temp.newFolder(),entry,45)
            assertEquals(119,m.assets.size)
            assertFalse(m.assets.keys.any {it in setOf("header","player","preview","brandIcon")})
            assertTrue(File(dir,"previews/specimen.webp").isFile)
            m.assets.forEach {(key,path)->
                val file=File(dir,path)
                if(key.endsWith("Font"))assertNotNull(Class.forName("java.awt.Font").getMethod("createFont",Int::class.javaPrimitiveType,File::class.java).invoke(null,0,file))
                else {validatePng(key,file)}
            }
        }
    }
}
