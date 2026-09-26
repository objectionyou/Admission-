package com.aj75.app.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.aj75.app.data.local.SettingsDataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var settings: SettingsDataStore

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val appContext = context.applicationContext
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ReminderType.entries.forEach { type ->
                    if (settings.reminderEnabledOnce(type.prefKey)) {
                        AlarmScheduler.schedule(appContext, type)
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
