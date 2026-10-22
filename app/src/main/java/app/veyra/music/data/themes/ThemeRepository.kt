package app.veyra.music.data.themes

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Typeface
import app.veyra.music.BuildConfig
import app.veyra.music.ui.themes.ThemeVisuals
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

/** Process-lifetime downloads survive configuration changes; playback is never restarted. */
object ThemeRepository {
    val ROOT_URL = "https://raw.githubusercontent.com/Bhavishyadalal/Veyra/refs/heads/" +
        (if (app.veyra.music.BuildConfig.VERSION_NAME.contains("-beta"))
            "preview/${app.veyra.music.BuildConfig.VERSION_NAME}" else "main") + "/themes/"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val client by lazy { OkHttpClient.Builder().connectTimeout(20,TimeUnit.SECONDS)
        .readTimeout(30,TimeUnit.SECONDS).callTimeout(120,TimeUnit.SECONDS)
        .followRedirects(false).followSslRedirects(false).build() }
    private lateinit var context: Context
    private val prefs get() = context.getSharedPreferences("theme_packs", Context.MODE_PRIVATE)
    private val root get() = File(context.filesDir,"theme-packs")
    @Volatile private var activeDefinition: ThemeManifest? = null
    fun activeManifest(): ThemeManifest? = activeDefinition
    private val _active = MutableStateFlow<ThemeVisuals?>(null)
    val active = _active.asStateFlow()
    private val _catalog = MutableStateFlow<List<ThemeListing>>(emptyList())
    val catalog = _catalog.asStateFlow()
    private val _installed = MutableStateFlow<Map<String,ThemeManifest>>(emptyMap())
    val installed = _installed.asStateFlow()
    data class Transfer(val id: String, val bytes: Long, val total: Long)
    private val _transfer = MutableStateFlow<Transfer?>(null)
    val transfer = _transfer.asStateFlow()
    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()
    private var job: Job? = null
    private var downloadCall: okhttp3.Call? = null
    private val installedDirectories = mutableMapOf<String,File>()
    private val lock = Any()

    fun init(app: Context) {
        context = app.applicationContext
        scope.launch {
            // Validate local data before it can influence theme construction.
            val selected = prefs.getString("active", null)
            root.listFiles()?.filter { it.name.startsWith(".staging-") || it.name.startsWith(".old-") }
                ?.forEach { it.deleteRecursively() }
            root.listFiles()?.filter { it.isDirectory && !it.name.startsWith('.') }?.forEach { folder ->
                runCatching {
                    val m = loadInstalled(folder)
                    if (m.version > (_installed.value[m.id]?.version ?: 0)) {
                        synchronized(lock) { installedDirectories[m.id] = folder }
                        _installed.value = _installed.value + (m.id to m)
                    }
                    if (folder.name == selected) {
                        activeDefinition = m
                        _active.value = visuals(m,folder)
                    }
                }.onFailure {
                    if (folder.name == selected) prefs.edit().remove("active").apply()
                    folder.deleteRecursively()
                }
            }
            runCatching {
                context.assets.open("theme-catalog.json").use { parseCatalog(ThemePack.readBounded(it,65536)) }
            }.onSuccess { _catalog.value = it }
        }
    }

    fun clearMessage() { _message.value = null }
    private var catalogJob: Job? = null
    @Volatile private var lastCatalogRefresh = 0L
    /** Reopening Studio reuses recent data; the refresh button always requests fresh data. */
    fun refreshCatalog(force: Boolean = false): Job? = synchronized(lock) {
        val elapsed = System.nanoTime() - lastCatalogRefresh
        if (catalogJob?.isActive == true || (!force && lastCatalogRefresh != 0L &&
                elapsed in 0 until TimeUnit.MINUTES.toNanos(10) && _catalog.value.isNotEmpty())) return@synchronized catalogJob
        scope.launch {
            runCatching {
                client.newCall(Request.Builder().url(ROOT_URL+"catalog.json").build()).execute().use { r ->
                    require(r.isSuccessful) { "Theme catalog unavailable. Try again later." }
                    parseCatalog(ThemePack.readBounded(r.body.byteStream(),65536))
                }
            }.onSuccess { _catalog.value = it; lastCatalogRefresh = System.nanoTime() }
                .onFailure { _message.value = "Could not refresh themes. Installed themes still work offline." }
        }.also { catalogJob = it }
    }

    internal fun parseCatalog(bytes: ByteArray): List<ThemeListing> {
        val c = ThemePack.json.decodeFromString<ThemeCatalog>(bytes.decodeToString())
        require(c.schema == 1 && c.themes.size <= 64 && c.themes.map { it.id }.distinct().size == c.themes.size)
        c.themes.forEach { e ->
            require(e.id.matches(Regex("[a-z0-9][a-z0-9-]{0,47}")) && e.name.length in 1..60 && e.description.length <= 300)
            require(e.url == ROOT_URL+"packs/${e.id}-v${e.version}.zip")
            require(e.preview == ROOT_URL+"${e.id}/artwork/preview.webp")
            require(e.bytes in 1..ThemePack.MAX_ZIP && e.sha256.matches(Regex("[a-f0-9]{64}")))
            require(e.version > 0 && e.minAppCode >= 23)
        }
        return c.themes
    }

