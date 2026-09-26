package com.aj75.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aj75.app.data.AdmissionRepository
import com.aj75.app.data.RoutineDataGenerator
import com.aj75.app.data.model.DayPlan
import com.aj75.app.data.model.RevisionDueItem
import com.aj75.app.data.model.TaskItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val dayNum: Int = 1,
    val remaining: Int = RoutineDataGenerator.TOTAL_DAYS - 1,
    val dayPlan: DayPlan? = null,
    val overallPercent: Int = 0,
    val streak: Int = 0,
    val todayTasks: List<TaskItem> = emptyList(),
    val completions: Map<String, Boolean> = emptyMap(),
    val quote: String = "",
    val revisionDue: List<RevisionDueItem> = emptyList(),
    val notes: Map<Int, String> = emptyMap(),
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AdmissionRepository,
) : ViewModel() {

    private val _selectedDay = MutableStateFlow<Int?>(null)
    val selectedDay: StateFlow<Int?> = _selectedDay.asStateFlow()

    private val _showCelebration = MutableStateFlow(false)
    val showCelebration: StateFlow<Boolean> = _showCelebration.asStateFlow()

    val uiState: StateFlow<HomeUiState> = combine(
        repository.currentDayInfo,
        repository.overallStats,
        repository.completions,
        repository.notes,
    ) { dayInfo, stats, completions, notes ->
        HomeUiState(
            dayNum = dayInfo.dayNum,
            remaining = dayInfo.remaining,
            dayPlan = dayInfo.data,
            overallPercent = stats.overallPercent,
            streak = stats.streak,
            todayTasks = RoutineDataGenerator.getDayTasks(dayInfo.data),
            completions = completions,
            quote = RoutineDataGenerator.MOTIVATIONAL_QUOTES[(dayInfo.dayNum - 1) % RoutineDataGenerator.MOTIVATIONAL_QUOTES.size],
            revisionDue = AdmissionRepository.computeRevisionDue(dayInfo.dayNum),
            notes = notes,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    fun openDay(day: Int) {
        _selectedDay.value = day
    }

    fun closeDay() {
        _selectedDay.value = null
    }

    fun dismissCelebration() {
        _showCelebration.value = false
    }

    fun toggleTask(taskId: String) {
        viewModelScope.launch {
            val allDone = repository.toggleTask(taskId)
            if (allDone) _showCelebration.value = true
        }
    }

    fun saveNote(day: Int, note: String) {
        viewModelScope.launch { repository.saveNote(day, note) }
    }
}
