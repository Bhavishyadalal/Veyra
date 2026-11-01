package app.veyra.music.data.themes

import java.io.File
import java.security.MessageDigest

/** Validate bounded bytes without allocating entire artwork/font files during startup. */
internal fun verifiedPackFileHash(file: File, maxBytes: Long): String {
    val length = file.length()
    require(length in 1..maxBytes)
    val digest = MessageDigest.getInstance("SHA-256")
    var read = 0L
    file.inputStream().use { input ->
        val buffer = ByteArray(minOf(length, 64L * 1024).toInt())
        while (true) {
            val count = input.read(buffer)
            if (count < 0) break
            read += count
            require(read <= maxBytes)
            digest.update(buffer, 0, count)
        }
    }
    require(read == length)
    return digest.digest().joinToString("") { "%02x".format(it) }
}
