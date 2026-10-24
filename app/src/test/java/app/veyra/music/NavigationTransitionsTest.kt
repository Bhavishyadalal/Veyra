package app.veyra.music

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.SnapSpec
import androidx.compose.ui.unit.IntSize
import app.veyra.music.ui.NavigationMotion
import app.veyra.music.ui.navigationTransition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import app.veyra.music.data.settings.AppMotionStyle
import app.veyra.music.data.settings.MotionTempo

class NavigationTransitionsTest {
    @Test fun reducedMotionAndDisabledDestinationsOverrideEveryCustomProfile() {
        for(style in AppMotionStyle.entries)for(tempo in MotionTempo.entries)for(rtl in listOf(false,true)) {
            val off=navigationTransition(NavigationMotion.Forward,true,rtl,style=style,tempo=tempo)
            assertEquals(EnterTransition.None,off.targetContentEnter)
            assertEquals(ExitTransition.None,off.initialContentExit)
            val disabled=navigationTransition(NavigationMotion.None,false,rtl,style=style,tempo=tempo)
            assertEquals(EnterTransition.None,disabled.targetContentEnter)
            val active=navigationTransition(NavigationMotion.Forward,false,rtl,style=style,tempo=tempo)
            assertTrue(active.sizeTransform!!.createAnimationSpec(IntSize(390,844),IntSize(844,390)) is SnapSpec<*>)
        }
    }
    @Test fun defaultStyleKeepsThemeTransitionWhenPaceChanges() {
        val slow=navigationTransition(NavigationMotion.Forward,false,false,materialStyle="punk_sticker",tempo=MotionTempo.RELAXED)
        val fast=navigationTransition(NavigationMotion.Forward,false,false,materialStyle="punk_sticker",tempo=MotionTempo.QUICK)
        assertTrue(slow.targetContentEnter!=EnterTransition.None)
        assertTrue(fast.targetContentEnter!=EnterTransition.None)
        assertTrue(slow.targetContentEnter!=fast.targetContentEnter)
    }
    @Test fun subtleMotionStillHasAFadeButOffAlwaysWins() {
        val subtle = navigationTransition(NavigationMotion.Forward, false, false, subtleMotion = true)
        assertTrue(subtle.targetContentEnter != EnterTransition.None)
        val off = navigationTransition(NavigationMotion.Forward, true, false, subtleMotion = true)
        assertEquals(EnterTransition.None, off.targetContentEnter)
        assertEquals(ExitTransition.None, off.initialContentExit)
    }

    @Test fun reducedMotionDisablesEveryPageEnterAndExitIncludingRtl() {
        for (rtl in listOf(false, true)) for (motion in NavigationMotion.entries) {
            val transition = navigationTransition(motion, reduceMotion = true, rtl = rtl)
            assertEquals(EnterTransition.None, transition.targetContentEnter)
            assertEquals(ExitTransition.None, transition.initialContentExit)
        }
    }

    @Test fun viewportChangesNeverSpringFullScreenListsThroughIntermediateSizes() {
        for (motion in NavigationMotion.entries) {
            val size = navigationTransition(motion, false, false).sizeTransform!!
            assertTrue(size.createAnimationSpec(IntSize(390, 844), IntSize(844, 390)) is SnapSpec<*>)
        }
    }

    @Test fun backRevealsTheRetainedParentWhileForwardDrawsTheNewPageAboveIt() {
        assertEquals(-1f, navigationTransition(NavigationMotion.Back, false, false).targetContentZIndex)
        assertEquals(1f, navigationTransition(NavigationMotion.Forward, false, false).targetContentZIndex)
        assertEquals(1f, navigationTransition(NavigationMotion.Tab, false, false).targetContentZIndex)
    }
}
