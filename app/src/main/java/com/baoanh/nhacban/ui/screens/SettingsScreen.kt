package com.baoanh.nhacban.ui.screens

import android.app.AlarmManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import com.baoanh.nhacban.calendar.CalendarSyncScheduler
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.baoanh.nhacban.ui.theme.EmeraldSuccess
import com.baoanh.nhacban.ui.viewmodel.MainViewModel

@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()
    val scrollState = rememberScrollState()

    var showImportDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Cài Đặt Hệ Thống",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Tùy chỉnh thông số nhắc nhở và quyền ứng dụng",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION: Reminder Configuration
        SettingSectionHeader(title = "CẤU HÌNH NHẮC NHỞ LIÊN TỤC")

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Default Snooze Interval
                Text(
                    text = "Thời gian hoãn mặc định (Snooze)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(5, 10, 15, 30).forEach { mins ->
                        FilterChip(
                            selected = settings.defaultSnoozeMinutes == mins,
                            onClick = { viewModel.updateSettings(settings.copy(defaultSnoozeMinutes = mins)) },
                            label = { Text("$mins phút") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Default Persistent Repeat Interval
                Text(
                    text = "Chu kỳ nhắc lại liên tục mặc định",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(1, 3, 5, 10).forEach { mins ->
                        FilterChip(
                            selected = settings.defaultRepeatIntervalMinutes == mins,
                            onClick = { viewModel.updateSettings(settings.copy(defaultRepeatIntervalMinutes = mins)) },
                            label = { Text("$mins phút") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION: Automatic Google Calendar Sync
        SettingSectionHeader(title = "TỰ ĐỘNG ĐỒNG BỘ GOOGLE CALENDAR")

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Tự động kiểm tra & đồng bộ ngầm",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Tự động quét lịch mới mà không cần mở app hay bấm nút",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = settings.autoSyncGoogleCalendar,
                        onCheckedChange = { isChecked ->
                            val updated = settings.copy(autoSyncGoogleCalendar = isChecked)
                            viewModel.updateSettings(updated)
                            CalendarSyncScheduler.schedulePeriodicSync(context)
                        }
                    )
                }

                if (settings.autoSyncGoogleCalendar) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Tần suất tự động kiểm tra:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val frequencyOptions = listOf(
                        1 to "Mỗi 1 giờ",
                        4 to "Mỗi 4 giờ (6 lần/ngày)",
                        8 to "Mỗi 8 giờ (3 lần/ngày)",
                        24 to "1 lần/ngày (24 giờ)"
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        frequencyOptions.forEach { (hours, label) ->
                            FilterChip(
                                selected = settings.syncFrequencyHours == hours,
                                onClick = {
                                    val updated = settings.copy(syncFrequencyHours = hours)
                                    viewModel.updateSettings(updated)
                                    CalendarSyncScheduler.schedulePeriodicSync(context)
                                },
                                label = { Text(label, fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // FILTER OUT HOLIDAYS / TET / ALL-DAY EVENTS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Bỏ qua ngày lễ, Tết & sự kiện cả ngày",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Không tạo báo động cho các ngày lễ công cộng, Tết, giỗ tổ hoặc sự kiện cả ngày",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = settings.ignoreAllDayAndHolidays,
                        onCheckedChange = { isChecked ->
                            val updated = settings.copy(ignoreAllDayAndHolidays = isChecked)
                            viewModel.updateSettings(updated)
                            if (isChecked) {
                                viewModel.cleanUpHolidayReminders { }
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // CLEANUP BUTTON FOR ALREADY IMPORTED HOLIDAYS
                OutlinedButton(
                    onClick = {
                        viewModel.cleanUpHolidayReminders { deletedCount ->
                            Toast.makeText(
                                context,
                                if (deletedCount > 0) "Đã dọn dẹp $deletedCount lời nhắc ngày lễ/Tết khỏi ứng dụng!"
                                else "Không có lời nhắc ngày lễ nào cần xóa.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("🧹 Dọn dẹp các lời nhắc ngày lễ/Tết đã nhập", fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION: Sound & Vibration
        SettingSectionHeader(title = "ÂM THANH & RUNG")

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Âm thanh chuông báo", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                    Switch(
                        checked = settings.soundEnabled,
                        onCheckedChange = { viewModel.updateSettings(settings.copy(soundEnabled = it)) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Vibration, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Rung khi có thông báo", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                    Switch(
                        checked = settings.vibrationEnabled,
                        onCheckedChange = { viewModel.updateSettings(settings.copy(vibrationEnabled = it)) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION: System Permissions
        SettingSectionHeader(title = "QUẢN LÝ QUYỀN HỆ THỐNG")

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                PermissionRow(
                    icon = Icons.Default.Alarm,
                    title = "Báo thức chính xác (Exact Alarm)",
                    desc = "Cần thiết để nhắc nhở reo đúng từng phút ngay cả khi tắt màn hình",
                    buttonText = "Cài đặt",
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                                data = Uri.parse("package:${context.packageName}")
                            }
                            context.startActivity(intent)
                        } else {
                            Toast.makeText(context, "Thiết bị của bạn đã hỗ trợ sẵn báo thức chính xác!", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                PermissionRow(
                    icon = Icons.Default.Notifications,
                    title = "Thông báo hệ thống",
                    desc = "Hiển thị thông báo nổi và nút thao tác Hoàn thành/Báo lại",
                    buttonText = "Kiểm tra",
                    onClick = {
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        }
                        context.startActivity(intent)
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                PermissionRow(
                    icon = Icons.Default.CalendarMonth,
                    title = "Truy cập Lịch (Google Calendar)",
                    desc = "Cho phép ứng dụng đồng bộ hai chiều với tài khoản Google Calendar",
                    buttonText = "Cấp quyền",
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.parse("package:${context.packageName}")
                        }
                        context.startActivity(intent)
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                PermissionRow(
                    icon = Icons.Default.BatteryChargingFull,
                    title = "Tối ưu hóa pin",
                    desc = "Nên tắt tối ưu hóa pin cho app để chuông không bị hệ điều hành tắt ngầm",
                    buttonText = "Mở",
                    onClick = {
                        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Mở phần Cài đặt pin của thiết bị", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION: Backup & Restore
        SettingSectionHeader(title = "SAO LƯU & KHÔI PHỤC")

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val json = viewModel.exportBackupJson()
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("BaoAnhNhacBan_Backup", json))
                            Toast.makeText(context, "Đã sao chép toàn bộ dữ liệu JSON vào bộ nhớ tạm!", Toast.LENGTH_LONG).show()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sao lưu JSON", fontSize = 13.sp)
                    }

                    Button(
                        onClick = { showImportDialog = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Khôi phục", fontSize = 13.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // App Info
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Bảo Anh Nhắc Bạn",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Phiên bản 1.0.0 (Bản chuẩn phát hành APK)",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "🔒 Hoạt động hoàn toàn Offline • Không thu thập dữ liệu • Bảo vệ quyền riêng tư 100%",
                    fontSize = 11.sp,
                    color = EmeraldSuccess,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }

    if (showImportDialog) {
        ImportBackupDialog(
            onDismiss = { showImportDialog = false },
            onImport = { json ->
                val success = viewModel.importBackupJson(json)
                if (success) {
                    Toast.makeText(context, "Khôi phục dữ liệu thành công!", Toast.LENGTH_SHORT).show()
                    showImportDialog = false
                } else {
                    Toast.makeText(context, "Lỗi: Dữ liệu JSON không hợp lệ!", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}

@Composable
fun SettingSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    )
}

@Composable
fun PermissionRow(
    icon: ImageVector,
    title: String,
    desc: String,
    buttonText: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(text = desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
        }
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedButton(
            onClick = onClick,
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(buttonText, fontSize = 12.sp)
        }
    }
}

@Composable
fun ImportBackupDialog(
    onDismiss: () -> Unit,
    onImport: (String) -> Unit
) {
    var jsonText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Khôi Phục Dữ Liệu",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Dán chuỗi dữ liệu JSON đã sao lưu trước đó vào ô bên dưới:",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = jsonText,
                    onValueChange = { jsonText = it },
                    placeholder = { Text("Dán chuỗi JSON vào đây...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(10.dp)) {
                        Text("Hủy")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = { onImport(jsonText.trim()) },
                        enabled = jsonText.isNotBlank(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Xác nhận nhập", color = Color.White)
                    }
                }
            }
        }
    }
}
