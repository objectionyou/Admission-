package com.aj75.app.data

import android.content.Context
import com.aj75.app.data.local.DayNoteDao
import com.aj75.app.data.local.DayNoteEntity
import com.aj75.app.data.local.FocusLogDao
import com.aj75.app.data.local.FocusLogEntity
import com.aj75.app.data.local.McqLogDao
import com.aj75.app.data.local.McqLogEntity
import com.aj75.app.data.local.SettingsDataStore
import com.aj75.app.data.local.TaskCompletionDao
import com.aj75.app.data.local.TaskCompletionEntity
import com.aj75.app.data.model.CurrentDayInfo
import com.aj75.app.data.model.DayPlan
import com.aj75.app.data.model.MissedDayTasks
import com.aj75.app.data.model.OverallStats
import com.aj75.app.data.model.RevisionDueItem
import com.aj75.app.data.model.SubjectStat
import com.aj75.app.data.model.TaskItem
import com.aj75.app.widget.WidgetRefresher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdmissionRepository @Inject constructor(
    private val taskCompletionDao: TaskCompletionDao,
    private val mcqLogDao: McqLogDao,
    private val focusLogDao: FocusLogDao,
    private val dayNoteDao: DayNoteDao,
    private val settings: SettingsDataStore,
    @ApplicationContext private val context: Context,
) {
    val completions: Flow<Map<String, Boolean>> =
        taskCompletionDao.observeAll().map { rows -> rows.associate { it.taskId to it.completed } }

    val simulatedDate: Flow<String?> = settings.simulatedDate

    val currentDayInfo: Flow<CurrentDayInfo> = simulatedDate.map { computeCurrentDayInfo(it) }

    val mcqLogs: Flow<List<McqLogEntity>> = mcqLogDao.observeAll()
    val focusLogs: Flow<List<FocusLogEntity>> = focusLogDao.observeAll()
    val notes: Flow<Map<Int, String>> = dayNoteDao.observeAll().map { rows -> rows.associate { it.day to it.note } }

    val overallStats: Flow<OverallStats> =
        combine(completions, currentDayInfo) { c, info -> computeOverallStats(c, info.dayNum) }

    val subjectStats: Flow<List<SubjectStat>> = completions.map { computeSubjectStats(it) }

    val todayTasks: Flow<List<TaskItem>> = currentDayInfo.map { RoutineDataGenerator.getDayTasks(it.data) }

    val revisionDue: Flow<List<RevisionDueItem>> = currentDayInfo.map { computeRevisionDue(it.dayNum) }

    val missedPastTasks: Flow<List<MissedDayTasks>> =
        combine(completions, currentDayInfo) { c, info -> computeMissedPastTasks(c, info.dayNum) }

    val currentQuote: Flow<String> = currentDayInfo.map { info ->
        RoutineDataGenerator.MOTIVATIONAL_QUOTES[(info.dayNum - 1) % RoutineDataGenerator.MOTIVATIONAL_QUOTES.size]
    }

    suspend fun dayTasksWithCompletion(day: Int): Pair<DayPlan, List<TaskItem>>? {
        val dayData = RoutineDataGenerator.ROUTINE_DATA.find { it.day == day } ?: return null
        return dayData to RoutineDataGenerator.getDayTasks(dayData)
    }

    /** Flips [taskId]'s completion and reports whether *today's* full task set is now complete. */
    suspend fun toggleTask(taskId: String): Boolean {
        val current = taskCompletionDao.getAllOnce().associate { it.taskId to it.completed }
        val newValue = !(current[taskId] ?: false)
        taskCompletionDao.upsert(TaskCompletionEntity(taskId, newValue, System.currentTimeMillis()))

        val info = computeCurrentDayInfo(settings.simulatedDateOnce())
        val updated = current + (taskId to newValue)
        val today = RoutineDataGenerator.getDayTasks(info.data)
        WidgetRefresher.refreshAll(context)
        return today.isNotEmpty() && today.all { updated[it.id] == true }
    }

    suspend fun noteForDay(day: Int): String = dayNoteDao.getForDay(day)?.note.orEmpty()

    suspend fun saveNote(day: Int, note: String) {
        dayNoteDao.upsert(DayNoteEntity(day, note))
    }

    suspend fun addMcqLog(attempted: Int, correct: Int) {
        val wrong = attempted - correct
        val accuracy = if (attempted > 0) ((correct * 100.0) / attempted).let(Math::round).toInt() else 0
        mcqLogDao.insert(
            McqLogEntity(
                dateIso = Instant.now().toString(),
                attempted = attempted,
                correct = correct,
                wrong = wrong,
                accuracy = accuracy,
            ),
        )
    }

    suspend fun addFocusLog(minutes: Int) {
        focusLogDao.insert(FocusLogEntity(dateIso = Instant.now().toString(), minutes = minutes))
    }

    suspend fun setSimulatedDate(dateIso: String?) {
        settings.setSimulatedDate(dateIso)
        WidgetRefresher.refreshAll(context)
    }

    suspend fun resetToDayOne() {
        settings.setSimulatedDate(null)
        WidgetRefresher.refreshAll(context)
    }

    suspend fun resetAllData() {
        taskCompletionDao.clearAll()
        mcqLogDao.clearAll()
        focusLogDao.clearAll()
        dayNoteDao.clearAll()
        settings.setSimulatedDate(null)
        WidgetRefresher.refreshAll(context)
    }

    /** Same four keys the web app's "Backup & Export Progress" writes: completions, mcqLogs, focusLogs, notes. */
    suspend fun exportBackupJson(): String {
        val root = JSONObject()

        val completionsObj = JSONObject()
        taskCompletionDao.getAllOnce().forEach { completionsObj.put(it.taskId, it.completed) }
        root.put("completions", completionsObj)

        val mcqArray = JSONArray()
        mcqLogDao.getAllOnce().forEach {
            mcqArray.put(
                JSONObject()
                    .put("date", it.dateIso)
                    .put("attempted", it.attempted)
                    .put("correct", it.correct)
                    .put("wrong", it.wrong)
                    .put("accuracy", it.accuracy),
            )
        }
        root.put("mcqLogs", mcqArray)

        val focusArray = JSONArray()
        focusLogDao.getAllOnce().forEach {
            focusArray.put(JSONObject().put("date", it.dateIso).put("mins", it.minutes))
        }
        root.put("focusLogs", focusArray)

        val notesObj = JSONObject()
        dayNoteDao.getAllOnce().forEach { notesObj.put("day_${it.day}", it.note) }
        root.put("notes", notesObj)

        return root.toString(2)
    }

    /** Restores a backup written by [exportBackupJson]. Replaces existing local data entirely. */
    suspend fun importBackupJson(json: String) {
        val root = JSONObject(json)

        taskCompletionDao.clearAll()
        root.optJSONObject("completions")?.let { obj ->
            obj.keys().forEach { key ->
                taskCompletionDao.upsert(TaskCompletionEntity(key, obj.optBoolean(key), System.currentTimeMillis()))
            }
        }

        mcqLogDao.clearAll()
        root.optJSONArray("mcqLogs")?.let { arr ->
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                mcqLogDao.insert(
                    McqLogEntity(
                        dateIso = o.optString("date", Instant.now().toString()),
                        attempted = o.optInt("attempted"),
                        correct = o.optInt("correct"),
                        wrong = o.optInt("wrong"),
                        accuracy = o.optInt("accuracy"),
                    ),
                )
            }
        }

        focusLogDao.clearAll()
        root.optJSONArray("focusLogs")?.let { arr ->
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                focusLogDao.insert(
                    FocusLogEntity(
                        dateIso = o.optString("date", Instant.now().toString()),
                        minutes = o.optInt("mins"),
                    ),
                )
            }
        }

        dayNoteDao.clearAll()
        root.optJSONObject("notes")?.let { obj ->
            obj.keys().forEach { key ->
                val day = key.removePrefix("day_").toIntOrNull()
                if (day != null) dayNoteDao.upsert(DayNoteEntity(day, obj.optString(key)))
            }
        }

        WidgetRefresher.refreshAll(context)
    }

    companion object {
        fun computeCurrentDayInfo(simulatedDateIso: String?): CurrentDayInfo {
            val start = LocalDate.parse(RoutineDataGenerator.START_DATE)
            val current = simulatedDateIso
                ?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
                ?: LocalDate.now()
            val diffDays = ChronoUnit.DAYS.between(start, current).toInt() + 1
            val dayNum = diffDays.coerceIn(1, RoutineDataGenerator.TOTAL_DAYS)
            val remaining = (RoutineDataGenerator.TOTAL_DAYS - dayNum).coerceAtLeast(0)
            val data = RoutineDataGenerator.ROUTINE_DATA.find { it.day == dayNum } ?: RoutineDataGenerator.ROUTINE_DATA[0]
            return CurrentDayInfo(dayNum, remaining, data)
        }

        fun computeOverallStats(completions: Map<String, Boolean>, dayNum: Int): OverallStats {
            val totalTasks = RoutineDataGenerator.TOTAL_TASKS
            val completedCount = completions.values.count { it }
            val overallPercent = if (totalTasks > 0) {
                Math.round((completedCount * 100.0) / totalTasks).toInt()
            } else {
                0
            }

            var streak = 0
            var longestStreak = 0
            var tempStreak = 0

            for (i in 1..dayNum) {
                val dayObj = RoutineDataGenerator.ROUTINE_DATA.find { it.day == i } ?: continue
                val done = RoutineDataGenerator.getDayTasks(dayObj).count { completions[it.id] == true }
                if (done >= 4) {
                    tempStreak++
                    if (i == dayNum || tempStreak > streak) streak = tempStreak
                    if (tempStreak > longestStreak) longestStreak = tempStreak
                } else if (i < dayNum) {
                    tempStreak = 0
                }
            }

            return OverallStats(completedCount, totalTasks, overallPercent, streak, longestStreak)
        }

        private val subjectMeta = listOf(
            Triple("Bengali", "বাংলা (Bengali)", null),
            Triple("English", "English Grammar & EFT", null),
            Triple("Vocabulary", "Vocabulary & Birachon", null),
            Triple("GK", "GK (সাধারণ জ্ঞান)", null),
            Triple("Written", "Written Practice", null),
            Triple("MCQ", "MCQ & Revisions", null),
        )

        fun computeSubjectStats(completions: Map<String, Boolean>): List<SubjectStat> {
            val totals = linkedMapOf("Bengali" to 0, "English" to 0, "Vocabulary" to 0, "GK" to 0, "Written" to 0, "MCQ" to 0)
            val done = linkedMapOf("Bengali" to 0, "English" to 0, "Vocabulary" to 0, "GK" to 0, "Written" to 0, "MCQ" to 0)

            RoutineDataGenerator.ROUTINE_DATA.forEach { day ->
                RoutineDataGenerator.getDayTasks(day).forEach { t ->
                    val cat = when (t.category) {
                        "English" -> "English"
                        "Vocabulary", "Birachon" -> "Vocabulary"
                        "GK" -> "GK"
                        "Written" -> "Written"
                        "MCQ" -> "MCQ"
                        else -> "Bengali"
                    }
                    totals[cat] = (totals[cat] ?: 0) + 1
                    if (completions[t.id] == true) done[cat] = (done[cat] ?: 0) + 1
                }
            }

            return subjectMeta.map { (key, title, _) ->
                val total = totals[key] ?: 0
                val doneCount = done[key] ?: 0
                val pct = if (total > 0) Math.round((doneCount * 100.0) / total).toInt() else 0
                SubjectStat(key = key, title = title, pct = pct, completed = doneCount, total = total)
            }
        }

        fun computeRevisionDue(dayNum: Int): List<RevisionDueItem> {
            return listOf(1, 3, 7).mapNotNull { offset ->
                val pastDay = dayNum - offset
                if (pastDay < 1) return@mapNotNull null
                val pastDayObj = RoutineDataGenerator.ROUTINE_DATA.find { it.day == pastDay } ?: return@mapNotNull null
                RevisionDueItem(offset, pastDay, pastDayObj.topic, pastDayObj.mainSubject)
            }
        }

        fun computeMissedPastTasks(completions: Map<String, Boolean>, dayNum: Int): List<MissedDayTasks> {
            val missed = mutableListOf<MissedDayTasks>()
            for (i in 1 until dayNum) {
                val dayData = RoutineDataGenerator.ROUTINE_DATA.find { it.day == i } ?: continue
                val uncompleted = RoutineDataGenerator.getDayTasks(dayData).filter { completions[it.id] != true }
                if (uncompleted.isNotEmpty()) missed.add(MissedDayTasks(dayData, uncompleted))
            }
            return missed
        }
    }
}
