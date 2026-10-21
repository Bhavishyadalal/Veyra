package app.veyra.music.data.settings

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable enum class TransportAppearance { DEFAULT, GLYPHS, RINGS, FILLED }
@Serializable enum class ScrubberAppearance { DEFAULT, HAIRLINE, BOLD, THUMB }
@Serializable enum class PreloadPolicy { OPENING, NEXT_SONG, NEXT_THREE }
@Serializable enum class MiniPlayerDesign { PILL, CARD, SLIM }
@Serializable enum class MiniPlayerProgress { NONE, LINE, FILL }
@Serializable enum class NavigationLabels { ALWAYS, SELECTED, HIDDEN }
@Serializable enum class NavigationDesign { FLOATING, DOCKED, MINIMAL }
@Serializable enum class ImmersiveBackground { SONG, THEME, BLEND, MINIMAL }
@Serializable enum class ImmersiveCover { FILL, FIT, ZOOMED }
@Serializable enum class ImmersiveLyricsPosition { TOP, CENTER, BOTTOM, FULL }

/** Optional presentation controls. Defaults preserve the existing player and network behavior. */
@Serializable
data class ExperienceOptions(
    val immersive: Boolean = false,
    val immersiveBackground: ImmersiveBackground = ImmersiveBackground.SONG,
    val immersiveCover: ImmersiveCover = ImmersiveCover.ZOOMED,
    val immersiveLyrics: ImmersiveLyricsPosition = ImmersiveLyricsPosition.BOTTOM,
    val doubleTapSeekSeconds: Int = 0,
    val holdSeekSeconds: Int = 0,
    val showTrackLength: Boolean = false,
    val dataSaver: Boolean = false,
    val appFont: String = "",
    val lyricsFont: String = "",
    val lyricsScale: Float = 1f,
    val centeredLyrics: Boolean = false,
    val navigation: List<Int> = listOf(0,1,2,3),
    val startupTab: Int = 0,
    val navigationLabels: NavigationLabels = NavigationLabels.ALWAYS,
    val navigationDesign: NavigationDesign = NavigationDesign.FLOATING,
    val searchKeyboard: Boolean = true,
    val miniPlayerDesign: MiniPlayerDesign = MiniPlayerDesign.PILL,
    val miniPlayerProgress: MiniPlayerProgress = MiniPlayerProgress.NONE,
    val miniSkipButtons: Boolean = true,
    val homeGreeting: String = "",
    val homeSpeedDial: Boolean = false,
    val homeShortcuts: List<Int> = emptyList(),
    val quickPicksRadio: Boolean = true,
    val preload: PreloadPolicy = PreloadPolicy.NEXT_SONG,
    val preloadCellular: Boolean = true,
    val contentCountry: String = "",
    val transportAppearance: TransportAppearance = TransportAppearance.DEFAULT,
    val scrubberAppearance: ScrubberAppearance = ScrubberAppearance.DEFAULT,
    val showOutputDevice: Boolean = true,
    val shrinkArtworkOnPause: Boolean = true,
) {
    fun normalized() = copy(doubleTapSeekSeconds = doubleTapSeekSeconds.coerceIn(0, 30), holdSeekSeconds = holdSeekSeconds.coerceIn(0, 20),
        contentCountry=contentCountry.uppercase(java.util.Locale.ROOT).takeIf {it in java.util.Locale.getISOCountries()}.orEmpty(),
        homeGreeting=homeGreeting.take(100),homeShortcuts=homeShortcuts.filter {it in 0..7}.distinct().take(6),
        navigation=normalizedNavigation(navigation),startupTab=startupTab.takeIf {it in 0..6} ?: 0,
        appFont=appFont.takeIf {it.matches(Regex("[a-f0-9]{64}"))}.orEmpty(), lyricsFont=lyricsFont.takeIf {it.matches(Regex("[a-f0-9]{64}"))}.orEmpty(),
        lyricsScale=lyricsScale.takeIf {it.isFinite()}?.coerceIn(.8f,1.5f) ?: 1f)
    fun encode(): String = json.encodeToString(serializer(), normalized())
    companion object {
        private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }
        fun decode(value: String?): ExperienceOptions = value?.let {
            runCatching { json.decodeFromString(serializer(), it).normalized() }.getOrNull()
        } ?: ExperienceOptions()
    }
}

/** Handles unknown/zero durations and overflow without seeking outside a finite track. */
fun boundedSeek(positionMs: Long, deltaMs: Long, durationMs: Long): Long {
    val position = positionMs.coerceAtLeast(0)
    val sum = if (deltaMs > 0 && position > Long.MAX_VALUE - deltaMs) Long.MAX_VALUE else position + deltaMs
    return sum.coerceIn(0, durationMs.takeIf { it > 0 } ?: Long.MAX_VALUE)
}

/** Keep Home reachable, ignore future/duplicate IDs and bound the bar to five accessible targets. */
fun normalizedNavigation(ids:List<Int>):List<Int> {
    val valid=ids.filter {it in 0..6}.distinct().take(5).toMutableList()
    if(0 !in valid) {if(valid.size==5)valid.removeAt(valid.lastIndex);valid.add(0,0)}
    if(valid.size==1)valid.add(1)
    return valid
}

/** Only the preferences the app root consumes; typing a greeting must not invalidate navigation. */
data class RootExperience(val navigation:List<Int>,val startupTab:Int,val searchKeyboard:Boolean,val quickPicksRadio:Boolean,val immersive:Boolean)
fun ExperienceOptions.forAppRoot()=RootExperience(navigation,startupTab,searchKeyboard,quickPicksRadio,immersive)
