package com.aj75.app.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aj75.app.ui.components.DayDetailSheet
import com.aj75.app.ui.components.GlassCard
import com.aj75.app.ui.components.GlassPill
import com.aj75.app.ui.components.ProgressRing
import com.aj75.app.ui.theme.Amber400
import com.aj75.app.ui.theme.BrandSuccess
import com.aj75.app.util.BengaliFormat
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    onNavigateToTab: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val selectedDay by viewModel.selectedDay.collectAsState()
    val showCelebration by viewModel.showCelebration.collectAsState()

    LaunchedEffect(showCelebration) {
        if (showCelebration) {
            delay(1800)
            viewModel.dismissCelebration()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(BrandSuccess),
                )
                Text(
                    "  Admission",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            GlassCard {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    GlassPill {
                        Text(
                            "Day ${BengaliFormat.toBengaliNum(state.dayNum)} of ${BengaliFormat.toBengaliNum(75)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Box(modifier = Modifier.padding(vertical = 12.dp)) {
                        ProgressRing(
                            percent = state.overallPercent,
                            centerTop = BengaliFormat.toBengaliNum(state.remaining),
                            centerBottom = "দিন বাকি",
                            centerSub = "${state.overallPercent}% Completed",
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        StatChip(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Filled.LocalFireDepartment,
                            tint = Amber400,
                            label = "${BengaliFormat.toBengaliNum(state.streak)} Day Streak",
                        )
                        StatChip(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Filled.CheckCircle,
                            tint = BrandSuccess,
                            label = "${state.todayTasks.count { state.completions[it.id] == true }}/${state.todayTasks.size} আজ সম্পন্ন",
                        )
                    }
                }
            }

            GlassCard {
                Text(
                    state.quote,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            GlassCard {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("আজকের Tasks", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp)
                    Text(
                        "${state.todayTasks.count { state.completions[it.id] == true }}/${state.todayTasks.size} DONE",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Column(modifier = Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    state.todayTasks.forEach { task ->
                        val done = state.completions[task.id] == true
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (done) BrandSuccess.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface)
                                .clickable { viewModel.openDay(state.dayNum) }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                if (done) Icons.Filled.CheckCircle else Icons.Filled.Circle,
                                contentDescription = null,
                                tint = if (done) BrandSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { viewModel.toggleTask(task.id) },
                            )
                            Text(
                                task.title,
                                modifier = Modifier.padding(start = 10.dp),
                                fontSize = 12.sp,
                                color = if (done) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onBackground,
                            )
                        }
                    }
                }
            }

            if (state.revisionDue.isNotEmpty()) {
                GlassCard {
                    Text("Spaced Repetition — Revision Due", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground)
                    Column(modifier = Modifier.padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        state.revisionDue.forEach { item ->
                            Text(
                                "Day ${BengaliFormat.toBengaliNum(item.dayNum)} (${item.offset} দিন আগে): ${item.mainSubject} — ${item.topic}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showCelebration,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center),
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 28.dp, vertical = 18.dp),
            ) {
                Text(
                    "🎉 আজকের সব Task সম্পন্ন! দারুণ ধারাবাহিকতা!",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                )
            }
        }
    }

    selectedDay?.let { day ->
        DayDetailSheet(
            day = day,
            tasks = state.todayTasks,
            completions = state.completions,
            note = state.notes[day].orEmpty(),
            onDismiss = { viewModel.closeDay() },
            onToggleTask = { viewModel.toggleTask(it) },
            onSaveNote = { note -> viewModel.saveNote(day, note) },
        )
    }
}

@Composable
private fun StatChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    label: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
        Text(label, modifier = Modifier.padding(start = 6.dp), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
