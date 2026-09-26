package com.aj75.app.ui.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aj75.app.ui.components.ColorDot
import com.aj75.app.ui.components.GlassCard
import com.aj75.app.ui.components.ThinProgressBar
import com.aj75.app.ui.theme.Amber500
import com.aj75.app.ui.theme.BrandPrimary
import com.aj75.app.ui.theme.Cyan500
import com.aj75.app.ui.theme.Emerald500
import com.aj75.app.ui.theme.Pink500
import com.aj75.app.ui.theme.Purple500
import com.aj75.app.util.BengaliFormat

private val subjectColors = mapOf(
    "Bengali" to BrandPrimary,
    "English" to Purple500,
    "Vocabulary" to Amber500,
    "GK" to Emerald500,
    "Written" to Pink500,
    "MCQ" to Cyan500,
)

@Composable
fun ProgressScreen(viewModel: ProgressViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Text("বিষয়ভিত্তিক অগ্রগতি", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        }

        item {
            GlassCard {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    OverallStatColumn("${state.overall.overallPercent}%", "মোট সম্পন্ন")
                    OverallStatColumn(BengaliFormat.toBengaliNum(state.overall.streak), "বর্তমান স্ট্রিক")
                    OverallStatColumn(BengaliFormat.toBengaliNum(state.overall.longestStreak), "সেরা স্ট্রিক")
                }
                Text(
                    "${state.overall.completedCount} / ${state.overall.totalTasks} টাস্ক সম্পন্ন",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 10.dp),
                )
            }
        }

        item {
            Text("Subject Performance Breakdown", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        items(state.subjects, key = { it.key }) { subject ->
            val color = subjectColors[subject.key] ?: BrandPrimary
            GlassCard {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ColorDot(color)
                        Text(subject.title, modifier = Modifier.padding(start = 8.dp), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
                    }
                    Text("${subject.pct}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
                }
                ThinProgressBar(percent = subject.pct, color = color, modifier = Modifier.padding(top = 10.dp))
                Text(
                    "${subject.completed} / ${subject.total}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun OverallStatColumn(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onBackground)
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
