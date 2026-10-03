package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.MantraEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MantraDao {
    @Query("SELECT * FROM mantras ORDER BY isActive DESC, createdTimestamp ASC")
    fun getAllMantras(): Flow<List<MantraEntity>>

    @Query("SELECT * FROM mantras WHERE isActive = 1 LIMIT 1")
    fun getActiveMantra(): Flow<MantraEntity?>

    @Query("SELECT * FROM mantras WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveMantraOnce(): MantraEntity?

    @Query("SELECT * FROM mantras WHERE id = :id LIMIT 1")
    suspend fun getMantraById(id: Long): MantraEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMantra(mantra: MantraEntity): Long

    @Update
    suspend fun updateMantra(mantra: MantraEntity)

    @Query("DELETE FROM mantras WHERE id = :id")
    suspend fun deleteMantraById(id: Long)

    @Query("UPDATE mantras SET isActive = CASE WHEN id = :selectedId THEN 1 ELSE 0 END")
    suspend fun setActiveMantra(selectedId: Long)

    @Query("UPDATE mantras SET totalJaapCount = totalJaapCount + :addedJaaps, totalMalasCompleted = totalMalasCompleted + :addedMalas WHERE id = :id")
    suspend fun incrementCounts(id: Long, addedJaaps: Int, addedMalas: Int)

    @Query("UPDATE mantras SET dailyGoalMalas = :goalMalas WHERE id = :id")
    suspend fun updateDailyGoal(id: Long, goalMalas: Int)

    @Query("SELECT COUNT(*) FROM mantras")
    suspend fun getMantraCount(): Int

    @Query("SELECT COUNT(*) FROM mantras WHERE name = :name")
    suspend fun getMantraCountByName(name: String): Int

    @Query("SELECT * FROM mantras WHERE name = :name LIMIT 1")
    suspend fun getMantraByName(name: String): MantraEntity?
}
