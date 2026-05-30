package com.sirbenhenry.screenguard.data.dao

import androidx.room.*
import com.sirbenhenry.screenguard.data.entity.Achievement
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements ORDER BY earnedAt DESC")
    fun getAllFlow(): Flow<List<Achievement>>

    @Query("SELECT * FROM achievements WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): Achievement?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(a: Achievement): Long

    @Query("SELECT COUNT(*) FROM achievements")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM achievements WHERE isRare = 1")
    suspend fun rareCount(): Int
}
