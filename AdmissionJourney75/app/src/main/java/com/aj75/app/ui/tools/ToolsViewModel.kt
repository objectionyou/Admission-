package com.aj75.app.ui.tools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aj75.app.data.AdmissionRepository
import com.aj75.app.data.local.FocusLogEntity
import com.aj75.app.data.local.McqLogEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

val TIMER_PRESETS = listOf(25, 45, 60, 90)

data class ToolsUiState(
    val presetMinutes: Int = 25,
    val remainingSeconds: Int = 25 * 60,
    val isRunning: Boolean = false,
    val recentFocusLogs: List<FocusLogEntity> = emptyList(),
    val recentMcqLogs: List<McqLogEntity> = emptyList(),
)

@HiltViewModel
class ToolsViewModel @Inject constructor(
    private val repository: AdmissionRepository,
) : ViewModel() {

    private val _presetMinutes = MutableStateFlow(25)
    private val _remainingSeconds = MutableStateFlow(25 * 60)
    private val _isRunning = MutableStateFlow(false)
    private var tickerJob: Job? = null

    val uiState: StateFlow<ToolsUiState> = combine(
        _presetMinutes,
        _remainingSeconds,
        _isRunning,
        repository.focusLogs,
        repository.mcqLogs,
    ) { preset, remaining, running, focusLogs, mcqLogs ->
        ToolsUiState(preset, remaining, running, focusLogs.take(5), mcqLogs.take(3))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ToolsUiState())

    fun selectPreset(minutes: Int) {
        if (_isRunning.value) return
        _presetMinutes.value = minutes
        _remainingSeconds.value = minutes * 60
    }

    fun start() {
        if (_isRunning.value || _remainingSeconds.value <= 0) return
        _isRunning.value = true
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            while (_remainingSeconds.value > 0 && _isRunning.value) {
                delay(1000)
                if (_isRunning.value) _remainingSeconds.value = (_remainingSeconds.value - 1).coerceAtLeast(0)
            }
            if (_remainingSeconds.value == 0) {
                _isRunning.value = false
                repository.addFocusLog(_presetMinutes.value)
            }
        }
    }

    fun pause() {
        _isRunning.value = false
        tickerJob?.cancel()
    }

    fun reset() {
        tickerJob?.cancel()
        _isRunning.value = false
        _remainingSeconds.value = _presetMinutes.value * 60
    }

    fun logMcq(attempted: Int, correct: Int) {
        if (attempted <= 0 || correct < 0 || correct > attempted) return
        viewModelScope.launch { repository.addMcqLog(attempted, correct) }
    }

    override fun onCleared() {
        tickerJob?.cancel()
        super.onCleared()
    }
}
