package com.sirbenhenry.screenguard

import android.app.Application
import android.content.Intent
import com.sirbenhenry.screenguard.service.UsageMonitorService
import com.sirbenhenry.screenguard.util.NotificationUtil

class ScreenGuardApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationUtil.createChannels(this)
        startForegroundService(Intent(this, UsageMonitorService::class.java))
    }
}
