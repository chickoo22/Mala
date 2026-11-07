package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.MalaDatabase
import com.example.data.MalaRepository
import com.example.data.entity.JaapSessionEntity
import com.example.data.entity.MantraEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MalaDaoTest {
    private lateinit var db: MalaDatabase
    private lateinit var repository: MalaRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, MalaDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = MalaRepository(db.mantraDao(), db.jaapSessionDao())
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testSeedDefaultMantrasAndActiveSelection() = runBlocking {
        repository.checkAndSeedDefaultMantras()
        val mantras = repository.allMantras.first()
        assertTrue("Mantras should be seeded", mantras.isNotEmpty())
        assertEquals(24, mantras.size)

        val active = repository.activeMantra.first()
        assertNotNull("Active mantra should be present", active)
        assertEquals("Gayatri Mantra", active?.name)
    }

    @Test
    fun testRecordSessionAndGoalProgress() = runBlocking {
        repository.checkAndSeedDefaultMantras()
        val active = repository.activeMantra.first()!!

        // Record a completed Mala (108 counts)
        repository.recordJaapSession(
            mantraId = active.id,
            mantraName = active.name,
            count = 108,
            malas = 1,
            durationSeconds = 60
        )

        val todayJaaps = repository.getTodayJaapCount().first()
        val todayMalas = repository.getTodayMalasCount().first()
        assertEquals(108, todayJaaps)
        assertEquals(1, todayMalas)

        val summaries = repository.dailySummaries.first()
        assertTrue(summaries.isNotEmpty())
        assertEquals(108, summaries.first().totalJaaps)
        assertEquals(1, summaries.first().totalMalas)
    }

    @Test
    fun testCustomMantraGoalUpdate() = runBlocking {
        val customId = repository.insertMantra(
            MantraEntity(
                name = "Om Shanti",
                sanskritText = "ॐ शान्तिः शान्तिः शान्तिः",
                transliteration = "Om Shanti Shanti Shanti",
                dailyGoalMalas = 3,
                beadsPerMala = 108
            )
        )

        repository.updateDailyGoal(customId, 5)
        val updated = db.mantraDao().getMantraById(customId)
        assertEquals(5, updated?.dailyGoalMalas)
    }

    @Test
    fun testStreakCalculation() {
        val today = repository.getTodayString()
        val streak = repository.calculateStreak(listOf(today))
        assertEquals(1, streak)
    }
}
