package com.aj75.app.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aj75.app.data.AdmissionRepository
import com.aj75.app.data.local.SettingsDataStore
import com.aj75.app.notifications.AlarmScheduler
import com.aj75.app.notifications.ReminderType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class SettingsUiState(
    val simulatedDate: String? = null,
    val reminderEnabled: Map<ReminderType, Boolean> = ReminderType.entries.associateWith { true },
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: AdmissionRepository,
    private val settings: SettingsDataStore,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        repository.simulatedDate,
        settings.reminderEnabled(ReminderType.MORNING.prefKey),
        settings.reminderEnabled(ReminderType.EVENING.prefKey),
        settings.reminderEnabled(ReminderType.REVISION.prefKey),
    ) { simDate, morning, evening, revision ->
        SettingsUiState(
            simulatedDate = simDate,
            reminderEnabled = mapOf(
                ReminderType.MORNING to morning,
                ReminderType.EVENING to evening,
                ReminderType.REVISION to revision,
            ),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState())

    fun setReminderEnabled(type: ReminderType, enabled: Boolean) {
        viewModelScope.launch {
            settings.setReminderEnabled(type.prefKey, enabled)
            AlarmScheduler.setEnabled(context, type, enabled)
        }
    }

    fun sendTestNotification(type: ReminderType) {
        com.aj75.app.notifications.NotificationHelper.show(context, type)
    }

    fun setSimulatedDate(date: LocalDate) {
        viewModelScope.launch { repository.setSimulatedDate(date.toString()) }
    }

    fun resetToRealDate() {
        viewModelScope.launch { repository.resetToDayOne() }
    }

    fun resetAllData() {
        viewModelScope.launch { repository.resetAllData() }
    }

    suspend fun exportBackupJson(): String = repository.exportBackupJson()

    fun importBackupJson(json: String, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            val ok = runCatching { repository.importBackupJson(json) }.isSuccess
            onDone(ok)
        }
    }
}
