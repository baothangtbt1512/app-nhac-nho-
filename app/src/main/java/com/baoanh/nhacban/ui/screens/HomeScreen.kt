package com.baoanh.nhacban.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.draw.scale
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.baoanh.nhacban.data.model.Reminder
import com.baoanh.nhacban.ui.theme.AmberWarning
import com.baoanh.nhacban.ui.theme.EmeraldSuccess
import com.baoanh.nhacban.ui.theme.RoseDanger
import com.baoanh.nhacban.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val reminders by viewModel.filteredReminders.collectAsState()
    val allReminders by viewModel.allReminders.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingReminder by remember { mutableStateOf<Reminder?>(null) }
    var quickAddText by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }

    // Counts
    val now = System.currentTimeMillis()
    val activeCount = allReminders.count { it.isActive }
    val overdueCount = allReminders.count { it.isActive && it.scheduledAt < now }
    val completedCount = allReminders.count { it.isCompleted }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingReminder = null
                    showAddDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tạo lời nhắc", modifier = Modifier.size(28.dp))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // App Top Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Bảo Anh Nhắc Bạn",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Nhắc nhở dai dẳng đến khi bạn hoàn thành 🎯",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                IconButton(onClick = { isSearchVisible = !isSearchVisible }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Tìm kiếm",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            AnimatedVisibility(visible = isSearchVisible) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Tìm kiếm lời nhắc...", fontSize = 14.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Stats Banner
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Đang chờ",
                    count = activeCount.toString(),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Quá hạn",
                    count = overdueCount.toString(),
                    color = RoseDanger,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Đã xong",
                    count = completedCount.toString(),
                    color = EmeraldSuccess,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Add Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = quickAddText,
                    onValueChange = { quickAddText = it },
                    placeholder = { Text("Thêm nhanh lời nhắc...", fontSize = 14.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (quickAddText.isNotBlank()) {
                            val in15Mins = System.currentTimeMillis() + 15 * 60 * 1000L
                            viewModel.addReminder(
                                Reminder(
                                    title = quickAddText.trim(),
                                    scheduledAt = in15Mins,
                                    repeatIntervalMinutes = 5,
                                    priority = 2
                                )
                            )
                            quickAddText = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Thêm", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Filter Chips Row
            val filters = listOf(
                "ALL" to "Tất cả",
                "TODAY" to "Hôm nay",
                "ACTIVE" to "Đang nhắc",
                "OVERDUE" to "Quá hạn",
                "COMPLETED" to "Đã xong"
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filters) { (key, label) ->
                    FilterChip(
                        selected = selectedFilter == key,
                        onClick = { viewModel.setFilter(key) },
                        label = { Text(label, fontSize = 13.sp) },
                        shape = RoundedCornerShape(16.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Reminders List
            if (reminders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Chưa có lời nhắc nào ở mục này",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "Nhấn dấu + bên dưới để tạo ngay lời nhắc mới!",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(reminders, key = { it.id }) { item ->
                        ReminderCard(
                            reminder = item,
                            onComplete = { viewModel.markReminderCompleted(item.id) },
                            onSnooze = { viewModel.snoozeReminder(item.id, 10) },
                            onToggleEnabled = { enabled -> viewModel.setReminderEnabled(item.id, enabled) },
                            onEdit = {
                                editingReminder = item
                                showAddDialog = true
                            },
                            onDelete = { viewModel.deleteReminder(item.id) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

    if (showAddDialog) {
        ReminderDialog(
            initialReminder = editingReminder,
            onDismiss = {
                showAddDialog = false
                editingReminder = null
            },
            onSave = { reminder ->
                if (editingReminder == null) {
                    viewModel.addReminder(reminder)
                } else {
                    viewModel.updateReminder(reminder)
                }
                showAddDialog = false
                editingReminder = null
            }
        )
    }
}

@Composable
fun StatCard(
    title: String,
    count: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.12f),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun ReminderCard(
    reminder: Reminder,
    onComplete: () -> Unit,
    onSnooze: () -> Unit,
    onToggleEnabled: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val priorityColor = when (reminder.priority) {
        3 -> RoseDanger
        2 -> AmberWarning
        else -> EmeraldSuccess
    }

    val isOverdue = reminder.isActive && reminder.scheduledAt < System.currentTimeMillis()

    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val scheduledDate = Date(reminder.scheduledAt)
    val timeText = "${timeFormat.format(scheduledDate)} - ${dateFormat.format(scheduledDate)}"

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                reminder.isCompleted -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                reminder.isPaused -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (reminder.isCompleted || reminder.isPaused) 0.dp else 2.dp
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Priority tag, Category, Switch (Bật/Tắt) and Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (reminder.isPaused) MaterialTheme.colorScheme.outline else priorityColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when {
                            reminder.isPaused -> "Đã tắt chuông"
                            reminder.priority == 3 -> "Ưu tiên cao"
                            reminder.priority == 2 -> "Ưu tiên vừa"
                            else -> "Ưu tiên thấp"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (reminder.isPaused) MaterialTheme.colorScheme.outline else priorityColor
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "•  ${reminder.category}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                // Công tắc gạt bật/tắt (Switch) + Dropdown menu
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = reminder.isActive,
                        onCheckedChange = { isChecked ->
                            onToggleEnabled(isChecked)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.scale(0.8f)
                    )

                    Box {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.MoreVert, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Chỉnh sửa") },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                                onClick = {
                                    menuExpanded = false
                                    onEdit()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Xóa lời nhắc", color = RoseDanger) },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = RoseDanger) },
                                onClick = {
                                    menuExpanded = false
                                    onDelete()
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Title & Description
            Text(
                text = reminder.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = when {
                    reminder.isCompleted -> MaterialTheme.colorScheme.outline
                    reminder.isPaused -> MaterialTheme.colorScheme.outline
                    else -> MaterialTheme.colorScheme.onSurface
                },
                textDecoration = if (reminder.isCompleted) TextDecoration.LineThrough else null,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (reminder.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = reminder.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Time & Persistent indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Alarm,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = when {
                        reminder.isPaused -> MaterialTheme.colorScheme.outline
                        isOverdue -> RoseDanger
                        else -> MaterialTheme.colorScheme.primary
                    }
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when {
                        reminder.isPaused -> "Lịch nhắc: $timeText (Tạm dừng)"
                        isOverdue -> "Quá hạn: $timeText"
                        else -> timeText
                    },
                    fontSize = 12.sp,
                    fontWeight = if (isOverdue && !reminder.isPaused) FontWeight.Bold else FontWeight.Normal,
                    color = when {
                        reminder.isPaused -> MaterialTheme.colorScheme.outline
                        isOverdue -> RoseDanger
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                // Persistent Badge or Paused Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (reminder.isPaused) MaterialTheme.colorScheme.surfaceVariant
                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Repeat,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = if (reminder.isPaused) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (reminder.isPaused) "Đang tắt" else "Lặp lại ${reminder.repeatIntervalMinutes}p",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (reminder.isPaused) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Quick Actions Bar (Complete, Snooze, or Resume)
            if (!reminder.isCompleted) {
                Spacer(modifier = Modifier.height(12.dp))
                if (reminder.isActive) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Complete button
                        TextButton(
                            onClick = onComplete,
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .background(EmeraldSuccess.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Xong", color = EmeraldSuccess, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        // Snooze button
                        TextButton(
                            onClick = onSnooze,
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .background(AmberWarning.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                        ) {
                            Icon(Icons.Default.Snooze, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Hoãn 10p", color = AmberWarning, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                } else if (reminder.isPaused) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Lời nhắc đang tắt chuông",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                        TextButton(
                            onClick = { onToggleEnabled(true) },
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text(
                                text = "Bật lại ngay",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
