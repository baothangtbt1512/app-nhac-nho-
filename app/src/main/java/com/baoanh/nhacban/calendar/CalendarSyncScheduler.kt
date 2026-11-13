package com.baoanh.nhacban.calendar

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.baoanh.nhacban.data.db.DatabaseHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object CalendarSyncScheduler {

    private const val TAG = "CalendarSyncScheduler"
    private const val REQUEST_CODE = 99991

    fun schedulePeriodicSync(context: Context) {
        val db = DatabaseHelper.getInstance(context)
        val settings = db.getSettings()

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, CalendarSyncReceiver::class.java).apply {
            action = CalendarSyncReceiver.ACTION_PERIODIC_SYNC
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (!settings.autoSyncGoogleCalendar) {
            alarmManager.cancel(pendingIntent)
            Log.d(TAG, "Auto sync disabled, cancelled periodic alarm")
            return
        }

        val intervalMillis = settings.syncFrequencyHours * 3600 * 1000L
        val triggerAtMillis = System.currentTimeMillis() + intervalMillis

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
            Log.d(TAG, "Scheduled auto sync every ${settings.syncFrequencyHours}h, next run at $triggerAtMillis")
        } catch (e: Exception) {
            Log.e(TAG, "Error scheduling periodic calendar sync", e)
        }
    }

    fun performSilentSync(context: Context, onComplete: ((Int) -> Unit)? = null) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            Log.d(TAG, "Cannot sync: READ_CALENDAR permission not granted")
            onComplete?.invoke(0)
            return
        }

        val db = DatabaseHelper.getInstance(context)
        val settings = db.getSettings()
        val calendars = GoogleCalendarHelper.getAvailableCalendars(context, excludeHolidays = settings.ignoreAllDayAndHolidays)

        CoroutineScope(Dispatchers.IO).launch {
            // Clean up any previously stored holiday reminders if filter is active
            if (settings.ignoreAllDayAndHolidays) {
                db.cleanUpHolidayReminders()
            }

            var totalImported = 0
            for (cal in calendars) {
                // Skip holiday calendars
                if (settings.ignoreAllDayAndHolidays && cal.isHolidayCalendar) {
                    continue
                }

                // Sync all Google calendars
                if (cal.accountType == "com.google" || cal.isPrimary) {
                    val events = GoogleCalendarHelper.fetchUpcomingEvents(
                        context = context,
                        calendarId = cal.id,
                        daysAhead = 30,
                        ignoreAllDayAndHolidays = settings.ignoreAllDayAndHolidays
                    )
                    val imported = db.importGoogleCalendarEvents(events)
                    totalImported += imported
                }
            }
            Log.d(TAG, "Auto-sync complete: $totalImported new events imported")
            onComplete?.invoke(totalImported)
        }
    }
}
