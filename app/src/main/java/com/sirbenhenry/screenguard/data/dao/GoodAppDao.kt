package com.sirbenhenry.screenguard.data.dao

import androidx.room.*
import com.sirbenhenry.screenguard.data.entity.GoodApp
import kotlinx.coroutines.flow.Flow

@Dao
interface GoodAppDao {
    @Query("SELECT * FROM good_apps ORDER BY sortOrder, appName")
    fun getAllFlow(): Flow<List<GoodApp>>

    @Query("SELECT * FROM good_apps ORDER BY sortOrder, appName")
    suspend fun getAll(): List<GoodApp>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(app: GoodApp)

    @Delete
    suspend fun delete(app: GoodApp)
}
