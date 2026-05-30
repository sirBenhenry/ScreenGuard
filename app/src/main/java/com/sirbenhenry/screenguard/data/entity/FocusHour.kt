package com.sirbenhenry.screenguard.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_hours")
data class FocusHour(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startHour: Int,      // 0-23
    val startMinute: Int,    // 0-59
    val endHour: Int,
    val endMinute: Int,
    val label: String = "Focus time",
    val isEnabled: Boolean = true,
    val appliesWeekdays: Boolean = true,
    val appliesWeekends: Boolean = false
)
