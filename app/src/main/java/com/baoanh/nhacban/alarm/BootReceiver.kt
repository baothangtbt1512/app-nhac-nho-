package com.baoanh.nhacban.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.baoanh.nhacban.calendar.CalendarSyncScheduler
import com.baoanh.nhacban.data.db.DatabaseHelper

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            Log.d("BootReceiver", "Device rebooted or package replaced, rescheduling active reminders...")
            val db = DatabaseHelper.getInstance(context)
            val scheduler = AlarmScheduler(context)
            val activeReminders = db.getActiveReminders()

            var count = 0
            for (reminder in activeReminders) {
                scheduler.schedule(reminder)
                count++
            }
            Log.d("BootReceiver", "Successfully restored $count reminders.")

            // Schedule periodic Google Calendar sync after boot
            CalendarSyncScheduler.schedulePeriodicSync(context)
            CalendarSyncScheduler.performSilentSync(context)
        }
    }
}
