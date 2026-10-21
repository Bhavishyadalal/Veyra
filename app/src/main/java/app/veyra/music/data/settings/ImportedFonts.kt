package app.veyra.music.data.settings

import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest

/** Immutable, validated font blobs. Stored references are hashes, never arbitrary backup paths. */
object ImportedFonts {
    const val MAX_BYTES=4*1024*1024
    fun file(context:Context,hash:String):File? = hash.takeIf {it.matches(Regex("[a-f0-9]{64}"))}
        ?.let {File(context.filesDir,"imported-fonts/$it.font").takeIf {f->f.isFile}}
    suspend fun import(context:Context,uri:Uri):String = withContext(Dispatchers.IO) {
        val bytes=context.contentResolver.openInputStream(uri)?.use {input -> val out=java.io.ByteArrayOutputStream(); val buffer=ByteArray(8192); var total=0; while(true) {val n=input.read(buffer); if(n<0)break; total+=n; require(total<=MAX_BYTES) {"Font exceeds 4 MB"}; out.write(buffer,0,n)}; out.toByteArray()} ?: error("Cannot open this font")
        store(context,bytes)
    }
    internal fun store(context:Context,bytes:ByteArray):String {
        require(bytes.size in 12..MAX_BYTES) {"Choose a TTF or OTF font smaller than 4 MB"}
        val signature=bytes.take(4).toByteArray()
        require(signature.contentEquals(byteArrayOf(0,1,0,0)) || String(signature,Charsets.US_ASCII) in setOf("OTTO","ttcf","true")) {"This is not a supported TTF or OTF font"}
        val hash=MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") {"%02x".format(it)}
        val root=File(context.filesDir,"imported-fonts").apply {mkdirs()}
        val target=File(root,"$hash.font")
        if(target.isFile)return hash
        val staging=File.createTempFile("font-",".tmp",root)
        try {
            staging.writeBytes(bytes)
            try {Typeface.createFromFile(staging)} catch(error:Exception) {throw IllegalArgumentException("This font cannot be read",error)}
            check(staging.renameTo(target)) {"Cannot save this font"}
        } finally {staging.delete()}
        return hash
    }
}

/** Decode off the UI thread. Keyed state never exposes a prior font under a new selection. */
@Composable
internal fun rememberImportedFont(hash:String):FontFamily? {
    val context=LocalContext.current.applicationContext
    var family by remember(hash) {mutableStateOf<FontFamily?>(null)}
    LaunchedEffect(hash) {
        family=withContext(Dispatchers.IO) {ImportedFonts.file(context,hash)?.let {runCatching {FontFamily(Typeface.createFromFile(it))}.getOrNull()}}
    }
    return family
}
