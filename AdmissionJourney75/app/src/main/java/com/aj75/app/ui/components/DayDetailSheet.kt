package com.aj75.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aj75.app.data.RoutineDataGenerator
import com.aj75.app.data.model.TaskItem
import com.aj75.app.ui.theme.Amber300
import com.aj75.app.ui.theme.BrandSuccess
import com.aj75.app.util.BengaliFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDetailSheet(
    day: Int,
    tasks: List<TaskItem>,
    completions: Map<String, Boolean>,
    note: String,
    onDismiss: () -> Unit,
    onToggleTask: (String) -> Unit,
    onSaveNote: (String) -> Unit,
) {
    val dayPlan = remember(day) { RoutineDataGenerator.ROUTINE_DATA.find { it.day == day } }
    var noteText by remember(day) { mutableStateOf(note) }
    var viewingTask by remember(day) { mutableStateOf<TaskItem?>(null) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
        ) {
            Text(
                "DAY ${BengaliFormat.toBengaliNum(day)}" +
                    (dayPlan?.let { " — ${BengaliFormat.formatBengaliDate(it.date)}" } ?: ""),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
            )
            if (dayPlan != null) {
                Text(
                    "${dayPlan.mainSubject}: ${dayPlan.topic}",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }

            viewingTask?.let { task ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(task.category, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text(
                            "সব টাস্ক দেখুন",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp,
                            modifier = Modifier.clickable { viewingTask = null },
                        )
                    }
                    Text(task.title, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(10.dp),
                    ) {
                        Text("🎯 আজকের Target: ${task.detail}", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Amber300.copy(alpha = 0.1f))
                            .padding(10.dp),
                    ) {
                        Text(
                            "💡 কেন এই পড়াটি গুরুত্বপূর্ণ: ${RoutineDataGenerator.getTaskImportance(task.title)}",
                            color = Amber300,
                            fontSize = 12.sp,
                        )
                    }
                    val done = completions[task.id] == true
                    Button(
                        onClick = { onToggleTask(task.id) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (done) BrandSuccess else MaterialTheme.colorScheme.primary,
                        ),
                    ) {
                        Icon(
                            if (done) Icons.Filled.CheckCircle else Icons.Filled.Circle,
                            contentDescription = null,
                            modifier = Modifier.height(16.dp),
                        )
                        Text(if (done) " সম্পন্ন হয়েছে!" else " Mark as Completed")
                    }
                }
            }

            Text(
                "Daily Task Routine Checklist",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            )
            LazyColumn(modifier = Modifier.height(240.dp)) {
                items(tasks, key = { it.id }) { t ->
                    val done = completions[t.id] == true
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (done) BrandSuccess.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface)
                            .clickable { viewingTask = t }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(
                                if (done) Icons.Filled.CheckCircle else Icons.Filled.Circle,
                                contentDescription = null,
                                tint = if (done) BrandSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .height(18.dp)
                                    .clickable { onToggleTask(t.id) },
                            )
                            Column {
                                Text(t.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                                Text(t.detail, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                            }
                        }
                    }
                }
            }

            Text(
                "My Daily Study Notes",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            )
            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                placeholder = { Text("এই দিনের কোনো বিশেষ নোট বা সমস্যা লিখে রাখুন...") },
            )
            Button(
                onClick = { onSaveNote(noteText); onDismiss() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
            ) {
                Text("Save Note & Close")
            }
        }
    }
}
