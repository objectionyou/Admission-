package com.aj75.app.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Uses AlarmManager.setAndAllowWhileIdle (Doze-aware, but not "exact") rather than an exact
 * alarm. A study reminder landing a few minutes after 8:00 is perfectly fine, and it avoids the
 * SCHEDULE_EXACT_ALARM permission — which the Play Store restricts to alarm-clock/calendar-style
 * apps and which a daily study nudge doesn't need.
 */
object AlarmScheduler {
    private const val EXTRA_REMINDER_KEY = "reminder_pref_key"

    fun schedule(context: Context, type: ReminderType) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val triggerAtMillis = nextTriggerMillis(type)
        runCatching {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntentFor(context, type))
        }
    }

    fun cancel(context: Context, type: ReminderType) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        alarmManager.cancel(pendingIntentFor(context, type))
    }

    fun setEnabled(context: Context, type: ReminderType, enabled: Boolean) {
        if (enabled) schedule(context, type) else cancel(context, type)
    }

    /** Called by ReminderReceiver right after firing, so the one-shot alarm arms itself for tomorrow. */
    fun rescheduleNextOccurrence(context: Context, type: ReminderType) = schedule(context, type)

    private fun pendingIntentFor(context: Context, type: ReminderType): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(EXTRA_REMINDER_KEY, type.prefKey)
        }
        return PendingIntent.getBroadcast(
            context,
            type.requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun nextTriggerMillis(type: ReminderType): Long {
        val zone = ZoneId.systemDefault()
        val now = LocalDateTime.now(zone)
        var target = now.withHour(type.hour).withMinute(type.minute).withSecond(0).withNano(0)
        if (!target.isAfter(now)) target = target.plusDays(1)
        return target.atZone(zone).toInstant().toEpochMilli()
    }

    fun extractReminderType(intent: Intent): ReminderType? =
        ReminderType.fromPrefKey(intent.getStringExtra(EXTRA_REMINDER_KEY))
}
