package com.baoanh.nhacban.data.model

data class Reminder(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val scheduledAt: Long,
    val repeatType: String = "ONCE", // ONCE, DAILY, WEEKLY, MONTHLY
    val repeatIntervalMinutes: Int = 5, // 1, 3, 5, 10, 15, 30, 60 minutes persistent reminder
    val priority: Int = 2, // 1: Thấp, 2: Trung bình, 3: Cao
    val category: String = "Chung", // Công việc, Học tập, Cá nhân, Sức khỏe, Gia đình
    val vibrationEnabled: Boolean = true,
    val escalationLevel: Int = 3, // 1: Thông báo, 2: Rung, 3: Chuông + nổi, 4: Toàn màn hình
    val maxRepeatCount: Int = 0, // 0 = nhắc liên tục đến khi hoàn thành
    val status: String = "ACTIVE", // ACTIVE, SNOOZED, PAUSED, COMPLETED
    val repeatCount: Int = 0,
    val calendarEventId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val isCompleted: Boolean get() = status == "COMPLETED"
    val isPaused: Boolean get() = status == "PAUSED"
    val isSnoozed: Boolean get() = status == "SNOOZED"
    val isActive: Boolean get() = status == "ACTIVE" || status == "SNOOZED"
}
