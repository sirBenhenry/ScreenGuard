package com.sirbenhenry.screenguard.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.sirbenhenry.screenguard.MainActivity
import com.sirbenhenry.screenguard.R

object NotificationUtil {
    const val CHANNEL_MONITOR = "monitor"
    const val CHANNEL_ALERTS = "alerts"
    const val CHANNEL_UPDATES = "updates"
    const val CHANNEL_ACHIEVEMENTS = "achievements"
    const val CHANNEL_DAILY = "daily_summary"

    const val NOTIF_MONITOR = 1
    const val NOTIF_ALERT_BASE = 1000
    const val NOTIF_ACHIEVEMENT_BASE = 5000
    const val NOTIF_DAILY_SUMMARY = 9000

    fun createChannels(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_MONITOR, "Background Monitor", NotificationManager.IMPORTANCE_MIN)
                .apply { description = "Silent notification keeping ScreenGuard active" }
        )
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_ALERTS, "Screen Time Alerts", NotificationManager.IMPORTANCE_HIGH)
                .apply { description = "Warnings when approaching daily limits" }
        )
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_UPDATES, "App Updates", NotificationManager.IMPORTANCE_DEFAULT)
        )
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_ACHIEVEMENTS, "Achievements", NotificationManager.IMPORTANCE_HIGH)
                .apply { description = "Unlocked achievement badges" }
        )
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_DAILY, "Daily Summary", NotificationManager.IMPORTANCE_DEFAULT)
                .apply { description = "Daily score recap sent at 9pm" }
        )
    }

    fun sendDailySummary(context: Context, score: Int, streak: Int) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val pi = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        val streakText = if (streak > 0) " • 🔥 $streak-day streak" else ""
        val scoreEmoji = when {
            score >= 90 -> "🟢"
            score >= 70 -> "🔵"
            score >= 50 -> "🟡"
            else -> "🔴"
        }
        val notif = NotificationCompat.Builder(context, CHANNEL_DAILY)
            .setContentTitle("$scoreEmoji Today's score: $score/100$streakText")
            .setContentText(dailySummaryBody(score))
            .setSmallIcon(android.R.drawable.ic_menu_recent_history)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()
        nm.notify(NOTIF_DAILY_SUMMARY, notif)
    }

    private fun dailySummaryBody(score: Int) = when {
        score >= 90 -> "Outstanding day. You stayed in control."
        score >= 70 -> "Solid day. Keep the momentum going."
        score >= 50 -> "Not your best, but tomorrow's a fresh start."
        else -> "Rough day. You know what to do tomorrow."
    }

    fun sendAchievementUnlocked(context: Context, emoji: String, title: String, description: String, notifId: Int) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val pi = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        val notif = NotificationCompat.Builder(context, CHANNEL_ACHIEVEMENTS)
            .setContentTitle("$emoji Achievement unlocked!")
            .setContentText(title)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$title\n$description"))
            .setSmallIcon(android.R.drawable.btn_star_big_on)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()
        nm.notify(notifId, notif)
    }

    fun buildMonitorNotification(context: Context): Notification {
        val pi = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(context, CHANNEL_MONITOR)
            .setContentTitle("ScreenGuard active")
            .setContentText("Monitoring your screen time")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentIntent(pi)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    fun sendLimitWarning(context: Context, appName: String, percentUsed: Int, notifId: Int) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val msg = when {
            percentUsed >= 95 -> "Almost out of time in $appName!"
            percentUsed >= 90 -> "Only 10% of your $appName limit left"
            else -> "75% of daily $appName limit used"
        }
        val notif = NotificationCompat.Builder(context, CHANNEL_ALERTS)
            .setContentTitle("Time check: $appName")
            .setContentText(msg)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setAutoCancel(true)
            .build()
        nm.notify(notifId, notif)
    }

    fun sendLimitReached(context: Context, appName: String, notifId: Int) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notif = NotificationCompat.Builder(context, CHANNEL_ALERTS)
            .setContentTitle("Daily limit reached: $appName")
            .setContentText("You've hit your limit. $appName is now blocked for today.")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .build()
        nm.notify(notifId, notif)
    }
}
