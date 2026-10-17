package app.veyra.music

import app.veyra.music.ui.player.CanvasRenderPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CanvasRenderPolicyTest {
    @Test fun decoderRequiresVisiblePlaybackOutsideTransitions() {
        for (playing in listOf(false, true)) {
            for (foreground in listOf(false, true)) {
                for (transitioning in listOf(false, true)) {
                    assertEquals(playing && foreground && !transitioning,
                        CanvasRenderPolicy.shouldPlay(playing, foreground, transitioning))
                }
            }
        }
    }

    @Test fun refreshNeedsActiveDecoderAndRenderedSurface() {
        assertTrue(CanvasRenderPolicy.shouldRefresh(true, true, 3000L))
        assertFalse(CanvasRenderPolicy.shouldRefresh(false, true, 3000L))
        assertFalse(CanvasRenderPolicy.shouldRefresh(true, false, 3000L))
    }

    @Test fun disabledOrInvalidIntervalsNeverStartBusyLoops() {
        for (interval in listOf(null, 0L, -1L, Long.MIN_VALUE)) {
            assertFalse(CanvasRenderPolicy.shouldRefresh(true, true, interval))
        }
    }
}
