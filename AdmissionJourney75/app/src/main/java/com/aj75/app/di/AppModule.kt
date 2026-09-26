package com.aj75.app.di

import android.content.Context
import androidx.room.Room
import com.aj75.app.data.local.AppDatabase
import com.aj75.app.data.local.DayNoteDao
import com.aj75.app.data.local.FocusLogDao
import com.aj75.app.data.local.McqLogDao
import com.aj75.app.data.local.TaskCompletionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME).build()

    @Provides
    fun provideTaskCompletionDao(db: AppDatabase): TaskCompletionDao = db.taskCompletionDao()

    @Provides
    fun provideMcqLogDao(db: AppDatabase): McqLogDao = db.mcqLogDao()

    @Provides
    fun provideFocusLogDao(db: AppDatabase): FocusLogDao = db.focusLogDao()

    @Provides
    fun provideDayNoteDao(db: AppDatabase): DayNoteDao = db.dayNoteDao()
}
