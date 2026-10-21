package app.veyra.music.data.themes

/** Stable preference names; choosing a theme never overwrites this setting. */
enum class AppearanceFont {
    FOLLOW_THEME, VEYRA_DEFAULT, MANROPE, FREDOKA, SPACE_GROTESK, RAJDHANI, BARLOW_CONDENSED, GRENZE_GOTISCH;

    fun resolved(hasTheme: Boolean): AppearanceFont =
        if (this == FOLLOW_THEME && !hasTheme) VEYRA_DEFAULT else this

    companion object {
        fun fromStoredName(name: String?): AppearanceFont = entries.firstOrNull { it.name == name } ?: FOLLOW_THEME
    }
}
