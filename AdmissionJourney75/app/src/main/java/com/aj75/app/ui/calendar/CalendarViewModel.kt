package com.aj75.app.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aj75.app.data.AdmissionRepository
import com.aj75.app.data.RoutineDataGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CalendarUiState(
    val dayNum: Int = 1,
    val completionPctByDay: Map<Int, Int> = emptyMap(),
    val completions: Map<String, Boolean> = emptyMap(),
    val notes: Map<Int, String> = emptyMap(),
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: AdmissionRepository,
) : ViewModel() {

    private val _selectedDay = MutableStateFlow<Int?>(null)
    val selectedDay: StateFlow<Int?> = _selectedDay.asStateFlow()

    val uiState: StateFlow<CalendarUiState> = combine(
        repository.currentDayInfo,
        repository.completions,
        repository.notes,
    ) { dayInfo, completions, notes ->
        val pctByDay = RoutineDataGenerator.ROUTINE_DATA.associate { day ->
            val tasks = RoutineDataGenerator.getDayTasks(day)
            val done = tasks.count { completions[it.id] == true }
            day.day to if (tasks.isNotEmpty()) (done * 100) / tasks.size else 0
        }
        CalendarUiState(dayNum = dayInfo.dayNum, completionPctByDay = pctByDay, completions = completions, notes = notes)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CalendarUiState())

    fun openDay(day: Int) {
        _selectedDay.value = day
    }

    fun closeDay() {
        _selectedDay.value = null
    }

    fun toggleTask(taskId: String) {
        viewModelScope.launch { repository.toggleTask(taskId) }
    }

    fun saveNote(day: Int, note: String) {
        viewModelScope.launch { repository.saveNote(day, note) }
    }
}
