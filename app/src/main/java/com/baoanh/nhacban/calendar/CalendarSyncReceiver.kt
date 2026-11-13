package com.baoanh.nhacban.calendar

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class CalendarSyncReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Received periodic calendar sync broadcast")

        // Perform silent sync in background
        CalendarSyncScheduler.performSilentSync(context) { count ->
            Log.d(TAG, "Background sync finished with $count new reminders")
        }

        // Reschedule next check
        CalendarSyncScheduler.schedulePeriodicSync(context)
    }

    companion object {
        private const val TAG = "CalendarSyncReceiver"
        const val ACTION_PERIODIC_SYNC = "com.baoanh.nhacban.ACTION_PERIODIC_CALENDAR_SYNC"
    }
}
