package app.veyra.music.update

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import app.veyra.music.MainActivity
import app.veyra.music.R
import app.veyra.music.data.AppUpdateChecker
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

/** APK transfer belongs to the foreground service, never to a sheet or Activity scope. */
class AppUpdateService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var work: Job? = null
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(NotificationChannel(CHANNEL, getString(R.string.app_updates), NotificationManager.IMPORTANCE_LOW))
        if (Build.VERSION.SDK_INT >= 29) startForeground(ID, notification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        else startForeground(ID, notification())
        if (intent?.action == CANCEL) {
            AppUpdateChecker.cancelDownload()
            if (work == null) { stopForeground(STOP_FOREGROUND_REMOVE); stopSelf() }
            return START_NOT_STICKY
        }
        if (work == null) work = scope.launch {
            val networkGuard = if (intent?.getBooleanExtra("unmetered_only", false) == true) launch {
                app.veyra.music.data.settings.AppSettings.meteredConnection.collectLatest {
                    if (it != false) AppUpdateChecker.cancelDownload()
                }
            } else null
            val notifier = launch {
                AppUpdateChecker.download.collectLatest { delay(300); manager.notify(ID, notification()) }
            }
            try { AppUpdateChecker.downloadApk(applicationContext) }
            finally {
                notifier.cancel()
                networkGuard?.cancel()
                val ready = AppUpdateChecker.download.value is AppUpdateChecker.DownloadState.Ready
                stopForeground(STOP_FOREGROUND_REMOVE)
                if (ready) manager.notify(ID, notification(ready = true))
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }
    override fun onTimeout(startId: Int, fgsType: Int) { AppUpdateChecker.cancelDownload(); stopSelf() }
    override fun onDestroy() { AppUpdateChecker.cancelDownload(); scope.cancel(); super.onDestroy() }
    private fun notification(ready: Boolean = false): android.app.Notification {
        val progress = AppUpdateChecker.download.value as? AppUpdateChecker.DownloadState.Downloading
        val open = PendingIntent.getActivity(this, ID, Intent(this, MainActivity::class.java)
            .putExtra("open_app_update", true), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val cancel = PendingIntent.getService(this, ID, Intent(this, javaClass).setAction(CANCEL),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        return NotificationCompat.Builder(this, CHANNEL).setSmallIcon(R.drawable.ic_notification_logo)
            .setContentTitle(getString(if (ready) R.string.update_ready_to_install else R.string.update_downloading_apk))
            .setContentText(if (ready) getString(R.string.update_install_permission_hint) else "${((progress?.fraction ?: 0f) * 100).toInt()}%")
            .setContentIntent(open).setAutoCancel(ready).setOngoing(!ready).setSilent(true).setOnlyAlertOnce(true)
            .apply { if (!ready) { setProgress(100, ((progress?.fraction ?: 0f) * 100).toInt(), progress?.fraction == 0f)
                addAction(0, getString(android.R.string.cancel), cancel) } }.build()
    }
    private companion object { const val CHANNEL = "app-updates"; const val ID = 0x8177; const val CANCEL = "app.veyra.UPDATE_CANCEL" }
}
