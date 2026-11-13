package com.baoanh.nhacban.calendar

import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import android.util.Log
import com.baoanh.nhacban.data.model.Reminder
import java.util.TimeZone

data class CalendarAccount(
    val id: Long,
    val displayName: String,
    val accountName: String,
    val accountType: String,
    val isPrimary: Boolean,
    val isHolidayCalendar: Boolean = false
)

object GoogleCalendarHelper {

    private const val TAG = "GoogleCalendarHelper"

    fun isHolidayCalendar(displayName: String, accountName: String): Boolean {
        val text = "$displayName $accountName".lowercase()
        return text.contains("holiday") ||
                text.contains("ngày lễ") ||
                text.contains("lễ hội") ||
                text.contains("vietnamese__vi") ||
                text.contains("sinh nhật") ||
                text.contains("birthday") ||
                accountName.contains("holiday.calendar.google.com")
    }

    fun isHolidayOrAllDayEvent(title: String, isAllDay: Boolean, ignoreHolidays: Boolean): Boolean {
        if (!ignoreHolidays) return false
        // 1. All-day events in Google Calendar are almost always holidays or birthdays, not time-specific appointments
        if (isAllDay) return true

        // 2. Keyword check for holidays / Tết
        val titleLower = title.lowercase().trim()
        val holidayKeywords = listOf(
            "tết", "nghỉ lễ", "ngày lễ", "quốc khánh", "giỗ tổ", "chiến thắng",
            "lao động", "giáng sinh", "năm mới", "holiday", "observance",
            "new year", "christmas", "phật đản", "trung thu", "giao thừa",
            "mùng 1", "mùng 2", "mùng 3", "2/9", "30/4", "1/5", "10/3",
            "lễ hội", "sinh nhật", "birthday"
        )
        return holidayKeywords.any { titleLower.contains(it) }
    }

    fun getAvailableCalendars(context: Context, excludeHolidays: Boolean = true): List<CalendarAccount> {
        val list = mutableListOf<CalendarAccount>()
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.ACCOUNT_TYPE,
            CalendarContract.Calendars.IS_PRIMARY
        )

        try {
            val cursor = context.contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                projection,
                null,
                null,
                "${CalendarContract.Calendars.ACCOUNT_NAME} ASC"
            )

            cursor?.use {
                val idCol = it.getColumnIndexOrThrow(CalendarContract.Calendars._ID)
                val nameCol = it.getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)
                val accNameCol = it.getColumnIndexOrThrow(CalendarContract.Calendars.ACCOUNT_NAME)
                val accTypeCol = it.getColumnIndexOrThrow(CalendarContract.Calendars.ACCOUNT_TYPE)
                val primCol = it.getColumnIndexOrThrow(CalendarContract.Calendars.IS_PRIMARY)

                while (it.moveToNext()) {
                    val id = it.getLong(idCol)
                    val name = it.getString(nameCol) ?: "Lịch"
                    val accName = it.getString(accNameCol) ?: ""
                    val accType = it.getString(accTypeCol) ?: ""
                    val isPrimary = it.getInt(primCol) == 1
                    val isHoliday = isHolidayCalendar(name, accName)

                    if (!excludeHolidays || !isHoliday) {
                        list.add(CalendarAccount(id, name, accName, accType, isPrimary, isHoliday))
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Calendar permission denied", e)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching calendars", e)
        }

        // Prioritize Google Calendars
        return list.sortedWith(
            compareByDescending<CalendarAccount> { it.accountType == "com.google" }
                .thenByDescending { it.isPrimary }
        )
    }

    fun fetchUpcomingEvents(
        context: Context,
        calendarId: Long,
        daysAhead: Int = 30,
        ignoreAllDayAndHolidays: Boolean = true
    ): List<Reminder> {
        val reminders = mutableListOf<Reminder>()
        val now = System.currentTimeMillis()
        val endTime = now + (daysAhead * 86400000L)

        val projection = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.ALL_DAY
        )

        val selection = "(${CalendarContract.Events.CALENDAR_ID} = ?) AND " +
                "(${CalendarContract.Events.DTSTART} >= ?) AND " +
                "(${CalendarContract.Events.DTSTART} <= ?) AND " +
                "(${CalendarContract.Events.DELETED} = 0)"

        val selectionArgs = arrayOf(calendarId.toString(), now.toString(), endTime.toString())

        try {
            val cursor = context.contentResolver.query(
                CalendarContract.Events.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                "${CalendarContract.Events.DTSTART} ASC"
            )

            cursor?.use {
                val idCol = it.getColumnIndexOrThrow(CalendarContract.Events._ID)
                val titleCol = it.getColumnIndexOrThrow(CalendarContract.Events.TITLE)
                val descCol = it.getColumnIndexOrThrow(CalendarContract.Events.DESCRIPTION)
                val startCol = it.getColumnIndexOrThrow(CalendarContract.Events.DTSTART)
                val allDayCol = it.getColumnIndexOrThrow(CalendarContract.Events.ALL_DAY)

                while (it.moveToNext()) {
                    val eventId = it.getLong(idCol)
                    val title = it.getString(titleCol) ?: "Sự kiện lịch"
                    val desc = it.getString(descCol) ?: ""
                    val startTime = it.getLong(startCol)
                    val isAllDay = it.getInt(allDayCol) == 1

                    // FILTER OUT HOLIDAYS AND ALL-DAY EVENTS
                    if (isHolidayOrAllDayEvent(title, isAllDay, ignoreAllDayAndHolidays)) {
                        Log.d(TAG, "Skipping holiday/all-day event: '$title' (allDay=$isAllDay)")
                        continue
                    }

                    reminders.add(
                        Reminder(
                            title = title,
                            description = desc,
                            scheduledAt = startTime,
                            repeatType = "ONCE",
                            repeatIntervalMinutes = 5,
                            priority = 2,
                            category = "Google Calendar",
                            vibrationEnabled = true,
                            escalationLevel = 3,
                            status = "ACTIVE",
                            calendarEventId = eventId
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching events from calendar", e)
        }

        return reminders
    }

    fun exportReminderToCalendar(
        context: Context,
        calendarId: Long,
        reminder: Reminder
    ): Long? {
        val cv = ContentValues().apply {
            put(CalendarContract.Events.CALENDAR_ID, calendarId)
            put(CalendarContract.Events.TITLE, "⏰ ${reminder.title}")
            put(CalendarContract.Events.DESCRIPTION, "${reminder.description}\n[Được đồng bộ từ Bảo Anh Nhắc Bạn]")
            put(CalendarContract.Events.DTSTART, reminder.scheduledAt)
            put(CalendarContract.Events.DTEND, reminder.scheduledAt + (30 * 60 * 1000L))
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            put(CalendarContract.Events.HAS_ALARM, 1)
        }

        return try {
            val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, cv)
            uri?.lastPathSegment?.toLongOrNull()
        } catch (e: Exception) {
            Log.e(TAG, "Error exporting reminder to calendar", e)
            null
        }
    }
}
