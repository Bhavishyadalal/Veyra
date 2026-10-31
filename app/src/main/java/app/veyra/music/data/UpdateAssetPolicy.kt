package app.veyra.music.data

internal data class UpdateApkAsset(val name: String, val url: String)

/** Releases use both hyphenated and underscored ABI names. Never pick another device's split. */
internal fun chooseUpdateApk(assets: List<UpdateApkAsset>, supportedAbis: List<String>, prefix: String): UpdateApkAsset? {
    val knownAbis = listOf("arm64-v8a", "armeabi-v7a", "x86_64", "x86", "armeabi")
    fun token(name: String, value: String) = Regex("(?:^|[-_.])${Regex.escape(value)}(?:[-_.]|$)", RegexOption.IGNORE_CASE).containsMatchIn(name)
    val candidates = assets.filter { it.name.startsWith(prefix, true) && it.name.endsWith(".apk", true) &&
        listOf("debug", "dev", "tv", "wear").none { excluded -> token(it.name, excluded) } }
    fun abiOf(asset: UpdateApkAsset) = knownAbis.firstOrNull { token(asset.name, it) }
    return candidates.firstOrNull { token(it.name, "universal") }
        ?: candidates.firstOrNull { abiOf(it) == null }
        ?: supportedAbis.firstNotNullOfOrNull { abi -> candidates.firstOrNull { abiOf(it) == abi } }
}
