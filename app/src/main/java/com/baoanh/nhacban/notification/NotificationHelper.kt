package com.baoanh.nhacban.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.baoanh.nhacban.AlarmAlertActivity
import com.baoanh.nhacban.MainActivity
import com.baoanh.nhacban.R
import com.baoanh.nhacban.alarm.NotificationActionReceiver

class NotificationHelper(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.channel_reminder_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.channel_reminder_desc)
                enableLights(true)
                enableVibration(true)
                setSound(soundUri, audioAttributes)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showReminderNotification(
        reminderId: Long,
        title: String,
        description: String,
        escalationLevel: Int
    ) {
        // Intent to open MainActivity
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("reminder_id", reminderId)
        }
        val openPendingIntent = PendingIntent.getActivity(
            context,
            reminderId.toInt(),
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: Complete
        val completeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_COMPLETE
            putExtra(NotificationActionReceiver.EXTRA_REMINDER_ID, reminderId)
        }
        val completePendingIntent = PendingIntent.getBroadcast(
            context,
            (reminderId * 10 + 1).toInt(),
            completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: Snooze
        val snoozeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_SNOOZE
            putExtra(NotificationActionReceiver.EXTRA_REMINDER_ID, reminderId)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            (reminderId * 10 + 2).toInt(),
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: Pause
        val pauseIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_PAUSE
            putExtra(NotificationActionReceiver.EXTRA_REMINDER_ID, reminderId)
        }
        val pausePendingIntent = PendingIntent.getBroadcast(
            context,
            (reminderId * 10 + 3).toInt(),
            pauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("⏰ $title")
            .setContentText(if (description.isNotEmpty()) description else "Lời nhắc từ Bảo Anh Nhắc Bạn")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(openPendingIntent)
            .setAutoCancel(false)
            .setOngoing(true) // Persistent! Swiping won't dismiss while active
            .addAction(0, "✓ Hoàn thành", completePendingIntent)
            .addAction(0, "⏰ Hoãn 10p", snoozePendingIntent)
            .addAction(0, "⏸ Tạm dừng", pausePendingIntent)

        // Full screen intent for escalation level 4
        if (escalationLevel >= 4) {
            val alertIntent = Intent(context, AlarmAlertActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("reminder_id", reminderId)
                putExtra("reminder_title", title)
                putExtra("reminder_desc", description)
            }
            val alertPendingIntent = PendingIntent.getActivity(
                context,
                (reminderId * 10 + 4).toInt(),
                alertIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.setFullScreenIntent(alertPendingIntent, true)
        }

        notificationManager.notify(reminderId.toInt(), builder.build())
    }

    fun dismissNotification(reminderId: Long) {
        notificationManager.cancel(reminderId.toInt())
    }

    companion object {
        const val CHANNEL_ID = "channel_baoanh_persistent_reminder"
    }
}
