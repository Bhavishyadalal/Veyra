package app.veyra.music

import androidx.compose.runtime.saveable.SaverScope
import app.veyra.music.ui.screens.SettingsCategory
import app.veyra.music.ui.screens.SettingsNavigationState
import org.junit.Assert.*
import org.junit.Test

class SettingsNavigationStateTest {
    @Test fun oneBackReturnsFromCategoryAndNextBackBelongsToHost() {
        val state = SettingsNavigationState()
        state.category = SettingsCategory.APPEARANCE
        assertTrue(state.back())
        assertNull(state.category)
        assertFalse(state.back())
    }
    @Test fun queryBackRestoresItsCategoryBeforeLeavingSettings() {
        val state = SettingsNavigationState()
        state.category = SettingsCategory.SOUND
        state.query = "quality"
        assertTrue(state.back())
        assertEquals("", state.query)
        assertEquals(SettingsCategory.SOUND, state.category)
        assertTrue(state.back())
        assertNull(state.category)
        assertFalse(state.back())
    }
    @Test fun categoryAndSearchSurviveSaveRestoreWithoutDuplicatingBackLayers() {
        val original = SettingsNavigationState().apply {
            category = SettingsCategory.APPEARANCE
            query = "font"
        }
        val scope = object : SaverScope { override fun canBeSaved(value: Any) = true }
        val saved = with(SettingsNavigationState.Saver) { scope.save(original) }!!
        val restored = SettingsNavigationState.Saver.restore(saved)!!
        assertEquals(original.category, restored.category)
        assertEquals(original.query, restored.query)
        assertTrue(restored.back())
        assertTrue(restored.back())
        assertFalse(restored.back())
    }
    @Test fun unknownSavedCategorySafelyRestoresOverview() {
        val restored = SettingsNavigationState.Saver.restore(listOf("future-category", ""))!!
        assertNull(restored.category)
        assertFalse(restored.back())
    }
}
