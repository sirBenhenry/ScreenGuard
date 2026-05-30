package com.sirbenhenry.screenguard.data.dao

import androidx.room.*
import com.sirbenhenry.screenguard.data.entity.MonitoredApp
import kotlinx.coroutines.flow.Flow

@Dao
interface MonitoredAppDao {
    @Query("SELECT * FROM monitored_apps ORDER BY appName")
    fun getAllFlow(): Flow<List<MonitoredApp>>

    @Query("SELECT * FROM monitored_apps WHERE isEnabled = 1")
    suspend fun getEnabled(): List<MonitoredApp>

    @Query("SELECT * FROM monitored_apps WHERE packageName = :pkg LIMIT 1")
    suspend fun getByPackage(pkg: String): MonitoredApp?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(app: MonitoredApp)

    @Update
    suspend fun update(app: MonitoredApp)

    @Delete
    suspend fun delete(app: MonitoredApp)

    @Query("SELECT COUNT(*) FROM monitored_apps")
    suspend fun count(): Int
}
