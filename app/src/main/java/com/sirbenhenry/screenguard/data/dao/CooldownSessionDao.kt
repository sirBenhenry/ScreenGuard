package com.sirbenhenry.screenguard.data.dao

import androidx.room.*
import com.sirbenhenry.screenguard.data.entity.CooldownSession

@Dao
interface CooldownSessionDao {
    @Insert
    suspend fun insert(session: CooldownSession)

    @Query("SELECT COUNT(*) FROM cooldown_sessions WHERE completedFully = 1")
    suspend fun countCompleted(): Int

    @Query("SELECT COUNT(*) FROM cooldown_sessions WHERE timestamp >= :since")
    suspend fun countSince(since: Long): Int

    @Query("SELECT * FROM cooldown_sessions ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLast(): CooldownSession?
}
