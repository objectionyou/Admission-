package com.aj75.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Mirrors the web app's `aj75_completions` map: one row per TaskItem.id that has ever been toggled. */
@Entity(tableName = "task_completions")
data class TaskCompletionEntity(
    @PrimaryKey val taskId: String,
    val completed: Boolean,
    val updatedAt: Long,
)

/** Mirrors `aj75_mcq_logs`. */
@Entity(tableName = "mcq_logs")
data class McqLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateIso: String, // full ISO instant, matches new Date().toISOString() in the web app
    val attempted: Int,
    val correct: Int,
    val wrong: Int,
    val accuracy: Int,
)

/** Mirrors `aj75_focus_logs`. */
@Entity(tableName = "focus_logs")
data class FocusLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateIso: String,
    val minutes: Int,
)

/** Mirrors `aj75_notes` (keyed by day, one free-text note each). */
@Entity(tableName = "day_notes")
data class DayNoteEntity(
    @PrimaryKey val day: Int,
    val note: String,
)
