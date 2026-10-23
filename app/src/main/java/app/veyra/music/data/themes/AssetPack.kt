package app.veyra.music.data.themes

import kotlinx.serialization.Serializable
import java.io.File
import java.util.UUID
import java.util.zip.ZipFile

@Serializable
data class AssetManifest(
    val schema: Int, val id: String, val name: String, val author: String,
    val version: Int, val minAppCode: Int,
    val assets: Map<String,String>, val files: Map<String,String>,
    val materialStyle: String, val seekStyle: String, val seekHeight: Int,
    val rowCorner: Int, val buttonCorner: Int, val navCorner: Int,
    val headingInk: String, val motionStyle: String,
)
@Serializable data class AssetCatalog(val schema: Int, val packs: List<AssetListing>)
@Serializable data class AssetListing(
    val id: String, val name: String, val description: String, val version: Int,
    val minAppCode: Int, val url: String, val sha256: String, val bytes: Long, val preview: String,
)

/** Independent data-only overrides. Artwork, palette, branding and executable files are forbidden. */
object AssetPack {
    val json = ThemePack.json
    const val MAX_ZIP = ThemePack.MAX_ZIP
    const val MAX_FILE = ThemePack.MAX_FILE
    const val MAX_TOTAL = ThemePack.MAX_TOTAL
    fun safePath(path:String) = ThemePack.safePath(path)
    fun sha256(bytes:ByteArray) = ThemePack.sha256(bytes)
    fun readBounded(input:java.io.InputStream,max:Int) = ThemePack.readBounded(input,max)
    val allowedKeys = ThemePack.assetKeys - setOf("header","player","preview","brandIcon")
    val materials = setOf("classic","pixel","botanical","deco","telemetry","flame","punk_gel","punk_rivet","punk_sticker","punk_torn","punk_chrome")
    fun validate(m:AssetManifest,appCode:Int) {
        require(m.schema == 1 && m.minAppCode in 33..appCode) { "Update Veyra for independent asset packs." }
        require(m.id.matches(Regex("[a-z0-9][a-z0-9-]{0,47}")))
        require(m.name.length in 1..60 && m.author.length in 1..80 && m.version in 1..10000)
        require(m.rowCorner in 8..28 && m.buttonCorner in 12..36 && m.navCorner in 18..48)
        require(m.materialStyle in materials && m.motionStyle in materials)
        require(m.seekStyle in setOf("plain","sketch","road","vine","skyline","telemetry","flame","gel","cable","beads","tape","plasma") && m.seekHeight in 6..12)
        require(m.headingInk.matches(Regex("#[0-9A-Fa-f]{6}")))
        require(m.files.size in 1..128 && m.files.all {safePath(it.key) && it.value.matches(Regex("[a-f0-9]{64}"))})
        require("previews/specimen.webp" in m.files)
        require(m.assets.isNotEmpty() && m.assets.keys.all {it in allowedKeys})
        require(m.assets.keys.containsAll(setOf("headingFont","bodyFont")))
        require(m.assets.values.all {it in m.files})
        m.assets.forEach {(key,path)->require(if(key.endsWith("Font")) path.endsWith(".ttf") else path.endsWith(".png") || path.endsWith(".webp"))}
    }

    /** Validate the entire archive before publishing the new immutable installation. */
    fun install(zip: File, root: File, listing: AssetListing, appCode: Int,
        validateAssets: (AssetManifest, File) -> Unit = { _, _ -> },
    ): Pair<AssetManifest, File> {
        require(zip.length() in 1..MAX_ZIP && zip.length() == listing.bytes) { "Incomplete asset pack download." }
        require(sha256(zip.readBytes()) == listing.sha256) { "Asset checksum does not match." }
        root.mkdirs()
        val staging = File(root, ".staging-${UUID.randomUUID()}").apply { mkdirs() }
        try {
            val manifest = ZipFile(zip).use { archive ->
                val entries = archive.entries().asSequence().take(130).toList()
                require(entries.size in 1..129 && entries.none { it.isDirectory }) { "Invalid asset pack archive." }
                val names = entries.map { it.name }
                require(names.size == names.toSet().size && names.all { it == "assets.json" || safePath(it) })
                val entry = archive.getEntry("assets.json") ?: error("Missing asset pack manifest.")
                val data = archive.getInputStream(entry).use { readBounded(it, 65536) }
                require(data.size <= 65536)
                val m = json.decodeFromString<AssetManifest>(data.decodeToString())
                validate(m, appCode)
                require(m.id == listing.id && m.version == listing.version && m.minAppCode == listing.minAppCode)
                require(names.toSet() == m.files.keys + "assets.json") { "Unexpected asset pack files." }
                var total = 0L
                for ((path, hash) in m.files) {
                    val e = archive.getEntry(path)
                    require(e.size in 1..MAX_FILE)
                    val bytes = archive.getInputStream(e).use { readBounded(it, MAX_FILE.toInt()) }
                    total += bytes.size
                    require(bytes.size.toLong() == e.size && total <= MAX_TOTAL)
                    require(sha256(bytes) == hash) { "Damaged asset pack asset." }
                    File(staging, path).apply { parentFile!!.mkdirs(); writeBytes(bytes) }
                }
                File(staging, "assets.json").writeBytes(data)
                m
            }
            validateAssets(manifest, staging)
            val target = File(root, "${manifest.id}-v${manifest.version}")
            if (target.exists()) {
                require(File(target,"assets.json").readBytes().contentEquals(File(staging,"assets.json").readBytes()))
                // Repair damaged local data only after a completely validated re-download.
                val backup = File(root, ".old-${UUID.randomUUID()}")
                require(target.renameTo(backup))
                if (!staging.renameTo(target)) { backup.renameTo(target); error("Unable to install asset pack.") }
                backup.deleteRecursively()
            } else require(staging.renameTo(target)) { "Unable to install asset pack." }
            return manifest to target
        } finally { staging.deleteRecursively() }
    }
}
