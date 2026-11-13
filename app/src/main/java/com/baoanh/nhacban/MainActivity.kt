package com.baoanh.nhacban

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.content.ContextCompat
import com.baoanh.nhacban.ui.screens.CalendarScreen
import com.baoanh.nhacban.ui.screens.HabitScreen
import com.baoanh.nhacban.ui.screens.HomeScreen
import com.baoanh.nhacban.ui.screens.SettingsScreen
import com.baoanh.nhacban.ui.screens.StatisticsScreen
import com.baoanh.nhacban.ui.theme.BaoAnhNhacBanTheme
import com.baoanh.nhacban.ui.viewmodel.MainViewModel

enum class NavigationItem(val label: String, val icon: ImageVector) {
    HOME("Nhắc nhở", Icons.Default.Alarm),
    CALENDAR("Lịch", Icons.Default.CalendarMonth),
    HABITS("Thói quen", Icons.Default.Star),
    STATS("Thống kê", Icons.Default.BarChart),
    SETTINGS("Cài đặt", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
            // Notification permission handled
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkAndRequestPermissions()

        setContent {
            val settings by viewModel.settings.collectAsState()
            val darkTheme = settings.darkTheme ?: androidx.compose.foundation.isSystemInDarkTheme()

            BaoAnhNhacBanTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainAppContent(viewModel = viewModel)
                }
            }
        }
    }

    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    var selectedItem by remember { mutableStateOf(NavigationItem.HOME) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationItem.entries.forEach { item ->
                    val isSelected = selectedItem == item
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedItem = item },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            when (selectedItem) {
                NavigationItem.HOME -> HomeScreen(viewModel = viewModel)
                NavigationItem.CALENDAR -> CalendarScreen(viewModel = viewModel)
                NavigationItem.HABITS -> HabitScreen(viewModel = viewModel)
                NavigationItem.STATS -> StatisticsScreen(viewModel = viewModel)
                NavigationItem.SETTINGS -> SettingsScreen(viewModel = viewModel)
            }
        }
    }
}
