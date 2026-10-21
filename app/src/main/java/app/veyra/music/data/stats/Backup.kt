package app.veyra.music.data.stats

import android.content.Context
import android.net.Uri
import app.veyra.music.BuildConfig
import app.veyra.music.data.settings.AppSettings
import app.veyra.music.data.settings.SearchHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * The whole of what this app knows about you, as one JSON file you own.
 *
 * Two errands, and they are the same errand: moving to a new phone, and being
 * able to see what is being kept. Everything Replay counts lives only on this
 * device, which is the point of it — and the flip side of that is that a factory
 * reset takes it with no way back, so there has to be a way out.
 *
 * ## What is in it
 *
 * Every preference except credentials (see [AppSettings.exportPrefs]), and every
 * month of listening. Not the audio cache, the downloads or the artwork: those
 * are megabytes that re-fetch themselves, and a backup that is mostly cache is
 * one nobody keeps.
 *
 * ## Typed values rather than a JSON object
 *
 * Preferences come off Android as `Map<String, Any?>` and go back the same way,
 * where the *type* decides which `put` is called. Written as plain JSON, `0.5`
 * comes back a Double and lands in a Float preference as a class-cast crash the
 * first time it is read — weeks later, in a settings screen, with nothing
 * pointing at the import. So each value carries its type and is parsed back into
 * exactly the type it left as, and anything unrecognised is skipped rather than
 * guessed at.
 */
