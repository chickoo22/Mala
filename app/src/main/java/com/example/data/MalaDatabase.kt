package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.JaapSessionDao
import com.example.data.dao.MantraDao
import com.example.data.entity.JaapSessionEntity
import com.example.data.entity.MantraEntity

@Database(
    entities = [MantraEntity::class, JaapSessionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MalaDatabase : RoomDatabase() {
    abstract fun mantraDao(): MantraDao
    abstract fun jaapSessionDao(): JaapSessionDao

    companion object {
        @Volatile
        private var INSTANCE: MalaDatabase? = null

        fun getInstance(context: Context): MalaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MalaDatabase::class.java,
                    "mala_jaap.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
