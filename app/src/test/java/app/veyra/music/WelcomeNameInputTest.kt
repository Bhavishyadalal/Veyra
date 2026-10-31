package app.veyra.music

import app.veyra.music.ui.onboarding.welcomeNameInput
import org.junit.Assert.*
import org.junit.Test

class WelcomeNameInputTest {
    @Test fun `emoji names keep complete code points at the limit`() {
        val name = welcomeNameInput("🎵".repeat(41), 40)
        assertEquals(40, name.codePointCount(0, name.length))
        assertEquals(name, String(name.toByteArray()))
    }
    @Test fun `pasted control characters cannot introduce hidden name lines`() {
        assertEquals("first last ", welcomeNameInput("first\nlast\u0000", 40))
        assertEquals("", welcomeNameInput("name", 0))
    }
}
