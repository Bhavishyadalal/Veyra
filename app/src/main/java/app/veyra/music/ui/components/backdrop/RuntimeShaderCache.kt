/*
 * Vendored from Kyant0/backdrop v2.0.0 (io.github.kyant0:backdrop)
 * https://github.com/Kyant0/backdrop — Copyright 2025 Kyant0, Apache License 2.0
 *
 * Vendored so the library ships as source with this app (binary AARs compiled
 * against older Compose broke at runtime) and to add a backdrop resolution
 * scale for cheaper effect rendering. KMP expect/actual declarations were
 * merged into this single Android source set. Package renamed accordingly.
 */
package app.veyra.music.ui.components.backdrop

import org.intellij.lang.annotations.Language

sealed interface RuntimeShaderCache {

    fun obtainRuntimeShader(key: String, @Language("AGSL") string: String): RuntimeShader
}

internal class RuntimeShaderCacheImpl : RuntimeShaderCache {

    private val runtimeShaders = mutableMapOf<String, RuntimeShader>()

    override fun obtainRuntimeShader(key: String, string: String): RuntimeShader {
        // Every rendering caller already uses the supported-shader fallback.
        // Keep the API guard at this final construction boundary as well.
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            return runtimeShaders.getOrPut(key) { RuntimeShader(string) }
        }
        error("Runtime shaders require Android 13 or later")
    }

    fun clear() {
        runtimeShaders.clear()
    }
}
