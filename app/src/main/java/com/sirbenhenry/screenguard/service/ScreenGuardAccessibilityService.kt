package com.sirbenhenry.screenguard.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.sirbenhenry.screenguard.data.AppDatabase
import com.sirbenhenry.screenguard.data.entity.DailyOpenCount
import com.sirbenhenry.screenguard.ui.overlay.CooldownActivity
import com.sirbenhenry.screenguard.ui.overlay.LimitReachedActivity
import com.sirbenhenry.screenguard.util.DateUtil
import com.sirbenhenry.screenguard.util.UsageStatsUtil
import kotlinx.coroutines.*
import java.util.Calendar
import com.sirbenhenry.screenguard.data.entity.FocusHour

class ScreenGuardAccessibilityService : AccessibilityService() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val limitShownToday = mutableSetOf<String>()
    private val cooldownShownThisLaunch = mutableMapOf<String, Long>() // pkg -> timestamp
    private var lastForegroundPackage = ""
    private var lastEventTime = 0L

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val pkg = event?.packageName?.toString() ?: return
        if (pkg == packageName) return
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val now = System.currentTimeMillis()
        if (pkg == lastForegroundPackage && now - lastEventTime < 800) return
        lastForegroundPackage = pkg
        lastEventTime = now

        scope.launch { handleAppOpened(pkg) }
    }

    private suspend fun handleAppOpened(pkg: String) {
        val db = AppDatabase.get(this)
        val app = db.monitoredAppDao().getByPackage(pkg) ?: return
        if (!app.isEnabled) return

        val today = DateUtil.today()
        val usedMinutes = UsageStatsUtil.getTodayUsageMinutes(this, pkg)
        val limitMinutes = effectiveLimit(app)

        // Focus hours: total block, no cooldown
        if (isDuringFocusHour()) {
            showFocusBlock(app.appName)
            return
        }

        if (usedMinutes >= limitMinutes) {
            if (!limitShownToday.contains(pkg)) {
                limitShownToday.add(pkg)
                showLimitReached(pkg, app.appName)
            } else {
                goHome()
            }
            return
        }

        // Progressive cooldown: track opens today
        val now = System.currentTimeMillis()
        val lastShown = cooldownShownThisLaunch[pkg] ?: 0
        // Don't show cooldown again if user just finished one (within 5 seconds)
        if (now - lastShown < 5000) return

        val openRecord = db.dailyOpenCountDao().get(pkg, today)
        val openCount = openRecord?.openCount ?: 0

        // Record this open
        if (openRecord == null) {
            db.dailyOpenCountDao().insert(DailyOpenCount(packageName = pkg, dateKey = today, openCount = 1))
        } else {
            db.dailyOpenCountDao().update(openRecord.copy(openCount = openRecord.openCount + 1))
        }

        // Progressive cooldown: 30s → 45s → 60s → 75s → 90s
        val cooldownSecs = when (openCount) {
            0 -> app.baseCooldownSeconds
            1 -> (app.baseCooldownSeconds * 1.5).toInt()
            2 -> app.baseCooldownSeconds * 2
            3 -> (app.baseCooldownSeconds * 2.5).toInt()
            else -> app.baseCooldownSeconds * 3
        }.coerceAtLeast(20)

        cooldownShownThisLaunch[pkg] = now
        showCooldown(pkg, app.appName, cooldownSecs, openCount + 1, usedMinutes, limitMinutes)
    }

    private suspend fun isDuringFocusHour(): Boolean {
        val cal = Calendar.getInstance()
        val nowMin = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
        val dow = cal.get(Calendar.DAY_OF_WEEK)
        val isWeekend = dow == Calendar.SATURDAY || dow == Calendar.SUNDAY
        val focusHours = AppDatabase.get(this).focusHourDao().getEnabled()
        return focusHours.any { fh ->
            val applies = if (isWeekend) fh.appliesWeekends else fh.appliesWeekdays
            if (!applies) return@any false
            val start = fh.startHour * 60 + fh.startMinute
            val end = fh.endHour * 60 + fh.endMinute
            if (end > start) nowMin in start until end
            else nowMin >= start || nowMin < end // overnight range
        }
    }

    private fun showFocusBlock(appName: String) {
        startActivity(Intent(this, com.sirbenhenry.screenguard.ui.overlay.FocusBlockActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("appName", appName)
        })
    }

    private fun effectiveLimit(app: com.sirbenhenry.screenguard.data.entity.MonitoredApp): Int {
        val cal = Calendar.getInstance()
        val dow = cal.get(Calendar.DAY_OF_WEEK)
        val isWeekend = dow == Calendar.SATURDAY || dow == Calendar.SUNDAY
        return if (isWeekend) app.weekendLimitMinutes else app.dailyLimitMinutes
    }

    private fun showCooldown(
        pkg: String, appName: String, cooldownSecs: Int,
        openNumber: Int, usedMin: Int, limitMin: Int
    ) {
        startActivity(Intent(this, CooldownActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(CooldownActivity.EXTRA_PACKAGE, pkg)
            putExtra(CooldownActivity.EXTRA_APP_NAME, appName)
            putExtra(CooldownActivity.EXTRA_COOLDOWN_SECS, cooldownSecs)
            putExtra(CooldownActivity.EXTRA_OPEN_NUMBER, openNumber)
            putExtra(CooldownActivity.EXTRA_USED_MIN, usedMin)
            putExtra(CooldownActivity.EXTRA_LIMIT_MIN, limitMin)
        })
    }

    private fun showLimitReached(pkg: String, appName: String) {
        startActivity(Intent(this, LimitReachedActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(LimitReachedActivity.EXTRA_PACKAGE, pkg)
            putExtra(LimitReachedActivity.EXTRA_APP_NAME, appName)
        })
    }

    private fun goHome() {
        startActivity(Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    override fun onInterrupt() {}

    override fun onServiceConnected() {
        super.onServiceConnected()
        limitShownToday.clear()
        cooldownShownThisLaunch.clear()
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
