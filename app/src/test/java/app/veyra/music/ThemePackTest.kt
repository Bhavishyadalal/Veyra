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
    private fun catalog() = ThemePack.json.decodeFromString<ThemeCatalog>(File(project,"themes/catalog.json").readText()).themes.single()
    private fun manifest() = ThemePack.json.decodeFromString<ThemeManifest>(File(project,"themes/halloween-nocturne/theme.json").readText())
    private fun rejects(block: () -> Unit) { try { block(); fail("Invalid pack was accepted") } catch (_: IllegalArgumentException) {} }
    private fun zip(entries: Map<String,ByteArray>): File = temp.newFile().also { file ->
        ZipOutputStream(file.outputStream()).use { z -> entries.forEach { (name,data) -> z.putNextEntry(ZipEntry(name));z.write(data);z.closeEntry() } }
    }
    private fun listing(file: File) = catalog().copy(bytes=file.length(),sha256=ThemePack.sha256(file.readBytes()))
    @Test fun officialPackInstallsWithExactlyItsDeclaredAssetsAndNoExecutableContent() {
        val pack=File(project,"themes/packs/halloween-nocturne-v2.zip")
        val root=temp.newFolder()
        val (m,dir)=ThemePack.install(pack,root,catalog(),23)
        assertEquals("halloween-nocturne-v2",dir.name)
        assertEquals(manifest(),m)
        assertEquals(m.files.keys+"theme.json",dir.walkTopDown().filter{it.isFile}.map{it.relativeTo(dir).invariantSeparatorsPath}.toSet())
        assertEquals(setOf(dir.name),root.list()!!.toSet())
    }
    @Test fun decoderFailurePreservesAnExistingWorkingInstallation() {
        val pack=File(project,"themes/packs/halloween-nocturne-v2.zip")
        val root=temp.newFolder()
        val (_,dir)=ThemePack.install(pack,root,catalog(),23)
        val original=File(dir,"theme.json").readBytes()
        rejects { ThemePack.install(pack,root,catalog(),23) { _,_ -> throw IllegalArgumentException("Decoder rejected asset") } }
        assertArrayEquals(original,File(dir,"theme.json").readBytes())
        assertEquals(setOf(dir.name),root.list()!!.toSet())
    }
    @Test fun corruptedDownloadNeverCreatesAnInstallation() {
        val f=temp.newFile().apply{writeText("truncated")};val root=temp.newFolder()
        rejects { ThemePack.install(f,root,catalog(),23) }
        assertTrue(root.list()!!.isEmpty())
    }
    @Test fun archiveTraversalIsRejectedBeforeWritingAnythingOutsideStaging() {
        val f=zip(mapOf("../escaped.txt" to "bad".toByteArray()));val root=temp.newFolder()
        rejects { ThemePack.install(f,root,listing(f),23) }
        assertTrue(root.list()!!.isEmpty());assertFalse(File(root.parent,"escaped.txt").exists())
    }
    @Test fun unexpectedArchiveEntriesAreRejectedEvenWithAValidOuterHash() {
        val f=zip(mapOf("theme.json" to File(project,"themes/halloween-nocturne/theme.json").readBytes(),"icons/extra.png" to byteArrayOf(1)))
        val root=temp.newFolder();rejects{ThemePack.install(f,root,listing(f),23)};assertTrue(root.list()!!.isEmpty())
    }
    @Test fun alteredAssetCannotPassItsInnerHash() {
        val base=File(project,"themes/halloween-nocturne")
        val entries=base.walkTopDown().filter{it.isFile}.associate{it.relativeTo(base).invariantSeparatorsPath to it.readBytes()}.toMutableMap()
        entries["icons/play.png"]=byteArrayOf(1,2,3)
        val f=zip(entries);val root=temp.newFolder();rejects{ThemePack.install(f,root,listing(f),23)};assertTrue(root.list()!!.isEmpty())
    }
    @Test fun newerEngineAndAppRequirementsFailSafely() {
        rejects{ThemePack.validate(manifest().copy(schema=2),23)}
        rejects{ThemePack.validate(manifest().copy(minAppCode=24),23)}
    }
    @Test fun unreadableTextAndUnboundedGeometryAreRejected() {
        val m=manifest()
        rejects{ThemePack.validate(m.copy(palette=m.palette.copy(text=m.palette.background)),23)}
        rejects{ThemePack.validate(m.copy(buttonCorner=999),23)}
        ThemePack.validate(m,23)
    }
    @Test fun onlyCanonicalBoundedDataPathsAreAllowed() {
        for(path in listOf("/tmp/file.png","../escape.png","icons/../../escape.ttf","icons/evil.dex","icons\\escape.png","icons//x.png","icons/../x.png")) assertFalse(path,ThemePack.safePath(path))
        assertTrue(ThemePack.safePath("icons/play.png"))
    }
    @Test fun oversizedStreamsStopWithoutAllocatingTheWholeResponse() {
        rejects{ThemePack.readBounded(ByteArray(1025).inputStream(),1024)}
        assertEquals(1024,ThemePack.readBounded(ByteArray(1024).inputStream(),1024).size)
    }
}
