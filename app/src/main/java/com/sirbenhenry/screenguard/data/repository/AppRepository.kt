package com.sirbenhenry.screenguard.data.repository

import android.content.Context
import com.sirbenhenry.screenguard.data.AppDatabase
import com.sirbenhenry.screenguard.data.entity.*
import com.sirbenhenry.screenguard.util.DateUtil
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
