package com.sirbenhenry.screenguard.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_open_counts")
data class DailyOpenCount(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val dateKey: String,
    val openCount: Int = 1
)
