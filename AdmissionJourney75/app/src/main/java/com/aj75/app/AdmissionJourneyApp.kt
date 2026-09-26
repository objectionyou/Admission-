package com.aj75.app

import android.app.Application
import com.aj75.app.data.local.SettingsDataStore
import com.aj75.app.notifications.AlarmScheduler
import com.aj75.app.notifications.ReminderType
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class AdmissionJourneyApp : Application() {

    @Inject
    lateinit var settings: SettingsDataStore

    private val appScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        // Re-arm every enabled reminder on each process start (cheap - just updates existing
        // PendingIntents) so a fresh install's default-on reminders actually start firing
        // without the user needing to open Settings first.
        appScope.launch {
            ReminderType.entries.forEach { type ->
                if (settings.reminderEnabledOnce(type.prefKey)) {
                    AlarmScheduler.schedule(applicationContext, type)
                }
            }
        }
    }
}