    fun cancelDownload() { job?.cancel(); downloadCall?.cancel() }
    fun download(listing: ThemeListing) {
        if (job?.isCompleted == false) return
        job = scope.launch {
            val temp = File(context.cacheDir,"theme-download.zip")
            try {
                require(_catalog.value.contains(listing) && listing.minAppCode <= BuildConfig.VERSION_CODE)
                _message.value = null
                _transfer.value = Transfer(listing.id,0,listing.bytes)
                val call = client.newCall(Request.Builder().url(listing.url).build())
                downloadCall = call
                call.execute().use { response ->
                    require(response.isSuccessful) { "Theme download unavailable. Try again later." }
                    val body = response.body ?: error("Empty theme download.")
                    require(body.contentLength() <= ThemePack.MAX_ZIP)
                    body.byteStream().use { input ->
                        temp.outputStream().use { output ->
                            val buffer = ByteArray(16384); var read = 0L; var reported = 0L
                            while (true) {
                                ensureActive()
                                val n = input.read(buffer); if (n < 0) break
                                read += n; require(read <= listing.bytes && read <= ThemePack.MAX_ZIP)
                                output.write(buffer,0,n)
                                if (read-reported >= 65536 || read == listing.bytes) {
                                    _transfer.value = Transfer(listing.id,read,listing.bytes); reported = read
                                }
                            }
                        }
                    }
                }
                ensureActive()
                val (m, folder) = ThemePack.install(temp,root,listing,BuildConfig.VERSION_CODE, ::validateDecodedAssets)
                // Install already checked hashes and decoded assets before its atomic publish.
                synchronized(lock) { installedDirectories[m.id] = folder }
                _installed.value = _installed.value + (m.id to m)
                _message.value = "${m.name} is installed. Select Apply when you are ready."
            } catch (cancel: CancellationException) {
                throw cancel
            } catch (e: Exception) {
                ensureActive()
                _message.value = "Could not install theme. ${e.message?.take(140) ?: "Please try again."}"
            } finally { temp.delete(); downloadCall = null; _transfer.value = null }
        }
    }

    /** Caller asks for confirmation before this. No process exit or playback-service stop. */
    suspend fun apply(id: String?): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            if (id == null) {
                check(prefs.edit().remove("active").commit())
                activeDefinition = null
                _active.value = null
            } else {
                val dir = synchronized(lock) { installedDirectories[id] } ?: error("Download this theme first.")
                val m = loadInstalled(dir)
                check(prefs.edit().putString("active",dir.name).commit())
                activeDefinition = m
                _active.value = visuals(m,dir)
            }
        }.onFailure { _message.value = "Could not apply theme. Original appearance is still available." }.isSuccess
    }

    private fun loadInstalled(folder: File): ThemeManifest {
        val m = ThemePack.json.decodeFromString<ThemeManifest>(File(folder,"theme.json").readText())
        ThemePack.validate(m,BuildConfig.VERSION_CODE)
        require(folder.name == "${m.id}-v${m.version}")
        for ((path, hash) in m.files) {
            val file = File(folder,path)
            require(file.length() in 1..ThemePack.MAX_FILE && ThemePack.sha256(file.readBytes()) == hash)
        }
        validateDecodedAssets(m,folder)
        return m
    }

    private fun validateDecodedAssets(m: ThemeManifest, folder: File) {
        for ((key,path) in m.assets) {
            val file = File(folder,path)
            if (key.endsWith("Font")) {
                val header = file.inputStream().use { input -> ByteArray(4).also { check(input.read(it) == 4) } }
                require(header.contentEquals(byteArrayOf(0,1,0,0)) || header.decodeToString() in setOf("OTTO","true")) { "Invalid theme font." }
                Typeface.createFromFile(file)
            }
            else {
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeFile(file.path, options)
                ThemePack.validateImageBounds(key, options.outWidth, options.outHeight)
            }
        }
    }

    suspend fun remove(id: String): Boolean = withContext(Dispatchers.IO) {
        if (_active.value?.id == id || _transfer.value != null) return@withContext false
        if (synchronized(lock) { installedDirectories[id] } == null) return@withContext true
        val versions = root.listFiles()?.filter { it.isDirectory && it.name.matches(Regex("${Regex.escape(id)}-v[0-9]+")) }.orEmpty()
        if (!versions.map { it.deleteRecursively() }.all { it }) return@withContext false
        synchronized(lock) { installedDirectories.remove(id) }
        _installed.value = _installed.value - id
        true
    }

    fun manifest(id: String): ThemeManifest? = _installed.value[id]
    fun directory(id: String): File? = synchronized(lock) { installedDirectories[id] }
    private fun visuals(m: ThemeManifest, dir: File) = ThemeVisuals(m.id,m.name,
        color(m.palette.accent),color(m.palette.surface),color(m.palette.raised),color(m.palette.outline),
        m.rowCorner,m.buttonCorner,m.navCorner,m.assets.mapValues { File(dir,it.value).absolutePath },m.seekStyle,m.seekHeight,m.version, color(m.palette.background),color(m.palette.text),color(m.palette.muted),
        color(m.palette.secondary),m.sceneStyle,m.tagline)
    fun color(value: String) = Color(0xFF000000L or value.removePrefix("#").toLong(16))
}
