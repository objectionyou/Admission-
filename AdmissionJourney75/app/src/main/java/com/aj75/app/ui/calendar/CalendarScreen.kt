package com.aj75.app.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aj75.app.data.RoutineDataGenerator
import com.aj75.app.ui.components.DayDetailSheet
import com.aj75.app.ui.theme.BrandPrimary
import com.aj75.app.ui.theme.BrandSuccess
import com.aj75.app.ui.theme.Slate700
import com.aj75.app.ui.theme.Slate800
import com.aj75.app.util.BengaliFormat

@Composable
fun CalendarScreen(viewModel: CalendarViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val selectedDay by viewModel.selectedDay.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("৭৫ দিনের ক্যালেন্ডার ম্যাপ", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Text(
            "সবুজ = সম্পন্ন, বেগুনি = আংশিক, ধূসর = বাকি",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp, bottom = 14.dp),
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(RoutineDataGenerator.ROUTINE_DATA, key = { it.day }) { day ->
                val pct = state.completionPctByDay[day.day] ?: 0
                val isToday = day.day == state.dayNum
                val cellColor = when {
                    pct == 100 -> BrandSuccess.copy(alpha = 0.85f)
                    pct > 0 -> BrandPrimary.copy(alpha = 0.55f)
                    else -> Slate800
                }
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(cellColor)
                        .border(
                            width = if (isToday) 2.dp else 0.dp,
                            color = if (isToday) MaterialTheme.colorScheme.primary else Slate700,
                            shape = RoundedCornerShape(10.dp),
                        )
                        .clickable { viewModel.openDay(day.day) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        BengaliFormat.toBengaliNum(day.day),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }
    }

    selectedDay?.let { day ->
        val tasks = RoutineDataGenerator.getDayTasks(RoutineDataGenerator.ROUTINE_DATA.first { it.day == day })
        DayDetailSheet(
            day = day,
            tasks = tasks,
            completions = state.completions,
            note = state.notes[day].orEmpty(),
            onDismiss = { viewModel.closeDay() },
            onToggleTask = { viewModel.toggleTask(it) },
            onSaveNote = { note -> viewModel.saveNote(day, note) },
        )
    }
}
