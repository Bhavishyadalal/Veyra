package app.veyra.music.download

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

/** Export survives a closed sheet/activity, with an explicit foreground lifetime and cancellation. */
class DownloadExportService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var work: Job? = null
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(NotificationChannel(CHANNEL, getString(R.string.export_all_downloads), NotificationManager.IMPORTANCE_LOW))
        if (Build.VERSION.SDK_INT >= 29) startForeground(ID, notification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        else startForeground(ID, notification())
        if (intent?.action == CANCEL) {
            work?.cancel()
            stopSelf()
            return START_NOT_STICKY
        }
        if (work == null) {
            val notifier = scope.launch {
                DownloadExport.state.collectLatest {
                    delay(350)
                    manager.notify(ID, notification())
                }
            }
            work = scope.launch {
                try { DownloadExport.run(applicationContext) }
                finally {
                    notifier.cancel()
                    DownloadExport.finished()
                    work = null
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            }
        }
        return START_NOT_STICKY
    }

    override fun onTimeout(startId: Int, fgsType: Int) {
        scope.cancel()
        stopSelf()
    }

    override fun onDestroy() {
        scope.cancel()
        if (work == null) DownloadExport.interrupted()
        super.onDestroy()
    }

    private fun notification(): android.app.Notification {
        val state = DownloadExport.state.value
        val cancel = PendingIntent.getService(this, ID, Intent(this, javaClass).setAction(CANCEL), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val open = PendingIntent.getActivity(this, ID, Intent(this, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, CHANNEL)
            .setSmallIcon(R.drawable.ic_notification_logo)
            .setContentTitle(getString(R.string.export_all_downloads))
            .setContentText("${state.processed}/${state.total} · ${state.title.ifEmpty { "Music/Veyra" }}")
            .setContentIntent(open)
            .setProgress(state.total.coerceAtLeast(1), state.processed, state.total == 0)
            .setOngoing(true).setSilent(true).setOnlyAlertOnce(true)
            .addAction(0, getString(android.R.string.cancel), cancel).build()
    }

    private companion object {
        const val CHANNEL = "download-export"
        const val ID = 0x8176
        const val CANCEL = "app.veyra.music.download.EXPORT_CANCEL"
    }
}
