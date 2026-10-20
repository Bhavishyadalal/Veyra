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

class NavigationTransitionsTest {
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
