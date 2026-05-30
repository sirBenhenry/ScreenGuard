package com.sirbenhenry.screenguard.data.dao

import androidx.room.*
import com.sirbenhenry.screenguard.data.entity.FocusHour
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusHourDao {
    @Query("SELECT * FROM focus_hours ORDER BY startHour, startMinute")
    fun getAllFlow(): Flow<List<FocusHour>>

    @Query("SELECT * FROM focus_hours WHERE isEnabled = 1")
    suspend fun getEnabled(): List<FocusHour>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fh: FocusHour): Long

    @Delete
    suspend fun delete(fh: FocusHour)

    @Update
    suspend fun update(fh: FocusHour)
}
