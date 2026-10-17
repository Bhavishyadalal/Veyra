package app.veyra.music

import app.veyra.music.data.listentogether.GuestPartyIdentity
import app.veyra.music.data.listentogether.partyDisplayName
import org.junit.Assert.*
import org.junit.Test
import java.util.Locale

class GuestPartyIdentityTest {
    @Test fun guestIdentitySurvivesNicknameChangesAndRecreation() {
        val original = GuestPartyIdentity.forDevice("install-one", "Alice")
        assertEquals(original, GuestPartyIdentity.forDevice("install-one", "Alice"))
        assertEquals(original.userId, GuestPartyIdentity.forDevice("install-one", "Bob").userId)
        assertNotEquals(original.userId, GuestPartyIdentity.forDevice("install-two", "Alice").userId)
        assertFalse(original.userId.contains("install-one"))
        assertTrue(original.userId.matches(Regex("guest_[a-f0-9]{32}")))
    }

    @Test fun guestsCanJoinWithoutChoosingANickname() {
        val identity = GuestPartyIdentity.forDevice("install", "  ")
        assertTrue(identity.name.matches(Regex("Guest [A-F0-9]{4}")))
        assertEquals(identity.name, GuestPartyIdentity.forDevice("install", "").name)
    }

    @Test fun namesFitTheServerUtf8LimitWithoutSplittingEmoji() {
        assertEquals("Alice", partyDisplayName("  Alice  "))
        assertEquals(80, partyDisplayName("a".repeat(120)).length)
        val emoji = "🎵".repeat(30)
        val result = partyDisplayName(emoji)
        assertEquals("🎵".repeat(20), result)
        assertEquals(80, result.toByteArray(Charsets.UTF_8).size)
        assertTrue(partyDisplayName("界".repeat(40)).toByteArray(Charsets.UTF_8).size <= 80)
    }

    @Test fun identityDoesNotDependOnThePhoneLocale() {
        val previous = Locale.getDefault()
        try {
            val english = GuestPartyIdentity.forDevice("device-i", "")
            Locale.setDefault(Locale.forLanguageTag("tr"))
            assertEquals(english, GuestPartyIdentity.forDevice("device-i", ""))
        } finally { Locale.setDefault(previous) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun emptyInstallationIdIsRejected() { GuestPartyIdentity.forDevice("  ", "Guest") }
}
