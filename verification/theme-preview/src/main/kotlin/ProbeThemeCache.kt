package app.veyra.music.ui.appearance

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.request.SuccessResult
import coil3.request.ErrorResult
import coil3.decode.DataSource
import coil3.disk.DiskCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import app.veyra.music.ui.themes.themePreviewRequest
import com.sun.net.httpserver.HttpServer
import java.io.File
import java.net.InetSocketAddress
import java.nio.file.Files
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okio.Path.Companion.toOkioPath

/** Real Coil memory/disk cache with a local HTTP server, not Android device testing. */
fun main() = runBlocking {
    val root=File(System.getProperty("veyra.root"))
    val temp=Files.createTempDirectory("veyra-theme-cache-").toFile()
    val bytes=File(root,"themes/matcha-mischief/artwork/preview.webp").readBytes()
    val calls=AtomicInteger()
    var fail=true
    val server=HttpServer.create(InetSocketAddress("127.0.0.1",0),0)
    server.createContext("/") { exchange ->
        calls.incrementAndGet()
        val failed=exchange.requestURI.path=="/retry" && fail
        exchange.responseHeaders.add("Content-Type","image/webp")
        exchange.responseHeaders.add("Cache-Control",if(failed)"no-store" else "public, max-age=86400")
        exchange.sendResponseHeaders(if(failed)500 else 200,if(failed)0 else bytes.size.toLong())
        exchange.responseBody.use {if(!failed)it.write(bytes)}
    }
    server.start()
    val client=OkHttpClient.Builder().retryOnConnectionFailure(false).build()
    fun loader()=ImageLoader.Builder(PlatformContext.INSTANCE)
        .components {add(OkHttpNetworkFetcherFactory(callFactory={client}))}
        .diskCache {DiskCache.Builder().directory(temp.toOkioPath()).maxSizeBytes(8L*1024*1024).build()}.build()
    var images=loader()
    val base="http://127.0.0.1:${server.address.port}"
    val request=themePreviewRequest(PlatformContext.INSTANCE,"matcha-mischief",1,"$base/preview")
    val results=mutableListOf<String>()
    try {
        val cold=images.execute(request) as SuccessResult
        check(cold.dataSource==DataSource.NETWORK)
        check(cold.memoryCacheKey==request.placeholderMemoryCacheKey) {"Placeholder key must match Coil's actual decoded-image key"}
        check(calls.get()==1)
        results+="Cold load: network image decoded and cached."
        check((images.execute(request) as SuccessResult).dataSource==DataSource.MEMORY_CACHE)
        check(calls.get()==1)
        results+="Warm reopening: memory hit; no HTTP request."
        val revision=themePreviewRequest(PlatformContext.INSTANCE,"matcha-mischief",2,"$base/preview")
        check((images.execute(revision) as SuccessResult).dataSource==DataSource.NETWORK)
        check(calls.get()==2)
        results+="New revision: old cached pixels not reused."
        val retry=themePreviewRequest(PlatformContext.INSTANCE,"retry",1,"$base/retry")
        check(images.execute(retry) is ErrorResult)
        fail=false
        check(images.execute(retry) is SuccessResult)
        results+="Failed request: retry recovers when the server becomes available."
        val beforeOffline=calls.get()
        server.stop(0)
        images.shutdown()
        images=loader()
        check((images.execute(request) as SuccessResult).dataSource==DataSource.DISK)
        check(calls.get()==beforeOffline)
        results+="Offline fresh loader: disk hit after memory/process-equivalent reset."
        val missing=themePreviewRequest(PlatformContext.INSTANCE,"not-cached",1,"$base/missing")
        check(images.execute(missing) is ErrorResult)
        results+="Offline cache miss: explicit failure, not a stuck loading result."
        val remote=themePreviewRequest(PlatformContext.INSTANCE,"sample",3,"https://example.com/preview.webp")
        check(remote.data.toString().endsWith("?themeVersion=3"))
        check(remote.placeholderMemoryCacheKey?.key==remote.memoryCacheKey)
        results+="Versioned HTTPS URL and memory placeholder identity verified."
        val out=File(root,"verification/APPEARANCE-CACHE-RESULTS.txt")
        out.writeText(results.joinToString("\n")+"\nDesktop Coil 3.0.4 probe; Android lifecycle/network behavior remains unverified.\n")
        println(results.joinToString("\n"))
    } finally {
        server.stop(0);images.shutdown();client.dispatcher.executorService.shutdown()
        temp.deleteRecursively()
    }
}
