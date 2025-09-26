package space.webkombinat.feg2.Model

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import jakarta.inject.Inject
import space.webkombinat.feg2.MainActivity
import space.webkombinat.feg2.Model.Constants.NOTIF_CONTENT_TEXT
import space.webkombinat.feg2.R

class Notif @Inject constructor (
    private val context: Context,
) {
    val notifManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val resultIntent = Intent(context, MainActivity::class.java)
    val resultPendingIntent = TaskStackBuilder.create(context).run {
        addNextIntentWithParentStack(resultIntent)
        getPendingIntent(0,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    var notifCreated = false
        private set

    val notificationPending = PendingIntent.getService(
        context,
        0,
        Intent(context, BackgroundService::class.java).also { intent ->
            intent.action = BackgroundService.Action.NOTIF_STOP.toString()
//            context.startService(intent)
        },
        PendingIntent.FLAG_IMMUTABLE
    )

    val notifBuilder =  NotificationCompat.Builder(context, "running_channel")
        .setContentTitle("FEG2")
        .setContentText(NOTIF_CONTENT_TEXT(time = "00:00", temp_f = "00.0", temp_s = "00.0"))
        .setSmallIcon(R.drawable.graphsvg)
        .setOngoing(true)
        .addAction(0, "停止", notificationPending)
        .setContentIntent(resultPendingIntent)

    fun notifCreate() {
        if (notifCreated) return
        notifCreated = true
        val channel = NotificationChannel(
            "running_channel",
            "Running Notifications",
            NotificationManager.IMPORTANCE_MIN
        )
        println("called notifCreate")
        notifManager.createNotificationChannel(channel)
    }

    fun delete() {
        notifCreated = false
    }

    fun notifUpdate(time: String, temp_f: String, temp_s: String) {
        println("notifUpdateCalled")
        notifManager.notify(
            1,
            notifBuilder
                .setContentText(NOTIF_CONTENT_TEXT(time = time, temp_f = temp_f, temp_s = temp_s))
                .build()
        )
    }

    fun stopNotif() {
        notifManager.cancel(1)
    }

}