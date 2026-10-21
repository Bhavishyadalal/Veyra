package app.veyra.music

import app.veyra.music.ui.styles.*
import app.veyra.music.ui.themes.ThemeVisuals
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import org.junit.Assert.*
import org.junit.Test

class AppStylePolicyTest {
    @Test fun originalIsTheSafeDefaultAndAllChoicesRoundTrip() {
        listOf(null,"","deleted-style","after_dark").forEach {assertEquals(AppStyle.ORIGINAL,AppStyle.fromStoredName(it))}
        AppStyle.entries.forEach {assertEquals(it,AppStyle.fromStoredName(it.name))}
    }
    @Test fun customStylesSuspendDownloadedArtWithoutReplacingTheSavedObject() {
        val saved=ThemeVisuals("saved","Saved",Color.White,Color.Black,Color.Black,Color.Gray,12,12,12,
            mapOf("header" to "downloaded-art", "headingFont" to "downloaded-font"))
        AppStyle.entries.filter {it.custom}.forEach {style->
            val effective=effectiveStyleVisuals(style,saved)!!
            assertNotEquals(saved.id,effective.id)
            assertFalse(effective.assets.values.contains("downloaded-art"))
            assertFalse(effective.assets.values.contains("downloaded-font"))
            assertEquals(style.title,effective.name)
        }
        assertSame(saved,effectiveStyleVisuals(AppStyle.ORIGINAL,saved))
        assertNull(effectiveStyleVisuals(AppStyle.ORIGINAL,null))
    }
    @Test fun everyStyleHasReadableBodyMutedAndButtonColors() {
        fun contrast(a:Color,b:Color):Float {val x=a.luminance();val y=b.luminance();return (maxOf(x,y)+.05f)/(minOf(x,y)+.05f)}
        AppStyle.entries.filter {it.custom}.forEach {style->
            val c=style.colorScheme()
            for((fg,bg) in listOf(c.onBackground to c.background,c.onSurface to c.surface,
                c.onSurfaceVariant to c.surfaceVariant,c.onPrimary to c.primary)) {
                assertTrue("${style.name}: contrast ${contrast(fg,bg)}",contrast(fg,bg)>=4.5f)
            }
        }
    }
    @Test fun onlyListeningRoomNeedsBundledArtAndNeverInheritsAThemePortrait() {
        assertTrue(AppStyle.AFTER_DARK.visuals()!!.assets.isEmpty())
        assertTrue(AppStyle.MUSIC_EDITORIAL.visuals()!!.assets.isEmpty())
        val room=AppStyle.LISTENING_ROOM.visuals(mapOf("header" to "local-botanical.webp"))!!
        assertEquals("local-botanical.webp",room.asset("header"))
    }
    @Test fun leavingListeningRoomRejectsItsRetainedArtworkDuringStyleSwitch() {
        val retained = mapOf("header" to "local-botanical.webp")
        assertEquals(retained, effectiveStyleVisuals(AppStyle.LISTENING_ROOM, null, retained)!!.assets)
        for (style in listOf(AppStyle.MUSIC_EDITORIAL, AppStyle.AFTER_DARK)) {
            assertTrue(effectiveStyleVisuals(style, null, retained)!!.assets.isEmpty())
        }
        assertNull(effectiveStyleVisuals(AppStyle.ORIGINAL, null, retained))
    }
}
