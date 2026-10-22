package app.veyra.music.ui

/** A late provider answer must still belong to the visible search interaction. */
internal fun searchResponseIsCurrent(requestId: Long, newestId: Long, youtube: Boolean): Boolean =
    youtube && requestId == newestId

internal fun typeaheadIsCurrent(input: String, query: String, submitted: Boolean, youtube: Boolean): Boolean =
    youtube && input.isNotBlank() && input == query && !submitted

/** A repeated page/token must not turn a stationary viewport into a request loop. */
internal fun searchPageMadeProgress(previousCount: Int, mergedCount: Int, previousToken: String, nextToken: String?): Boolean =
    mergedCount > previousCount || nextToken != previousToken
