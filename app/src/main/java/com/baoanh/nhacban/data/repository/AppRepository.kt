package com.baoanh.nhacban.data.repository

import android.content.Context
import com.baoanh.nhacban.alarm.AlarmScheduler
import com.baoanh.nhacban.calendar.CalendarAccount
import com.baoanh.nhacban.calendar.GoogleCalendarHelper
import com.baoanh.nhacban.data.db.DatabaseHelper
import com.baoanh.nhacban.data.model.AppSettings
import com.baoanh.nhacban.data.model.Habit
import com.baoanh.nhacban.data.model.Reminder
import com.baoanh.nhacban.data.model.TaskItem
import kotlinx.coroutines.flow.StateFlow

class AppRepository(private val context: Context) {
    private val db = DatabaseHelper.getInstance(context)
    private val alarmScheduler = AlarmScheduler(context)

    val reminders: StateFlow<List<Reminder>> = db.remindersFlow
    val tasks: StateFlow<List<TaskItem>> = db.tasksFlow
    val habits: StateFlow<List<Habit>> = db.habitsFlow

    fun getSettings(): AppSettings = db.getSettings()
    fun saveSettings(settings: AppSettings) = db.saveSettings(settings)

    fun addReminder(reminder: Reminder): Long {
        val id = db.insertReminder(reminder)
        val created = db.getReminderById(id)
        if (created != null && created.isActive) {
            alarmScheduler.schedule(created)
        }
        return id
    }

    fun updateReminder(reminder: Reminder) {
        db.updateReminder(reminder)
        if (reminder.isActive) {
            alarmScheduler.schedule(reminder)
        } else {
            alarmScheduler.cancel(reminder.id)
        }
    }

    fun deleteReminder(id: Long) {
        alarmScheduler.cancel(id)
        db.deleteReminder(id)
    }

    fun markReminderCompleted(id: Long) {
        val reminder = db.getReminderById(id) ?: return
        alarmScheduler.cancel(id)
        val updated = reminder.copy(status = "COMPLETED", updatedAt = System.currentTimeMillis())
        db.updateReminder(updated)
    }

    fun snoozeReminder(id: Long, minutes: Int? = null) {
        val reminder = db.getReminderById(id) ?: return
        val snoozeMins = minutes ?: db.getSettings().defaultSnoozeMinutes
        val nextTime = System.currentTimeMillis() + (snoozeMins * 60 * 1000L)
        val updated = reminder.copy(
            scheduledAt = nextTime,
            status = "SNOOZED",
            updatedAt = System.currentTimeMillis()
        )
        db.updateReminder(updated)
        alarmScheduler.schedule(updated)
    }

    fun togglePauseReminder(id: Long) {
        val reminder = db.getReminderById(id) ?: return
        if (reminder.isPaused) {
            setReminderEnabled(id, true)
        } else {
            setReminderEnabled(id, false)
        }
    }

    fun setReminderEnabled(id: Long, enabled: Boolean) {
        val reminder = db.getReminderById(id) ?: return
        if (enabled) {
            val now = System.currentTimeMillis()
            val nextTime = if (reminder.scheduledAt <= now) {
                if (reminder.repeatType != "ONCE") {
                    calculateNextCycle(reminder.repeatType, reminder.scheduledAt)
                } else {
                    now + 60_000L // 1 minute from now if in past
                }
            } else reminder.scheduledAt

            val updated = reminder.copy(
                status = "ACTIVE",
                scheduledAt = nextTime,
                repeatCount = 0,
                updatedAt = System.currentTimeMillis()
            )
            db.updateReminder(updated)
            alarmScheduler.schedule(updated)
        } else {
            alarmScheduler.cancel(id)
            val updated = reminder.copy(
                status = "PAUSED",
                updatedAt = System.currentTimeMillis()
            )
            db.updateReminder(updated)
        }
    }

    private fun calculateNextCycle(repeatType: String, originalTime: Long): Long {
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = originalTime
        val now = java.util.Calendar.getInstance()
        while (cal.before(now) || cal.timeInMillis <= System.currentTimeMillis()) {
            when (repeatType) {
                "DAILY" -> cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
                "WEEKLY" -> cal.add(java.util.Calendar.WEEK_OF_YEAR, 1)
                "MONTHLY" -> cal.add(java.util.Calendar.MONTH, 1)
                else -> cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
            }
        }
        return cal.timeInMillis
    }

    // Tasks
    fun addTask(task: TaskItem) = db.insertTask(task)
    fun updateTask(task: TaskItem) = db.updateTask(task)
    fun deleteTask(id: Long) = db.deleteTask(id)
    fun toggleTaskStatus(id: Long) {
        val tasks = db.getAllTasks()
        val task = tasks.find { it.id == id } ?: return
        val newStatus = if (task.isDone) "TODO" else "DONE"
        val completedAt = if (newStatus == "DONE") System.currentTimeMillis() else null
        db.updateTask(task.copy(status = newStatus, completedAt = completedAt))
    }

    // Habits
    fun addHabit(habit: Habit) = db.insertHabit(habit)
    fun updateHabit(habit: Habit) = db.updateHabit(habit)
    fun deleteHabit(id: Long) = db.deleteHabit(id)
    fun toggleHabitToday(id: Long) = db.toggleHabitToday(id)

    // Backup & Restore
    fun exportBackup(): String = db.exportToJson()
    fun importBackup(json: String): Boolean {
        val ok = db.importFromJson(json)
        if (ok) {
            // Reschedule active reminders
            rescheduleAllActiveReminders()
        }
        return ok
    }

    // Google Calendar Sync
    fun getAvailableCalendars(excludeHolidays: Boolean = true): List<CalendarAccount> {
        return GoogleCalendarHelper.getAvailableCalendars(context, excludeHolidays)
    }

    fun syncFromCalendar(calendarId: Long, daysAhead: Int = 30, ignoreHolidays: Boolean = true): Int {
        val events = GoogleCalendarHelper.fetchUpcomingEvents(context, calendarId, daysAhead, ignoreHolidays)
        val importedCount = db.importGoogleCalendarEvents(events)
        if (importedCount > 0) {
            rescheduleAllActiveReminders()
        }
        return importedCount
    }

    fun cleanUpHolidayReminders(): Int {
        return db.cleanUpHolidayReminders()
    }

    fun exportReminderToGoogleCalendar(reminder: Reminder, calendarId: Long): Long? {
        return GoogleCalendarHelper.exportReminderToCalendar(context, calendarId, reminder)
    }

    fun rescheduleAllActiveReminders() {
        val active = db.getActiveReminders()
        active.forEach { r ->
            alarmScheduler.schedule(r)
        }
    }
}
