package com.baoanh.nhacban

import android.app.Application
import com.baoanh.nhacban.calendar.CalendarSyncScheduler
import com.baoanh.nhacban.data.repository.AppRepository
import com.baoanh.nhacban.notification.NotificationHelper

class BaoAnhApp : Application() {

    lateinit var repository: AppRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize Notification Channels
        NotificationHelper(this)

        // Initialize Repository and ensure alarms are active
        repository = AppRepository(this)
        repository.rescheduleAllActiveReminders()

        // Start periodic Google Calendar auto-sync
        CalendarSyncScheduler.schedulePeriodicSync(this)
        CalendarSyncScheduler.performSilentSync(this)
    }

    companion object {
        lateinit var instance: BaoAnhApp
            private set
    }
}
