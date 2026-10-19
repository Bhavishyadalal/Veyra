package app.veyra.music.ui.screens

import java.util.Locale

/** Stable per-title occurrence keys; repeated upstream headings must never crash the feed. */
internal fun homeShelfKeys(titles: List<String>): List<String> {
    val occurrences = mutableMapOf<String, Int>()
    return titles.map { title ->
        val normalized = title.lowercase(Locale.ROOT)
        val occurrence = occurrences.getOrDefault(normalized, 0)
        occurrences[normalized] = occurrence + 1
        "home:shelf:${normalized.length}:$normalized:$occurrence"
    }
}
