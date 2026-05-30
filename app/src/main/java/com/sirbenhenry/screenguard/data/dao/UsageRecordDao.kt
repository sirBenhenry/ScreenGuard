package com.sirbenhenry.screenguard.data.dao

import androidx.room.*
import com.sirbenhenry.screenguard.data.entity.UsageRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface UsageRecordDao {
    @Query("SELECT * FROM usage_records WHERE dateKey = :date ORDER BY totalMinutes DESC")
    fun getForDateFlow(date: String): Flow<List<UsageRecord>>

    @Query("SELECT * FROM usage_records WHERE dateKey = :date")
    suspend fun getForDate(date: String): List<UsageRecord>

    @Query("SELECT * FROM usage_records WHERE packageName = :pkg AND dateKey = :date LIMIT 1")
    suspend fun getForPackageDate(pkg: String, date: String): UsageRecord?

    @Query("SELECT * FROM usage_records WHERE dateKey >= :fromDate ORDER BY dateKey DESC")
    suspend fun getSince(fromDate: String): List<UsageRecord>

    @Query("SELECT * FROM usage_records ORDER BY dateKey DESC LIMIT 365")
    fun getLast365Flow(): Flow<List<UsageRecord>>

    @Query("SELECT SUM(totalMinutes) FROM usage_records WHERE packageName = :pkg AND dateKey >= :fromDate")
    suspend fun totalMinutesSince(pkg: String, fromDate: String): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: UsageRecord)

    @Update
    suspend fun update(record: UsageRecord)
}
