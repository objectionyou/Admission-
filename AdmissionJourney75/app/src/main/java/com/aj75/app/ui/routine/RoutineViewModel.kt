package com.aj75.app.ui.routine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aj75.app.data.AdmissionRepository
import com.aj75.app.data.RoutineDataGenerator
import com.aj75.app.data.model.DayPlan
import com.aj75.app.data.model.MissedDayTasks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class RoutineFilter(val label: String) {
    TODAY("Today"),
    TOMORROW("Tomorrow"),
    WEEK("This Week"),
    ALL("All 75 Days"),
}

data class RoutineUiState(
    val dayNum: Int = 1,
    val days: List<DayPlan> = emptyList(),
    val completions: Map<String, Boolean> = emptyMap(),
    val notes: Map<Int, String> = emptyMap(),
    val missed: List<MissedDayTasks> = emptyList(),
)

@HiltViewModel
class RoutineViewModel @Inject constructor(
    private val repository: AdmissionRepository,
) : ViewModel() {

    private val _filter = MutableStateFlow(RoutineFilter.TODAY)
    val filter: StateFlow<RoutineFilter> = _filter.asStateFlow()

    private val _selectedDay = MutableStateFlow<Int?>(null)
    val selectedDay: StateFlow<Int?> = _selectedDay.asStateFlow()

    val uiState: StateFlow<RoutineUiState> = combine(
        repository.currentDayInfo,
        repository.completions,
        repository.notes,
        _filter,
    ) { dayInfo, completions, notes, filter ->
        val dayNum = dayInfo.dayNum
        val days = when (filter) {
            RoutineFilter.TODAY -> RoutineDataGenerator.ROUTINE_DATA.filter { it.day == dayNum }
            RoutineFilter.TOMORROW -> RoutineDataGenerator.ROUTINE_DATA.filter { it.day == dayNum + 1 }
            RoutineFilter.WEEK -> {
                val start = dayNum.coerceAtLeast(1)
                RoutineDataGenerator.ROUTINE_DATA.filter { it.day in start until (start + 7) }
            }
            RoutineFilter.ALL -> RoutineDataGenerator.ROUTINE_DATA
        }
        RoutineUiState(
            dayNum = dayNum,
            days = days,
            completions = completions,
            notes = notes,
            missed = AdmissionRepository.computeMissedPastTasks(completions, dayNum),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RoutineUiState())

    fun setFilter(filter: RoutineFilter) {
        _filter.value = filter
    }

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
