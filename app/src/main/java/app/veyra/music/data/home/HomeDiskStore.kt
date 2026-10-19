package app.veyra.music.data.home

import app.veyra.music.data.model.HomeShelf
import app.veyra.music.data.model.ShelfItem
import java.io.*
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest

/** Bounded private metadata only: no cookies, playback URLs, audio or continuation tokens. Call on IO. */
internal class HomeDiskStore(private val directory: File, private val now: () -> Long = System::currentTimeMillis) {
    data class Snapshot(val savedAt: Long, val shelves: List<HomeShelf>)

    @Synchronized fun read(owner: String): Snapshot? = runCatching {
        val file = file(owner, "feed")
        if (!file.isFile || file.length() > MAX_BYTES) return null
        DataInputStream(file.inputStream().buffered()).use { input ->
            check(input.readInt() == FEED_MAGIC && input.readInt() == VERSION)
            val at = input.readLong().also { check(it > 0) }
            val count = input.readInt().also { check(it in 1..MAX_SHELVES) }
            val shelves = List(count) {
                val title = input.text(); val subtitle = input.text()
                val browse = input.optional(); val params = input.optional()
                val size = input.readInt().also { check(it in 1..MAX_ITEMS) }
                HomeShelf(title, List(size) {
                    ShelfItem(input.text(), input.text(), input.optional(), input.optional(), input.optional())
                }, subtitle, browse, params)
            }
            check(input.read() == -1)
            Snapshot(at, shelves)
        }
    }.getOrNull()

    @Synchronized fun write(owner: String, shelves: List<HomeShelf>): Boolean {
        val bounded = shelves.filter { it.items.isNotEmpty() }.take(MAX_SHELVES)
        if (bounded.isEmpty()) return false // Never replace useful saved content with an empty refresh.
        return runCatching {
            atomic(file(owner, "feed")) { out ->
                out.writeInt(FEED_MAGIC); out.writeInt(VERSION); out.writeLong(now())
                out.writeInt(bounded.size)
                bounded.forEach { shelf ->
                    out.text(shelf.title); out.text(shelf.subtitle)
                    out.optional(shelf.moreBrowseId); out.optional(shelf.moreParams)
                    val items = shelf.items.take(MAX_ITEMS)
                    out.writeInt(items.size)
                    items.forEach {
                        out.text(it.title); out.text(it.subtitle); out.optional(it.thumbnailUrl)
                        out.optional(it.videoId); out.optional(it.browseId)
                    }
                }
            }
            directory.listFiles { f -> f.extension == "feed" && f != file(owner, "feed") }?.sortedByDescending { it.lastModified() }
                ?.drop(MAX_IDENTITIES - 1)?.forEach { it.delete() }
            true
        }.getOrDefault(false)
    }

    @Synchronized fun readLayout(owner: String): HomeLayout = runCatching {
        val file = file(owner, "layout")
        if (!file.isFile || file.length() > MAX_BYTES) return HomeLayout()
        DataInputStream(file.inputStream().buffered()).use { input ->
            check(input.readInt() == LAYOUT_MAGIC && input.readInt() == VERSION)
            fun ids(): List<String> = List(input.readInt().also { check(it in 0..MAX_SECTION_PREFS) }) { input.text() }.distinct()
            val result = HomeLayout(ids(), ids().toSet())
            check(input.read() == -1)
            result
        }
    }.getOrDefault(HomeLayout())

    @Synchronized fun writeLayout(owner: String, layout: HomeLayout): Boolean = runCatching {
        atomic(file(owner, "layout")) { out ->
            out.writeInt(LAYOUT_MAGIC); out.writeInt(VERSION)
            listOf(layout.order.distinct(), layout.hidden.toList()).forEach { ids ->
                check(ids.size <= MAX_SECTION_PREFS)
                out.writeInt(ids.size); ids.forEach { out.text(it) }
            }
        }
        true
    }.getOrDefault(false)

    private fun file(owner: String, suffix: String): File {
        val key = MessageDigest.getInstance("SHA-256").digest(owner.toByteArray())
            .joinToString("") { "%02x".format(it) }
        return directory.resolve("$key.$suffix")
    }

    private fun atomic(target: File, write: (DataOutputStream) -> Unit) {
        check(directory.isDirectory || directory.mkdirs())
        val temp = File.createTempFile("home-", ".tmp", directory)
        try {
            FileOutputStream(temp).use { file ->
                val out = DataOutputStream(BufferedOutputStream(file))
                write(out); out.flush(); file.fd.sync()
            }
            check(temp.length() <= MAX_BYTES)
            try {
                Files.move(temp.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
            } catch (_: AtomicMoveNotSupportedException) {
                Files.move(temp.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
        } finally { temp.delete() }
    }

    private fun DataInputStream.text(): String = readUTF().also { check(it.length <= MAX_TEXT) }
    private fun DataInputStream.optional(): String? = if (readBoolean()) text() else null
    private fun DataOutputStream.text(value: String) { check(value.length <= MAX_TEXT); writeUTF(value) }
    private fun DataOutputStream.optional(value: String?) { writeBoolean(value != null); if (value != null) text(value) }

    companion object {
        private const val VERSION = 1
        private const val FEED_MAGIC = 0x56485246
        private const val LAYOUT_MAGIC = 0x5648524c
        private const val MAX_BYTES = 1_048_576L
        private const val MAX_TEXT = 4096
        internal const val MAX_SHELVES = 24
        internal const val MAX_ITEMS = 20
        private const val MAX_IDENTITIES = 8
        private const val MAX_SECTION_PREFS = 256
        fun owner(identity: String?): String = identity?.let { "listener:$it" } ?: "guest"
    }
}
