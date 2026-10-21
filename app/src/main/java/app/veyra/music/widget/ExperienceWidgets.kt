package app.veyra.music.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import androidx.compose.ui.graphics.toArgb
import app.veyra.music.MainActivity
import app.veyra.music.R
import app.veyra.music.data.settings.AppSettings
import app.veyra.music.data.themes.ThemeRepository
import app.veyra.music.playback.PlayerDeepLink
import app.veyra.music.ui.styles.visuals
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.roundToInt

/** Additional event-driven widgets. No polling, services or animation loops are started by a widget. */
abstract class ExperienceWidget : AppWidgetProvider() {
    override fun onUpdate(context:Context, manager:AppWidgetManager, ids:IntArray) = update(context)
    override fun onAppWidgetOptionsChanged(context:Context, manager:AppWidgetManager, id:Int, newOptions:Bundle) = update(context)
    override fun onDeleted(context:Context, ids:IntArray) { ids.forEach(ExperienceWidgets::forget) }
    private fun update(context:Context) {
        val pending=goAsync()
        ExperienceWidgets.update(context) { runCatching {pending.finish()} }
    }
}
class MediaWidgetSleeve : ExperienceWidget()
class MediaWidgetVinyl : ExperienceWidget()
class MediaWidgetStrip : ExperienceWidget()
class MediaWidgetLyrics : ExperienceWidget()
class MediaWidgetStudio : ExperienceWidget()

internal enum class ExperienceWidgetKind { SLEEVE, VINYL, STRIP, LYRICS, STUDIO }

internal object ExperienceWidgets {
    private val scope=CoroutineScope(SupervisorJob()+Dispatchers.IO)
    private val mutex=Mutex()
    private val changes=Channel<Context>(Channel.CONFLATED)
    private val cache=object:android.util.LruCache<String,Bitmap>(8*1024*1024) {
        override fun sizeOf(key:String,value:Bitmap)=value.allocationByteCount
    }
    @Volatile private var hasLyricWidget = false
    @Volatile private var liveLyric: Pair<String,String>? = null
    private val drawn=java.util.concurrent.ConcurrentHashMap<Int,String>()
    private val providers=listOf(MediaWidgetSleeve::class.java to ExperienceWidgetKind.SLEEVE,
        MediaWidgetVinyl::class.java to ExperienceWidgetKind.VINYL, MediaWidgetStrip::class.java to ExperienceWidgetKind.STRIP,
        MediaWidgetLyrics::class.java to ExperienceWidgetKind.LYRICS,MediaWidgetStudio::class.java to ExperienceWidgetKind.STUDIO)
    init {scope.launch {for(context in changes) runCatching {withTimeout(6500) {mutex.withLock {renderAll(context)}}} }}
    fun refresh(context:Context) {changes.trySend(context.applicationContext)}
    fun update(context:Context,finish:()->Unit) {
        scope.launch {try {withTimeoutOrNull(6500) {mutex.withLock {renderAll(context.applicationContext)}}} finally {finish()}}
    }
    fun forget(id:Int) {drawn.remove(id)}

