package com.sirbenhenry.screenguard.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "streak_freezes")
data class StreakFreeze(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val earnedAt: Long = System.currentTimeMillis(),
    val usedOnDate: String? = null    // null = available
)
