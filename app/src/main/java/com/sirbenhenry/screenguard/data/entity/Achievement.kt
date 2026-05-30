package com.sirbenhenry.screenguard.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val earnedAt: Long = System.currentTimeMillis(),
    val isRare: Boolean = false
)
