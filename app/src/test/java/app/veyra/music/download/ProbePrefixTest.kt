package app.veyra.music.download

import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayInputStream

class ProbePrefixTest {
    @Test fun `probe is bounded and leaves subsequent audio unread`() {
        val input = ByteArrayInputStream(ByteArray(80) { it.toByte() })
        assertArrayEquals(ByteArray(32) { it.toByte() }, input.readProbePrefix(32))
        assertEquals(32, input.read())
    }
    @Test fun `short network reads and early EOF retain actual header bytes`() {
        val input = object : ByteArrayInputStream("fLaC".toByteArray()) {
            override fun read(b: ByteArray, off: Int, len: Int): Int = super.read(b, off, minOf(1, len))
        }
        assertArrayEquals("fLaC".toByteArray(), input.readProbePrefix(32))
        assertTrue(input.readProbePrefix(32).isEmpty())
    }
    @Test fun `zero length reads cannot trap the probe in a busy loop`() {
        val input = object : ByteArrayInputStream("ab".toByteArray()) {
            override fun read(b: ByteArray, off: Int, len: Int): Int = 0
        }
        assertArrayEquals("ab".toByteArray(), input.readProbePrefix(32))
    }
}
