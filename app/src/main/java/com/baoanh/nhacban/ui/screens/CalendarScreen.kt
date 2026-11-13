package com.baoanh.nhacban.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.baoanh.nhacban.calendar.CalendarAccount
import com.baoanh.nhacban.ui.theme.EmeraldSuccess
import com.baoanh.nhacban.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CalendarScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val allReminders by viewModel.allReminders.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()

    var showSyncDialog by remember { mutableStateOf(false) }
    var calendarList by remember { mutableStateOf<List<CalendarAccount>>(emptyList()) }

    // Calendar permissions launcher
    val calendarPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val readGranted = permissions[Manifest.permission.READ_CALENDAR] ?: false
        val writeGranted = permissions[Manifest.permission.WRITE_CALENDAR] ?: false
        if (readGranted || writeGranted) {
            val list = viewModel.getAvailableCalendars()
            calendarList = list
            showSyncDialog = true
        } else {
            Toast.makeText(context, "Cần cấp quyền truy cập lịch để đồng bộ với Google Calendar!", Toast.LENGTH_SHORT).show()
        }
    }

    val checkAndOpenSync = {
        val hasRead = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED
        val hasWrite = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED
        if (hasRead && hasWrite) {
            calendarList = viewModel.getAvailableCalendars()
            showSyncDialog = true
        } else {
            calendarPermissionLauncher.launch(
                arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)
            )
        }
    }

    var selectedCalendar by remember { mutableStateOf(Calendar.getInstance()) }
    var currentMonthOffset by remember { mutableIntStateOf(0) }

    val monthCal = remember(currentMonthOffset) {
        Calendar.getInstance().apply {
            add(Calendar.MONTH, currentMonthOffset)
            set(Calendar.DAY_OF_MONTH, 1)
        }
    }

    val monthFormat = SimpleDateFormat("MMMM yyyy", Locale("vi", "VN"))
    val dayOfWeekFormat = SimpleDateFormat("EEE", Locale("vi", "VN"))
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    // Days in current selected month
    val daysInMonth = monthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val daysList = (1..daysInMonth).map { day ->
        Calendar.getInstance().apply {
            timeInMillis = monthCal.timeInMillis
            set(Calendar.DAY_OF_MONTH, day)
        }
    }

    // Filter reminders and tasks for selected date
    val selectedYear = selectedCalendar.get(Calendar.YEAR)
    val selectedDayOfYear = selectedCalendar.get(Calendar.DAY_OF_YEAR)

    val remindersForDay = allReminders.filter { r ->
        val cal = Calendar.getInstance().apply { timeInMillis = r.scheduledAt }
        cal.get(Calendar.YEAR) == selectedYear && cal.get(Calendar.DAY_OF_YEAR) == selectedDayOfYear
    }

    val tasksForDay = allTasks.filter { t ->
        t.deadline?.let { dl ->
            val cal = Calendar.getInstance().apply { timeInMillis = dl }
            cal.get(Calendar.YEAR) == selectedYear && cal.get(Calendar.DAY_OF_YEAR) == selectedDayOfYear
        } ?: false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top App Bar with Title & Google Calendar Sync Action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Lịch & Sự Kiện",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Lịch biểu & Đồng bộ Google Calendar",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            OutlinedButton(
                onClick = { checkAndOpenSync() },
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Google Calendar", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Month Selector Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonthOffset-- }) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Tháng trước")
            }

            Text(
                text = monthFormat.format(monthCal.time).replaceFirstChar { it.uppercase() },
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            IconButton(onClick = { currentMonthOffset++ }) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Tháng sau")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal Days Strip
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(daysList) { calDay ->
                val dayNum = calDay.get(Calendar.DAY_OF_MONTH)
                val dayOfWeek = dayOfWeekFormat.format(calDay.time)
                val isSelected = calDay.get(Calendar.YEAR) == selectedYear &&
                        calDay.get(Calendar.DAY_OF_YEAR) == selectedDayOfYear

                // Check if this day has any reminders
                val hasEvents = allReminders.any { r ->
                    val rCal = Calendar.getInstance().apply { timeInMillis = r.scheduledAt }
                    rCal.get(Calendar.YEAR) == calDay.get(Calendar.YEAR) &&
                            rCal.get(Calendar.DAY_OF_YEAR) == calDay.get(Calendar.DAY_OF_YEAR)
                }

                Surface(
                    modifier = Modifier
                        .width(52.dp)
                        .height(72.dp)
                        .clickable { selectedCalendar = calDay.clone() as Calendar },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    tonalElevation = if (isSelected) 4.dp else 0.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = dayOfWeek.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = dayNum.toString(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                        if (hasEvents) {
                            Spacer(modifier = Modifier.height(3.dp))
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color.White else MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Selected Date Label
        val selectedDateStr = SimpleDateFormat("EEEE, dd/MM/yyyy", Locale("vi", "VN")).format(selectedCalendar.time)
        Text(
            text = selectedDateStr.replaceFirstChar { it.uppercase() },
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Content for selected date
        if (remindersForDay.isEmpty() && tasksForDay.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Không có lịch nhắc nào trong ngày này",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (remindersForDay.isNotEmpty()) {
                    item {
                        Text(
                            text = "Lời Nhắc (${remindersForDay.size})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    items(remindersForDay) { r ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Alarm, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = r.title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                    Text(
                                        text = "${timeFormat.format(r.scheduledAt)} • Nhắc lại mỗi ${r.repeatIntervalMinutes}p" +
                                                if (r.calendarEventId != null) " • [Google Calendar]" else "",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                                if (r.isCompleted) {
                                    Text("Đã xong", color = EmeraldSuccess, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                if (tasksForDay.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Công Việc (${tasksForDay.size})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    items(tasksForDay) { t ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(text = t.title, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(70.dp)) }
            }
        }
    }

    // Google Calendar Sync Dialog
    if (showSyncDialog) {
        GoogleCalendarSyncDialog(
            calendars = calendarList,
            onDismiss = { showSyncDialog = false },
            onSyncCalendar = { calId, ignoreHolidays ->
                viewModel.syncFromGoogleCalendar(calId, ignoreHolidays = ignoreHolidays) { count ->
                    Toast.makeText(
                        context,
                        if (count > 0) "Đã đồng bộ thành công $count sự kiện (đã loại bỏ ngày lễ)!" else "Không có sự kiện mới cần đồng bộ.",
                        Toast.LENGTH_LONG
                    ).show()
                    showSyncDialog = false
                }
            }
        )
    }
}

@Composable
fun GoogleCalendarSyncDialog(
    calendars: List<CalendarAccount>,
    onDismiss: () -> Unit,
    onSyncCalendar: (Long, Boolean) -> Unit
) {
    var selectedCalId by remember {
        mutableLongStateOf(calendars.firstOrNull()?.id ?: -1L)
    }
    var ignoreHolidays by remember { mutableStateOf(true) }
    var isSyncing by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Đồng Bộ Google Calendar",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Chọn tài khoản lịch trên điện thoại để nhập các cuộc hẹn, sự kiện cá nhân:",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.outline
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (calendars.isEmpty()) {
                    Text(
                        text = "Không tìm thấy lịch Google nào trên máy. Hãy đảm bảo bạn đã đăng nhập tài khoản Google trên điện thoại.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.height(140.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(calendars) { cal ->
                            val isSelected = cal.id == selectedCalId
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedCalId = cal.id }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedCalId = cal.id }
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = cal.displayName,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "${cal.accountName} (${if (cal.accountType == "com.google") "Google" else cal.accountType})",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Toggle Ignore Holidays & All-day Events
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Bỏ qua ngày lễ & sự kiện cả ngày",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Không tạo báo thức cho Tết, nghỉ lễ, sự kiện cả ngày",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = ignoreHolidays,
                        onCheckedChange = { ignoreHolidays = it }
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Đóng")
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            if (selectedCalId != -1L) {
                                isSyncing = true
                                onSyncCalendar(selectedCalId, ignoreHolidays)
                            }
                        },
                        enabled = selectedCalId != -1L && !isSyncing,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Đồng bộ ngay", color = Color.White)
                    }
                }
            }
        }
    }
}
