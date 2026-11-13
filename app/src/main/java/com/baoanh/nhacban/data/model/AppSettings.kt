package com.baoanh.nhacban.data.model

data class AppSettings(
    val defaultSnoozeMinutes: Int = 10,
    val defaultRepeatIntervalMinutes: Int = 5,
    val darkTheme: Boolean? = null, // null for system default
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val defaultEscalationLevel: Int = 3,
    val autoSyncGoogleCalendar: Boolean = true,
    val syncFrequencyHours: Int = 4, // e.g., every 4 hours (6 times a day)
    val ignoreAllDayAndHolidays: Boolean = true // Filter out holidays like Tết, National holidays, All-day events
)
