package com.sirbenhenry.screenguard.data.dao

import androidx.room.*
import com.sirbenhenry.screenguard.data.entity.StreakRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakRecordDao {
    @Query("SELECT * FROM streak_records ORDER BY dateKey DESC")
    fun getAllFlow(): Flow<List<StreakRecord>>

    @Query("SELECT * FROM streak_records ORDER BY dateKey DESC LIMIT 365")
    suspend fun getLast365(): List<StreakRecord>

    @Query("SELECT * FROM streak_records WHERE dateKey = :date LIMIT 1")
    suspend fun getForDate(date: String): StreakRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: StreakRecord)

    @Query("SELECT COUNT(*) FROM streak_records WHERE allUnderLimit = 1 AND dateKey >= :fromDate")
    suspend fun countGoodDaysSince(fromDate: String): Int
}
