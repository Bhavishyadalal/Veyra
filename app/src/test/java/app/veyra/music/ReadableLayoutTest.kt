package app.veyra.music

import app.veyra.music.ui.layout.stackForReadability
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReadableLayoutTest {
    @Test fun widePhoneWithLargeTextUsesStackedControls() {
        assertTrue(stackForReadability(400f, 2f, 300f))
        assertTrue(stackForReadability(368f, 1.3f, 300f))
        assertFalse(stackForReadability(400f, 1f, 300f))
    }
    @Test fun compactTimelineLeavesQualityItsOwnLine() {
        assertTrue(stackForReadability(288f, 1f, 320f))
        assertTrue(stackForReadability(360f, 1.2f, 320f))
        assertFalse(stackForReadability(360f, 1f, 320f))
    }
    @Test fun invalidMeasurementIsConservativeAndInvalidScaleUsesSystemDefault() {
        assertTrue(stackForReadability(Float.NaN, 1f, 300f))
        assertTrue(stackForReadability(0f, 1f, 300f))
        assertFalse(stackForReadability(400f, Float.NaN, 300f))
    }
}
