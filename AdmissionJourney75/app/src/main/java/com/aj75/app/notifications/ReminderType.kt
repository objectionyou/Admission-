package com.aj75.app.notifications

/**
 * The three reminder types from the web prototype's Settings > Notification Preferences list.
 * The prototype's UI showed exact times for morning/evening only; "revision" had none specified,
 * so 9:00 PM (right after the evening check, before a typical bedtime) is this app's own choice.
 */
enum class ReminderType(
    val requestCode: Int,
    val hour: Int,
    val minute: Int,
    val title: String,
    val message: String,
    val prefKey: String,
) {
    MORNING(
        requestCode = 1001,
        hour = 8,
        minute = 0,
        title = "সকালের Study Reminder",
        message = "আজকের Admission Journey শুরু করো!",
        prefKey = "notif_morning",
    ),
    EVENING(
        requestCode = 1002,
        hour = 20,
        minute = 0,
        title = "সান্ধ্য Target Check",
        message = "আজকের কতগুলো Target বাকি আছে দেখে নাও।",
        prefKey = "notif_evening",
    ),
    REVISION(
        requestCode = 1003,
        hour = 21,
        minute = 0,
        title = "Spaced Repetition Alert",
        message = "আজকে Vocabulary ও Revision এর সময় হয়েছে!",
        prefKey = "notif_revision",
    ),
    ;

    companion object {
        fun fromPrefKey(key: String?): ReminderType? = entries.find { it.prefKey == key }
    }
}
