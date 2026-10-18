package app.veyra.music.data.artwork

import java.io.File
import java.io.IOException
import java.net.URI
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest
import java.util.Properties
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/** Public artwork URL metadata. Coil separately owns the cached image bytes. */
internal class ExploreArtworkCache(
    private val directory: File,
    private val nowMs: () -> Long = System::currentTimeMillis,
) {
    private data class Entry(val url: String?, val at: Long) {
        fun fresh(now: Long): Boolean {
            val age = now - at
            return age >= 0 && age < if (url == null) EMPTY_TTL_MS else ARTWORK_TTL_MS
        }
    }

    private val locks = ConcurrentHashMap<String, Mutex>()

    /** Call from IO before publishing the grid, so cached covers never start blank. */
    fun cached(key: String): String? = read(key)?.url

    suspend fun resolve(
        key: String,
        forceRefresh: Boolean = false,
        lookup: suspend () -> String?,
    ): String? = withContext(Dispatchers.IO) {
        locks.getOrPut(key) { Mutex() }.withLock {
            val existing = read(key)
            if (!forceRefresh && existing?.fresh(nowMs()) == true) return@withLock existing.url
            try {
                val fetched = lookup()
                currentCoroutineContext().ensureActive()
                // Reject malformed answers without replacing a usable cached cover.
                if (fetched != null && !validUrl(fetched)) return@withLock existing?.url
                if (fetched == null && existing?.url != null) return@withLock existing.url
                val next = Entry(fetched, nowMs())
                // A cache-full/read-only device must still be able to show fresh art.
                try { write(key, next) } catch (_: IOException) { }
                fetched
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                existing?.url
            }
        }
    }

    private fun file(key: String): File {
        val digest = MessageDigest.getInstance("SHA-256").digest(key.toByteArray(Charsets.UTF_8))
        return File(directory, digest.joinToString("") { "%02x".format(it) } + ".properties")
    }

    private fun read(key: String): Entry? = try {
        val properties = Properties()
        file(key).inputStream().use(properties::load)
        val url = properties.getProperty("url")?.takeIf(String::isNotBlank)
        val at = properties.getProperty("at")?.toLongOrNull()
        if (properties.getProperty("version") != "1" || !properties.containsKey("url") || at == null || (url != null && !validUrl(url))) null
        else Entry(url, at)
    } catch (_: IOException) { null } catch (_: IllegalArgumentException) { null }

    private fun write(key: String, entry: Entry) {
        directory.mkdirs()
        val target = file(key)
        val temp = File.createTempFile("artwork-", ".tmp", directory)
        try {
            val properties = Properties().apply {
                setProperty("version", "1")
                setProperty("url", entry.url.orEmpty())
                setProperty("at", entry.at.toString())
            }
            temp.outputStream().buffered().use { properties.store(it, null) }
            try {
                Files.move(temp.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
            } catch (_: AtomicMoveNotSupportedException) {
                Files.move(temp.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
            directory.listFiles { file -> file.extension == "properties" }
                ?.filter { it != target }?.sortedByDescending(File::lastModified)
                ?.drop(MAX_ENTRIES - 1)?.forEach(File::delete)
        } finally {
            temp.delete()
        }
    }

    companion object {
        private const val ARTWORK_TTL_MS = 7L * 24 * 60 * 60 * 1000
        private const val EMPTY_TTL_MS = 6L * 60 * 60 * 1000
        private const val MAX_ENTRIES = 256

        /** Length prefixes distinguish params, locales and null from the empty string. */
        fun key(browseId: String, params: String?, region: String, language: String): String =
            listOf(browseId, params, region, language).joinToString("") { value ->
                if (value == null) "-1:" else "${value.length}:$value"
            }

        private fun validUrl(value: String): Boolean = try {
            val uri = URI(value)
            uri.scheme in setOf("https", "http") && !uri.host.isNullOrBlank() && uri.userInfo == null
        } catch (_: Exception) { false }
    }
}
