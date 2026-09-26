package com.aj75.app.widget

import android.content.Context
import androidx.glance.appwidget.updateAll

object WidgetRefresher {
    suspend fun refreshAll(context: Context) {
        runCatching { CountdownWidget().updateAll(context) }
        runCatching { TasksWidget().updateAll(context) }
    }
}
