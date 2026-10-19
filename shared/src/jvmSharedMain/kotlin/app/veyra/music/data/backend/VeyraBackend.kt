package app.veyra.music.data.backend

import java.net.URI

/** Official service identity, independent of a user's custom room server. */
object VeyraBackend {
    const val ORIGIN = "https://veyra-listen-together.onrender.com"
    const val HOST = "veyra-listen-together.onrender.com"
    const val PRESENCE_ENDPOINT = "$ORIGIN/api/presence"
    const val EXPIRED_INVITE_MESSAGE =
        "This invite uses the previous listening service. Ask the host to create a new Veyra room and share a new invite."

    // Recognize old managed destinations only to retire them, never to contact them.
    private val retiredHosts = setOf(
        "bitchord-listen-together.onrender.com",
        "api.bitchord.kushagrasingh.in",
        "bitchord.kushagrasingh.in",
    )

    fun isRetired(value: String?): Boolean {
        val text = value?.trim().orEmpty()
        if (text.isEmpty()) return false
        val candidate = if (text.contains("://")) text else "https://$text"
        val host = runCatching { URI(candidate).host }.getOrNull() ?: return false
        return host.lowercase().trimEnd('.') in retiredHosts
    }

    /** Empty means use this build's official default. Deliberate self-hosted URLs survive. */
    fun migrateSavedServer(value: String?): String =
        value?.trim().orEmpty().let { if (isRetired(it)) "" else it }

    /** Never send a previous service's room code/token to the new service. */
    fun discardSavedMembership(savedServer: String?, migrationComplete: Boolean): Boolean =
        isRetired(savedServer) || (!migrationComplete && savedServer.isNullOrBlank())

    fun requireActive(value: String) {
        require(!isRetired(value)) { EXPIRED_INVITE_MESSAGE }
    }
}
