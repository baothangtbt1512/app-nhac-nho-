package com.baoanh.nhacban.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.baoanh.nhacban.data.model.Reminder

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(reminder: Reminder) {
        val triggerTime = reminder.scheduledAt
        val now = System.currentTimeMillis()

        // If trigger time is in the past by more than 1 minute and it's ONCE and never repeated yet,
        // trigger shortly, otherwise schedule at triggerTime.
        val finalTriggerTime = if (triggerTime <= now) {
            now + 2000L // Trigger in 2 seconds if overdue
        } else {
            triggerTime
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_REMINDER_ID, reminder.id)
            putExtra(EXTRA_TITLE, reminder.title)
            putExtra(EXTRA_DESCRIPTION, reminder.description)
            putExtra(EXTRA_ESCALATION, reminder.escalationLevel)
            putExtra(EXTRA_VIBRATE, reminder.vibrationEnabled)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        finalTriggerTime,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        finalTriggerTime,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    finalTriggerTime,
                    pendingIntent
                )
            }
            Log.d("AlarmScheduler", "Scheduled alarm for reminder #${reminder.id} at $finalTriggerTime")
        } catch (e: SecurityException) {
            Log.e("AlarmScheduler", "Exact alarm permission missing", e)
        } catch (e: Exception) {
            Log.e("AlarmScheduler", "Error scheduling alarm", e)
        }
    }

    fun cancel(reminderId: Long) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d("AlarmScheduler", "Cancelled alarm for reminder #$reminderId")
        }
    }

    companion object {
        const val EXTRA_REMINDER_ID = "extra_reminder_id"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_DESCRIPTION = "extra_description"
        const val EXTRA_ESCALATION = "extra_escalation"
        const val EXTRA_VIBRATE = "extra_vibrate"
    }
}
