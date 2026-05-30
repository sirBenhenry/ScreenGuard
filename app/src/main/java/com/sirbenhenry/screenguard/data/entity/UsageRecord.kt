package com.sirbenhenry.screenguard.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usage_records")
data class UsageRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val dateKey: String,       // "2025-05-31"
    val totalMinutes: Int,
    val sessions: Int = 1,
    val limitMinutes: Int,
    val underLimit: Boolean
)
