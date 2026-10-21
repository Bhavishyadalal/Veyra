package app.veyra.music.download

import java.io.InputStream

/** Small format sniffing reads compatible with Android 8+, including short reads/EOF. */
internal fun InputStream.readProbePrefix(limit: Int): ByteArray {
    require(limit in 0..4096)
    val bytes = ByteArray(limit)
    var size = 0
    while (size < limit) {
        val read = read(bytes, size, limit - size)
        if (read < 0) break
        if (read == 0) {
            val byte = read()
            if (byte < 0) break
            bytes[size++] = byte.toByte()
        } else size += read
    }
    return bytes.copyOf(size)
}
