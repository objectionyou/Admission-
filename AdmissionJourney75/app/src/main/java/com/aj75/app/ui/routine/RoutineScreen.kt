package com.aj75.app.ui.routine

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.aj75.app.ui.components.GlassCard
import com.aj75.app.ui.components.ThinProgressBar
import com.aj75.app.ui.theme.BrandDanger
import com.aj75.app.ui.theme.BrandSuccess
import com.aj75.app.util.BengaliFormat

@Composable
fun RoutineScreen(viewModel: RoutineViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val selectedDay by viewModel.selectedDay.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("৭৫ দিনের মাস্টার রুটিন", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(RoutineFilter.entries) { f ->
                val selected = f == filter
                Text(
                    f.label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (selected) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                        .clickable { viewModel.setFilter(f) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
            items(state.days, key = { it.day }) { day ->
                val tasks = RoutineDataGenerator.getDayTasks(day)
                val doneCount = tasks.count { state.completions[it.id] == true }
                val pct = if (tasks.isNotEmpty()) (doneCount * 100) / tasks.size else 0
                val isToday = day.day == state.dayNum

                GlassCard(modifier = Modifier.clickable { viewModel.openDay(day.day) }) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(
                                "Day ${BengaliFormat.toBengaliNum(day.day)}" + if (isToday) " • আজ" else "",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                "${day.mainSubject}: ${day.topic}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                            Text(BengaliFormat.formatBengaliDate(day.date), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text("$doneCount/${tasks.size}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (pct == 100) BrandSuccess else MaterialTheme.colorScheme.primary)
                    }
                    ThinProgressBar(
                        percent = pct,
                        color = if (pct == 100) BrandSuccess else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 10.dp),
                    )
                }
            }

            if (state.missed.isNotEmpty()) {
                item {
                    GlassCard {
                        Text("বাদ পড়া টাস্ক", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDanger)
                        Column(modifier = Modifier.padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            state.missed.take(10).forEach { m ->
                                Text(
                                    "Day ${BengaliFormat.toBengaliNum(m.dayData.day)}: ${m.uncompleted.size}টি টাস্ক বাকি",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.clickable { viewModel.openDay(m.dayData.day) },
                                )
                            }
                        }
                    }
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
