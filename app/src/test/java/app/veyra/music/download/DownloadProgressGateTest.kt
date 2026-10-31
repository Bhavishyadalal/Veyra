package app.veyra.music.download

import org.junit.Assert.*
import org.junit.Test

class DownloadProgressGateTest {
    @Test fun `unknown lengths are finite and duplicate callbacks stay quiet`() {
        val gate = DownloadProgressGate { 0L }
        assertEquals(0f, gate.update(1, 0))
        assertNull(gate.update(2, -1))
    }

    @Test fun `frequent byte callbacks are bounded but completion publishes immediately`() {
        var now = 0L
        val gate = DownloadProgressGate { now }
        assertEquals(0.1f, gate.update(10, 100))
        now = 30
        assertNull(gate.update(20, 100))
        now = 125
        assertEquals(0.3f, gate.update(30, 100))
        now = 126
        assertEquals(1f, gate.update(100, 100))
        assertNull(gate.update(101, 100))
    }

    @Test fun `negative and oversized counts never escape progress bounds`() {
        assertEquals(0f, DownloadProgressGate().update(-4, 10))
        assertEquals(1f, DownloadProgressGate().update(Long.MAX_VALUE, 1))
    }
}