object Backup {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = true
    }

    /** A suggested filename, dated so successive exports don't collide. */
    fun suggestedName(): String =
        "veyra-backup-${DateTimeFormatter.ofPattern("yyyy-MM-dd").format(
            Instant.now().atZone(ZoneId.systemDefault()),
        )}.json"

    /**
     * Writes a backup to [target], a document the user picked.
     *
     * Through the content resolver rather than a [java.io.File] because the
     * destination is wherever they chose — Drive, a USB stick, a folder this app
     * has no path to and no permission for. The picker grants access to that one
     * document and nothing else, which is the correct amount.
     */
    suspend fun exportTo(context: Context, target: Uri): Result<Int> = withContext(Dispatchers.IO) {
        runCatching {
            val buckets = ListeningStats.exportAll()
            val file = BackupFile(
                versionName = BuildConfig.VERSION_NAME,
                exportedAt = Instant.now().toString(),
                settings = AppSettings.exportPrefs().mapNotNull { (key, value) ->
                    PrefValue.of(value)?.let { key to it }
                }.toMap(),
                listening = buckets,
                assets = buildMap {
                    val options=AppSettings.experience.value
                    for((slot,hash) in listOf("font/app" to options.appFont,"font/lyrics" to options.lyricsFont)) {
                        app.veyra.music.data.settings.ImportedFonts.file(context,hash)?.let {put(slot,android.util.Base64.encodeToString(it.readBytes(),android.util.Base64.NO_WRAP))}
                    }
                },
            )
            val text = json.encodeToString(BackupFile.serializer(), file)
            val bytes=text.toByteArray()
            require(bytes.size<=32*1024*1024) {"Backup exceeds 32 MB"}
            context.contentResolver.openOutputStream(target, "wt")
                ?.use { it.write(bytes) }
                ?: error("Couldn't open that file for writing")
            buckets.size
        }
    }

    /**
     * Reads [source] and replaces this device's settings and listening with it.
     *
     * Validated before anything is written: a file that isn't one of ours, or is
     * from a schema this build can't read, is refused whole. A half-applied
     * import is worse than a refused one — it leaves settings from two devices
     * mixed together with nothing to say which came from where.
     */
    suspend fun importFrom(context: Context, source: Uri, expectedHash: String? = null): Result<Summary> = withContext(Dispatchers.IO) {
        runCatching {
            val text = readBounded(context,source)
            require(expectedHash==null || digest(text)==expectedHash) {"The backup changed after its preview. Choose it again."}
            val file = decode(text)
            val restoredFonts=buildMap {
                for((key,encoded) in file.assets) {
                    require(key in setOf("font/app","font/lyrics")) {"Unsupported backup asset"}
                    require(encoded.length<=6*1024*1024) {"Backup font is too large"}
                    val hash=app.veyra.music.data.settings.ImportedFonts.store(context,android.util.Base64.decode(encoded,android.util.Base64.DEFAULT))
                    put(key,hash)
                }
            }

            ListeningStats.importAll(file.listening)
            AppSettings.importPrefs(file.settings.mapValues { it.value.decoded() })
            AppSettings.setExperience(AppSettings.experience.value.copy(appFont=restoredFonts["font/app"].orEmpty(),lyricsFont=restoredFonts["font/lyrics"].orEmpty()))
            // Shares AppSettings' preference file, so it has already been
            // overwritten by the line above — it just doesn't know yet.
            SearchHistory.reload()
            Summary(
                months = file.listening.size,
                settings = file.settings.size,
                from = file.versionName,
                at = file.exportedAt,
            )
        }
    }

    suspend fun inspect(context:Context,source:Uri):Result<Summary> = withContext(Dispatchers.IO) {
        runCatching { val text=readBounded(context,source);val file=decode(text)
            Summary(file.listening.size,file.settings.size,file.versionName,file.exportedAt,digest(text)) }
    }

    private fun digest(text:String)=java.security.MessageDigest.getInstance("SHA-256").digest(text.toByteArray()).joinToString("") {"%02x".format(it)}
    internal fun decode(text:String):BackupFile {
        val document=runCatching {json.parseToJsonElement(text) as? kotlinx.serialization.json.JsonObject}.getOrNull()
        require(document!=null && (document["app"] as? kotlinx.serialization.json.JsonPrimitive)?.content==APP_TAG &&
            listOf("version","settings","listening").all {it in document}) {"That does not look like a Veyra backup"}
        val file=runCatching {json.decodeFromJsonElement(BackupFile.serializer(),document)}.getOrElse {error("That does not look like a Veyra backup")}
        require(file.app==APP_TAG) {"That backup is from another app"}
        require(file.version in 1..SCHEMA_VERSION) {"Unsupported backup version"}
        require(file.assets.size<=2) {"Too many backup assets"}
        require(file.assets.keys.all {it in setOf("font/app","font/lyrics")}) {"Unsupported backup asset"}
        require(file.assets.values.all {it.length<=6*1024*1024}) {"Backup font is too large"}
        return file
    }
    private fun readBounded(context:Context,source:Uri):String {
        return context.contentResolver.openInputStream(source)?.use {input->
            val out=java.io.ByteArrayOutputStream();val buffer=ByteArray(8192);var total=0
            while(true){val n=input.read(buffer);if(n<0)break;total+=n;require(total<=32*1024*1024) {"Backup exceeds 32 MB"};out.write(buffer,0,n)}
            out.toString(Charsets.UTF_8.name())
        } ?: error("Cannot open this backup")
    }

    /** What an import turned out to contain, for the line shown afterwards. */
    data class Summary(
        val months: Int,
        val settings: Int,
        val from: String,
        val at: String,
        val hash: String = "",
    )

    private const val APP_TAG = "veyra"

    /**
     * Bump when the shape below stops being readable by an older build. A file
     * from a *newer* schema is refused rather than partially read; one from an
     * older schema is read as-is, since every field has a default.
     */
    private const val SCHEMA_VERSION = 2

    @Serializable
    data class BackupFile(
        val app: String = APP_TAG,
        val version: Int = SCHEMA_VERSION,
        /** Which build wrote it — for the reader, not for any logic here. */
        val versionName: String = "",
        val exportedAt: String = "",
        val settings: Map<String, PrefValue> = emptyMap(),
        val listening: List<StoredBucket> = emptyList(),
        val assets: Map<String,String> = emptyMap(),
    )

    /** One preference, with the type it has to be restored as. */
    @Serializable
    data class PrefValue(
        val type: String,
        val value: String? = null,
        val values: List<String> = emptyList(),
    ) {
        fun decoded(): Any? = when (type) {
            BOOLEAN -> value?.toBooleanStrictOrNull()
            INT -> value?.toIntOrNull()
            LONG -> value?.toLongOrNull()
            FLOAT -> value?.toFloatOrNull()
            STRING -> value
            STRING_SET -> values.toSet()
            else -> null
        }

        companion object {
            /**
             * Numbers go out as strings on purpose. JSON has one number type and
             * every parser picks its own Kotlin type back off it, which is
             * exactly the ambiguity this whole class exists to remove.
             */
            fun of(value: Any?): PrefValue? = when (value) {
                is Boolean -> PrefValue(BOOLEAN, value.toString())
                is Int -> PrefValue(INT, value.toString())
                is Long -> PrefValue(LONG, value.toString())
                is Float -> PrefValue(FLOAT, value.toString())
                is String -> PrefValue(STRING, value)
                is Set<*> -> PrefValue(STRING_SET, values = value.filterIsInstance<String>())
                else -> null
            }

            private const val BOOLEAN = "bool"
            private const val INT = "int"
            private const val LONG = "long"
            private const val FLOAT = "float"
            private const val STRING = "string"
            private const val STRING_SET = "stringSet"
        }
    }
}
