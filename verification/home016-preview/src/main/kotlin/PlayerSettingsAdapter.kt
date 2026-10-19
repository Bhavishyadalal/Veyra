package app.veyra.music.ui.player
import kotlinx.coroutines.flow.MutableStateFlow
// Compile-time host adapter; component bodies use their real production source.
object PlayerSettings { val reduceAnimation = MutableStateFlow(false) }
