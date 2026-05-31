package com.sirbenhenry.screenguard.data.repository

import android.content.Context
import com.sirbenhenry.screenguard.data.AppDatabase
import com.sirbenhenry.screenguard.data.entity.*
import com.sirbenhenry.screenguard.util.DateUtil
import com.sirbenhenry.screenguard.util.Prefs
import com.sirbenhenry.screenguard.util.UsageStatsUtil
import kotlinx.coroutines.flow.Flow

class AppRepository(private val context: Context) {
    private val db = AppDatabase.get(context)

    // Monitored apps
    fun monitoredAppsFlow(): Flow<List<MonitoredApp>> = db.monitoredAppDao().getAllFlow()
    suspend fun addMonitoredApp(app: MonitoredApp) = db.monitoredAppDao().insert(app)
    suspend fun removeMonitoredApp(app: MonitoredApp) = db.monitoredAppDao().delete(app)
    suspend fun updateMonitoredApp(app: MonitoredApp) = db.monitoredAppDao().update(app)

    // Good apps
    fun goodAppsFlow(): Flow<List<GoodApp>> = db.goodAppDao().getAllFlow()
    suspend fun addGoodApp(app: GoodApp) = db.goodAppDao().insert(app)
    suspend fun removeGoodApp(app: GoodApp) = db.goodAppDao().delete(app)

    // Usage records
    fun todayUsageFlow(): Flow<List<UsageRecord>> = db.usageRecordDao().getForDateFlow(DateUtil.today())
    fun last365UsageFlow(): Flow<List<UsageRecord>> = db.usageRecordDao().getLast365Flow()

    // Streak
    fun streakFlow(): Flow<List<StreakRecord>> = db.streakRecordDao().getAllFlow()

    // Today's live usage from UsageStats API
    fun getTodayUsage(pkg: String) = UsageStatsUtil.getTodayUsageMinutes(context, pkg)
    fun getAllTodayUsage() = UsageStatsUtil.getAllTodayUsage(context)

    // Achievements
    fun achievementsFlow() = db.achievementDao().getAllFlow()
    suspend fun insertAchievement(a: com.sirbenhenry.screenguard.data.entity.Achievement) = db.achievementDao().insert(a)
    suspend fun hasAchievement(id: String) = db.achievementDao().getById(id) != null

    // Streak freeze: use one for yesterday; returns true if applied
    suspend fun useFreezeForYesterday(): Boolean {
        val yesterday = DateUtil.daysAgo(1)
        if (db.streakFreezeDao().usedOnDate(yesterday) > 0) return false
        val available = db.streakFreezeDao().getAvailable()
        if (available.isEmpty()) return false
        db.streakFreezeDao().update(available.first().copy(usedOnDate = yesterday))
        return true
    }

    // Recalculate streak from DB records (same logic as UsageMonitorService)
    suspend fun recalculateAndSaveStreak(): Int {
        val records = db.streakRecordDao().getLast365()
        if (records.isEmpty()) { Prefs.updateStreak(context, 0, 0); return 0 }
        val sorted = records.sortedByDescending { it.dateKey }
        var streak = 0
        for (r in sorted) {
            if (streak == 0 && r.dateKey >= DateUtil.today()) continue
            val countable = r.allUnderLimit || (db.streakFreezeDao().usedOnDate(r.dateKey) > 0)
            if (!countable) break
            streak++
        }
        val longest = records.sortedBy { it.dateKey }
            .fold(0 to 0) { (cur, max), r ->
                val nc = if (r.allUnderLimit) cur + 1 else 0
                nc to maxOf(max, nc)
            }.second
        Prefs.updateStreak(context, streak, longest)
        return streak
    }

    // Stats aggregates
    suspend fun totalCooldownsCompleted() = db.cooldownSessionDao().countCompleted()
    suspend fun totalOpensToday() = db.dailyOpenCountDao().totalOpensForDate(DateUtil.today()) ?: 0
    suspend fun availableFreezes() = db.streakFreezeDao().getAvailable().size

    suspend fun todayScore(monitoredApps: List<MonitoredApp>): Int {
        if (monitoredApps.isEmpty()) return 100
        val usageMap = getAllTodayUsage()
        var totalScore = 0
        for (app in monitoredApps) {
            val used = usageMap[app.packageName] ?: 0
            val limit = app.dailyLimitMinutes
            val appScore = if (limit > 0) ((1f - (used.toFloat() / limit)).coerceIn(0f, 1f) * 100).toInt() else 100
            totalScore += appScore
        }
        return totalScore / monitoredApps.size
    }
}
