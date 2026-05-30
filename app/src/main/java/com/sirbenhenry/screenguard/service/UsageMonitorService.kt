package com.sirbenhenry.screenguard.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.sirbenhenry.screenguard.data.AppDatabase
import com.sirbenhenry.screenguard.data.entity.StreakRecord
import com.sirbenhenry.screenguard.data.entity.UsageRecord
import com.sirbenhenry.screenguard.util.DateUtil
import com.sirbenhenry.screenguard.util.NotificationUtil
import com.sirbenhenry.screenguard.util.Prefs
import com.sirbenhenry.screenguard.util.UsageStatsUtil
import kotlinx.coroutines.*

class UsageMonitorService : Service() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val warnedAt = mutableMapOf<String, Int>() // pkg -> last warned threshold

    override fun onCreate() {
        super.onCreate()
        NotificationUtil.createChannels(this)
        startForeground(NotificationUtil.NOTIF_MONITOR, NotificationUtil.buildMonitorNotification(this))
        startMonitoring()
    }

    private fun startMonitoring() {
        scope.launch {
            while (isActive) {
                tick()
                delay(30_000) // check every 30 seconds
            }
        }

        // Midnight reset job
        scope.launch {
            while (isActive) {
                val now = System.currentTimeMillis()
                val nextMidnight = DateUtil.startOfDayMs() + 86_400_000
                delay(nextMidnight - now)
                onMidnightReset()
            }
        }
    }

    private suspend fun tick() {
        val db = AppDatabase.get(this)
        val monitoredApps = db.monitoredAppDao().getEnabled()
        if (monitoredApps.isEmpty()) return

        val allUsage = UsageStatsUtil.getAllTodayUsage(this)
        val today = DateUtil.today()

        var allUnderLimit = true
        var totalUsed = 0
        var totalLimit = 0

        for (app in monitoredApps) {
            val used = allUsage[app.packageName] ?: 0
            val limit = app.dailyLimitMinutes
            totalUsed += used
            totalLimit += limit

            val pct = if (limit > 0) (used * 100 / limit) else 0
            if (pct >= 100) allUnderLimit = false

            // Save/update usage record
            val existing = db.usageRecordDao().getForPackageDate(app.packageName, today)
            if (existing != null) {
                db.usageRecordDao().update(existing.copy(
                    totalMinutes = used,
                    underLimit = pct < 100
                ))
            } else if (used > 0) {
                db.usageRecordDao().insert(UsageRecord(
                    packageName = app.packageName,
                    dateKey = today,
                    totalMinutes = used,
                    limitMinutes = limit,
                    underLimit = pct < 100
                ))
            }

            // Warning notifications
            val prevWarn = warnedAt[app.packageName] ?: 0
            val notifId = NotificationUtil.NOTIF_ALERT_BASE + app.packageName.hashCode()
            when {
                pct >= 95 && prevWarn < 95 -> {
                    warnedAt[app.packageName] = 95
                    NotificationUtil.sendLimitWarning(this, app.appName, 95, notifId)
                }
                pct >= 90 && prevWarn < 90 -> {
                    warnedAt[app.packageName] = 90
                    NotificationUtil.sendLimitWarning(this, app.appName, 90, notifId)
                }
                pct >= 75 && prevWarn < 75 -> {
                    warnedAt[app.packageName] = 75
                    NotificationUtil.sendLimitWarning(this, app.appName, 75, notifId)
                }
                pct >= 100 && prevWarn < 100 -> {
                    warnedAt[app.packageName] = 100
                    NotificationUtil.sendLimitReached(this, app.appName, notifId)
                }
            }
        }

        // Update streak record for today
        val streakRecord = StreakRecord(
            dateKey = today,
            allUnderLimit = allUnderLimit,
            totalMinutesUsed = totalUsed,
            totalLimitMinutes = totalLimit
        )
        db.streakRecordDao().insert(streakRecord)
    }

    private suspend fun onMidnightReset() {
        warnedAt.clear()
        recalculateStreak()
    }

    private suspend fun recalculateStreak() {
        val db = AppDatabase.get(this)
        val records = db.streakRecordDao().getLast365()
        if (records.isEmpty()) return

        var streak = 0
        val sorted = records.sortedByDescending { it.dateKey }
        val yesterday = DateUtil.daysAgo(1)

        for (r in sorted) {
            if (!r.allUnderLimit) break
            if (streak == 0 && r.dateKey > yesterday) continue // don't count today yet
            streak++
        }

        val longest = db.streakRecordDao().getLast365()
            .sortedBy { it.dateKey }
            .fold(Pair(0, 0)) { (cur, max), r ->
                val newCur = if (r.allUnderLimit) cur + 1 else 0
                Pair(newCur, maxOf(max, newCur))
            }.second

        Prefs.updateStreak(this, streak, longest)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
