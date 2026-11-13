package com.baoanh.nhacban.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.baoanh.nhacban.AlarmAlertActivity
import com.baoanh.nhacban.audio.AudioVibratorHelper
import com.baoanh.nhacban.data.db.DatabaseHelper
import com.baoanh.nhacban.notification.NotificationHelper

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra(AlarmScheduler.EXTRA_REMINDER_ID, -1L)
        if (reminderId == -1L) return

        Log.d("AlarmReceiver", "Received alarm for reminder #$reminderId")

        val db = DatabaseHelper.getInstance(context)
        val reminder = db.getReminderById(reminderId) ?: return

        // Only fire if reminder is still active or snoozed (not completed or paused)
        if (!reminder.isActive) {
            Log.d("AlarmReceiver", "Reminder #$reminderId is ${reminder.status}, skipping alert")
            return
        }

        val updatedRepeatCount = reminder.repeatCount + 1

        // Check max repeat count if specified (> 0)
        if (reminder.maxRepeatCount > 0 && updatedRepeatCount > reminder.maxRepeatCount) {
            // Reached max repeats, mark as completed or paused
            db.updateReminder(reminder.copy(status = "PAUSED", repeatCount = updatedRepeatCount))
            return
        }

        // Trigger Audio & Vibration
        AudioVibratorHelper.playAlarm(
            context = context,
            escalationLevel = reminder.escalationLevel,
            vibrateEnabled = reminder.vibrationEnabled
        )

        // Show Persistent Notification with Action Buttons
        val notificationHelper = NotificationHelper(context)
        notificationHelper.showReminderNotification(
            reminderId = reminder.id,
            title = reminder.title,
            description = reminder.description,
            escalationLevel = reminder.escalationLevel
        )

        // If level 4 escalation, also directly launch the full screen alert activity
        if (reminder.escalationLevel >= 4) {
            val alertIntent = Intent(context, AlarmAlertActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("reminder_id", reminder.id)
                putExtra("reminder_title", reminder.title)
                putExtra("reminder_desc", reminder.description)
            }
            context.startActivity(alertIntent)
        }

        // PERSISTENT REMINDER MECHANISM:
        // Schedule next ring at now + repeatIntervalMinutes
        val nextRingTime = System.currentTimeMillis() + (reminder.repeatIntervalMinutes * 60 * 1000L)
        val updatedReminder = reminder.copy(
            scheduledAt = nextRingTime,
            repeatCount = updatedRepeatCount,
            status = "ACTIVE"
        )
        db.updateReminder(updatedReminder)

        val scheduler = AlarmScheduler(context)
        scheduler.schedule(updatedReminder)
        Log.d("AlarmReceiver", "Rescheduled next persistent ring in ${reminder.repeatIntervalMinutes} min")
    }
}
