package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mantras")
data class MantraEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val sanskritText: String = "",
    val transliteration: String = "",
    val meaning: String = "",
    val dailyGoalMalas: Int = 1, // e.g., 1 Mala = 108 counts
    val beadsPerMala: Int = 108, // 108 (standard), 54, 27
    val totalJaapCount: Long = 0,
    val totalMalasCompleted: Long = 0,
    val isActive: Boolean = false,
    val colorHex: Long = 0xFFFF8C00,
    val createdTimestamp: Long = System.currentTimeMillis()
)
