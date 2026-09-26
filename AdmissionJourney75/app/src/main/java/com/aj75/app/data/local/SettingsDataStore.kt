package com.aj75.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "aj75_settings")

/**
 * Unlike the web prototype (which pins "today" to a stored default and never advances it on its
 * own), [simulatedDate] is null by default — meaning "use the real device date". Setting it is
 * strictly a Settings > Date Simulator testing override, matching the original tool's intent.
 */
@Singleton
class SettingsDataStore @Inject constructor(@ApplicationContext private val context: Context) {
    private val simulatedDateKey = stringPreferencesKey("simulated_today")

    val simulatedDate: Flow<String?> = context.dataStore.data.map { it[simulatedDateKey] }

    suspend fun simulatedDateOnce(): String? = simulatedDate.first()

    suspend fun setSimulatedDate(dateIso: String?) {
        context.dataStore.edit { prefs ->
            if (dateIso == null) prefs.remove(simulatedDateKey) else prefs[simulatedDateKey] = dateIso
        }
    }

    // Per-reminder toggles for the Settings screen; each key mirrors ReminderType.prefKey.
    // Default true so a fresh install behaves like the web prototype's reminders being live.
    fun reminderEnabled(prefKey: String): Flow<Boolean> =
        context.dataStore.data.map { it[booleanPreferencesKey(prefKey)] ?: true }

    suspend fun reminderEnabledOnce(prefKey: String): Boolean = reminderEnabled(prefKey).first()

    suspend fun setReminderEnabled(prefKey: String, enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[booleanPreferencesKey(prefKey)] = enabled }
    }
}
