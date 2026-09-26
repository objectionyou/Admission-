package com.aj75.app.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Fired by AlarmManager at the scheduled time; shows the notification then re-arms for tomorrow. */
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val type = AlarmScheduler.extractReminderType(intent) ?: return
        NotificationHelper.show(context, type)
        AlarmScheduler.rescheduleNextOccurrence(context, type)
    }
}
