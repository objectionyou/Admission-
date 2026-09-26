package com.aj75.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        TaskCompletionEntity::class,
        McqLogEntity::class,
        FocusLogEntity::class,
        DayNoteEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskCompletionDao(): TaskCompletionDao
    abstract fun mcqLogDao(): McqLogDao
    abstract fun focusLogDao(): FocusLogDao
    abstract fun dayNoteDao(): DayNoteDao

    companion object {
        const val DATABASE_NAME = "admission_journey_75.db"
    }
}
