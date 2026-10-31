package app.veyra.music.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import app.veyra.music.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.Request
import java.io.File
import java.security.MessageDigest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import app.veyra.music.data.settings.AppSettings
import app.veyra.music.download.DownloadProgressGate
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import app.veyra.music.update.AppUpdateService

/**
 * Veyra ships as a sideloaded APK off GitHub Releases rather than through
 * a store, so there's nothing to push an update notice on its own — this
 * polls the repo's "latest release" once per launch and compares its tag
 * against the running build.
 *
 * The update itself is also handled here: the release's `.apk` asset is
 * downloaded into the app's cache and handed to the system package installer,
 * so the whole round trip stays inside the app instead of bouncing out to a
 * browser.
 */
object AppUpdateChecker {

    data class UpdateInfo(
        val version: String,
        val releaseUrl: String,
        val apkUrl: String?,
        /** The release's own Markdown body, shown as this update's "what's new". */
        val notes: String?,
        val digest: String? = null,
    )

    private const val CACHE_SUBDIR = "updates"

    private val LATEST_RELEASE_URL: String get() = BuildConfig.UPDATE_FEED_URL

    private val json = Json { ignoreUnknownKeys = true }

    private val _available = MutableStateFlow<UpdateInfo?>(null)
    val available = _available.asStateFlow()

    /** Where this update's APK download currently stands, for the dialog's progress row. */
    sealed interface DownloadState {
        data object Idle : DownloadState
        data class Downloading(val fraction: Float) : DownloadState
        data class Ready(val file: File) : DownloadState
        data class Failed(val message: String) : DownloadState
    }

    private val _download = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val download = _download.asStateFlow()

    /** Set from the UI thread when the user cancels; polled between network reads. */
    @Volatile
    private var downloadCancelled = false
    @Volatile private var activeCall: okhttp3.Call? = null
    private val checkMutex = Mutex()
    private val _checking = MutableStateFlow(false)
    val checking = _checking.asStateFlow()
    private val _checkMessage = MutableStateFlow("")
    val checkMessage = _checkMessage.asStateFlow()
    val configured: Boolean get() = LATEST_RELEASE_URL.isNotBlank()
    private val _openRequests = MutableStateFlow(0)
    val openRequests = _openRequests.asStateFlow()
    fun requestOpen() { _openRequests.value++ }

    @Synchronized fun startDownload(context: Context, automatic: Boolean = false) {
        if (_download.value is DownloadState.Downloading || _download.value is DownloadState.Ready) return
        if (_available.value?.apkUrl == null) return
        downloadCancelled = false
        _download.value = DownloadState.Downloading(0f)
        try { ContextCompat.startForegroundService(context, Intent(context, AppUpdateService::class.java).putExtra("unmetered_only", automatic)) }
        catch (e: Exception) { _download.value = DownloadState.Failed(e.message ?: "Could not start update download") }
    }

    suspend fun checkAndPrepare(context: Context, automatic: Boolean = false) {
        check()
        val info = _available.value ?: return
        if (_download.value is DownloadState.Idle) {
            withContext(Dispatchers.IO) {
                val target = File(context.cacheDir, "$CACHE_SUBDIR/veyra-update.apk")
                val version = context.getSharedPreferences("veyra_updates", 0).getString("ready_version", null)
                if (version == info.version && target.isFile && runCatching { validateApk(context, target) }.isSuccess) {
                    _download.value = DownloadState.Ready(target)
                }
            }
        }
        if (automatic && AppSettings.autoDownloadUpdates.value && AppSettings.meteredConnection.value == false) {
            startDownload(context, automatic = true)
        }
    }

    suspend fun check() = withContext(Dispatchers.IO) {
        if (!checkMutex.tryLock()) return@withContext
        _checking.value = true
        try {
            check(configured) { "Update checking is not configured for this build" }
            val request = Request.Builder().url(LATEST_RELEASE_URL).build()
            val body = Http.client.newCall(request).execute().use { response ->
                if (response.code == 404) { _checkMessage.value = "No published release is available yet"; return@withContext }
                check(response.isSuccessful) { "Update check failed (HTTP ${response.code})" }
                response.body.string()
            }
            currentCoroutineContext().ensureActive()
            val release = json.parseToJsonElement(body) as? JsonObject ?: error("Invalid release response")
            check(release["draft"]?.jsonPrimitive?.contentOrNull != "true" &&
                release["prerelease"]?.jsonPrimitive?.contentOrNull != "true") { "The feed must provide a stable release" }
            val tag = release["tag_name"]?.jsonPrimitive?.contentOrNull ?: error("Missing release version")
            val url = release["html_url"]?.jsonPrimitive?.contentOrNull ?: error("Missing release URL")
            val apkUrl = apkAssetUrl(release)
            val notes = release["body"]?.jsonPrimitive?.contentOrNull
            val latest = tag.removePrefix("v")
            check(latest.matches(Regex("[0-9]+(?:\\.[0-9]+)*(?:-[A-Za-z0-9.]+)?"))) { "Invalid release version" }
            val digest = release["assets"]?.jsonArray?.mapNotNull { it as? JsonObject }
                ?.firstOrNull { it["browser_download_url"]?.jsonPrimitive?.contentOrNull == apkUrl }
                ?.get("digest")?.jsonPrimitive?.contentOrNull
            val newer = isNewer(latest, BuildConfig.VERSION_NAME)
            _available.value = if (newer) UpdateInfo(latest, url, apkUrl, notes, digest) else null
            _checkMessage.value = if (newer) "Update $latest available" else "Veyra is up to date"
        } catch (e: CancellationException) { throw e
        } catch (e: Exception) { _checkMessage.value = e.message ?: "Could not check for updates"
        } finally { _checking.value = false; checkMutex.unlock() }
    }

