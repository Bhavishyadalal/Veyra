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
    @Test fun officialPackInstallsWithExactlyItsDeclaredAssetsAndNoExecutableContent() {
        val pack=File(project,"themes/packs/halloween-nocturne-v3.zip")
        val root=temp.newFolder()
        val (m,dir)=ThemePack.install(pack,root,catalog(),24)
        assertEquals("halloween-nocturne-v3",dir.name)
        assertEquals(manifest(),m)
        assertEquals(m.files.keys+"theme.json",dir.walkTopDown().filter{it.isFile}.map{it.relativeTo(dir).invariantSeparatorsPath}.toSet())
        assertEquals(setOf(dir.name),root.list()!!.toSet())
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
        assertEquals(setOf("halloween-nocturne","web-slinger","playtime-club"),catalog.themes.map { it.id }.toSet())
        for (entry in catalog.themes) {
            val pack=File(project,"themes/packs/${entry.id}-v${entry.version}.zip")
            val (m,dir)=ThemePack.install(pack,temp.newFolder(),entry,25)
            assertEquals(if(entry.id == "playtime-club") 3 else 2,m.schema)
            assertTrue(File(dir,m.assets.getValue("brandIcon")).isFile)
            rejects { ThemePack.validate(m,23) }
            if (entry.id == "playtime-club") {
                rejects { ThemePack.validate(m,24) }
                rejects { ThemePack.validate(m.copy(schema=2,minAppCode=24),25) }
                assertEquals("bubbles",m.seekStyle)
                assertEquals("playful",m.sceneStyle)
            }
        }
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
}
