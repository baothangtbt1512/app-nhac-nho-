package com.baoanh.nhacban.data.model

data class TaskItem(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val deadline: Long? = null,
    val priority: Int = 2, // 1: Thấp, 2: Vừa, 3: Cao
    val status: String = "TODO", // TODO, IN_PROGRESS, DONE
    val category: String = "Chung",
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
) {
    val isDone: Boolean get() = status == "DONE"
}
