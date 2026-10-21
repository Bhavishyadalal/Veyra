package app.veyra.music

import app.veyra.music.data.themes.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ThemePackTest {
    private val keepsakeIds=setOf("patchwork-picnic","clover-requiem","sunroom-mixtape","concrete-afterhours","matcha-mischief")
    @get:Rule val temp = TemporaryFolder()
    private val project: File get() = generateSequence(File(requireNotNull(System.getProperty("user.dir")))) { it.parentFile }
        .first { File(it,"themes/catalog.json").exists() }
    private fun catalog() = ThemePack.json.decodeFromString<ThemeCatalog>(File(project,"themes/catalog.json").readText()).themes.single { it.id == "halloween-nocturne" }
    private fun manifest() = ThemePack.json.decodeFromString<ThemeManifest>(File(project,"themes/halloween-nocturne/theme.json").readText())
    private fun rejects(block: () -> Unit) { try { block(); fail("Invalid pack was accepted") } catch (_: IllegalArgumentException) {} }
    private fun zip(entries: Map<String,ByteArray>): File = temp.newFile().also { file ->
        ZipOutputStream(file.outputStream()).use { z -> entries.forEach { (name,data) -> z.putNextEntry(ZipEntry(name));z.write(data);z.closeEntry() } }
    }
    private fun listing(file: File) = catalog().copy(bytes=file.length(),sha256=ThemePack.sha256(file.readBytes()))
    // Inspect the actual published PNG/VP8 headers on the JVM; Android's decoder
    // is not available in local unit tests. Both paths use the runtime size policy.
    private fun validatePublishedImageBounds(m: ThemeManifest, dir: File) {
        m.assets.filterKeys { !it.endsWith("Font") }.forEach { (key,path) ->
            val bytes=File(dir,path).readBytes()
            val (width,height)=if(path.endsWith(".png")) {
                assertArrayEquals(byteArrayOf(0x89.toByte(),0x50,0x4e,0x47,13,10,26,10),bytes.copyOfRange(0,8))
                assertEquals("IHDR",bytes.copyOfRange(12,16).decodeToString())
                val header=java.nio.ByteBuffer.wrap(bytes)
                header.getInt(16) to header.getInt(20)
            } else {
                assertEquals("RIFF",bytes.copyOfRange(0,4).decodeToString())
                assertEquals("WEBPVP8 ",bytes.copyOfRange(8,16).decodeToString())
                assertArrayEquals(byteArrayOf(0x9d.toByte(),0x01,0x2a),bytes.copyOfRange(23,26))
                fun dimension(offset: Int)=((bytes[offset].toInt() and 255) or
                    ((bytes[offset+1].toInt() and 255) shl 8)) and 0x3fff
                dimension(26) to dimension(28)
            }
            ThemePack.validateImageBounds(key,width,height)
            if(m.id in keepsakeIds && key !in setOf("header","player","preview")) {
                assertTrue("${m.id}: $key must also pass the original stable image-size rule",width in 1..256 && height in 1..256)
            }
        }
    }
    @Test fun officialPackInstallsWithExactlyItsDeclaredAssetsAndNoExecutableContent() {
        val pack=File(project,"themes/packs/halloween-nocturne-v3.zip")
        val root=temp.newFolder()
        val (m,dir)=ThemePack.install(pack,root,catalog(),24)
        assertEquals("halloween-nocturne-v3",dir.name)
        assertEquals(manifest(),m)
        assertEquals(m.files.keys+"theme.json",dir.walkTopDown().filter{it.isFile}.map{it.relativeTo(dir).invariantSeparatorsPath}.toSet())
        assertEquals(setOf(dir.name),root.list()!!.toSet())
    }
    @Test fun publishedBrandSizeIsAcceptedWithoutRelaxingControlOrSceneLimits() {
        ThemePack.validateImageBounds("brandIcon",512,512)
        ThemePack.validateImageBounds("play",256,256)
        ThemePack.validateImageBounds("header",2048,2048)
        rejects { ThemePack.validateImageBounds("brandIcon",513,512) }
        rejects { ThemePack.validateImageBounds("brandIcon",512,513) }
        rejects { ThemePack.validateImageBounds("play",512,512) }
        rejects { ThemePack.validateImageBounds("nav_home",257,256) }
        rejects { ThemePack.validateImageBounds("header",2049,2048) }
        rejects { ThemePack.validateImageBounds("brandIcon",-1,-1) }
        rejects { ThemePack.validateImageBounds("preview",0,100) }
    }
    @Test fun keepsakePacksInstallOnStable26WithoutTheBetaEngineOrLauncherAliases() {
        val entries=ThemePack.json.decodeFromString<ThemeCatalog>(File(project,"themes/catalog.json").readText()).themes
            .filter {it.id in keepsakeIds}
        assertEquals(5,entries.size)
        entries.forEach { entry ->
            val (m,_)=ThemePack.install(File(project,"themes/packs/${entry.id}-v${entry.version}.zip"),
                temp.newFolder(),entry,26,::validatePublishedImageBounds)
            assertEquals(26,m.minAppCode)
            rejects {ThemePack.validate(m,25)}
        }
    }
    @Test fun decoderFailurePreservesAnExistingWorkingInstallation() {
        val pack=File(project,"themes/packs/halloween-nocturne-v3.zip")
        val root=temp.newFolder()
        val (_,dir)=ThemePack.install(pack,root,catalog(),24)
        val original=File(dir,"theme.json").readBytes()
        rejects { ThemePack.install(pack,root,catalog(),24) { _,_ -> throw IllegalArgumentException("Decoder rejected asset") } }
        assertArrayEquals(original,File(dir,"theme.json").readBytes())
        assertEquals(setOf(dir.name),root.list()!!.toSet())
    }
    @Test fun corruptedDownloadNeverCreatesAnInstallation() {
        val f=temp.newFile().apply{writeText("truncated")};val root=temp.newFolder()
        rejects { ThemePack.install(f,root,catalog(),24) }
        assertTrue(root.list()!!.isEmpty())
    }
    @Test fun archiveTraversalIsRejectedBeforeWritingAnythingOutsideStaging() {
        val f=zip(mapOf("../escaped.txt" to "bad".toByteArray()));val root=temp.newFolder()
        rejects { ThemePack.install(f,root,listing(f),24) }
        assertTrue(root.list()!!.isEmpty());assertFalse(File(root.parent,"escaped.txt").exists())
    }
    @Test fun unexpectedArchiveEntriesAreRejectedEvenWithAValidOuterHash() {
        val f=zip(mapOf("theme.json" to File(project,"themes/halloween-nocturne/theme.json").readBytes(),"icons/extra.png" to byteArrayOf(1)))
        val root=temp.newFolder();rejects{ThemePack.install(f,root,listing(f),24)};assertTrue(root.list()!!.isEmpty())
    }
    @Test fun alteredAssetCannotPassItsInnerHash() {
        val base=File(project,"themes/halloween-nocturne")
        val entries=base.walkTopDown().filter{it.isFile}.associate{it.relativeTo(base).invariantSeparatorsPath to it.readBytes()}.toMutableMap()
        entries["icons/play.png"]=byteArrayOf(1,2,3)
        val f=zip(entries);val root=temp.newFolder();rejects{ThemePack.install(f,root,listing(f),24)};assertTrue(root.list()!!.isEmpty())
    }
    @Test fun newerEngineAndAppRequirementsFailSafely() {
        rejects{ThemePack.validate(manifest().copy(schema=4),24)}
        rejects{ThemePack.validate(manifest().copy(minAppCode=25),24)}
    }
    @Test fun unreadableTextAndUnboundedGeometryAreRejected() {
        val m=manifest()
        rejects{ThemePack.validate(m.copy(palette=m.palette.copy(text=m.palette.background)),24)}
        rejects{ThemePack.validate(m.copy(buttonCorner=999),24)}
        ThemePack.validate(m,24)
    }
    @Test fun onlyCanonicalBoundedDataPathsAreAllowed() {
        for(path in listOf("/tmp/file.png","../escape.png","icons/../../escape.ttf","icons/evil.dex","icons\\escape.png","icons//x.png","icons/../x.png")) assertFalse(path,ThemePack.safePath(path))
        assertTrue(ThemePack.safePath("icons/play.png"))
    }
    @Test fun allPublishedThemesInstallWithCorrectBrandingAndMinimumVersion() {
        val catalog=ThemePack.json.decodeFromString<ThemeCatalog>(File(project,"themes/catalog.json").readText())
        assertEquals(setOf("patchwork-picnic","clover-requiem","sunroom-mixtape","concrete-afterhours","matcha-mischief","halloween-nocturne","web-slinger","playtime-club","astral-tide","midnight-express","pumpkin-purr","sunset-crew","starry-spirits","analog-reverie","petal-promise","moonlit-herbarium","golden-afterglow","lilypond-parade","dustbyte-outpost","alpine-ember","starfall-pals","chrome-vesper","window-wish","cassette-corner","azure-bloom","strawberry-daydream","stained-glass-sonata","firefly-railway","porcelain-moon","black-honey","sakura-arcade","cloud-post-office","copper-canyon-radio","midnight-aquarium","sunday-bakery","rose-circuit","clockwork-garden","snowglobe-village","indigo-atelier","prism-playground","silent-cinema","riverside-pause","pixel-stargazer","roaming-cosmos","blue-hour-sanctuary","celestial-current"),catalog.themes.map { it.id }.toSet())
        for (entry in catalog.themes) {
            val pack=File(project,"themes/packs/${entry.id}-v${entry.version}.zip")
            val (m,dir)=ThemePack.install(pack,temp.newFolder(),entry,BuildConfig.VERSION_CODE,::validatePublishedImageBounds)
            assertEquals(if(entry.id in setOf("patchwork-picnic","matcha-mischief","playtime-club","pumpkin-purr","petal-promise","lilypond-parade","starfall-pals","strawberry-daydream","cloud-post-office","sunday-bakery","snowglobe-village","prism-playground")) 3 else 2,m.schema)
            assertTrue(File(dir,m.assets.getValue("brandIcon")).isFile)
            rejects { ThemePack.validate(m,23) }
            if (entry.id == "midnight-express") {
                rejects { ThemePack.validate(m,24) }
                assertEquals("fonts/Rajdhani.ttf",m.assets["headingFont"])
                assertEquals("web",m.seekStyle)
                assertEquals("mist",m.sceneStyle)
            }
            if (entry.id == "astral-tide") {
                rejects { ThemePack.validate(m,24) }
                assertEquals("fonts/SpaceGrotesk.ttf",m.assets["headingFont"])
                assertEquals("ember",m.seekStyle)
                assertEquals("mist",m.sceneStyle)
            }
            if (entry.id == "playtime-club") {
                rejects { ThemePack.validate(m,24) }
                rejects { ThemePack.validate(m.copy(schema=2,minAppCode=24),25) }
                assertEquals("bubbles",m.seekStyle)
                assertEquals("playful",m.sceneStyle)
            }
        }
    }
    @Test fun bundledAndPublishedCatalogsPassTheActualRuntimeUrlPolicy() {
        val published=File(project,"themes/catalog.json").readBytes()
        val bundled=File(project,"app/src/main/assets/theme-catalog.json").readBytes()
        assertArrayEquals(published,bundled)
        val entries=ThemeRepository.parseCatalog(published)
        assertEquals(46,entries.size)
        entries.forEach { entry ->
            assertTrue(File(project,"themes/${entry.id}/artwork/preview.webp").isFile)
        }
        val invalid=ThemePack.json.decodeFromString<ThemeCatalog>(published.decodeToString()).let { c ->
            c.copy(themes=listOf(c.themes.first().copy(preview="https://example.com/art.webp")))
        }
        rejects { ThemeRepository.parseCatalog(ThemePack.json.encodeToString(ThemeCatalog.serializer(),invalid).encodeToByteArray()) }
    }
    @Test fun legacyPacksStillValidateWithoutNewFields() {
        val zip=java.util.zip.ZipFile(File(project,"themes/packs/halloween-nocturne-v2.zip"))
        val legacy=zip.use { z -> ThemePack.json.decodeFromString<ThemeManifest>(z.getInputStream(z.getEntry("theme.json")).readBytes().decodeToString()) }
        ThemePack.validate(legacy,24)
        assertEquals("mist",legacy.sceneStyle)
        assertFalse("brandIcon" in legacy.assets)
    }
    @Test fun unsupportedSceneAndUnboundedCopyFailSafely() {
        rejects { ThemePack.validate(manifest().copy(sceneStyle="arbitrary-code"),24) }
        rejects { ThemePack.validate(manifest().copy(tagline="x".repeat(81)),24) }
        rejects { ThemePack.validate(manifest().copy(tagline="bad\ntext"),24) }
        rejects { ThemePack.validate(manifest().copy(schema=1),24) }
    }
    @Test fun oversizedStreamsStopWithoutAllocatingTheWholeResponse() {
        rejects{ThemePack.readBounded(ByteArray(1025).inputStream(),1024)}
        assertEquals(1024,ThemePack.readBounded(ByteArray(1024).inputStream(),1024).size)
    }
    @Test fun expandedCatalogStillHasAFiniteThemeLimit() {
        val entry=catalog()
        val prefix=entry.url.substringBefore("packs/")
        val entries=(0 until 65).map { i ->
            val id="bounded-$i"
            entry.copy(id=id,url=prefix+"packs/$id-v${entry.version}.zip",preview=prefix+"$id/artwork/preview.webp")
        }
        fun encoded(items: List<ThemeListing>)=ThemePack.json.encodeToString(ThemeCatalog.serializer(),ThemeCatalog(1,items)).encodeToByteArray()
        assertEquals(64,ThemeRepository.parseCatalog(encoded(entries.take(64))).size)
        rejects { ThemeRepository.parseCatalog(encoded(entries)) }
    }

}
