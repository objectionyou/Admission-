package com.aj75.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskCompletionDao {
    @Query("SELECT * FROM task_completions")
    fun observeAll(): Flow<List<TaskCompletionEntity>>

    @Upsert
    suspend fun upsert(entity: TaskCompletionEntity)

    @Query("DELETE FROM task_completions")
    suspend fun clearAll()

    @Query("SELECT * FROM task_completions")
    suspend fun getAllOnce(): List<TaskCompletionEntity>
}

@Dao
interface McqLogDao {
    @Query("SELECT * FROM mcq_logs ORDER BY id DESC")
    fun observeAll(): Flow<List<McqLogEntity>>

    @Insert
    suspend fun insert(entity: McqLogEntity)

    @Query("DELETE FROM mcq_logs")
    suspend fun clearAll()

    @Query("SELECT * FROM mcq_logs ORDER BY id DESC")
    suspend fun getAllOnce(): List<McqLogEntity>
}

@Dao
interface FocusLogDao {
    @Query("SELECT * FROM focus_logs ORDER BY id DESC")
    fun observeAll(): Flow<List<FocusLogEntity>>

    @Insert
    suspend fun insert(entity: FocusLogEntity)

    @Query("DELETE FROM focus_logs")
    suspend fun clearAll()

    @Query("SELECT * FROM focus_logs ORDER BY id DESC")
    suspend fun getAllOnce(): List<FocusLogEntity>
}

@Dao
interface DayNoteDao {
    @Query("SELECT * FROM day_notes")
    fun observeAll(): Flow<List<DayNoteEntity>>

    @Query("SELECT * FROM day_notes WHERE day = :day LIMIT 1")
    suspend fun getForDay(day: Int): DayNoteEntity?

    @Upsert
    suspend fun upsert(entity: DayNoteEntity)

    @Delete
    suspend fun delete(entity: DayNoteEntity)

    @Query("DELETE FROM day_notes")
    suspend fun clearAll()

    @Query("SELECT * FROM day_notes")
    suspend fun getAllOnce(): List<DayNoteEntity>
}
