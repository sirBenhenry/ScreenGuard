package com.sirbenhenry.screenguard.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sirbenhenry.screenguard.data.entity.Achievement
import com.sirbenhenry.screenguard.data.entity.GoodApp
import com.sirbenhenry.screenguard.data.entity.MonitoredApp
import com.sirbenhenry.screenguard.data.entity.StreakRecord
import com.sirbenhenry.screenguard.data.entity.UsageRecord
import com.sirbenhenry.screenguard.data.repository.AppRepository
import com.sirbenhenry.screenguard.util.Prefs
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val monitoredApps: List<MonitoredApp> = emptyList(),
    val todayUsageMap: Map<String, Int> = emptyMap(),
    val todayScore: Int = 100,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalSavedMinutes: Int = 0,
    val totalCooldowns: Int = 0,
    val availableFreezes: Int = 0,
    val isLoading: Boolean = true
)

data class StatsUiState(
    val streakRecords: List<StreakRecord> = emptyList(),
    val usageRecords: List<UsageRecord> = emptyList(),
    val monitoredApps: List<MonitoredApp> = emptyList(),
    val achievements: List<Achievement> = emptyList(),
    val isLoading: Boolean = true
)

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AppRepository(app)

    val homeState: StateFlow<HomeUiState> = combine(
        repo.monitoredAppsFlow(),
        repo.todayUsageFlow(),
        Prefs.currentStreakFlow(app),
        Prefs.longestStreakFlow(app),
        Prefs.totalSavedMinutesFlow(app)
    ) { apps, usage, streak, longest, saved ->
        val liveUsage = repo.getAllTodayUsage()
        HomeUiState(
            monitoredApps = apps,
            todayUsageMap = liveUsage,
            todayScore = repo.todayScore(apps),
            currentStreak = streak,
            longestStreak = longest,
            totalSavedMinutes = saved,
            totalCooldowns = repo.totalCooldownsCompleted(),
            availableFreezes = repo.availableFreezes(),
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    val statsState: StateFlow<StatsUiState> = combine(
        repo.streakFlow(),
        repo.last365UsageFlow(),
        repo.monitoredAppsFlow(),
        repo.achievementsFlow()
    ) { streaks, usage, apps, achievements ->
        StatsUiState(streaks, usage, apps, achievements, false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StatsUiState())

    val monitoredApps: StateFlow<List<MonitoredApp>> =
        repo.monitoredAppsFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val goodApps: StateFlow<List<GoodApp>> =
        repo.goodAppsFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addMonitoredApp(app: MonitoredApp) = viewModelScope.launch { repo.addMonitoredApp(app) }
    fun removeMonitoredApp(app: MonitoredApp) = viewModelScope.launch { repo.removeMonitoredApp(app) }
    fun updateMonitoredApp(app: MonitoredApp) = viewModelScope.launch { repo.updateMonitoredApp(app) }

    fun addGoodApp(app: GoodApp) = viewModelScope.launch { repo.addGoodApp(app) }
    fun removeGoodApp(app: GoodApp) = viewModelScope.launch { repo.removeGoodApp(app) }

    fun getTodayUsage(pkg: String) = repo.getTodayUsage(pkg)
}
