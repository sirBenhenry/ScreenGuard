package com.sirbenhenry.screenguard.data.dao

import androidx.room.*
import com.sirbenhenry.screenguard.data.entity.StreakFreeze
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakFreezeDao {
    @Query("SELECT * FROM streak_freezes ORDER BY earnedAt DESC")
    fun getAllFlow(): Flow<List<StreakFreeze>>

    @Query("SELECT * FROM streak_freezes WHERE usedOnDate IS NULL")
    suspend fun getAvailable(): List<StreakFreeze>

    @Query("SELECT COUNT(*) FROM streak_freezes WHERE usedOnDate IS NULL")
    fun availableCountFlow(): Flow<Int>

    @Insert
    suspend fun insert(freeze: StreakFreeze): Long

    @Update
    suspend fun update(freeze: StreakFreeze)

    @Query("SELECT COUNT(*) FROM streak_freezes WHERE usedOnDate = :date")
    suspend fun usedOnDate(date: String): Int
}
