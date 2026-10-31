package app.veyra.music.download

/** Keep byte callbacks cheap: at most eight UI publications per second and always completion. */
internal class DownloadProgressGate(private val nowMillis: () -> Long = { System.nanoTime() / 1_000_000 }) {
    private var lastAt: Long? = null
    private var lastFraction = -1f

    fun update(written: Long, total: Long): Float? {
        val fraction = if (total > 0) (written.toDouble() / total).coerceIn(0.0, 1.0).toFloat() else 0f
        val now = nowMillis()
        if (fraction == lastFraction) return null
        if (lastAt != null && now - lastAt!! < 125 && fraction < 1f) return null
        lastAt = now
        lastFraction = fraction
        return fraction
    }
}
