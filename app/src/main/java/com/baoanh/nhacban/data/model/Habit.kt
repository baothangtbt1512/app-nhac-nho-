package com.baoanh.nhacban.data.model

data class Habit(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val targetDaysPerWeek: Int = 7,
    val colorHex: String = "#4F46E5",
    val icon: String = "flag",
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val currentStreak: Int = 0,
    val completedToday: Boolean = false
)

data class HabitLog(
    val id: Long = 0,
    val habitId: Long,
    val dateString: String, // Format: YYYY-MM-DD
    val completedAt: Long = System.currentTimeMillis()
)
