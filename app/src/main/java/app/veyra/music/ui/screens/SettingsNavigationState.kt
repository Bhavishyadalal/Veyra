package app.veyra.music.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.listSaver

/** Owned by the visible route so covered Settings cannot consume system Back. */
class SettingsNavigationState {
    internal var category by mutableStateOf<SettingsCategory?>(null)
    var query by mutableStateOf("")

    /** Consume one visible Settings layer; false means the host should leave Settings. */
    fun back(): Boolean = when {
        query.isNotBlank() -> { query = ""; true }
        category != null -> { category = null; true }
        else -> false
    }

    companion object {
        val Saver = listSaver<SettingsNavigationState, String>(
            save = { listOf(it.category?.name.orEmpty(), it.query) },
            restore = { values -> SettingsNavigationState().apply {
                category = SettingsCategory.entries.firstOrNull { it.name == values.getOrNull(0) }
                query = values.getOrNull(1).orEmpty()
            } },
        )
    }
}
