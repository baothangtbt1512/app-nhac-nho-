package com.baoanh.nhacban.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object AudioVibratorHelper {
    private var currentRingtone: Ringtone? = null

    fun playAlarm(context: Context, escalationLevel: Int, vibrateEnabled: Boolean) {
        stop()

        val appContext = context.applicationContext

        // Play Sound
        if (escalationLevel >= 3) {
            try {
                var alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                if (alertUri == null) {
                    alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                }
                if (alertUri == null) {
                    alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                }

                currentRingtone = RingtoneManager.getRingtone(appContext, alertUri)?.apply {
                    audioAttributes = AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                    play()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Vibrate
        if (vibrateEnabled || escalationLevel >= 2) {
            try {
                val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vibratorManager = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                    vibratorManager.defaultVibrator
                } else {
                    @Suppress("DEPRECATION")
                    appContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                }

                val pattern = if (escalationLevel >= 3) {
                    longArrayOf(0, 500, 200, 500, 200, 1000)
                } else {
                    longArrayOf(0, 300, 200, 300)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(pattern, -1)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun stop() {
        try {
            currentRingtone?.let {
                if (it.isPlaying) {
                    it.stop()
                }
            }
            currentRingtone = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
