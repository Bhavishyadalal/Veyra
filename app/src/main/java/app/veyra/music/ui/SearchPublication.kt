package app.veyra.music.ui

/** A late provider answer must still belong to the visible search interaction. */
internal fun searchResponseIsCurrent(requestId: Long, newestId: Long, youtube: Boolean): Boolean =
    youtube && requestId == newestId

internal fun typeaheadIsCurrent(input: String, query: String, submitted: Boolean, youtube: Boolean): Boolean =
    youtube && input.isNotBlank() && input == query && !submitted
