package app.veyra.music.download

import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.junit.Assert.*
import org.junit.Test
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread

class DownloadRecordConcurrencyTest {
    @Test fun `concurrent completions persist both aliases without an older snapshot writing last`() {
        val prefsField = Downloads::class.java.getDeclaredField("prefs").apply { isAccessible = true }
        val originalPrefs = prefsField.get(Downloads)
        val savedField = Downloads::class.java.getDeclaredField("_saved").apply { isAccessible = true }
        val metaField = Downloads::class.java.getDeclaredField("_savedMetadata").apply { isAccessible = true }
        @Suppress("UNCHECKED_CAST")
        val saved = savedField.get(Downloads) as MutableStateFlow<Map<String,String>>
        @Suppress("UNCHECKED_CAST")
        val meta = metaField.get(Downloads) as MutableStateFlow<Map<String,SavedSongMetadata>>
        val originalSaved = saved.value
        val originalMeta = meta.value
        val disk = ConcurrentHashMap<String,String>()
        val editor = Proxy.newProxyInstance(SharedPreferences.Editor::class.java.classLoader,
            arrayOf(SharedPreferences.Editor::class.java)) { proxy, method, args ->
            when (method.name) {
                "putString" -> { disk[args!![0] as String] = args[1] as String; proxy }
                "apply" -> null
                "commit" -> true
                else -> if (method.returnType == SharedPreferences.Editor::class.java) proxy else null
            }
        }
        val prefs = Proxy.newProxyInstance(SharedPreferences::class.java.classLoader,
            arrayOf(SharedPreferences::class.java)) { _, method, _ -> if (method.name == "edit") editor else null }
        val record = Downloads::class.java.getDeclaredMethod("record", Function1::class.java, Function1::class.java).apply { isAccessible = true }
        val firstPaused = CountDownLatch(1)
        val releaseFirst = CountDownLatch(1)
        val secondEntered = CountDownLatch(1)
        val failures = AtomicReference<Throwable?>(null)
        var first: Thread? = null
        var second: Thread? = null
        try {
            prefsField.set(Downloads, prefs)
            saved.value = emptyMap(); meta.value = emptyMap()
            first = thread {
                try {
                    val addSaved: (Map<String,String>) -> Map<String,String> = { it + ("a" to "file:///a") }
                    val addMeta: (Map<String,SavedSongMetadata>) -> Map<String,SavedSongMetadata> = {
                        firstPaused.countDown()
                        check(releaseFirst.await(5, TimeUnit.SECONDS))
                        it
                    }
                    record.invoke(Downloads, addSaved, addMeta)
                } catch (t: Throwable) { failures.set(t) }
            }
            assertTrue(firstPaused.await(5, TimeUnit.SECONDS))
            second = thread {
                try {
                    val addSaved: (Map<String,String>) -> Map<String,String> = { secondEntered.countDown(); it + ("b" to "file:///b") }
                    val unchangedMeta: (Map<String,SavedSongMetadata>) -> Map<String,SavedSongMetadata> = { it }
                    record.invoke(Downloads, addSaved, unchangedMeta)
                } catch (t: Throwable) { failures.set(t) }
            }
            // The fixed implementation blocks before creating B's snapshot;
            // the old one reaches B's transform while A is paused.
            val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5)
            while (second.state != Thread.State.BLOCKED && secondEntered.count > 0 && System.nanoTime() < deadline) Thread.yield()
            if (secondEntered.count == 0L) second.join(5000)
            releaseFirst.countDown()
            first.join(5000); second.join(5000)
            assertFalse(first.isAlive || second.isAlive)
            failures.get()?.let { throw AssertionError(it) }
            assertEquals(setOf("a", "b"), Json.parseToJsonElement(disk.getValue("downloaded_tracks")).jsonObject.keys)
            assertEquals(saved.value.keys, Json.parseToJsonElement(disk.getValue("downloaded_tracks")).jsonObject.keys)
        } finally {
            releaseFirst.countDown()
            first?.join(5000); second?.join(5000)
            prefsField.set(Downloads, originalPrefs)
            saved.value = originalSaved; meta.value = originalMeta
        }
    }
}
