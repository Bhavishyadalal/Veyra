package app.veyra.music.ui.performance

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import app.veyra.music.AppIcon
import app.veyra.music.BuildConfig
import app.veyra.music.R
import coil3.SingletonImageLoader
import kotlinx.coroutines.*

/** Offline preparation only. No invented timer, downloads, or permanent performance claim. */
internal object StartupPreparation {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    lateinit var runner: PreparationRunner
        private set

    fun configure(context: Context, themeRestore: Job, assetRestore: Job) {
        val app = context.applicationContext
        val prefs = { app.getSharedPreferences("startup_preparation", Context.MODE_PRIVATE) }
        runner = PreparationRunner(scope, listOf(
            { themeRestore.join(); check(!themeRestore.isCancelled) },
            { assetRestore.join(); check(!assetRestore.isCancelled) },
            {
                // The same Android resource font cache used by Compose's font loader.
                for (font in listOf(R.font.veyra_regular, R.font.veyra_medium,
                    R.font.veyra_semibold, R.font.veyra_bold, R.font.veyra_heavy, R.font.super_malibu)) {
                    currentCoroutineContext().ensureActive()
                    checkNotNull(ResourcesCompat.getFont(app, font))
                }
            },
            { SingletonImageLoader.get(app); AppIcon.current(app) },
        ), isPrepared = { prefs().getInt("prepared_build", -1) == BuildConfig.VERSION_CODE },
            savePrepared = { check(prefs().edit().putInt("prepared_build", BuildConfig.VERSION_CODE).commit()) })
    }
}
