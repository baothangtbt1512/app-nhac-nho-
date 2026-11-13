package com.baoanh.nhacban.data.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.baoanh.nhacban.data.model.AppSettings
import com.baoanh.nhacban.data.model.Habit
import com.baoanh.nhacban.data.model.HabitLog
import com.baoanh.nhacban.data.model.Reminder
import com.baoanh.nhacban.data.model.TaskItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "baoanh_nhacban.db"
        private const val DATABASE_VERSION = 2

        @Volatile
        private var instance: DatabaseHelper? = null

        fun getInstance(context: Context): DatabaseHelper {
            return instance ?: synchronized(this) {
                instance ?: DatabaseHelper(context.applicationContext).also { instance = it }
            }
        }

        fun getTodayString(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(Date())
        }
    }

    private val _remindersFlow = MutableStateFlow<List<Reminder>>(emptyList())
    val remindersFlow = _remindersFlow.asStateFlow()

    private val _tasksFlow = MutableStateFlow<List<TaskItem>>(emptyList())
    val tasksFlow = _tasksFlow.asStateFlow()

    private val _habitsFlow = MutableStateFlow<List<Habit>>(emptyList())
    val habitsFlow = _habitsFlow.asStateFlow()

    init {
        refreshAll()
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Reminders table
        db.execSQL(
            """
            CREATE TABLE reminders (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                description TEXT,
                scheduled_at INTEGER NOT NULL,
                repeat_type TEXT NOT NULL,
                repeat_interval_minutes INTEGER NOT NULL,
                priority INTEGER NOT NULL,
                category TEXT NOT NULL,
                vibration_enabled INTEGER NOT NULL,
                escalation_level INTEGER NOT NULL,
                max_repeat_count INTEGER NOT NULL,
                status TEXT NOT NULL,
                repeat_count INTEGER NOT NULL,
                calendar_event_id INTEGER,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL
            )
            """.trimIndent()
        )

        // Tasks table
        db.execSQL(
            """
            CREATE TABLE tasks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                description TEXT,
                deadline INTEGER,
                priority INTEGER NOT NULL,
                status TEXT NOT NULL,
                category TEXT NOT NULL,
                created_at INTEGER NOT NULL,
                completed_at INTEGER
            )
            """.trimIndent()
        )

        // Habits table
        db.execSQL(
            """
            CREATE TABLE habits (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                description TEXT,
                target_days_per_week INTEGER NOT NULL,
                color_hex TEXT NOT NULL,
                icon TEXT NOT NULL,
                created_at INTEGER NOT NULL,
                is_active INTEGER NOT NULL
            )
            """.trimIndent()
        )

        // Habit logs table
        db.execSQL(
            """
            CREATE TABLE habit_logs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                habit_id INTEGER NOT NULL,
                date_string TEXT NOT NULL,
                completed_at INTEGER NOT NULL,
                UNIQUE(habit_id, date_string)
            )
            """.trimIndent()
        )

        // Settings table
        db.execSQL(
            """
            CREATE TABLE app_settings (
                key TEXT PRIMARY KEY,
                value TEXT NOT NULL
            )
            """.trimIndent()
        )

        // Seed initial sample data to get user started immediately
        seedInitialData(db)
    }

    private fun seedInitialData(db: SQLiteDatabase) {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 30) // First reminder in 30 minutes
        }

        // Sample reminder
        val cv = ContentValues().apply {
            put("title", "Uống một cốc nước ấm")
            put("description", "Nhắc nhở duy trì thói quen uống nước đều đặn mỗi ngày!")
            put("scheduled_at", calendar.timeInMillis)
            put("repeat_type", "DAILY")
            put("repeat_interval_minutes", 5)
            put("priority", 2)
            put("category", "Sức khỏe")
            put("vibration_enabled", 1)
            put("escalation_level", 3)
            put("max_repeat_count", 0)
            put("status", "ACTIVE")
            put("repeat_count", 0)
            put("created_at", now)
            put("updated_at", now)
        }
        db.insert("reminders", null, cv)

        // Sample habits
        val habit1 = ContentValues().apply {
            put("name", "Đọc sách 20 phút")
            put("description", "Đọc sách phát triển bản thân mỗi tối")
            put("target_days_per_week", 7)
            put("color_hex", "#4F46E5")
            put("icon", "book")
            put("created_at", now)
            put("is_active", 1)
        }
        db.insert("habits", null, habit1)

        val habit2 = ContentValues().apply {
            put("name", "Tập thể dục 30 phút")
            put("description", "Chạy bộ hoặc hít đất rèn luyện thể lực")
            put("target_days_per_week", 5)
            put("color_hex", "#10B981")
            put("icon", "fitness")
            put("created_at", now)
            put("is_active", 1)
        }
        db.insert("habits", null, habit2)

        // Sample task
        val task1 = ContentValues().apply {
            put("title", "Chuẩn bị kế hoạch công việc tuần này")
            put("description", "Lên danh sách việc cần làm theo mức độ ưu tiên")
            put("deadline", now + 86400000L)
            put("priority", 3)
            put("status", "TODO")
            put("category", "Công việc")
            put("created_at", now)
        }
        db.insert("tasks", null, task1)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            try {
                db.execSQL("ALTER TABLE reminders ADD COLUMN calendar_event_id INTEGER")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun refreshAll() {
        _remindersFlow.value = getAllReminders()
        _tasksFlow.value = getAllTasks()
        _habitsFlow.value = getAllHabits()
    }

    // --- REMINDERS CRUD ---
    fun insertReminder(reminder: Reminder): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("title", reminder.title)
            put("description", reminder.description)
            put("scheduled_at", reminder.scheduledAt)
            put("repeat_type", reminder.repeatType)
            put("repeat_interval_minutes", reminder.repeatIntervalMinutes)
            put("priority", reminder.priority)
            put("category", reminder.category)
            put("vibration_enabled", if (reminder.vibrationEnabled) 1 else 0)
            put("escalation_level", reminder.escalationLevel)
            put("max_repeat_count", reminder.maxRepeatCount)
            put("status", reminder.status)
            put("repeat_count", reminder.repeatCount)
            reminder.calendarEventId?.let { put("calendar_event_id", it) } ?: putNull("calendar_event_id")
            put("created_at", reminder.createdAt)
            put("updated_at", System.currentTimeMillis())
        }
        val id = db.insert("reminders", null, cv)
        refreshAll()
        return id
    }

    fun updateReminder(reminder: Reminder) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("title", reminder.title)
            put("description", reminder.description)
            put("scheduled_at", reminder.scheduledAt)
            put("repeat_type", reminder.repeatType)
            put("repeat_interval_minutes", reminder.repeatIntervalMinutes)
            put("priority", reminder.priority)
            put("category", reminder.category)
            put("vibration_enabled", if (reminder.vibrationEnabled) 1 else 0)
            put("escalation_level", reminder.escalationLevel)
            put("max_repeat_count", reminder.maxRepeatCount)
            put("status", reminder.status)
            put("repeat_count", reminder.repeatCount)
            reminder.calendarEventId?.let { put("calendar_event_id", it) } ?: putNull("calendar_event_id")
            put("updated_at", System.currentTimeMillis())
        }
        db.update("reminders", cv, "id = ?", arrayOf(reminder.id.toString()))
        refreshAll()
    }

    fun deleteReminder(id: Long) {
        val db = writableDatabase
        db.delete("reminders", "id = ?", arrayOf(id.toString()))
        refreshAll()
    }

    fun getReminderById(id: Long): Reminder? {
        val db = readableDatabase
        val cursor = db.query(
            "reminders", null, "id = ?",
            arrayOf(id.toString()), null, null, null
        )
        return cursor.use {
            if (it.moveToFirst()) parseReminder(it) else null
        }
    }

    fun getAllReminders(): List<Reminder> {
        val db = readableDatabase
        val list = mutableListOf<Reminder>()
        val cursor = db.query(
            "reminders", null, null, null, null, null, "scheduled_at ASC"
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(parseReminder(it))
            }
        }
        return list
    }

    fun getActiveReminders(): List<Reminder> {
        val db = readableDatabase
        val list = mutableListOf<Reminder>()
        val cursor = db.query(
            "reminders", null, "status = ? OR status = ?",
            arrayOf("ACTIVE", "SNOOZED"), null, null, "scheduled_at ASC"
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(parseReminder(it))
            }
        }
        return list
    }

    fun isCalendarEventImported(calendarEventId: Long): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            "reminders", arrayOf("id"), "calendar_event_id = ?",
            arrayOf(calendarEventId.toString()), null, null, null
        )
        return cursor.use { it.moveToFirst() }
    }

    fun importGoogleCalendarEvents(events: List<Reminder>): Int {
        var count = 0
        val db = writableDatabase
        db.beginTransaction()
        try {
            for (event in events) {
                val eventId = event.calendarEventId ?: continue
                if (!isCalendarEventImported(eventId)) {
                    insertReminder(event)
                    count++
                }
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
            refreshAll()
        }
        return count
    }

    private fun parseReminder(c: Cursor): Reminder {
        return Reminder(
            id = c.getLong(c.getColumnIndexOrThrow("id")),
            title = c.getString(c.getColumnIndexOrThrow("title")),
            description = c.getString(c.getColumnIndexOrThrow("description")) ?: "",
            scheduledAt = c.getLong(c.getColumnIndexOrThrow("scheduled_at")),
            repeatType = c.getString(c.getColumnIndexOrThrow("repeat_type")),
            repeatIntervalMinutes = c.getInt(c.getColumnIndexOrThrow("repeat_interval_minutes")),
            priority = c.getInt(c.getColumnIndexOrThrow("priority")),
            category = c.getString(c.getColumnIndexOrThrow("category")),
            vibrationEnabled = c.getInt(c.getColumnIndexOrThrow("vibration_enabled")) == 1,
            escalationLevel = c.getInt(c.getColumnIndexOrThrow("escalation_level")),
            maxRepeatCount = c.getInt(c.getColumnIndexOrThrow("max_repeat_count")),
            status = c.getString(c.getColumnIndexOrThrow("status")),
            repeatCount = c.getInt(c.getColumnIndexOrThrow("repeat_count")),
            calendarEventId = if (!c.isNull(c.getColumnIndexOrThrow("calendar_event_id"))) c.getLong(c.getColumnIndexOrThrow("calendar_event_id")) else null,
            createdAt = c.getLong(c.getColumnIndexOrThrow("created_at")),
            updatedAt = c.getLong(c.getColumnIndexOrThrow("updated_at"))
        )
    }

    // --- TASKS CRUD ---
    fun insertTask(task: TaskItem): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("title", task.title)
            put("description", task.description)
            task.deadline?.let { put("deadline", it) }
            put("priority", task.priority)
            put("status", task.status)
            put("category", task.category)
            put("created_at", task.createdAt)
            task.completedAt?.let { put("completed_at", it) }
        }
        val id = db.insert("tasks", null, cv)
        refreshAll()
        return id
    }

    fun updateTask(task: TaskItem) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("title", task.title)
            put("description", task.description)
            task.deadline?.let { put("deadline", it) } ?: putNull("deadline")
            put("priority", task.priority)
            put("status", task.status)
            put("category", task.category)
            task.completedAt?.let { put("completed_at", it) } ?: putNull("completed_at")
        }
        db.update("tasks", cv, "id = ?", arrayOf(task.id.toString()))
        refreshAll()
    }

    fun deleteTask(id: Long) {
        val db = writableDatabase
        db.delete("tasks", "id = ?", arrayOf(id.toString()))
        refreshAll()
    }

    fun getAllTasks(): List<TaskItem> {
        val db = readableDatabase
        val list = mutableListOf<TaskItem>()
        val cursor = db.query(
            "tasks", null, null, null, null, null, "status ASC, deadline ASC, created_at DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    TaskItem(
                        id = it.getLong(it.getColumnIndexOrThrow("id")),
                        title = it.getString(it.getColumnIndexOrThrow("title")),
                        description = it.getString(it.getColumnIndexOrThrow("description")) ?: "",
                        deadline = if (it.isNull(it.getColumnIndexOrThrow("deadline"))) null else it.getLong(it.getColumnIndexOrThrow("deadline")),
                        priority = it.getInt(it.getColumnIndexOrThrow("priority")),
                        status = it.getString(it.getColumnIndexOrThrow("status")),
                        category = it.getString(it.getColumnIndexOrThrow("category")),
                        createdAt = it.getLong(it.getColumnIndexOrThrow("created_at")),
                        completedAt = if (it.isNull(it.getColumnIndexOrThrow("completed_at"))) null else it.getLong(it.getColumnIndexOrThrow("completed_at"))
                    )
                )
            }
        }
        return list
    }

    // --- HABITS & LOGS ---
    fun insertHabit(habit: Habit): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("name", habit.name)
            put("description", habit.description)
            put("target_days_per_week", habit.targetDaysPerWeek)
            put("color_hex", habit.colorHex)
            put("icon", habit.icon)
            put("created_at", habit.createdAt)
            put("is_active", if (habit.isActive) 1 else 0)
        }
        val id = db.insert("habits", null, cv)
        refreshAll()
        return id
    }

    fun updateHabit(habit: Habit) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("name", habit.name)
            put("description", habit.description)
            put("target_days_per_week", habit.targetDaysPerWeek)
            put("color_hex", habit.colorHex)
            put("icon", habit.icon)
            put("is_active", if (habit.isActive) 1 else 0)
        }
        db.update("habits", cv, "id = ?", arrayOf(habit.id.toString()))
        refreshAll()
    }

    fun deleteHabit(id: Long) {
        val db = writableDatabase
        db.delete("habits", "id = ?", arrayOf(id.toString()))
        db.delete("habit_logs", "habit_id = ?", arrayOf(id.toString()))
        refreshAll()
    }

    fun toggleHabitToday(habitId: Long): Boolean {
        val today = getTodayString()
        val db = writableDatabase
        val cursor = db.query(
            "habit_logs", arrayOf("id"), "habit_id = ? AND date_string = ?",
            arrayOf(habitId.toString(), today), null, null, null
        )
        val alreadyCompleted = cursor.use { it.moveToFirst() }

        if (alreadyCompleted) {
            db.delete("habit_logs", "habit_id = ? AND date_string = ?", arrayOf(habitId.toString(), today))
        } else {
            val cv = ContentValues().apply {
                put("habit_id", habitId)
                put("date_string", today)
                put("completed_at", System.currentTimeMillis())
            }
            db.insertWithOnConflict("habit_logs", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
        }
        refreshAll()
        return !alreadyCompleted
    }

    fun getAllHabits(): List<Habit> {
        val db = readableDatabase
        val habits = mutableListOf<Habit>()
        val today = getTodayString()

        val cursor = db.query("habits", null, null, null, null, null, "created_at ASC")
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow("id"))
                val name = it.getString(it.getColumnIndexOrThrow("name"))
                val desc = it.getString(it.getColumnIndexOrThrow("description")) ?: ""
                val target = it.getInt(it.getColumnIndexOrThrow("target_days_per_week"))
                val color = it.getString(it.getColumnIndexOrThrow("color_hex"))
                val icon = it.getString(it.getColumnIndexOrThrow("icon"))
                val createdAt = it.getLong(it.getColumnIndexOrThrow("created_at"))
                val isActive = it.getInt(it.getColumnIndexOrThrow("is_active")) == 1

                val completedToday = isHabitCompletedOnDate(id, today)
                val streak = calculateHabitStreak(id)

                habits.add(
                    Habit(
                        id = id,
                        name = name,
                        description = desc,
                        targetDaysPerWeek = target,
                        colorHex = color,
                        icon = icon,
                        createdAt = createdAt,
                        isActive = isActive,
                        currentStreak = streak,
                        completedToday = completedToday
                    )
                )
            }
        }
        return habits
    }

    private fun isHabitCompletedOnDate(habitId: Long, dateString: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            "habit_logs", arrayOf("id"), "habit_id = ? AND date_string = ?",
            arrayOf(habitId.toString(), dateString), null, null, null
        )
        return cursor.use { it.moveToFirst() }
    }

    private fun calculateHabitStreak(habitId: Long): Int {
        val db = readableDatabase
        val cursor = db.query(
            "habit_logs", arrayOf("date_string"), "habit_id = ?",
            arrayOf(habitId.toString()), null, null, "date_string DESC"
        )
        val dates = mutableSetOf<String>()
        cursor.use {
            while (it.moveToNext()) {
                dates.add(it.getString(0))
            }
        }
        if (dates.isEmpty()) return 0

        var streak = 0
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Check if completed today, or completed yesterday
        val todayStr = sdf.format(cal.time)
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayStr = sdf.format(cal.time)

        var checkCal = Calendar.getInstance()
        if (dates.contains(todayStr)) {
            // Start counting from today backwards
            while (true) {
                val dateStr = sdf.format(checkCal.time)
                if (dates.contains(dateStr)) {
                    streak++
                    checkCal.add(Calendar.DAY_OF_YEAR, -1)
                } else {
                    break
                }
            }
        } else if (dates.contains(yesterdayStr)) {
            // Completed yesterday, streak is still alive
            checkCal.add(Calendar.DAY_OF_YEAR, -1)
            while (true) {
                val dateStr = sdf.format(checkCal.time)
                if (dates.contains(dateStr)) {
                    streak++
                    checkCal.add(Calendar.DAY_OF_YEAR, -1)
                } else {
                    break
                }
            }
        }

        return streak
    }

    // --- APP SETTINGS ---
    fun getSettings(): AppSettings {
        val db = readableDatabase
        val map = mutableMapOf<String, String>()
        val cursor = db.query("app_settings", null, null, null, null, null, null)
        cursor.use {
            while (it.moveToNext()) {
                map[it.getString(0)] = it.getString(1)
            }
        }

        val themeVal = map["darkTheme"]
        val darkTheme = when (themeVal) {
            "true" -> true
            "false" -> false
            else -> null
        }

        return AppSettings(
            defaultSnoozeMinutes = map["defaultSnoozeMinutes"]?.toIntOrNull() ?: 10,
            defaultRepeatIntervalMinutes = map["defaultRepeatIntervalMinutes"]?.toIntOrNull() ?: 5,
            darkTheme = darkTheme,
            soundEnabled = map["soundEnabled"]?.toBooleanStrictOrNull() ?: true,
            vibrationEnabled = map["vibrationEnabled"]?.toBooleanStrictOrNull() ?: true,
            defaultEscalationLevel = map["defaultEscalationLevel"]?.toIntOrNull() ?: 3,
            autoSyncGoogleCalendar = map["autoSyncGoogleCalendar"]?.toBooleanStrictOrNull() ?: true,
            syncFrequencyHours = map["syncFrequencyHours"]?.toIntOrNull() ?: 4,
            ignoreAllDayAndHolidays = map["ignoreAllDayAndHolidays"]?.toBooleanStrictOrNull() ?: true
        )
    }

    fun saveSettings(settings: AppSettings) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val save = { k: String, v: String ->
                val cv = ContentValues().apply {
                    put("key", k)
                    put("value", v)
                }
                db.insertWithOnConflict("app_settings", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
            }
            save("defaultSnoozeMinutes", settings.defaultSnoozeMinutes.toString())
            save("defaultRepeatIntervalMinutes", settings.defaultRepeatIntervalMinutes.toString())
            save("darkTheme", settings.darkTheme?.toString() ?: "system")
            save("soundEnabled", settings.soundEnabled.toString())
            save("vibrationEnabled", settings.vibrationEnabled.toString())
            save("defaultEscalationLevel", settings.defaultEscalationLevel.toString())
            save("autoSyncGoogleCalendar", settings.autoSyncGoogleCalendar.toString())
            save("syncFrequencyHours", settings.syncFrequencyHours.toString())
            save("ignoreAllDayAndHolidays", settings.ignoreAllDayAndHolidays.toString())
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun cleanUpHolidayReminders(): Int {
        val db = writableDatabase
        val reminders = getAllReminders().filter { it.category == "Google Calendar" }
        val holidayKeywords = listOf(
            "tết", "nghỉ lễ", "ngày lễ", "quốc khánh", "giỗ tổ", "chiến thắng",
            "lao động", "giáng sinh", "năm mới", "holiday", "observance",
            "new year", "christmas", "phật đản", "trung thu", "giao thừa",
            "mùng 1", "mùng 2", "mùng 3", "2/9", "30/4", "1/5", "10/3",
            "lễ hội", "sinh nhật", "birthday"
        )

        var deletedCount = 0
        db.beginTransaction()
        try {
            for (r in reminders) {
                val titleLower = r.title.lowercase()
                val isHoliday = holidayKeywords.any { titleLower.contains(it) }
                if (isHoliday) {
                    db.delete("reminders", "id = ?", arrayOf(r.id.toString()))
                    deletedCount++
                }
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
            refreshAll()
        }
        return deletedCount
    }

    // --- BACKUP & RESTORE JSON ---
    fun exportToJson(): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("appName", "Bảo Anh Nhắc Bạn")
        root.put("exportedAt", System.currentTimeMillis())

        val remindersArray = JSONArray()
        getAllReminders().forEach { r ->
            val obj = JSONObject().apply {
                put("title", r.title)
                put("description", r.description)
                put("scheduledAt", r.scheduledAt)
                put("repeatType", r.repeatType)
                put("repeatIntervalMinutes", r.repeatIntervalMinutes)
                put("priority", r.priority)
                put("category", r.category)
                put("vibrationEnabled", r.vibrationEnabled)
                put("escalationLevel", r.escalationLevel)
                put("maxRepeatCount", r.maxRepeatCount)
                put("status", r.status)
                put("repeatCount", r.repeatCount)
                put("createdAt", r.createdAt)
            }
            remindersArray.put(obj)
        }
        root.put("reminders", remindersArray)

        val tasksArray = JSONArray()
        getAllTasks().forEach { t ->
            val obj = JSONObject().apply {
                put("title", t.title)
                put("description", t.description)
                t.deadline?.let { put("deadline", it) }
                put("priority", t.priority)
                put("status", t.status)
                put("category", t.category)
                put("createdAt", t.createdAt)
            }
            tasksArray.put(obj)
        }
        root.put("tasks", tasksArray)

        val habitsArray = JSONArray()
        getAllHabits().forEach { h ->
            val obj = JSONObject().apply {
                put("name", h.name)
                put("description", h.description)
                put("targetDaysPerWeek", h.targetDaysPerWeek)
                put("colorHex", h.colorHex)
                put("icon", h.icon)
                put("createdAt", h.createdAt)
                put("isActive", h.isActive)
            }
            habitsArray.put(obj)
        }
        root.put("habits", habitsArray)

        return root.toString(2)
    }

    fun importFromJson(jsonString: String): Boolean {
        return try {
            val root = JSONObject(jsonString)
            val db = writableDatabase
            db.beginTransaction()
            try {
                if (root.has("reminders")) {
                    val arr = root.getJSONArray("reminders")
                    for (i in 0 until arr.length()) {
                        val o = arr.getJSONObject(i)
                        val r = Reminder(
                            title = o.getString("title"),
                            description = o.optString("description", ""),
                            scheduledAt = o.getLong("scheduledAt"),
                            repeatType = o.optString("repeatType", "ONCE"),
                            repeatIntervalMinutes = o.optInt("repeatIntervalMinutes", 5),
                            priority = o.optInt("priority", 2),
                            category = o.optString("category", "Chung"),
                            vibrationEnabled = o.optBoolean("vibrationEnabled", true),
                            escalationLevel = o.optInt("escalationLevel", 3),
                            maxRepeatCount = o.optInt("maxRepeatCount", 0),
                            status = o.optString("status", "ACTIVE")
                        )
                        insertReminder(r)
                    }
                }
                if (root.has("tasks")) {
                    val arr = root.getJSONArray("tasks")
                    for (i in 0 until arr.length()) {
                        val o = arr.getJSONObject(i)
                        val t = TaskItem(
                            title = o.getString("title"),
                            description = o.optString("description", ""),
                            deadline = if (o.has("deadline")) o.getLong("deadline") else null,
                            priority = o.optInt("priority", 2),
                            status = o.optString("status", "TODO"),
                            category = o.optString("category", "Chung")
                        )
                        insertTask(t)
                    }
                }
                if (root.has("habits")) {
                    val arr = root.getJSONArray("habits")
                    for (i in 0 until arr.length()) {
                        val o = arr.getJSONObject(i)
                        val h = Habit(
                            name = o.getString("name"),
                            description = o.optString("description", ""),
                            targetDaysPerWeek = o.optInt("targetDaysPerWeek", 7),
                            colorHex = o.optString("colorHex", "#4F46E5"),
                            icon = o.optString("icon", "flag"),
                            isActive = o.optBoolean("isActive", true)
                        )
                        insertHabit(h)
                    }
                }
                db.setTransactionSuccessful()
                true
            } finally {
                db.endTransaction()
                refreshAll()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
