package com.sirbenhenry.screenguard.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monitored_apps")
data class MonitoredApp(
    @PrimaryKey val packageName: String,
    val appName: String,
    val dailyLimitMinutes: Int = 30,
    val baseCooldownSeconds: Int = 30,   // escalates per open: 30, 45, 60, 75
    val addedAt: Long = System.currentTimeMillis(),
    val isEnabled: Boolean = true,
    val weekendLimitMinutes: Int = 45,   // higher limit on weekends
    val focusBlockEnabled: Boolean = false  // block during focus hours
)
