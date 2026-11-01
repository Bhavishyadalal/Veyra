package app.veyra.music.data.themes

import org.junit.Assert.*
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.Rule
import java.security.MessageDigest

class PackFileHashTest {
    @get:Rule val temp = TemporaryFolder()

    @Test fun streamedHashMatchesWholeFileAcrossMultipleBuffers() {
        val bytes = ByteArray(256 * 1024 + 13) { (it * 31).toByte() }
        val file = temp.newFile().apply { writeBytes(bytes) }
        val expected = MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }
        assertEquals(expected, verifiedPackFileHash(file, bytes.size.toLong()))
        file.appendBytes(byteArrayOf(1))
        assertNotEquals(expected, verifiedPackFileHash(file, file.length()))
    }

    @Test fun missingEmptyAndOversizedFilesAreRejected() {
        for (size in listOf(0, 1025)) {
            val file = temp.newFile().apply { writeBytes(ByteArray(size)) }
            assertThrows(IllegalArgumentException::class.java) { verifiedPackFileHash(file, 1024) }
        }
        assertThrows(IllegalArgumentException::class.java) { verifiedPackFileHash(java.io.File(temp.root, "missing"), 1024) }
    }
}
