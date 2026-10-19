package app.veyra.music

import app.veyra.music.data.backend.VeyraBackend
import app.veyra.music.data.listentogether.JamInvite
import app.veyra.music.data.listentogether.JamInviteLink
import org.junit.Assert.*
import org.junit.Test

class BackendInviteTest {
    @Test fun officialWebInvitePinsTheOfficialService() {
        val invite = JamInviteLink.parseInvite("${VeyraBackend.ORIGIN}/invite/ab12cd")!!
        assertEquals("AB12CD", invite.code)
        assertEquals(VeyraBackend.ORIGIN, invite.serverUrl)
    }
    @Test fun customServerSchemeInviteKeepsItsDestination() {
        val invite = JamInviteLink.parseInvite("veyra://party/AB12CD?server=https%3A%2F%2Frooms.example.com%2F")!!
        assertEquals("https://rooms.example.com", invite.serverUrl)
        assertEquals("AB12CD", invite.code)
    }
    @Test fun retiredWebInviteIsNotReboundToNewService() {
        val invite = JamInviteLink.parseInvite("https://bitchord.kushagrasingh.in/invite/AB12CD")!!
        assertTrue(VeyraBackend.isRetired(invite.serverUrl))
        assertNotEquals(VeyraBackend.ORIGIN, invite.serverUrl)
    }
    @Test fun retiredSchemeInviteRemainsRecognizableForRefusal() {
        val invite = JamInviteLink.parseInvite("veyra://party/AB12CD?server=https%3A%2F%2Fbitchord-listen-together.onrender.com")!!
        assertTrue(VeyraBackend.isRetired(invite.serverUrl))
    }
    @Test fun malformedExplicitServerIsNotSilentlyDropped() {
        assertNull(JamInviteLink.parseInvite("veyra://party/AB12CD?server=https%3A%2F%2F"))
        assertNull(JamInviteLink.parseInvite("veyra://party/AB12CD?server="))
        assertNull(JamInviteLink.parseInvite("veyra://party/AB12CD?server=ftp%3A%2F%2Frooms.example.com"))
        assertNull(JamInviteLink.parseInvite("veyra://party/AB12CD?server=https%3A%2F%2Fname%3Asecret%40rooms.example.com"))
    }
    @Test fun shareLinksUseVeyraWhileDeliberateCustomServersRemainUsable() {
        assertEquals("${VeyraBackend.ORIGIN}/invite/AB12CD", JamInviteLink.url("ab12cd"))
        val url = JamInviteLink.schemeUrl("AB12CD", "https://rooms.example.com")
        assertEquals("https://rooms.example.com", JamInviteLink.parseInvite(url)?.serverUrl)
    }
    @Test fun sharedInvitesAcceptMixedCaseOfficialOriginButRejectOldOrigin() {
        assertEquals("AB12CD", JamInvite.parse("HTTPS://VEYRA-LISTEN-TOGETHER.ONRENDER.COM/invite/ab12cd"))
        assertNull(JamInvite.parse("https://bitchord.kushagrasingh.in/invite/AB12CD"))
        assertNull(JamInvite.parse("${VeyraBackend.ORIGIN}.example.com/invite/AB12CD"))
        assertNull(JamInvite.parse("http://${VeyraBackend.HOST}/invite/AB12CD"))
    }
}
