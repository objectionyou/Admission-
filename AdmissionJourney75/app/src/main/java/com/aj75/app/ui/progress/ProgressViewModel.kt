package com.aj75.app.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aj75.app.data.AdmissionRepository
import com.aj75.app.data.model.OverallStats
import com.aj75.app.data.model.SubjectStat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ProgressUiState(
    val overall: OverallStats = OverallStats(0, 0, 0, 0, 0),
    val subjects: List<SubjectStat> = emptyList(),
)

@HiltViewModel
class ProgressViewModel @Inject constructor(
    repository: AdmissionRepository,
) : ViewModel() {

    val uiState: StateFlow<ProgressUiState> = combine(
        repository.overallStats,
        repository.subjectStats,
    ) { overall, subjects ->
        ProgressUiState(overall, subjects)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProgressUiState())
}
