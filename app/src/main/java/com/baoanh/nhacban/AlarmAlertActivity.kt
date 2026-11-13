package com.baoanh.nhacban

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.baoanh.nhacban.audio.AudioVibratorHelper
import com.baoanh.nhacban.data.repository.AppRepository
import com.baoanh.nhacban.notification.NotificationHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AlarmAlertActivity : ComponentActivity() {

    private lateinit var repository: AppRepository
    private var reminderId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repository = AppRepository(this)
        reminderId = intent.getLongExtra("reminder_id", -1L)
        val title = intent.getStringExtra("reminder_title") ?: "Lời nhắc quan trọng"
        val desc = intent.getStringExtra("reminder_desc") ?: ""

        setupWindowFlags()

        setContent {
            AlarmAlertScreen(
                title = title,
                desc = desc,
                onComplete = {
                    completeReminder()
                },
                onSnooze = {
                    snoozeReminder()
                },
                onPause = {
                    pauseReminder()
                }
            )
        }
    }

    private fun setupWindowFlags() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun completeReminder() {
        AudioVibratorHelper.stop()
        if (reminderId != -1L) {
            NotificationHelper(this).dismissNotification(reminderId)
            repository.markReminderCompleted(reminderId)
        }
        finish()
    }

    private fun snoozeReminder() {
        AudioVibratorHelper.stop()
        if (reminderId != -1L) {
            NotificationHelper(this).dismissNotification(reminderId)
            repository.snoozeReminder(reminderId, 10)
        }
        finish()
    }

    private fun pauseReminder() {
        AudioVibratorHelper.stop()
        if (reminderId != -1L) {
            NotificationHelper(this).dismissNotification(reminderId)
            repository.togglePauseReminder(reminderId)
        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        AudioVibratorHelper.stop()
    }
}

@Composable
fun AlarmAlertScreen(
    title: String,
    desc: String,
    onComplete: () -> Unit,
    onSnooze: () -> Unit,
    onPause: () -> Unit
) {
    val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: App name and current time
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "BẢO ANH NHẮC BẠN",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF818CF8),
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = currentTime,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }

            // Center Animated Icon and Title
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(140.dp)
                        .scale(pulseScale)
                        .background(Color(0x334F46E5), CircleShape)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(90.dp)
                            .background(Color(0xFF4F46E5), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                if (desc.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = desc,
                        fontSize = 16.sp,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Nhắc nhở dai dẳng đến khi bạn xử lý!",
                    fontSize = 13.sp,
                    color = Color(0xFFF59E0B),
                    fontWeight = FontWeight.Medium
                )
            }

            // Bottom Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Button(
                    onClick = onComplete,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "XÁC NHẬN HOÀN THÀNH",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onSnooze,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B))
                    ) {
                        Icon(Icons.Default.Snooze, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = "Hoãn 10 phút",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }

                    OutlinedButton(
                        onClick = onPause,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Tạm dừng",
                            fontSize = 14.sp,
                            color = Color(0xFFE2E8F0)
                        )
                    }
                }
            }
        }
    }
}
