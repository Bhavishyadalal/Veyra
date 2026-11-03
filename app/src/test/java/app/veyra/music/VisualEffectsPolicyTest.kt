package app.veyra.music

import app.veyra.music.data.settings.*
import org.junit.Assert.*
import org.junit.Test

class VisualEffectsPolicyTest {
    @Test fun allSavedCombinationsRemainUnchangedWhenFullEffectsAreAllowed() {
        for (mask in 0 until 32) {
            val saved = VisualEffectsPreferences(mask and 1 != 0, mask and 2 != 0,
                mask and 4 != 0, mask and 8 != 0, mask and 16 != 0)
            assertSame(saved, resolveVisualEffects(false, saved))
            val reduced = resolveVisualEffects(true, saved)
            assertTrue(reduced.reduceAnimation)
            assertTrue(reduced.reduceBlur)
            assertFalse(reduced.liquidGlass)
            assertFalse(reduced.animatedCanvas)
            assertFalse(reduced.lyricsBlur)
            assertEquals(saved, resolveVisualEffects(false, saved))
        }
    }
}
