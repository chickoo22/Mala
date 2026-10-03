package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MalaDatabase
import com.example.data.MalaRepository
import com.example.data.entity.DailySummary
import com.example.data.entity.JaapSessionEntity
import com.example.data.entity.MantraEntity
import com.example.util.HapticFeedbackHelper
import com.example.util.SoundFeedbackHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MalaViewModel(application: Application) : AndroidViewModel(application) {
    private val database = MalaDatabase.getInstance(application)
    private val repository = MalaRepository(database.mantraDao(), database.jaapSessionDao())
    private val hapticHelper = HapticFeedbackHelper(application)
    private val soundHelper = SoundFeedbackHelper()

    val allMantras: StateFlow<List<MantraEntity>> = repository.allMantras
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeMantra: StateFlow<MantraEntity?> = repository.activeMantra
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val dailySummaries: StateFlow<List<DailySummary>> = repository.dailySummaries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentSessions: StateFlow<List<JaapSessionEntity>> = repository.recentSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lifetimeJaaps: StateFlow<Long> = repository.totalLifetimeJaaps
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val lifetimeMalas: StateFlow<Long> = repository.totalLifetimeMalas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val currentStreak: StateFlow<Int> = repository.currentStreak
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todayTotalJaaps: StateFlow<Int> = repository.getTodayJaapCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todayTotalMalas: StateFlow<Int> = repository.getTodayMalasCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Current in-memory session counter state
    private val _currentBead = MutableStateFlow(0)
    val currentBead: StateFlow<Int> = _currentBead.asStateFlow()

    private val _sessionMalas = MutableStateFlow(0)
    val sessionMalas: StateFlow<Int> = _sessionMalas.asStateFlow()

    private val _soundEnabled = MutableStateFlow(true)
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _vibrationEnabled = MutableStateFlow(true)
    val vibrationEnabled: StateFlow<Boolean> = _vibrationEnabled.asStateFlow()

    private val _showLyrics = MutableStateFlow(true)
    val showLyrics: StateFlow<Boolean> = _showLyrics.asStateFlow()

    private val _justCompletedMala = MutableStateFlow(false)
    val justCompletedMala: StateFlow<Boolean> = _justCompletedMala.asStateFlow()

    private var sessionStartTime = System.currentTimeMillis()

    init {
        viewModelScope.launch {
            repository.checkAndSeedDefaultMantras()
        }
    }

    fun onBeadTapped() {
        val mantra = activeMantra.value ?: return
        val targetBeads = if (mantra.beadsPerMala > 0) mantra.beadsPerMala else 108
        val nextBead = _currentBead.value + 1

        if (nextBead >= targetBeads) {
            // Completed 1 full Mala!
            _currentBead.value = 0
            _sessionMalas.value += 1
            _justCompletedMala.value = true

            // Feedback: vibration + harmonic chime
            hapticHelper.malaCompletedVibration(_vibrationEnabled.value)
            soundHelper.playMalaCompletedChime(_soundEnabled.value)

            // Save to database
            viewModelScope.launch {
                val durationSec = (System.currentTimeMillis() - sessionStartTime) / 1000
                repository.recordJaapSession(
                    mantraId = mantra.id,
                    mantraName = mantra.name,
                    count = targetBeads,
                    malas = 1,
                    durationSeconds = durationSec.coerceAtLeast(1)
                )
                sessionStartTime = System.currentTimeMillis()
            }
        } else {
            _currentBead.value = nextBead
            _justCompletedMala.value = false
            hapticHelper.beadVibration(_vibrationEnabled.value)
            soundHelper.playBeadSound(_soundEnabled.value)
        }
    }

    fun dismissMalaCelebration() {
        _justCompletedMala.value = false
    }

    fun undoBead() {
        if (_currentBead.value > 0) {
            _currentBead.value -= 1
            hapticHelper.beadVibration(_vibrationEnabled.value)
        }
    }

    fun resetCurrentMala() {
        _currentBead.value = 0
        _justCompletedMala.value = false
    }

    fun toggleSound() {
        _soundEnabled.value = !_soundEnabled.value
    }

    fun toggleVibration() {
        _vibrationEnabled.value = !_vibrationEnabled.value
    }

    fun toggleLyrics() {
        _showLyrics.value = !_showLyrics.value
    }

    fun selectMantra(mantraId: Long) {
        viewModelScope.launch {
            // Save current partial progress if user wishes or reset bead
            _currentBead.value = 0
            _sessionMalas.value = 0
            sessionStartTime = System.currentTimeMillis()
            repository.setActiveMantra(mantraId)
        }
    }

    fun updateMantraGoal(mantraId: Long, newGoalMalas: Int) {
        viewModelScope.launch {
            repository.updateDailyGoal(mantraId, newGoalMalas.coerceAtLeast(1))
        }
    }

    fun addCustomMantra(
        name: String,
        sanskrit: String,
        transliteration: String,
        meaning: String,
        goalMalas: Int,
        beadsPerMala: Int,
        colorHex: Long
    ) {
        viewModelScope.launch {
            val mantra = MantraEntity(
                name = name.trim(),
                sanskritText = sanskrit.trim(),
                transliteration = transliteration.trim(),
                meaning = meaning.trim(),
                dailyGoalMalas = goalMalas.coerceAtLeast(1),
                beadsPerMala = if (beadsPerMala in listOf(27, 54, 108)) beadsPerMala else 108,
                isActive = false,
                colorHex = colorHex
            )
            repository.insertMantra(mantra)
        }
    }

    fun updateMantra(mantra: MantraEntity) {
        viewModelScope.launch {
            repository.updateMantra(mantra)
        }
    }

    fun deleteMantra(mantraId: Long) {
        viewModelScope.launch {
            repository.deleteMantra(mantraId)
        }
    }

    fun restoreDefaultMantras() {
        viewModelScope.launch {
            repository.checkAndSeedDefaultMantras()
        }
    }
}
