package app.veyra.music.data.listentogether

import java.security.MessageDigest

/** A party pseudonym bound to this installation; no account or hardware identifiers are uploaded. */
internal data class GuestPartyIdentity(val userId: String, val name: String) {
    companion object {
        fun forDevice(installationId: String, nickname: String): GuestPartyIdentity {
            require(installationId.isNotBlank())
            val digest = MessageDigest.getInstance("SHA-256")
                .digest("veyra-party-guest:$installationId".toByteArray(Charsets.UTF_8))
                .joinToString("") { "%02x".format(it) }
            return GuestPartyIdentity(
                userId = "guest_${digest.take(32)}",
                name = partyDisplayName(nickname).ifBlank { "Guest ${digest.take(4).uppercase(java.util.Locale.ROOT)}" },
            )
        }
    }
}

/** The party protocol limits names to 80 UTF-8 bytes, not 80 UTF-16 characters. */
internal fun partyDisplayName(value: String): String = buildString {
    val input = value.trim()
    var index = 0
    var bytes = 0
    while (index < input.length) {
        val point = Character.codePointAt(input, index)
        val part = String(Character.toChars(point))
        val count = part.toByteArray(Charsets.UTF_8).size
        if (bytes + count > 80) break
        append(part)
        bytes += count
        index += Character.charCount(point)
    }
}
