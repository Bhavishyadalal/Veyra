package app.veyra.music.data.themes

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import java.security.MessageDigest
import java.util.UUID
import java.util.zip.ZipFile
import kotlin.math.pow

@Serializable
data class ThemePalette(
    val background: String, val surface: String, val raised: String,
    val accent: String, val accentContainer: String, val text: String,
    val muted: String, val outline: String, val secondary: String,
)
@Serializable
data class ThemeManifest(
    val schema: Int, val id: String, val name: String, val author: String,
    val version: Int, val minAppCode: Int, val palette: ThemePalette,
    val rowCorner: Int, val buttonCorner: Int, val navCorner: Int,
    val assets: Map<String, String>, val files: Map<String, String>,
    val seekStyle: String = "plain", val seekHeight: Int = 7,
    val sceneStyle: String = "mist", val tagline: String = "A different kind of night.",
)
@Serializable
data class ThemeCatalog(val schema: Int, val themes: List<ThemeListing>)
@Serializable
data class ThemeListing(
    val id: String, val name: String, val description: String,
    val version: Int, val minAppCode: Int, val url: String,
    val sha256: String, val bytes: Long, val preview: String,
)

/** Data-only packs: no scripts, remote asset references, layouts or executable code. */
object ThemePack {
    val json = Json { ignoreUnknownKeys = false }
    const val MAX_ZIP = 12L * 1024 * 1024
    const val MAX_TOTAL = 32L * 1024 * 1024
    const val MAX_FILE = 8L * 1024 * 1024
    private val pathPattern = Regex("[a-zA-Z0-9_-]+(?:/[a-zA-Z0-9_.-]+)+")
    private val hashPattern = Regex("[a-f0-9]{64}")
    val assetKeys = setOf("header", "player", "preview", "headingFont", "bodyFont",
        "nav_home", "nav_search", "nav_explore", "nav_library", "play", "pause", "previous", "next",
        "queue", "lyrics", "settings", "back", "download", "brandIcon")

    fun sha256(bytes: ByteArray): String = MessageDigest.getInstance("SHA-256")
        .digest(bytes).joinToString("") { "%02x".format(it) }

    fun readBounded(input: InputStream, max: Int): ByteArray {
        val output = java.io.ByteArrayOutputStream()
        val buffer = ByteArray(8192)
        while (true) {
            val count = input.read(buffer)
            if (count < 0) break
            require(output.size() + count <= max) { "Theme file exceeds its size limit." }
            output.write(buffer, 0, count)
        }
        return output.toByteArray()
    }

    fun safePath(path: String): Boolean = path.length <= 120 && pathPattern.matches(path) &&
        path.split('/').none { it == "." || it == ".." || it.isEmpty() } &&
        path.substringAfterLast('.').lowercase() in setOf("webp", "png", "ttf", "txt")

    fun validate(manifest: ThemeManifest, appCode: Int) {
        require(manifest.schema in 1..2) { "This theme needs an unsupported theme engine." }
        require(manifest.id.matches(Regex("[a-z0-9][a-z0-9-]{0,47}"))) { "Invalid theme ID." }
        require(manifest.name.length in 1..60 && manifest.author.length in 1..80)
        require(manifest.version in 1..10000 && manifest.minAppCode in 23..appCode) { "Update Veyra before installing this theme." }
        require(manifest.rowCorner in 8..28 && manifest.buttonCorner in 12..36 && manifest.navCorner in 18..48)
        require(manifest.files.size in 1..48 && manifest.files.all { safePath(it.key) && hashPattern.matches(it.value) })
        require(manifest.assets.keys.all { it in assetKeys })
        require(manifest.sceneStyle in setOf("mist", "web"))
        require(manifest.tagline.length in 1..80 && manifest.tagline.none { it.isISOControl() })
        if (manifest.schema == 1) {
            require("brandIcon" !in manifest.assets && manifest.sceneStyle == "mist" && manifest.seekStyle != "web")
        } else require(manifest.minAppCode >= 24) { "This theme needs the updated theme engine." }
        require(manifest.assets.keys.containsAll(setOf("header", "player", "preview", "headingFont", "bodyFont")))
        require(manifest.assets.values.all { it in manifest.files })
        for ((key, path) in manifest.assets) {
            require(if (key.endsWith("Font")) path.endsWith(".ttf") else path.endsWith(".png") || path.endsWith(".webp"))
        }
        require(manifest.seekStyle in setOf("plain", "ember", "web") && manifest.seekHeight in 6..12)
        val p = manifest.palette
        listOf(p.background,p.surface,p.raised,p.accent,p.accentContainer,p.text,p.muted,p.outline,p.secondary)
            .forEach { require(it.matches(Regex("#[0-9A-Fa-f]{6}"))) { "Invalid theme color." } }
        require(contrast(p.text, p.background) >= 7 && contrast(p.text, p.surface) >= 7)
        require(contrast(p.muted, p.surface) >= 4.5 && contrast(p.muted, p.raised) >= 4.5)
        require(contrast(p.accent, "#101010") >= 4.5 && contrast(p.text, p.accentContainer) >= 4.5)
    }

