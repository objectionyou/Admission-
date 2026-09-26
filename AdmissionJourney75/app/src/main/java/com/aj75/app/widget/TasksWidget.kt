package com.aj75.app.widget

import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.aj75.app.MainActivity
import com.aj75.app.R
import com.aj75.app.data.RoutineDataGenerator
import kotlinx.coroutines.flow.first

private val TASK_ID_KEY = ActionParameters.Key<String>("task_id")

class ToggleTaskAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val taskId = parameters[TASK_ID_KEY] ?: return
        val repository = widgetRepository(context)
        repository.toggleTask(taskId)
        TasksWidget().update(context, glanceId)
        CountdownWidget().updateAll(context)
    }
}

class TasksWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = widgetRepository(context)
        val dayInfo = repository.currentDayInfo.first()
        val completions = repository.completions.first()
        val tasks = RoutineDataGenerator.getDayTasks(dayInfo.data).take(4)
        val doneCount = tasks.count { completions[it.id] == true }
        val openAppIntent = Intent(context, MainActivity::class.java)

        provideContent {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(ImageProvider(R.drawable.widget_background))
                    .padding(12.dp),
            ) {
                Row(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .clickable(actionStartActivity(openAppIntent)),
                    verticalAlignment = Alignment.Vertical.CenterVertically,
                ) {
                    Text(
                        "আজকের Tasks",
                        style = TextStyle(color = ColorProvider(Color.White), fontWeight = FontWeight.Bold, fontSize = 12.sp),
                    )
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    Text(
                        "$doneCount/${tasks.size} DONE",
                        style = TextStyle(color = ColorProvider(Color(0xFF818CF8)), fontWeight = FontWeight.Bold, fontSize = 10.sp),
                    )
                }
                Spacer(modifier = GlanceModifier.height(6.dp))
                tasks.forEach { task ->
                    val done = completions[task.id] == true
                    Row(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable(actionRunCallback<ToggleTaskAction>(actionParametersOf(TASK_ID_KEY to task.id))),
                        verticalAlignment = Alignment.Vertical.CenterVertically,
                    ) {
                        Text(if (done) "✅" else "⬜", style = TextStyle(fontSize = 13.sp))
                        Spacer(modifier = GlanceModifier.width(6.dp))
                        Text(
                            task.title,
                            style = TextStyle(
                                color = ColorProvider(if (done) Color(0xFF64748B) else Color(0xFFE2E8F0)),
                                fontSize = 11.sp,
                            ),
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}
