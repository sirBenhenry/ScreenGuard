package com.sirbenhenry.screenguard.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cooldown_sessions")
data class CooldownSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val completedFully: Boolean
)
