package app.veyra.music

import app.veyra.music.data.themes.*
import app.veyra.music.ui.themes.ThemeGlyphs
import org.junit.Assert.*
import org.junit.Test
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.File

class PocketRiotTest {
    @get:Rule val temp=TemporaryFolder()
    private val root get()=generateSequence(File(requireNotNull(System.getProperty("user.dir")))) {it.parentFile}.first {File(it,"themes/catalog.json").exists()}
    @Test fun everyPublishedThemeAndOverlayHasABundledCorrectlyGroupedLauncher() {
        val themes=ThemePack.json.decodeFromString(ThemeCatalog.serializer(),File(root,"themes/catalog.json").readText()).themes
        val packs=AssetPack.json.decodeFromString(AssetCatalog.serializer(),File(root,"asset-packs/catalog.json").readText()).packs
        assertEquals(72,themes.size);assertEquals(32,packs.size)
        themes.forEach {assertNotNull(it.id,AppIcon.forTheme(it.id));assertEquals(IconCollection.THEMES,IconPickerPolicy.collection(AppIcon.forTheme(it.id)!!))}
        packs.forEach {assertNotNull(it.id,AppIcon.forAssetPack(it.id));assertEquals(IconCollection.ASSETS,IconPickerPolicy.collection(AppIcon.forAssetPack(it.id)!!))}
        assertEquals(132,AppIcon.entries.size)
    }
    @Test fun newPacksInstallDecodeAndPreserveAllPressedStates() {
        val ids=setOf("kitty-mayhem","mochi-moshpit","peach-bruiser","bunny-voltage","cherry-bombshell","teddy-tantrum","bubble-fang","sugar-goblin","cosmic-pudding","sticker-syndicate")
        val entries=AssetRepository.parseCatalog(File(root,"asset-packs/catalog.json").readBytes()).filter {it.id in ids}
        assertEquals(10,entries.size)
        entries.forEach {entry->
            val (m,dir)=AssetPack.install(File(root,"asset-packs/packs/${entry.id}-v1.zip"),temp.newFolder(),entry,BuildConfig.VERSION_CODE)
            assertEquals(119,m.assets.size)
            for((key,path) in m.assets) {
                val file=File(dir,path)
                if(key.endsWith("Font"))assertNotNull(Class.forName("java.awt.Font").getMethod("createFont",Int::class.javaPrimitiveType,File::class.java).invoke(null,0,file))
                else {
                    val image=Class.forName("javax.imageio.ImageIO").getMethod("read",File::class.java).invoke(null,file);assertNotNull(image)
                    ThemePack.validateImageBounds(key,image.javaClass.getMethod("getWidth").invoke(image) as Int,image.javaClass.getMethod("getHeight").invoke(image) as Int)
                }
            }
            for(key in listOf("play","pause","previous","next"))assertNotEquals(m.files[m.assets[key]],m.files[m.assets[key+"_pressed"]])
        }
    }
    @Test fun assetCatalogLimitsAndDuplicateIdsRemainRejected() {
        val original=File(root,"asset-packs/catalog.json").readBytes();assertEquals(32,AssetRepository.parseCatalog(original).size)
        try {AssetRepository.parseCatalog(ByteArray(65537));fail("oversized catalog accepted")}catch(expected:IllegalArgumentException){}
        val catalog=AssetPack.json.decodeFromString(AssetCatalog.serializer(),original.decodeToString())
        val duplicate=AssetPack.json.encodeToString(AssetCatalog.serializer(),catalog.copy(packs=catalog.packs+catalog.packs.first())).encodeToByteArray()
        try {AssetRepository.parseCatalog(duplicate);fail("duplicate/oversized catalog accepted")}catch(expected:IllegalArgumentException){}
    }
    @Test fun deepControlsResolveToExistingSemanticAssets() {
        val expected=mapOf("Edit" to "notes","DeleteForever" to "delete","ExpandLess" to "arrow_up","ExpandMore" to "arrow_down","Movie" to "video","Palette" to "gradient","SwitchAccount" to "account","PhoneIphone" to "phone")
        expected.forEach {(name,key)->assertEquals(key,ThemeGlyphs.key(name));assertTrue(key in ThemeGlyphs.advancedKeys)}
    }
}
