package app.veyra.music.playback

/** Undo is valid only in the timeline left by the edit, before any other edit or track change. */
internal data class QueueUndo<T>(
    val removed: List<Pair<Int, T>>,
    val remainingIds: List<String>,
    val currentId: String?,
) {
    fun canRestore(ids: List<String>, current: String?): Boolean =
        removed.isNotEmpty() && ids == remainingIds && current == currentId
}