    /**
     * Clears abandoned partial transfers on cold start. A completed APK is
     * retained and revalidated against the current release and installed signer.
     */
    suspend fun clearCache(context: Context) = withContext(Dispatchers.IO) {
        if (_download.value is DownloadState.Downloading) return@withContext
        File(context.cacheDir, CACHE_SUBDIR).listFiles()?.filter { it.extension == "part" }?.forEach { it.delete() }
    }

    /**
     * One release carries the phone app and the TV app, each as a universal
     * APK plus per-ABI splits:
     *
     *     Veyra-v1.8.apk     Veyra-v1.8_arm64-v8a.apk     ...
     *     VeyraTV-v1.8.apk   VeyraTV-v1.8_arm64-v8a.apk   ...
     *
     * so this app's APKs are the ones named with its own
     * [BuildConfig.UPDATE_APK_PREFIX] — "Veyra-" never matches a TV file,
     * since the prefix ends at the hyphen. The universal build (no `_ABI`
     * suffix) is taken first, as it installs anywhere; failing that, the split
     * for this device's ABI. A release with neither leaves
     * [UpdateInfo.apkUrl] null and the UI falls back to opening the releases
     * page.
     */
    private fun apkAssetUrl(release: JsonObject): String? = runCatching {
        val assets = release["assets"]?.jsonArray?.mapNotNull { it as? JsonObject }?.mapNotNull { asset ->
            if (asset["state"]?.jsonPrimitive?.contentOrNull != "uploaded") return@mapNotNull null
            val name = asset["name"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
            val url = asset["browser_download_url"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
            UpdateApkAsset(name, url)
        }.orEmpty()
        chooseUpdateApk(assets, Build.SUPPORTED_ABIS.toList(), BuildConfig.UPDATE_APK_PREFIX)?.url
    }.getOrNull()

    /**
     * Streams the current update's APK into the app cache, reporting progress
     * through [download]. A finished file survives a cancelled dialog: until
     * the state is reset, "Install Now" comes straight back without a second
     * download.
     */
    suspend fun downloadApk(context: Context): Unit = withContext(Dispatchers.IO) {
        val info = _available.value
        val url = info?.apkUrl
        if (info == null || url == null) {
            _download.value = DownloadState.Failed("This release has no downloadable APK. Check for updates again.")
            return@withContext
        }
        var part: File? = null
        try {
            check(Uri.parse(url).scheme == "https") { "APK downloads require HTTPS" }
            val dir = File(context.cacheDir, CACHE_SUBDIR).apply { check(isDirectory || mkdirs()) }
            val partial = File(dir, "veyra-update.apk.part")
            part = partial
            val target = File(dir, "veyra-update.apk")
            val digest = MessageDigest.getInstance("SHA-256")
            val gate = DownloadProgressGate()
            val call = Http.client.newCall(Request.Builder().url(url).build())
            activeCall = call
            if (downloadCancelled) call.cancel()
            call.execute().use { response ->
                check(response.isSuccessful) { "Download failed: HTTP ${response.code}" }
                val body = response.body
                val total = body.contentLength()
                check(total <= MAX_APK_BYTES) { "APK exceeds the download limit" }
                var written = 0L
                body.byteStream().use { input -> partial.outputStream().use { output ->
                    val buffer = ByteArray(128 * 1024)
                    while (true) {
                        currentCoroutineContext().ensureActive()
                        if (downloadCancelled) throw CancellationException("Update download cancelled")
                        val count = input.read(buffer)
                        if (count < 0) break
                        written += count
                        check(written <= MAX_APK_BYTES) { "APK exceeds the download limit" }
                        output.write(buffer, 0, count)
                        digest.update(buffer, 0, count)
                        gate.update(written, total)?.let { _download.value = DownloadState.Downloading(it) }
                    }
                } }
                check(written > 0 && (total < 0 || written == total)) { "APK download is incomplete" }
            }
            info.digest?.let { expected ->
                check(expected.startsWith("sha256:") && digest.digest().joinToString("") { "%02x".format(it) }
                    .equals(expected.removePrefix("sha256:"), ignoreCase = true)) { "APK checksum does not match the release" }
            }
            validateApk(context, partial)
            currentCoroutineContext().ensureActive()
            if (downloadCancelled) throw CancellationException("Update download cancelled")
            check(partial.renameTo(target)) { "Could not finish saving the APK" }
            context.getSharedPreferences("veyra_updates", 0).edit().putString("ready_version", info.version).apply()
            _download.value = DownloadState.Ready(target)
        } catch (e: CancellationException) {
            _download.value = DownloadState.Idle
            throw e
        } catch (e: Exception) {
            _download.value = if (downloadCancelled) DownloadState.Idle else DownloadState.Failed(e.message ?: "Download failed")
        } finally { activeCall = null; part?.delete() }
    }

    @Suppress("DEPRECATION")
    private fun validateApk(context: Context, file: File) {
        val pm = context.packageManager
        val flags = if (Build.VERSION.SDK_INT >= 28) PackageManager.GET_SIGNING_CERTIFICATES else PackageManager.GET_SIGNATURES
        val archive = pm.getPackageArchiveInfo(file.path, flags) ?: error("Downloaded file is not a valid APK")
        val installed = pm.getPackageInfo(context.packageName, flags)
        check(archive.packageName == context.packageName) { "APK belongs to a different app" }
        val newer = if (Build.VERSION.SDK_INT >= 28) archive.longVersionCode > installed.longVersionCode else archive.versionCode > installed.versionCode
        check(newer) { "APK build is not newer than the installed app" }
        val trusted = if (Build.VERSION.SDK_INT >= 28) {
            val candidate = archive.signingInfo ?: error("APK has no signing certificate")
            val current = installed.signingInfo ?: error("Installed app has no signing certificate")
            if (candidate.hasMultipleSigners() || current.hasMultipleSigners()) {
                candidate.apkContentsSigners.toSet() == current.apkContentsSigners.toSet()
            } else candidate.signingCertificateHistory?.toSet()?.containsAll(current.apkContentsSigners.toSet()) == true
        } else archive.signatures?.toSet() == installed.signatures?.toSet() && !archive.signatures.isNullOrEmpty()
        check(trusted) { "APK signature does not match this installation" }
    }

    private const val MAX_APK_BYTES = 512L * 1024 * 1024

    /** Stops an in-flight download; the next read loop sees this and bails. */
    fun cancelDownload() {
        downloadCancelled = true
        activeCall?.cancel()
    }

    /** Back to square one after a failure, so the dialog offers Download again. */
    fun resetDownload() {
        if (_download.value !is DownloadState.Downloading) _download.value = DownloadState.Idle
    }

    /**
     * Hands a downloaded APK to the system installer.
     *
     * Sideloaded apps need the user's blessing per app ("install unknown apps");
     * without it the installer intent silently does nothing on most ROMs, so
     * the user is sent to that one switch first and taps Install again after.
     * Some TV builds have no such settings screen; there the installer is
     * launched anyway and shows its own "not allowed" prompt with a way in.
     */
    fun installApk(context: Context, file: File) {
        if (!file.isFile || file.canonicalFile.parentFile != File(context.cacheDir, CACHE_SUBDIR).canonicalFile) {
            _download.value = DownloadState.Failed("The downloaded APK is missing. Download it again.")
            return
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                !context.packageManager.canRequestPackageInstalls()
            ) {
                val opened = runCatching {
                    context.startActivity(
                        Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                            .setData(Uri.parse("package:${context.packageName}"))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                    )
                }.isSuccess
                if (opened) return
            }
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            // Keep the established OEM package-installer intent and URI grant behavior.
            @Suppress("DEPRECATION")
            context.startActivity(
                Intent(Intent.ACTION_INSTALL_PACKAGE)
                    .setDataAndType(uri, "application/vnd.android.package-archive")
                    .putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
                    .putExtra(Intent.EXTRA_RETURN_RESULT, true)
                    .addFlags(
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_ACTIVITY_NEW_TASK,
                    ),
            )
        } catch (e: Exception) {
            _download.value = DownloadState.Failed(e.message ?: "Could not open the installer")
        }
    }

    /** A version split into its numeric dotted parts and whether it carries a "-suffix" (e.g. "-beta2"). */
    private data class ParsedVersion(val parts: List<Int>, val isPreRelease: Boolean)

    private fun parseVersion(raw: String): ParsedVersion {
        val dash = raw.indexOf('-')
        val base = if (dash >= 0) raw.substring(0, dash) else raw
        return ParsedVersion(base.split(".").map { it.toIntOrNull() ?: 0 }, dash >= 0)
    }

    /**
     * Numeric, dot-separated comparison — "1.10" outranks "1.9" — with one
     * extra rule: a "-betaN" build (see the debug build type's
     * `versionNameSuffix` in app/build.gradle.kts) is treated as older than a
     * plain release of the same numbers, since the beta by definition predates
     * the tag it was testing toward. Without this, a beta and the release it
     * matches compare equal and testers never get nudged onto the real build.
     */
    internal fun isNewer(latest: String, current: String): Boolean {
        val l = parseVersion(latest)
        val c = parseVersion(current)
        for (i in 0 until maxOf(l.parts.size, c.parts.size)) {
            val a = l.parts.getOrElse(i) { 0 }
            val b = c.parts.getOrElse(i) { 0 }
            if (a != b) return a > b
        }
        return c.isPreRelease && !l.isPreRelease
    }
}
