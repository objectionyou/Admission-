package com.aj75.app.ui.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aj75.app.ui.components.GlassCard
import com.aj75.app.ui.theme.BrandSuccess
import com.aj75.app.util.BengaliFormat

@Composable
fun ToolsScreen(viewModel: ToolsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("স্টাডি টুলস ও টাইমার", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)

        GlassCard {
            Text("Focus Timer", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TIMER_PRESETS.forEach { minutes ->
                    val selected = minutes == state.presetMinutes
                    Text(
                        "${minutes}m",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (selected) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                            .clickable { viewModel.selectPreset(minutes) }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                    )
                }
            }

            val mm = state.remainingSeconds / 60
            val ss = state.remainingSeconds % 60
            Text(
                String.format("%02d:%02d", mm, ss),
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { if (state.isRunning) viewModel.pause() else viewModel.start() },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(if (state.isRunning) "Pause" else "Start")
                }
                OutlinedButton(onClick = { viewModel.reset() }, modifier = Modifier.weight(1f)) {
                    Text("Reset")
                }
            }

            if (state.recentFocusLogs.isNotEmpty()) {
                Text(
                    "সাম্প্রতিক সেশন: " + state.recentFocusLogs.joinToString(", ") { "${it.minutes}m" },
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 10.dp),
                )
            }
        }

        GlassCard {
            Text("MCQ Accuracy Log", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)

            var attempted by remember { mutableStateOf("") }
            var correct by remember { mutableStateOf("") }

            Row(modifier = Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = attempted,
                    onValueChange = { attempted = it.filter(Char::isDigit) },
                    label = { Text("Attempted") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                )
                OutlinedTextField(
                    value = correct,
                    onValueChange = { correct = it.filter(Char::isDigit) },
                    label = { Text("Correct") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                )
            }
            Button(
                onClick = {
                    val a = attempted.toIntOrNull() ?: 0
                    val c = correct.toIntOrNull() ?: 0
                    if (a > 0 && c in 0..a) {
                        viewModel.logMcq(a, c)
                        attempted = ""
                        correct = ""
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            ) {
                Text("Log Session")
            }

            if (state.recentMcqLogs.isNotEmpty()) {
                Column(modifier = Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    state.recentMcqLogs.forEach { log ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${log.correct}/${log.attempted} সঠিক", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                "${BengaliFormat.toBengaliNum(log.accuracy)}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (log.accuracy >= 70) BrandSuccess else MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        }
    }
}
