package com.baoanh.nhacban.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.baoanh.nhacban.audio.AudioVibratorHelper
import com.baoanh.nhacban.data.db.DatabaseHelper
import com.baoanh.nhacban.notification.NotificationHelper
import java.util.Calendar

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra(EXTRA_REMINDER_ID, -1L)
        if (reminderId == -1L) return

        val db = DatabaseHelper.getInstance(context)
        val scheduler = AlarmScheduler(context)
        val notificationHelper = NotificationHelper(context)

        // Always stop ringing sound & vibration upon any button tap
        AudioVibratorHelper.stop()

        val reminder = db.getReminderById(reminderId) ?: return

        when (intent.action) {
            ACTION_COMPLETE -> {
                scheduler.cancel(reminderId)
                notificationHelper.dismissNotification(reminderId)

                // If it's a recurring reminder (DAILY, WEEKLY, etc.), schedule the next cycle
                if (reminder.repeatType != "ONCE") {
                    val nextCycleTime = calculateNextCycleTime(reminder.repeatType, reminder.scheduledAt)
                    val nextReminder = reminder.copy(
                        scheduledAt = nextCycleTime,
                        status = "ACTIVE",
                        repeatCount = 0,
                        updatedAt = System.currentTimeMillis()
                    )
                    db.updateReminder(nextReminder)
                    scheduler.schedule(nextReminder)
                } else {
                    val completed = reminder.copy(
                        status = "COMPLETED",
                        updatedAt = System.currentTimeMillis()
                    )
                    db.updateReminder(completed)
                }
            }

            ACTION_SNOOZE -> {
                notificationHelper.dismissNotification(reminderId)
                val settings = db.getSettings()
                val snoozeMins = settings.defaultSnoozeMinutes
                val nextRingTime = System.currentTimeMillis() + (snoozeMins * 60 * 1000L)
                val snoozed = reminder.copy(
                    scheduledAt = nextRingTime,
                    status = "SNOOZED",
                    updatedAt = System.currentTimeMillis()
                )
                db.updateReminder(snoozed)
                scheduler.schedule(snoozed)
            }

            ACTION_PAUSE -> {
                scheduler.cancel(reminderId)
                notificationHelper.dismissNotification(reminderId)
                val paused = reminder.copy(
                    status = "PAUSED",
                    updatedAt = System.currentTimeMillis()
                )
                db.updateReminder(paused)
            }
        }
    }

    private fun calculateNextCycleTime(repeatType: String, originalTime: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = originalTime
        val now = Calendar.getInstance()

        while (cal.before(now) || cal.timeInMillis <= System.currentTimeMillis()) {
            when (repeatType) {
                "DAILY" -> cal.add(Calendar.DAY_OF_YEAR, 1)
                "WEEKLY" -> cal.add(Calendar.WEEK_OF_YEAR, 1)
                "MONTHLY" -> cal.add(Calendar.MONTH, 1)
                else -> cal.add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        return cal.timeInMillis
    }

    companion object {
        const val ACTION_COMPLETE = "com.baoanh.nhacban.ACTION_COMPLETE"
        const val ACTION_SNOOZE = "com.baoanh.nhacban.ACTION_SNOOZE"
        const val ACTION_PAUSE = "com.baoanh.nhacban.ACTION_PAUSE"
        const val EXTRA_REMINDER_ID = "extra_reminder_id"
    }
}
