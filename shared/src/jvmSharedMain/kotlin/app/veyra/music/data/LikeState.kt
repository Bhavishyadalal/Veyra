package app.veyra.music.data

import app.veyra.music.data.model.LikeStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Ratings changed during this app session, shared by the UI and playback service.
 *
 * The library remains the source for ratings that were already known at load time;
 * these overrides win over it so a notification tap and a player-screen tap paint
 * the same result immediately.
 */
object LikeState {
    private val editsLock = Any()
    private var nextEdit = 0L
    private var session = 0L
    private val edits = mutableMapOf<String, Long>()
    private val confirmed = mutableMapOf<String, Pair<Long, LikeStatus>>()
    data class Edit internal constructor(val videoId: String, internal val revision: Long, internal val session: Long)
    private val _overrides = MutableStateFlow<Map<String, LikeStatus>>(emptyMap())
    val overrides: StateFlow<Map<String, LikeStatus>> = _overrides.asStateFlow()

    fun set(videoId: String, status: LikeStatus) = synchronized(editsLock) {
        val revision = ++nextEdit
        edits[videoId] = revision
        confirmed[videoId] = revision to status
        _overrides.update { it + (videoId to status) }
    }

    /** Shared by player and notification actions; even LIKE → neutral → LIKE is a new intent. */
    fun beginEdit(videoId: String, status: LikeStatus, previous: LikeStatus? = null): Edit = synchronized(editsLock) {
        confirmed.getOrPut(videoId) { 0L to (previous ?: _overrides.value[videoId] ?: LikeStatus.INDIFFERENT) }
        val edit = Edit(videoId, ++nextEdit, session)
        edits[videoId] = edit.revision
        _overrides.update { it + (videoId to status) }
        edit
    }

    fun isCurrent(edit: Edit): Boolean = synchronized(editsLock) { edits[edit.videoId] == edit.revision }

    /** Record the last successful server write even if another optimistic edit is already visible. */
    fun confirm(edit: Edit, status: LikeStatus) = synchronized(editsLock) {
        if (edit.session == session && edit.revision > (confirmed[edit.videoId]?.first ?: -1L))
            confirmed[edit.videoId] = edit.revision to status
    }

    /** A failure from an older edit or a signed-out account cannot roll back the current rating. */
    fun rollback(edit: Edit, previous: LikeStatus): Boolean = synchronized(editsLock) {
        if (edits[edit.videoId] != edit.revision) return@synchronized false
        edits[edit.videoId] = ++nextEdit
        val restored = confirmed[edit.videoId]?.second ?: previous
        _overrides.update { it + (edit.videoId to restored) }
        true
    }

    /**
     * Records a rating read off a track's own menu, but only when the menu
     * actually states one. A missing like button or an absent rating (null)
     * must never overwrite what this session already knows — kept null
     * rather than INDIFFERENT so the two stay distinct — and an explicit
     * override already made this session always wins.
     */
    fun rememberStated(videoId: String, stated: LikeStatus?) {
        if (stated != null && stated != LikeStatus.INDIFFERENT) {
            _overrides.update {current -> if(videoId in current)current else current + (videoId to stated)}
        }
    }

    /** Seeds only ratings not already changed explicitly during this session. */
    fun seedLiked(videoIds: Set<String>) {
        if (videoIds.isEmpty()) return
        _overrides.update {current ->
            val next=current.toMutableMap()
            videoIds.forEach { next.putIfAbsent(it, LikeStatus.LIKE) }
            if(next==current)current else next
        }
    }

    fun clear() = synchronized(editsLock) {
        edits.clear()
        confirmed.clear()
        session++
        _overrides.value = emptyMap()
    }
}
