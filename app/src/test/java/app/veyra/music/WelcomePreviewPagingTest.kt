package app.veyra.music

import app.veyra.music.ui.onboarding.nextWelcomePreview
import app.veyra.music.ui.onboarding.welcomePreviewAfterSwipe
import org.junit.Assert.assertEquals
import org.junit.Test

class WelcomePreviewPagingTest {
    @Test fun forwardCyclesToTheFirstPreview() { assertEquals(0, nextWelcomePreview(2)) }
    @Test fun backwardsCyclesToTheLastPreview() { assertEquals(2, nextWelcomePreview(0, false)) }
    @Test fun shortDragDoesNotNavigate() { assertEquals(1, welcomePreviewAfterSwipe(1, -47f, 48f, false)) }
    @Test fun leftSwipeAdvancesInLtr() { assertEquals(2, welcomePreviewAfterSwipe(1, -60f, 48f, false)) }
    @Test fun rightSwipeAdvancesInRtl() { assertEquals(2, welcomePreviewAfterSwipe(1, 60f, 48f, true)) }
    @Test fun oppositeSwipeReturnsToPreviousPreview() { assertEquals(0, welcomePreviewAfterSwipe(1, 60f, 48f, false)) }
    @Test fun invalidGesturesDoNotNavigate() {
        assertEquals(1, welcomePreviewAfterSwipe(1, Float.NaN, 48f, false))
        assertEquals(1, welcomePreviewAfterSwipe(1, 80f, 0f, false))
        assertEquals(1, welcomePreviewAfterSwipe(1, 80f, Float.POSITIVE_INFINITY, false))
    }
}
