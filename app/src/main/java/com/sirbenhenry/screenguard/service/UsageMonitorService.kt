package com.sirbenhenry.screenguard.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.sirbenhenry.screenguard.data.AppDatabase
import com.sirbenhenry.screenguard.data.entity.Achievement
import com.sirbenhenry.screenguard.data.entity.StreakRecord
import com.sirbenhenry.screenguard.data.entity.UsageRecord
import com.sirbenhenry.screenguard.util.DateUtil
import com.sirbenhenry.screenguard.util.NotificationUtil
import com.sirbenhenry.screenguard.util.Prefs
import com.sirbenhenry.screenguard.util.UsageStatsUtil
import com.sirbenhenry.screenguard.widget.ScoreWidget
import kotlinx.coroutines.*

class UsageMonitorService : Service() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val warnedAt = mutableMapOf<String, Int>() // pkg -> last warned threshold
    private var dailySummarySentDate = ""

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

            // Warning notifications — check highest threshold first so each fires once
            val prevWarn = warnedAt[app.packageName] ?: 0
            val notifId = NotificationUtil.NOTIF_ALERT_BASE + app.packageName.hashCode()
            when {
                pct >= 100 && prevWarn < 100 -> {
                    warnedAt[app.packageName] = 100
                    NotificationUtil.sendLimitReached(this, app.appName, notifId)
                }
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

        checkAchievements(db, allUsage, monitoredApps.map { it.packageName })

        val score = if (monitoredApps.isEmpty()) 100 else
            monitoredApps.sumOf { app ->
                val used = allUsage[app.packageName] ?: 0
                val limit = app.dailyLimitMinutes
                if (limit > 0) ((1f - used.toFloat() / limit).coerceIn(0f, 1f) * 100).toInt() else 100
            } / monitoredApps.size
        ScoreWidget.push(this, score = score)

        // Daily summary at 9pm (once per day)
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        if (hour >= 21 && dailySummarySentDate != today && monitoredApps.isNotEmpty()) {
            dailySummarySentDate = today
            val streak = Prefs.currentStreakFlow(this).let { var v = 0; it.collect { x -> v = x }; v }
            NotificationUtil.sendDailySummary(this, score, streak)
        }
    }

    private suspend fun onMidnightReset() {
        val db = AppDatabase.get(this)

        // Credit saved minutes for yesterday before rolling over
        val yesterday = DateUtil.daysAgo(1)
        val yesterdayRecord = db.streakRecordDao().getForDate(yesterday)
        if (yesterdayRecord != null && yesterdayRecord.allUnderLimit) {
            val saved = (yesterdayRecord.totalLimitMinutes - yesterdayRecord.totalMinutesUsed).coerceAtLeast(0)
            if (saved > 0) Prefs.addSavedMinutes(this, saved)
        }

        warnedAt.clear()
        dailySummarySentDate = ""
        recalculateStreak()
        val monitoredPkgs = db.monitoredAppDao().getEnabled().map { it.packageName }
        checkAchievements(db, emptyMap(), monitoredPkgs)
    }

    private suspend fun recalculateStreak() {
        val db = AppDatabase.get(this)
        val records = db.streakRecordDao().getLast365()
        if (records.isEmpty()) return

        val sorted = records.sortedByDescending { it.dateKey }
        val yesterday = DateUtil.daysAgo(1)

        var streak = 0
        for (r in sorted) {
            if (streak == 0 && r.dateKey >= DateUtil.today()) continue
            val countable = r.allUnderLimit || (db.streakFreezeDao().usedOnDate(r.dateKey) > 0)
            if (!countable) break
            streak++
        }

        val longest = records.sortedBy { it.dateKey }
            .fold(Pair(0, 0)) { (cur, max), r ->
                val newCur = if (r.allUnderLimit) cur + 1 else 0
                Pair(newCur, maxOf(max, newCur))
            }.second

        val prevStreak = Prefs.currentStreakFlow(this).let {
            var v = 0; it.collect { x -> v = x }; v
        }

        // Award streak freeze at diamond milestones (30-day diamonds)
        val diamonds = listOf(30, 60, 90, 180, 270, 365)
        for (d in diamonds) {
            if (streak >= d && prevStreak < d) {
                db.streakFreezeDao().insert(com.sirbenhenry.screenguard.data.entity.StreakFreeze())
                sendStreakMilestoneNotif(streak)
            }
        }

        Prefs.updateStreak(this, streak, longest)
        ScoreWidget.push(this, streak = streak)
    }

    private suspend fun checkAchievements(
        db: AppDatabase,
        usageMap: Map<String, Int>,
        monitoredPkgs: List<String>
    ) {
        val cooldowns = db.cooldownSessionDao().countCompleted()
        val streak = Prefs.currentStreakFlow(this).let { var v = 0; it.collect { x -> v = x }; v }
        val streakRecords = db.streakRecordDao().getLast365()

        data class Def(val id: String, val title: String, val desc: String, val emoji: String, val rare: Boolean, val earned: Boolean)

        val candidates = listOf(
            Def("cooldown_1",   "First Mindful Pause",  "Completed your first breathing cooldown",      "🧘", false, cooldowns >= 1),
            Def("cooldown_10",  "Getting the Groove",   "Completed 10 mindful cooldowns",               "🌊", false, cooldowns >= 10),
            Def("cooldown_50",  "Halfway Hero",         "50 cooldowns completed — half a century",      "⚡", false, cooldowns >= 50),
            Def("cooldown_100", "Centurion",            "100 cooldowns. You breathed through all of it","💯", true,  cooldowns >= 100),
            Def("cooldown_500", "Ironclad",             "500 cooldowns. Unbreakable discipline",        "🛡️", true,  cooldowns >= 500),
            Def("streak_7",     "First Week",           "7-day streak — one full week under control",   "🔥", false, streak >= 7),
            Def("streak_14",    "Fortnight",            "14 days straight. Two weeks of intent",        "🌟", false, streak >= 14),
            Def("streak_30",    "Monthly Master",       "30-day streak. You rewired your habits",       "💎", true,  streak >= 30),
            Def("cold_turkey",  "Cold Turkey",          "Zero usage on all monitored apps today",       "🦃", true,
                monitoredPkgs.isNotEmpty() && monitoredPkgs.all { (usageMap[it] ?: 0) == 0 }),
            Def("week_clean",   "Perfect Week",         "7 consecutive good days in a row",             "✨", false,
                streakRecords.sortedByDescending { it.dateKey }.take(7).let { last -> last.size >= 7 && last.all { it.allUnderLimit } })
        )

        for (def in candidates) {
            if (!def.earned) continue
            if (db.achievementDao().getById(def.id) != null) continue
            val achievement = Achievement(
                id = def.id, title = def.title, description = def.desc, emoji = def.emoji, isRare = def.rare
            )
            val inserted = db.achievementDao().insert(achievement)
            if (inserted != -1L) {
                val notifId = NotificationUtil.NOTIF_ACHIEVEMENT_BASE + def.id.hashCode()
                NotificationUtil.sendAchievementUnlocked(this, def.emoji, def.title, def.desc, notifId)
            }
        }
    }

    private fun sendStreakMilestoneNotif(streak: Int) {
        val nm = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
        val notif = androidx.core.app.NotificationCompat.Builder(this, NotificationUtil.CHANNEL_ALERTS)
            .setContentTitle("🔥 $streak-day streak milestone!")
            .setContentText("You earned a streak freeze. Use it wisely.")
            .setSmallIcon(android.R.drawable.star_on)
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        nm.notify(streak + 2000, notif)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
