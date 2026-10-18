package app.veyra.music.data.sources

/** A one-time correction of legacy defaults, followed by the listener's persisted choices. */
object SourceEnablementPolicy {
    const val DEFAULT_ENABLED = true

    fun onStartup(kind: SourceKind, storedEnabled: Boolean, applyDefaultOnMigration: Boolean): Boolean = when {
        kind == SourceKind.YOUTUBE -> true
        kind == SourceKind.JIOSAAVN && applyDefaultOnMigration -> true
        else -> storedEnabled
    }
}
