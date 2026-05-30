package com.sirbenhenry.screenguard.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "streak_records")
data class StreakRecord(
    @PrimaryKey val dateKey: String,   // "2025-05-31"
    val allUnderLimit: Boolean,
    val totalMinutesUsed: Int,
    val totalLimitMinutes: Int
)
