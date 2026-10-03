package com.example.data

import com.example.data.dao.JaapSessionDao
import com.example.data.dao.MantraDao
import com.example.data.entity.DailySummary
import com.example.data.entity.JaapSessionEntity
import com.example.data.entity.MantraEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MalaRepository(
    private val mantraDao: MantraDao,
    private val jaapSessionDao: JaapSessionDao
) {
    val allMantras: Flow<List<MantraEntity>> = mantraDao.getAllMantras()
    val activeMantra: Flow<MantraEntity?> = mantraDao.getActiveMantra()
    val recentSessions: Flow<List<JaapSessionEntity>> = jaapSessionDao.getRecentSessions(30)
    val dailySummaries: Flow<List<DailySummary>> = jaapSessionDao.getDailySummaries(30)
    val totalLifetimeJaaps: Flow<Long> = jaapSessionDao.getTotalLifetimeJaaps()
    val totalLifetimeMalas: Flow<Long> = jaapSessionDao.getTotalLifetimeMalas()

    fun getTodayString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getTodayJaapCount(): Flow<Int> {
        return jaapSessionDao.getTodayJaapCountFlow(getTodayString())
    }

    fun getTodayMalasCount(): Flow<Int> {
        return jaapSessionDao.getTodayMalasCountFlow(getTodayString())
    }

    fun getTodayMalasCountForMantra(mantraId: Long): Flow<Int> {
        return jaapSessionDao.getTodayMalasCountForMantraFlow(getTodayString(), mantraId)
    }

    suspend fun checkAndSeedDefaultMantras() = withContext(Dispatchers.IO) {
        val defaultMantras = listOf(
            MantraEntity(
                name = "Gayatri Mantra",
                sanskritText = "ॐ भूर्भुवः स्वः तत्सवितुर्वरेण्यं भर्गो देवस्य धीमहि धियो यो नः प्रचोदयात्॥",
                transliteration = "Om Bhur Bhuvaḥ Swaḥ, Tat-Savitur Vareṇyaṃ, Bhargo Devasya Dhīmahi, Dhiyo Yo Naḥ Prachodayāt",
                meaning = "We meditate upon the radiant glory of the divine Sun, creator of the universe; may He illuminate and guide our intellect towards truth.",
                dailyGoalMalas = 1,
                beadsPerMala = 108,
                isActive = true,
                colorHex = 0xFFFF8C00 // Saffron Gold
            ),
            MantraEntity(
                name = "Maha Mrityunjaya",
                sanskritText = "ॐ त्र्यम्बकं यजामहे सुगन्धिं पुष्टिवर्धनम्। उर्वारुकमिव बन्धनान् मृत्योर्मुक्षीय मामृतात्॥",
                transliteration = "Om Tryambakaṃ Yajāmahe Sugandhiṃ Puṣṭi-vardhanam, Urvārukam-iva Bandhanān Mṛtyor-mukṣīya Māmṛtāt",
                meaning = "We worship the Three-Eyed Lord Shiva, fragrant and nourishing. May He liberate us from death and mortal attachment into the nectar of immortality.",
                dailyGoalMalas = 1,
                beadsPerMala = 108,
                isActive = false,
                colorHex = 0xFF0288D1 // Spiritual Blue
            ),
            MantraEntity(
                name = "Om Namah Shivaya",
                sanskritText = "ॐ नमः शिवाय",
                transliteration = "Om Namaḥ Śivāya",
                meaning = "I honor and bow to Shiva, the auspicious inner consciousness and true self.",
                dailyGoalMalas = 2,
                beadsPerMala = 108,
                isActive = false,
                colorHex = 0xFF5D4037 // Rudraksha Brown
            ),
            MantraEntity(
                name = "Hare Krishna Maha Mantra",
                sanskritText = "हरे कृष्ण हरे कृष्ण कृष्ण कृष्ण हरे हरे । हरे राम हरे राम राम राम हरे हरे ॥",
                transliteration = "Hare Kṛṣṇa Hare Kṛṣṇa Kṛṣṇa Kṛṣṇa Hare Hare, Hare Rāma Hare Rāma Rāma Rāma Hare Hare",
                meaning = "O supreme divine mother Hara (Radha) and all-attractive Lord Krishna and Rama, please engage me in pure loving devotion.",
                dailyGoalMalas = 4,
                beadsPerMala = 108,
                isActive = false,
                colorHex = 0xFFD81B60 // Lotus Pink
            ),
            MantraEntity(
                name = "Ganesh Beej & Vakratunda",
                sanskritText = "ॐ गं गणपतये नमः। वक्रतुण्ड महाकाय सूर्यकोटि समप्रभ। निर्विघ्नं कुरु मे देव सर्वकार्येषु सर्वदा॥",
                transliteration = "Om Gaṃ Gaṇapataye Namaḥ | Vakratuṇḍa Mahākāya Sūryakoṭi Samaprabha, Nirvighnaṃ Kuru Me Deva Sarva-kāryeṣu Sarvadā",
                meaning = "Salutations to Lord Ganesha, remover of hurdles. O magnificent deity of immense grace, keep all my paths free of impediments forever.",
                dailyGoalMalas = 1,
                beadsPerMala = 108,
                isActive = false,
                colorHex = 0xFFE65100 // Marigold Orange
            ),
            MantraEntity(
                name = "Hanuman Beej & Raksha",
                sanskritText = "ॐ हं हनुमते नमः | ॐ नमो भगवते आञ्जनेयाय महाबलाय स्वाहा॥",
                transliteration = "Om Haṃ Hanumate Namaḥ | Om Namo Bhagavate Āñjaneyāya Mahābalāya Svāhā",
                meaning = "Salutations to Lord Hanuman, the supreme embodiment of courage, boundless strength, unwavering devotion, and protection.",
                dailyGoalMalas = 1,
                beadsPerMala = 108,
                isActive = false,
                colorHex = 0xFFD32F2F // Sindoor Vermilion
            ),
            MantraEntity(
                name = "Mahalakshmi Mantra",
                sanskritText = "ॐ श्रीं ह्रीं क्लीं महालक्ष्म्यै नमः | ॐ महालक्ष्म्यै च विद्महे विष्णुपत्न्यै च धीमहि तन्नो लक्ष्मीः प्रचोदयात्॥",
                transliteration = "Om Śrīṃ Hrīṃ Klīṃ Mahālakṣmyai Namaḥ | Om Mahālakṣmyai Cha Vidmahe Viṣṇupatnyai Cha Dhīmahi Tanno Lakṣmīḥ Prachodayāt",
                meaning = "Salutations to Goddess Mahalakshmi, goddess of spiritual and material abundance, generosity, and auspicious blessings.",
                dailyGoalMalas = 1,
                beadsPerMala = 108,
                isActive = false,
                colorHex = 0xFFFFB300 // Golden Amber
            ),
            MantraEntity(
                name = "Durga Navarna Mantra",
                sanskritText = "ॐ ऐं ह्रीं क्लीं चामुण्डायै विच्चे॥",
                transliteration = "Om Aiṃ Hrīṃ Klīṃ Cāmuṇḍāyai Vicce",
                meaning = "O supreme Mother Durga, supreme vanquisher of inner ignorance, protect and elevate my heart and mind.",
                dailyGoalMalas = 1,
                beadsPerMala = 108,
                isActive = false,
                colorHex = 0xFFAD1457 // Crimson Red
            ),
            MantraEntity(
                name = "Saraswati Vandana",
                sanskritText = "ॐ ऐं सरस्वत्यै नमः। सरस्वति नमस्तुभ्यं वरदे कामरूपिणि। विद्यारम्भं करिष्यामि सिद्धिर्भवतु मे सदा॥",
                transliteration = "Om Aiṃ Saraswatyai Namaḥ | Sarasvati Namastubhyaṃ Varade Kāmarūpiṇi, Vidyārambhaṃ Kariṣyāmi Siddhir Bhavatu Me Sadā",
                meaning = "Salutations to Goddess Saraswati, granter of wisdom, arts, and learning. May mental clarity, knowledge, and eloquence guide me always.",
                dailyGoalMalas = 1,
                beadsPerMala = 108,
                isActive = false,
                colorHex = 0xFF00897B // Celadon Teal
            ),
            MantraEntity(
                name = "Surya Gayatri & Aditya",
                sanskritText = "ॐ सूर्याय नमः | ॐ भास्कराय विद्महे दिवाकराय धीमहि तन्नः सूर्यः प्रचोदयात्॥",
                transliteration = "Om Sūryāya Namaḥ | Om Bhāskarāya Vidmahe Divākarāya Dhīmahi Tannaḥ Sūryaḥ Prachodayāt",
                meaning = "Salutations to the Sun God, divine source of vitality, health, light, and renewed cosmic energy.",
                dailyGoalMalas = 1,
                beadsPerMala = 108,
                isActive = false,
                colorHex = 0xFFF57F17 // Solar Gold
            ),
            MantraEntity(
                name = "Om Namo Bhagavate Vasudevaya",
                sanskritText = "ॐ नमो भगवते वासुदेवाय॥",
                transliteration = "Om Namo Bhagavate Vāsudevāya",
                meaning = "I surrender and bow to Lord Vasudeva (Lord Krishna / Vishnu), the all-pervading protector and sustainer of creation.",
                dailyGoalMalas = 1,
                beadsPerMala = 108,
                isActive = false,
                colorHex = 0xFF1565C0 // Krishna Blue
            ),
            MantraEntity(
                name = "Maha Shanti Mantra",
                sanskritText = "ॐ सर्वेषां स्वस्तिर्भवतु। सर्वेषां शान्तिर्भवतु। सर्वेषां पूर्णं भवतु। सर्वेषां मङ्गलं भवतु। ॐ शान्तिः शान्तिः शान्तिः॥",
                transliteration = "Om Sarveṣāṃ Swastir Bhavatu, Sarveṣāṃ Śāntir Bhavatu, Sarveṣāṃ Pūrṇaṃ Bhavatu, Sarveṣāṃ Maṅgalaṃ Bhavatu. Om Śāntiḥ Śāntiḥ Śāntiḥ",
                meaning = "May well-being and auspiciousness be with all beings. May peace and wholeness prevail across the entire universe. Om Peace, Peace, Peace.",
                dailyGoalMalas = 1,
                beadsPerMala = 108,
                isActive = false,
                colorHex = 0xFF2E7D32 // Sacred Forest Green
            ),
            MantraEntity(
                name = "Om Mani Padme Hum",
                sanskritText = "ॐ मणि पद्मे हूँ",
                transliteration = "Om Maṇi Padme Hūṃ",
                meaning = "The sacred jewel resides in the lotus: opening our awareness to unconditional compassion and enlightened mindfulness.",
                dailyGoalMalas = 1,
                beadsPerMala = 108,
                isActive = false,
                colorHex = 0xFF7B1FA2 // Sacred Violet
            ),
            MantraEntity(
                name = "Shani Beej Mantra",
                sanskritText = "ॐ प्रां प्रीं प्रौं सः शनैश्चराय नमः॥",
                transliteration = "Om Prāṃ Prīṃ Prauṃ Saḥ Śanaiścharāya Namaḥ",
                meaning = "Salutations to Lord Shani, master of karmic balance, patience, discipline, and steady spiritual growth.",
                dailyGoalMalas = 1,
                beadsPerMala = 108,
                isActive = false,
                colorHex = 0xFF37474F // Saturn Dark Slate
            ),
            MantraEntity(
                name = "Navagraha Shanti",
                sanskritText = "ब्रह्मा मुरारिस्त्रपुरान्तकारी भानुः शशी भूमिसुतो बुधश्च। गुरुश्च शुक्रः शनिह राहुकेतवः सर्वे कुर्वन्तु मे शान्तिम्॥",
                transliteration = "Brahmā Murāris-tripurāntakārī Bhānuḥ Śaśī Bhūmisuto Budhaścha, Guruḥ Śukraḥ Śaniḥ Rāhu-ketavaḥ Sarve Kurvantu Me Śāntim",
                meaning = "May all cosmic planetary energies and divine forces bestow supreme peace, balance, and harmony upon my daily path.",
                dailyGoalMalas = 1,
                beadsPerMala = 108,
                isActive = false,
                colorHex = 0xFF455A64 // Cosmic Grey
            ),
            MantraEntity(
                name = "Kubera Prosperity Mantra",
                sanskritText = "ॐ यक्षाय कुबेराय वैश्रवणाय धनधान्याधिपतये धनधान्यसमृद्धिं मे देहि दापय स्वाहा॥",
                transliteration = "Om Yakṣāya Kuberāya Vaiśravaṇāya Dhana-dhānyādhipataye Dhana-dhānya-samṛddhiṃ Me Dehi Dāpaya Swāhā",
                meaning = "O Lord Kubera, divine treasurer of abundance, bless my endeavors with righteous prosperity, wisdom, and resourcefulness.",
                dailyGoalMalas = 1,
                beadsPerMala = 108,
                isActive = false,
                colorHex = 0xFFF9A825 // Wealth Gold
            )
        )

        for (mantra in defaultMantras) {
            val exists = mantraDao.getMantraCountByName(mantra.name) > 0
            if (!exists) {
                mantraDao.insertMantra(mantra)
            }
        }

        // Ensure there is at least one active mantra
        val currentActive = mantraDao.getActiveMantraOnce()
        if (currentActive == null) {
            val gayatri = mantraDao.getMantraByName("Gayatri Mantra")
            if (gayatri != null) {
                mantraDao.setActiveMantra(gayatri.id)
            }
        }
    }

    suspend fun recordJaapSession(
        mantraId: Long,
        mantraName: String,
        count: Int,
        malas: Int,
        durationSeconds: Long
    ) = withContext(Dispatchers.IO) {
        if (count <= 0 && malas <= 0) return@withContext
        val session = JaapSessionEntity(
            mantraId = mantraId,
            mantraName = mantraName,
            dateStr = getTodayString(),
            jaapCount = count,
            malasCompleted = malas,
            durationSeconds = durationSeconds,
            timestamp = System.currentTimeMillis()
        )
        jaapSessionDao.insertSession(session)
        mantraDao.incrementCounts(mantraId, count, malas)
    }

    suspend fun setActiveMantra(id: Long) = withContext(Dispatchers.IO) {
        mantraDao.setActiveMantra(id)
    }

    suspend fun updateDailyGoal(id: Long, goalMalas: Int) = withContext(Dispatchers.IO) {
        mantraDao.updateDailyGoal(id, goalMalas)
    }

    suspend fun insertMantra(mantra: MantraEntity): Long = withContext(Dispatchers.IO) {
        mantraDao.insertMantra(mantra)
    }

    suspend fun updateMantra(mantra: MantraEntity) = withContext(Dispatchers.IO) {
        mantraDao.updateMantra(mantra)
    }

    suspend fun deleteMantra(id: Long) = withContext(Dispatchers.IO) {
        val active = mantraDao.getActiveMantraOnce()
        mantraDao.deleteMantraById(id)
        if (active?.id == id) {
            val remaining = mantraDao.getActiveMantraOnce()
            if (remaining == null) {
                // activate first available
                val all = mantraDao.getAllMantras()
                // handled reactively or via seed
            }
        }
    }

    fun calculateStreak(activeDates: List<String>): Int {
        if (activeDates.isEmpty()) return 0
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayCalendar = Calendar.getInstance()
        val todayStr = sdf.format(todayCalendar.time)

        val yesterdayCalendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }
        val yesterdayStr = sdf.format(yesterdayCalendar.time)

        // Streak must be alive either today or yesterday
        val dateSet = activeDates.toSet()
        if (!dateSet.contains(todayStr) && !dateSet.contains(yesterdayStr)) {
            return 0
        }

        var streak = 0
        val checkCal = Calendar.getInstance()
        if (!dateSet.contains(todayStr) && dateSet.contains(yesterdayStr)) {
            checkCal.add(Calendar.DAY_OF_YEAR, -1)
        }

        while (true) {
            val dateToCheck = sdf.format(checkCal.time)
            if (dateSet.contains(dateToCheck)) {
                streak++
                checkCal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return streak
    }

    val currentStreak: Flow<Int> = jaapSessionDao.getActiveDates().map { dates ->
        calculateStreak(dates)
    }.flowOn(Dispatchers.Default)
}
