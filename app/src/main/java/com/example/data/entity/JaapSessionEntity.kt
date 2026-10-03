package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jaap_sessions")
data class JaapSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mantraId: Long,
    val mantraName: String,
    val dateStr: String, // format "YYYY-MM-DD" for day aggregation
    val jaapCount: Int,
    val malasCompleted: Int,
    val durationSeconds: Long = 0,
    val timestamp: Long = System.currentTimeMillis()
)

data class DailySummary(
    val dateStr: String,
    val totalJaaps: Int,
    val totalMalas: Int
)
