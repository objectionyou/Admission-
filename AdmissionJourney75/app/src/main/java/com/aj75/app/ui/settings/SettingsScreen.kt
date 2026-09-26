package com.aj75.app.ui.settings

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aj75.app.notifications.ReminderType
import com.aj75.app.ui.components.GlassCard
import com.aj75.app.ui.theme.BrandDanger
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDate

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showResetConfirm by remember { mutableStateOf(false) }

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            val json = viewModel.exportBackupJson()
            runCatching {
                context.contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray()) }
            }
            Toast.makeText(context, "ব্যাকআপ Export হয়েছে", Toast.LENGTH_SHORT).show()
        }
    }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val json = runCatching {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BufferedReader(InputStreamReader(stream)).readText()
            }
        }.getOrNull()
        if (json != null) {
            viewModel.importBackupJson(json) { ok ->
                Toast.makeText(context, if (ok) "ব্যাকআপ Import হয়েছে" else "Import ব্যর্থ হয়েছে", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("অ্যাপ সেটিংস", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)

        GlassCard {
            Text("Notification Preferences", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
            Column(modifier = Modifier.padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ReminderType.entries.forEach { type ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(type.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
                            Text(
                                "${String.format("%02d:%02d", type.hour, type.minute)} — ${type.message}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Switch(
                            checked = state.reminderEnabled[type] ?: true,
                            onCheckedChange = { viewModel.setReminderEnabled(type, it) },
                        )
                    }
                }
            }
            OutlinedButton(
                onClick = { viewModel.sendTestNotification(ReminderType.MORNING) },
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
            ) {
                Text("Test Notification Alert")
            }
        }

        GlassCard {
            Text("Date Simulator (Testing)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
            Text(
                "বর্তমানে: " + (state.simulatedDate?.let { "Simulated — $it" } ?: "Real device date ব্যবহার হচ্ছে"),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 10.dp),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = {
                        val base = state.simulatedDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() } ?: LocalDate.now()
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth -> viewModel.setSimulatedDate(LocalDate.of(year, month + 1, dayOfMonth)) },
                            base.year,
                            base.monthValue - 1,
                            base.dayOfMonth,
                        ).show()
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("তারিখ বদলাও")
                }
                OutlinedButton(onClick = { viewModel.resetToRealDate() }, modifier = Modifier.weight(1f)) {
                    Text("Real Date-এ ফিরুন")
                }
            }
        }

        GlassCard {
            Text("Data Management", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
            Column(modifier = Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { exportLauncher.launch("admission_journey_backup_${LocalDate.now()}.json") },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Backup & Export Progress")
                }
                OutlinedButton(
                    onClick = { importLauncher.launch(arrayOf("application/json")) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Restore from Backup")
                }
                Button(
                    onClick = { showResetConfirm = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandDanger),
                ) {
                    Text("Reset All Data")
                }
            }
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("সব ডেটা Reset করবেন?") },
            text = { Text("সব Task completion, Note, MCQ ও Focus log স্থায়ীভাবে মুছে যাবে। এই কাজটি ফেরানো যাবে না।") },
            confirmButton = {
                TextButton(onClick = { viewModel.resetAllData(); showResetConfirm = false }) {
                    Text("হ্যাঁ, Reset করো", color = BrandDanger)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) { Text("বাতিল") }
            },
        )
    }
}
