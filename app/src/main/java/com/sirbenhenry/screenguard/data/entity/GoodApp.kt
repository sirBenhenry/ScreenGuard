package com.sirbenhenry.screenguard.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "good_apps")
data class GoodApp(
    @PrimaryKey val packageName: String,
    val appName: String,
    val sortOrder: Int = 0
)
