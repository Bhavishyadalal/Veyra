package app.veyra.music.data.settings

import android.content.SharedPreferences
import java.lang.reflect.Proxy
import org.junit.*
import org.junit.Assert.*

class VisualEffectsSettingsTest {
    private val values = mutableMapOf<String, Boolean>()
    private var previous: Any? = null
    private val prefsField = AppSettings::class.java.getDeclaredField("prefs").apply { isAccessible = true }

    @Before fun installMemoryPreferences() {
        previous = prefsField.get(AppSettings)
        val editor = Proxy.newProxyInstance(SharedPreferences.Editor::class.java.classLoader,
            arrayOf(SharedPreferences.Editor::class.java)) { proxy, method, args ->
            when (method.name) {
                "putBoolean" -> { values[args!![0] as String] = args[1] as Boolean; proxy }
                "commit" -> true
                "apply" -> null
                else -> if (method.returnType == SharedPreferences.Editor::class.java) proxy else null
            }
        }
        val prefs = Proxy.newProxyInstance(SharedPreferences::class.java.classLoader,
            arrayOf(SharedPreferences::class.java)) { _, method, args ->
            when (method.name) {
                "edit" -> editor
                "getBoolean" -> values[args!![0]] ?: args[1]
                "contains" -> values.containsKey(args!![0])
                "getAll" -> values.toMap()
                else -> null
            }
        }
        prefsField.set(AppSettings, prefs)
        AppSettings.reloadVisualEffectsPreferences()
    }

    @After fun restore() {
        values.clear()
        AppSettings.reloadVisualEffectsPreferences()
        AppSettings.setHighPerformanceMode(false)
        prefsField.set(AppSettings, previous)
    }

    @Test fun masterOverridesEffectsAndRestoresTheExactSavedChoices() {
        AppSettings.setReduceAnimation(false)
        AppSettings.setReduceDynamicBlur(false)
        AppSettings.setLiquidGlass(true)
        AppSettings.setAnimatedCanvas(true)
        AppSettings.setLyricsBlur(true)
        AppSettings.setReduceVisualEffects(true)
        assertTrue(AppSettings.reduceAnimation.value)
        assertTrue(AppSettings.reduceDynamicBlur.value)
        assertFalse(AppSettings.liquidGlass.value)
        assertFalse(AppSettings.animatedCanvas.value)
        assertFalse(AppSettings.lyricsBlur.value)
        assertEquals(false, values["reduce_dynamic_blur"])
        assertEquals(true, values["liquid_glass"])
        AppSettings.setReduceVisualEffects(false)
        assertFalse(AppSettings.reduceAnimation.value)
        assertFalse(AppSettings.reduceDynamicBlur.value)
        assertTrue(AppSettings.liquidGlass.value)
        assertTrue(AppSettings.animatedCanvas.value)
        assertTrue(AppSettings.lyricsBlur.value)
    }

    @Test fun refreshRatePreferenceAndEffectsRemainIndependentInBothDirections() {
        AppSettings.setReduceVisualEffects(true)
        AppSettings.setHighPerformanceMode(true)
        assertTrue(AppSettings.highPerformanceMode.value)
        assertTrue(AppSettings.reduceVisualEffects.value)
        assertTrue(AppSettings.reduceDynamicBlur.value)
        assertTrue(AppSettings.reduceAnimation.value)
        AppSettings.setReduceDynamicBlur(false)
        AppSettings.setReduceAnimation(false)
        assertTrue(AppSettings.highPerformanceMode.value)
        assertTrue(AppSettings.reduceDynamicBlur.value)
        assertTrue(AppSettings.reduceAnimation.value)
        AppSettings.setReduceVisualEffects(false)
        assertTrue(AppSettings.highPerformanceMode.value)
        assertFalse(AppSettings.reduceDynamicBlur.value)
        assertFalse(AppSettings.reduceAnimation.value)
    }

    @Test fun persistedChoiceAndOverrideReloadTogether() {
        values["reduce_visual_effects"] = true
        values["visual_effects_choice_made"] = true
        values["reduce_dynamic_blur"] = false
        values["animated_canvas"] = true
        AppSettings.reloadVisualEffectsPreferences()
        assertTrue(AppSettings.reduceVisualEffects.value)
        assertTrue(AppSettings.visualEffectsChoiceMade.value)
        assertTrue(AppSettings.reduceDynamicBlur.value)
        assertFalse(AppSettings.animatedCanvas.value)
        AppSettings.setReduceVisualEffects(false)
        assertFalse(AppSettings.reduceDynamicBlur.value)
        assertTrue(AppSettings.animatedCanvas.value)
    }

    @Test fun dismissingSuggestionPreservesModeAndDoesNotRepromptAfterReload() {
        assertFalse(AppSettings.visualEffectsChoiceMade.value)
        AppSettings.dismissVisualEffectsSuggestion()
        AppSettings.reloadVisualEffectsPreferences()
        assertTrue(AppSettings.visualEffectsChoiceMade.value)
        assertFalse(AppSettings.reduceVisualEffects.value)
    }
}
