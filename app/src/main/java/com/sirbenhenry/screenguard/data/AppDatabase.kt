package com.sirbenhenry.screenguard.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sirbenhenry.screenguard.data.dao.*
import com.sirbenhenry.screenguard.data.entity.*

@Database(
    entities = [
        MonitoredApp::class,
        GoodApp::class,
        UsageRecord::class,
        StreakRecord::class,
        CooldownSession::class,
        DailyOpenCount::class,
        FocusHour::class,
        StreakFreeze::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun monitoredAppDao(): MonitoredAppDao
    abstract fun goodAppDao(): GoodAppDao
    abstract fun usageRecordDao(): UsageRecordDao
    abstract fun streakRecordDao(): StreakRecordDao
    abstract fun cooldownSessionDao(): CooldownSessionDao
    abstract fun dailyOpenCountDao(): DailyOpenCountDao
    abstract fun focusHourDao(): FocusHourDao
    abstract fun streakFreezeDao(): StreakFreezeDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "screenguard.db")
                .fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }
        }
    }
}
