package com.orbit.spatialjournal.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.orbit.spatialjournal.MainActivity
import com.orbit.spatialjournal.R
import com.orbit.spatialjournal.core.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    init { ensureChannels() }

    private fun ensureChannels() {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(
            NotificationChannel(Constants.NOTIFICATION_CHANNEL_REMINDERS, context.getString(R.string.channel_reminders), NotificationManager.IMPORTANCE_DEFAULT)
        )
        manager.createNotificationChannel(
            NotificationChannel(Constants.NOTIFICATION_CHANNEL_VISITS, context.getString(R.string.channel_visits), NotificationManager.IMPORTANCE_MIN)
        )
        manager.createNotificationChannel(
            NotificationChannel(Constants.NOTIFICATION_CHANNEL_BACKUP, context.getString(R.string.channel_backup), NotificationManager.IMPORTANCE_LOW)
        )
    }

    private fun deepLinkIntent(path: String): PendingIntent {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("orbit://$path"), context, MainActivity::class.java)
        return PendingIntent.getActivity(context, path.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    fun showReminderNotification(reminderId: String, title: String) {
        val notification = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_REMINDERS)
            .setSmallIcon(R.drawable.ic_orbit_marker)
            .setContentTitle(title)
            .setContentText(context.getString(R.string.notification_reminder_body))
            .setContentIntent(deepLinkIntent("reminder/$reminderId"))
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(reminderId.hashCode(), notification)
    }

    fun showPlaceReminderNotification(reminderId: String) {
        val notification = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_REMINDERS)
            .setSmallIcon(R.drawable.ic_orbit_marker)
            .setContentTitle(context.getString(R.string.notification_place_reminder_title))
            .setContentText(context.getString(R.string.notification_reminder_body))
            .setContentIntent(deepLinkIntent("reminder/$reminderId"))
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(reminderId.hashCode(), notification)
    }

    fun showDuplicatesFoundNotification(count: Int) {
        val notification = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_BACKUP)
            .setSmallIcon(R.drawable.ic_orbit_marker)
            .setContentTitle(context.getString(R.string.notification_duplicates_title))
            .setContentText(context.getString(R.string.notification_duplicates_body, count))
            .setContentIntent(deepLinkIntent("duplicates"))
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(9001, notification)
    }
}
