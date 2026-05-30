package com.sirbenhenry.screenguard.data.dao

import androidx.room.*
import com.sirbenhenry.screenguard.data.entity.DailyOpenCount

@Dao
interface DailyOpenCountDao {
    @Query("SELECT * FROM daily_open_counts WHERE packageName = :pkg AND dateKey = :date LIMIT 1")
    suspend fun get(pkg: String, date: String): DailyOpenCount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: DailyOpenCount)

    @Update
    suspend fun update(record: DailyOpenCount)

    @Query("SELECT SUM(openCount) FROM daily_open_counts WHERE dateKey = :date")
    suspend fun totalOpensForDate(date: String): Int?

    @Query("SELECT SUM(openCount) FROM daily_open_counts WHERE packageName = :pkg AND dateKey >= :fromDate")
    suspend fun totalOpensSince(pkg: String, fromDate: String): Int?
}
