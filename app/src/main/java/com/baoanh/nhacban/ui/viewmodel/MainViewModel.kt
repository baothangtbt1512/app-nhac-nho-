package com.baoanh.nhacban.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.baoanh.nhacban.calendar.CalendarAccount
import com.baoanh.nhacban.data.model.AppSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.baoanh.nhacban.data.model.Habit
import com.baoanh.nhacban.data.model.Reminder
import com.baoanh.nhacban.data.model.TaskItem
import com.baoanh.nhacban.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)

    val allReminders: StateFlow<List<Reminder>> = repository.reminders
    val allTasks: StateFlow<List<TaskItem>> = repository.tasks
    val allHabits: StateFlow<List<Habit>> = repository.habits

    private val _selectedFilter = MutableStateFlow("ALL") // ALL, TODAY, ACTIVE, OVERDUE, COMPLETED
    val selectedFilter = _selectedFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _settings = MutableStateFlow(repository.getSettings())
    val settings = _settings.asStateFlow()

    // Filtered Reminders
    val filteredReminders = combine(allReminders, _selectedFilter, _searchQuery) { list, filter, query ->
        val now = System.currentTimeMillis()
        val calNow = Calendar.getInstance()
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val todayEnd = todayStart + 86400000L

        list.filter { r ->
            // Search query filter
            val matchesQuery = query.isEmpty() ||
                    r.title.contains(query, ignoreCase = true) ||
                    r.description.contains(query, ignoreCase = true) ||
                    r.category.contains(query, ignoreCase = true)

            if (!matchesQuery) return@filter false

            // Tab filter
            when (filter) {
                "TODAY" -> r.scheduledAt in todayStart..todayEnd
                "ACTIVE" -> r.isActive
                "OVERDUE" -> r.isActive && r.scheduledAt < now
                "COMPLETED" -> r.isCompleted
                else -> true
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.addReminder(reminder)
        }
    }

    fun updateReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.updateReminder(reminder)
        }
    }

    fun deleteReminder(id: Long) {
        viewModelScope.launch {
            repository.deleteReminder(id)
        }
    }

    fun markReminderCompleted(id: Long) {
        viewModelScope.launch {
            repository.markReminderCompleted(id)
        }
    }

    fun snoozeReminder(id: Long, minutes: Int? = null) {
        viewModelScope.launch {
            repository.snoozeReminder(id, minutes)
        }
    }

    fun togglePauseReminder(id: Long) {
        viewModelScope.launch {
            repository.togglePauseReminder(id)
        }
    }

    fun setReminderEnabled(id: Long, enabled: Boolean) {
        viewModelScope.launch {
            repository.setReminderEnabled(id, enabled)
        }
    }

    // Task actions
    fun addTask(task: TaskItem) {
        viewModelScope.launch { repository.addTask(task) }
    }

    fun updateTask(task: TaskItem) {
        viewModelScope.launch { repository.updateTask(task) }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch { repository.deleteTask(id) }
    }

    fun toggleTaskStatus(id: Long) {
        viewModelScope.launch { repository.toggleTaskStatus(id) }
    }

    // Habit actions
    fun addHabit(habit: Habit) {
        viewModelScope.launch { repository.addHabit(habit) }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch { repository.updateHabit(habit) }
    }

    fun deleteHabit(id: Long) {
        viewModelScope.launch { repository.deleteHabit(id) }
    }

    fun toggleHabitToday(id: Long) {
        viewModelScope.launch { repository.toggleHabitToday(id) }
    }

    // Settings
    fun updateSettings(newSettings: AppSettings) {
        viewModelScope.launch {
            repository.saveSettings(newSettings)
            _settings.value = newSettings
        }
    }

    // Backup & Restore
    fun exportBackupJson(): String = repository.exportBackup()
    fun importBackupJson(json: String): Boolean = repository.importBackup(json)

    // Google Calendar Sync
    fun getAvailableCalendars(excludeHolidays: Boolean = true): List<CalendarAccount> =
        repository.getAvailableCalendars(excludeHolidays)

    fun syncFromGoogleCalendar(calendarId: Long, ignoreHolidays: Boolean = true, onResult: (Int) -> Unit) {
        viewModelScope.launch {
            val count = withContext(Dispatchers.IO) {
                repository.syncFromCalendar(calendarId, ignoreHolidays = ignoreHolidays)
            }
            onResult(count)
        }
    }

    fun cleanUpHolidayReminders(onResult: (Int) -> Unit) {
        viewModelScope.launch {
            val count = withContext(Dispatchers.IO) {
                repository.cleanUpHolidayReminders()
            }
            onResult(count)
        }
    }

    fun exportReminderToGoogle(reminder: Reminder, calendarId: Long, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val eventId = withContext(Dispatchers.IO) {
                repository.exportReminderToGoogleCalendar(reminder, calendarId)
            }
            onResult(eventId != null)
        }
    }
}
