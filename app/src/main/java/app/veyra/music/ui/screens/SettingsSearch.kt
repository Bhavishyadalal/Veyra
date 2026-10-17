package app.veyra.music.ui.screens

/** Pure search model shared by the category filter and its unit tests. */
internal class SettingsSearch<T>(query: String, private val category: T?) {
    private val words = query.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }

    var anyMatch = false
        private set

    // Searching is global, including when started from inside a category.
    fun includes(group: T): Boolean = words.isNotEmpty() || category == group

    fun matches(vararg keywords: String?): Boolean {
        val hit = words.all { word ->
            keywords.any { it?.contains(word, ignoreCase = true) == true }
        }
        if (hit) anyMatch = true
        return hit
    }
}
