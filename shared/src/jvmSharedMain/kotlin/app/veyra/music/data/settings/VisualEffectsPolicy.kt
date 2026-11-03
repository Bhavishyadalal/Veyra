package app.veyra.music.data.settings

/** Saved preferences remain independent of the temporary master override and refresh rate. */
data class VisualEffectsPreferences(
    val reduceAnimation: Boolean = false,
    val reduceBlur: Boolean = true,
    val liquidGlass: Boolean = false,
    val animatedCanvas: Boolean = true,
    val lyricsBlur: Boolean = true,
)

fun resolveVisualEffects(reduced: Boolean, saved: VisualEffectsPreferences): VisualEffectsPreferences =
    if (!reduced) saved else saved.copy(
        reduceAnimation = true, reduceBlur = true, liquidGlass = false,
        animatedCanvas = false, lyricsBlur = false,
    )
