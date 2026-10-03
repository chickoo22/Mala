package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.DailySummary
import com.example.data.entity.JaapSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JaapSessionDao {
    @Query("SELECT * FROM jaap_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<JaapSessionEntity>>

    @Query("SELECT * FROM jaap_sessions ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentSessions(limit: Int = 20): Flow<List<JaapSessionEntity>>

    @Query("SELECT * FROM jaap_sessions WHERE dateStr = :dateStr ORDER BY timestamp DESC")
    fun getSessionsForDate(dateStr: String): Flow<List<JaapSessionEntity>>

    @Query("SELECT dateStr, SUM(jaapCount) as totalJaaps, SUM(malasCompleted) as totalMalas FROM jaap_sessions GROUP BY dateStr ORDER BY dateStr DESC LIMIT :limit")
    fun getDailySummaries(limit: Int = 30): Flow<List<DailySummary>>

    @Query("SELECT COALESCE(SUM(jaapCount), 0) FROM jaap_sessions WHERE dateStr = :dateStr")
    fun getTodayJaapCountFlow(dateStr: String): Flow<Int>

    @Query("SELECT COALESCE(SUM(malasCompleted), 0) FROM jaap_sessions WHERE dateStr = :dateStr")
    fun getTodayMalasCountFlow(dateStr: String): Flow<Int>

    @Query("SELECT COALESCE(SUM(jaapCount), 0) FROM jaap_sessions WHERE dateStr = :dateStr AND mantraId = :mantraId")
    fun getTodayJaapCountForMantraFlow(dateStr: String, mantraId: Long): Flow<Int>

    @Query("SELECT COALESCE(SUM(malasCompleted), 0) FROM jaap_sessions WHERE dateStr = :dateStr AND mantraId = :mantraId")
    fun getTodayMalasCountForMantraFlow(dateStr: String, mantraId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: JaapSessionEntity): Long

    @Query("SELECT COALESCE(SUM(jaapCount), 0) FROM jaap_sessions")
    fun getTotalLifetimeJaaps(): Flow<Long>

    @Query("SELECT COALESCE(SUM(malasCompleted), 0) FROM jaap_sessions")
    fun getTotalLifetimeMalas(): Flow<Long>

    @Query("SELECT DISTINCT dateStr FROM jaap_sessions WHERE jaapCount > 0 ORDER BY dateStr DESC")
    fun getActiveDates(): Flow<List<String>>
}