    private fun luminance(hex: String): Double {
        val n = hex.removePrefix("#").toInt(16)
        fun channel(shift: Int): Double {
            val s = ((n shr shift) and 255) / 255.0
            return if (s <= 0.04045) s / 12.92 else ((s + 0.055) / 1.055).pow(2.4)
        }
        return .2126 * channel(16) + .7152 * channel(8) + .0722 * channel(0)
    }
    fun contrast(a: String, b: String): Double {
        val x = luminance(a); val y = luminance(b)
        return (maxOf(x,y) + .05) / (minOf(x,y) + .05)
    }

    /** Validate the entire archive before publishing the new immutable installation. */
    fun install(zip: File, root: File, listing: ThemeListing, appCode: Int,
        validateAssets: (ThemeManifest, File) -> Unit = { _, _ -> },
    ): Pair<ThemeManifest, File> {
        require(zip.length() in 1..MAX_ZIP && zip.length() == listing.bytes) { "Incomplete theme download." }
        require(sha256(zip.readBytes()) == listing.sha256) { "Theme checksum does not match." }
        root.mkdirs()
        val staging = File(root, ".staging-${UUID.randomUUID()}").apply { mkdirs() }
        try {
            val manifest = ZipFile(zip).use { archive ->
                val entries = archive.entries().asSequence().take(50).toList()
                require(entries.size in 1..49 && entries.none { it.isDirectory }) { "Invalid theme archive." }
                val names = entries.map { it.name }
                require(names.size == names.toSet().size && names.all { it == "theme.json" || safePath(it) })
                val entry = archive.getEntry("theme.json") ?: error("Missing theme manifest.")
                val data = archive.getInputStream(entry).use { readBounded(it, 65536) }
                require(data.size <= 65536)
                val m = json.decodeFromString<ThemeManifest>(data.decodeToString())
                validate(m, appCode)
                require(m.id == listing.id && m.version == listing.version && m.minAppCode == listing.minAppCode)
                require(names.toSet() == m.files.keys + "theme.json") { "Unexpected theme files." }
                var total = 0L
                for ((path, hash) in m.files) {
                    val e = archive.getEntry(path)
                    require(e.size in 1..MAX_FILE)
                    val bytes = archive.getInputStream(e).use { readBounded(it, MAX_FILE.toInt()) }
                    total += bytes.size
                    require(bytes.size.toLong() == e.size && total <= MAX_TOTAL)
                    require(sha256(bytes) == hash) { "Damaged theme asset." }
                    File(staging, path).apply { parentFile!!.mkdirs(); writeBytes(bytes) }
                }
                File(staging, "theme.json").writeBytes(data)
                m
            }
            validateAssets(manifest, staging)
            val target = File(root, "${manifest.id}-v${manifest.version}")
            if (target.exists()) {
                require(File(target,"theme.json").readBytes().contentEquals(File(staging,"theme.json").readBytes()))
                // Repair damaged local data only after a completely validated re-download.
                val backup = File(root, ".old-${UUID.randomUUID()}")
                require(target.renameTo(backup))
                if (!staging.renameTo(target)) { backup.renameTo(target); error("Unable to install theme.") }
                backup.deleteRecursively()
            } else require(staging.renameTo(target)) { "Unable to install theme." }
            return manifest to target
        } finally { staging.deleteRecursively() }
    }
}
