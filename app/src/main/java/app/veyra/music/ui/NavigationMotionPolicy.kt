package app.veyra.music.ui

/** Depth describes context, independently of the retained page's identity. */
data class NavigationDestination(val key: String, val depth: Int)

enum class NavigationMotion { None, Tab, Forward, Back }

object NavigationMotionPolicy {
    fun between(from: NavigationDestination, to: NavigationDestination): NavigationMotion = when {
        from.key == to.key -> NavigationMotion.None
        from.key.startsWith("tab:") && to.key.startsWith("tab:") -> NavigationMotion.Tab
        to.depth < from.depth -> NavigationMotion.Back
        else -> NavigationMotion.Forward
    }
}
