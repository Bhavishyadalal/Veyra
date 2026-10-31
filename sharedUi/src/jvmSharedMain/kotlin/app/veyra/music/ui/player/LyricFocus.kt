package app.veyra.music.ui.player

import app.veyra.music.data.lyrics.LyricLine

/** Precompute interval bounds once per lyric document, not twice per animation frame. */
internal class LyricFocusIndex(private val lines: List<LyricLine>) {
    private val ordered = lines.zipWithNext().all { (a, b) -> a.timeMs <= b.timeMs }
    private val ends = LongArray(lines.size) { i ->
        val line = lines[i]
        if (!line.isGap && (line.hasKnownEnd || line.background?.hasKnownEnd == true)) line.endMs else Long.MIN_VALUE
    }
    private val prefixEnd = LongArray(lines.size).also { bounds ->
        var max = Long.MIN_VALUE
        ends.indices.forEach { i -> max = maxOf(max, ends[i]); bounds[i] = max }
    }

    fun latest(positionMs: Long): Int {
        if (!ordered) return lines.indexOfLast { it.timeMs <= positionMs }
        var low = 0
        var high = lines.size
        while (low < high) {
            val middle = (low + high) ushr 1
            if (lines[middle].timeMs <= positionMs) low = middle + 1 else high = middle
        }
        return low - 1
    }

    fun activeRows(positionMs: Long): List<Int> {
        if (!ordered) return activeLyricRows(lines, positionMs)
        val latest = latest(positionMs)
        if (latest < 0) return emptyList()
        val rows = arrayListOf(latest)
        var index = latest - 1
        while (index >= 0 && prefixEnd[index] > positionMs) {
            if (ends[index] > positionMs) rows += index
            index--
        }
        rows.reverse()
        return rows
    }
}

/** Keep every unfinished vocal visible, including overlaps spanning more than two rows. */
internal fun activeLyricRows(lines: List<LyricLine>, positionMs: Long): List<Int> {
    val latest = lines.indexOfLast { it.timeMs <= positionMs }
    if (latest < 0) return emptyList()
    return (0..latest).filter { index ->
        val line = lines[index]
        index == latest || (!line.isGap &&
            (line.hasKnownEnd || line.background?.hasKnownEnd == true) &&
            line.timeMs <= positionMs && positionMs < line.endMs)
    }
}