    /** Called only when the service's lyric text changes, never on every playhead tick. */
    fun publishLyric(context:Context,mediaId:String,text:String) {
        liveLyric=mediaId to text
        if (!hasLyricWidget) return
        val prefs=context.getSharedPreferences("veyra_widget",Context.MODE_PRIVATE)
        if(prefs.getString("lyric_media",null)==mediaId && prefs.getString("lyric_text",null)==text)return
        prefs.edit().putString("lyric_media",mediaId).putString("lyric_text",text).apply()
        refresh(context)
    }
    private suspend fun renderAll(context:Context) {
        val manager=AppWidgetManager.getInstance(context)
        val snapshot=MediaWidgetSnapshot.load(context)
        val style=AppSettings.appStyle.value
        val roomArt=java.io.File(context.filesDir,"app-style-listening-room-v1.webp").takeIf {it.isFile}?.path
        val pack=if(style.custom)style.visuals(roomArt?.let {mapOf("header" to it)}.orEmpty()) else ThemeRepository.active.value
        val background=pack?.surface?.toArgb() ?: Color.rgb(20,25,33)
        val foreground=pack?.text?.toArgb() ?: Color.rgb(245,242,231)
        val accent=pack?.accent?.toArgb() ?: Color.rgb(183,211,234)
        val header=pack?.asset("header")
        for((provider,kind) in providers) {
            val ids=manager.getAppWidgetIds(ComponentName(context,provider))
            if (kind==ExperienceWidgetKind.LYRICS) hasLyricWidget=ids.isNotEmpty()
            for(id in ids) {
                currentCoroutineContext().ensureActive()
                val options=manager.getAppWidgetOptions(id)
                val width=options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH,if(kind==ExperienceWidgetKind.STRIP)250 else 180).coerceIn(110,600)
                val height=options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT,if(kind==ExperienceWidgetKind.STRIP)56 else 180).coerceIn(56,500)
                val density=context.resources.displayMetrics.density
                val w=(width*density).roundToInt().coerceIn(180,800)
                val h=(height*density).roundToInt().coerceIn(100,650)
                val prefs=context.getSharedPreferences("veyra_widget",Context.MODE_PRIVATE)
                val lyric=if(kind==ExperienceWidgetKind.LYRICS) liveLyric?.takeIf {it.first==snapshot.mediaId}?.second
                    ?: if(prefs.getString("lyric_media",null)==snapshot.mediaId)prefs.getString("lyric_text",null).orEmpty() else "" else ""
                val imageKey="${snapshot.artworkUrl}|$kind|$w|$h|$background|$foreground|$accent|$header|${header?.let {java.io.File(it).lastModified()}}"
                val stateKey="$imageKey|$snapshot|$lyric"
                if(drawn[id]==stateKey)continue
                var image=cache.get(imageKey)
                // Controls update before artwork I/O. Placeholder never carries a previous song's cover.
                manager.updateAppWidget(id,views(context,snapshot,kind,image,foreground,lyric,width))
                if(image==null) {
                    val cover=withTimeoutOrNull(1800) {snapshot.artworkUrl?.let {
                        if(kind==ExperienceWidgetKind.VINYL || kind==ExperienceWidgetKind.STRIP) MediaWidgetArt.circle(context,it,minOf(w,h),it)
                        else MediaWidgetArt.render(context,it,minOf(w,h),minOf(w,h),0,it,minOf(w,h)*.06f)
                    }}
                    image=draw(context,kind,cover,header,w,h,background,foreground,accent)
                    // A transient cover failure should be retried on the next real event.
                    if(cover!=null || snapshot.artworkUrl==null)cache.put(imageKey,image)
                }
                manager.updateAppWidget(id,views(context,snapshot,kind,image,foreground,lyric,width))
                drawn[id]=stateKey.takeIf {snapshot.artworkUrl==null || cache.get(imageKey)!=null} ?: ""
            }
        }
    }
    private fun draw(context:Context,kind:ExperienceWidgetKind,cover:Bitmap?,header:String?,w:Int,h:Int,bg:Int,fg:Int,accent:Int):Bitmap {
        val bitmap=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        val c=Canvas(bitmap);val p=Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val bounds=RectF(0f,0f,w.toFloat(),h.toFloat());val radius=minOf(w,h)*.12f
        val clip=Path().apply {addRoundRect(bounds,radius,radius,Path.Direction.CW)}
        c.clipPath(clip);c.drawColor(bg)
        if(header!=null) {
            val opts=BitmapFactory.Options().apply {inJustDecodeBounds=true};BitmapFactory.decodeFile(header,opts)
            opts.inSampleSize=generateSequence(1){it*2}.first {maxOf(opts.outWidth,opts.outHeight)/it<=800 || it>=64};opts.inJustDecodeBounds=false
            BitmapFactory.decodeFile(header,opts)?.let {theme->p.alpha=48;c.drawBitmap(theme,null,bounds,p);p.alpha=255;theme.recycle()}
        }
        p.color=accent;p.alpha=25;c.drawCircle(w*.87f,h*.05f,w*.7f,p);p.alpha=255
        if(kind==ExperienceWidgetKind.VINYL) {
            val cx=w*.5f;val cy=h*.37f;val r=minOf(w*.43f,h*.34f)
            p.color=Color.rgb(13,16,19);c.drawCircle(cx,cy,r,p)
            p.style=Paint.Style.STROKE;p.strokeWidth=1.2f;p.color=Color.argb(45,255,255,255)
            for(i in 1..9)c.drawCircle(cx,cy,r*(.45f+i*.05f),p)
            p.style=Paint.Style.FILL
            cover?.let {c.drawBitmap(it,null,RectF(cx-r*.43f,cy-r*.43f,cx+r*.43f,cy+r*.43f),p)}
            p.color=accent;c.drawCircle(cx,cy,r*.07f,p)
        } else if(kind==ExperienceWidgetKind.SLEEVE || kind==ExperienceWidgetKind.STUDIO) {
            val side=if(kind==ExperienceWidgetKind.STUDIO) minOf(w*.43f,h*.61f) else minOf(w*.7f,h*.59f)
            val left=if(kind==ExperienceWidgetKind.STUDIO) w*.08f else (w-side)/2
            val top=h*.07f
            p.color=accent;p.alpha=40;c.drawRoundRect(RectF(left-3,top-3,left+side+3,top+side+3),side*.08f,side*.08f,p);p.alpha=255
            cover?.let {c.drawBitmap(it,null,RectF(left,top,left+side,top+side),p)}
            if(kind==ExperienceWidgetKind.STUDIO) {
                p.color=accent;p.strokeWidth=3f
                for(i in 0..10){val x=w*.62f+i*w*.024f;val line=h*(.04f+(.11f*(1-kotlin.math.abs(i-5)/6f)));c.drawLine(x,h*.36f-line,x,h*.36f+line,p)}
            }
        } else if(kind==ExperienceWidgetKind.STRIP) {
            val side=h*.75f;cover?.let {c.drawBitmap(it,null,RectF(w*.025f,(h-side)/2,w*.025f+side,(h+side)/2),p)}
        } else {
            p.color=accent;p.textSize=minOf(w,h)*.29f;p.typeface=Typeface.create("serif",Typeface.BOLD);p.alpha=60
            c.drawText("“",w*.07f,h*.34f,p);p.alpha=255
        }
        p.shader=LinearGradient(0f,h*.48f,0f,h.toFloat(),intArrayOf(Color.TRANSPARENT,bg),null,Shader.TileMode.CLAMP)
        c.drawRect(bounds,p);p.shader=null
        p.style=Paint.Style.STROKE;p.strokeWidth=2f;p.color=fg;p.alpha=35;c.drawRoundRect(RectF(1f,1f,w-1f,h-1f),radius,radius,p)
        return bitmap
    }
    private fun views(context:Context,s:MediaWidgetSnapshot,kind:ExperienceWidgetKind,image:Bitmap?,fg:Int,lyric:String,width:Int):RemoteViews {
        val strip=kind==ExperienceWidgetKind.STRIP
        val v=RemoteViews(context.packageName,if(strip)R.layout.widget_experience_strip else R.layout.widget_experience)
        image?.let {v.setImageViewBitmap(R.id.widget_experience_art,it)}
        v.setTextViewText(R.id.widget_title,if(s.hasTrack)s.title else context.getString(R.string.widget_nothing_played))
        v.setTextViewText(R.id.widget_artist,s.artist)
        for(id in listOf(R.id.widget_title,R.id.widget_artist)){v.setTextColor(id,fg)}
        if(!strip){v.setViewVisibility(R.id.widget_experience_lyric,if(kind==ExperienceWidgetKind.LYRICS)View.VISIBLE else View.GONE)
            v.setTextViewText(R.id.widget_experience_lyric,lyric.ifBlank {context.getString(R.string.widget_lyrics_empty)});v.setTextColor(R.id.widget_experience_lyric,fg)}
        v.setImageViewResource(R.id.widget_toggle,if(s.isPlaying)R.drawable.ic_widget_pause else R.drawable.ic_widget_play)
        v.setContentDescription(R.id.widget_toggle,context.getString(if(s.isPlaying)R.string.immersive_pause else R.string.immersive_play))
        val open=PendingIntent.getActivity(context,0,Intent(context,MainActivity::class.java).putExtra(PlayerDeepLink.EXTRA_OPEN_PLAYER,true).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        v.setOnClickPendingIntent(R.id.widget_root,open)
        for((id,action,enabled) in listOf(Triple(R.id.widget_previous,MediaWidgetActions.ACTION_PREVIOUS,s.hasTrack && s.hasPrevious),Triple(R.id.widget_toggle,MediaWidgetActions.ACTION_TOGGLE,s.hasTrack),Triple(R.id.widget_next,MediaWidgetActions.ACTION_NEXT,s.hasTrack && s.hasNext))) {
            v.setImageViewResource(id,when(id){R.id.widget_previous->R.drawable.ic_widget_previous;R.id.widget_next->R.drawable.ic_widget_next;else->if(s.isPlaying)R.drawable.ic_widget_pause else R.drawable.ic_widget_play})
            v.setInt(id,"setColorFilter",fg);v.setInt(id,"setImageAlpha",if(enabled)255 else 90)
            v.setOnClickPendingIntent(id,if(enabled)MediaWidgetActions.pendingIntent(context,action) else open)
        }
        v.setViewVisibility(R.id.widget_previous,if(strip && width<240)View.GONE else View.VISIBLE)
        return v
    }
}
