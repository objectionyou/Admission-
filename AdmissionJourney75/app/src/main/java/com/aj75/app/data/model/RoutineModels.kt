package com.aj75.app.data.model

/** One of the 75 generated study days. Mirrors `generate75DaysData()` from the web prototype. */
data class DayPlan(
    val day: Int,
    val date: String, // ISO yyyy-MM-dd
    val mainSubject: String, // "বাংলা" or "English"
    val topic: String,
    val literature: String,
    val vocabulary: String,
    val composition: String,
    val englishForToday: String,
    val englishWritten: String,
    val gk: String,
    val revision: Boolean,
    val mcqTarget: Int,
)

/** A single checkable item within a day. Mirrors `getDayTasks()`. */
data class TaskItem(
    val id: String, // "d{day}_{suffix}" e.g. "d1_vocab"
    val day: Int,
    val title: String,
    val detail: String,
    val category: String, // Vocabulary | Birachon | English | Written | GK | বাংলা/English | MCQ
)

data class SubjectStat(
    val key: String,
    val title: String,
    val pct: Int,
    val completed: Int,
    val total: Int,
)

data class OverallStats(
    val completedCount: Int,
    val totalTasks: Int,
    val overallPercent: Int,
    val streak: Int,
    val longestStreak: Int,
)

data class RevisionDueItem(
    val offset: Int, // 1, 3 or 7 days ago
    val dayNum: Int,
    val topic: String,
    val mainSubject: String,
)

data class CurrentDayInfo(
    val dayNum: Int,
    val remaining: Int,
    val data: DayPlan,
)

data class MissedDayTasks(
    val dayData: DayPlan,
    val uncompleted: List<TaskItem>,
)
