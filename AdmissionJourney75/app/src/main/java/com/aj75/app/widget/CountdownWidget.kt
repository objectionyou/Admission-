package com.aj75.app.widget

import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.aj75.app.MainActivity
import com.aj75.app.R
import com.aj75.app.util.BengaliFormat
import kotlinx.coroutines.flow.first

class CountdownWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = widgetRepository(context)
        val dayInfo = repository.currentDayInfo.first()
        val stats = repository.overallStats.first()
        val openAppIntent = Intent(context, MainActivity::class.java)

        provideContent {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(ImageProvider(R.drawable.widget_background))
                    .padding(14.dp)
                    .clickable(actionStartActivity(openAppIntent)),
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                verticalAlignment = Alignment.Vertical.CenterVertically,
            ) {
                Text(
                    "DAY ${BengaliFormat.toBengaliNum(dayInfo.dayNum)} / ${BengaliFormat.toBengaliNum(75)}",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFA5B4FC)),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                    ),
                )
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    BengaliFormat.toBengaliNum(dayInfo.remaining),
                    style = TextStyle(color = ColorProvider(Color.White), fontWeight = FontWeight.Bold, fontSize = 30.sp),
                )
                Text(
                    "দিন বাকি",
                    style = TextStyle(color = ColorProvider(Color(0xFF94A3B8)), fontSize = 10.sp),
                )
                Spacer(modifier = GlanceModifier.height(8.dp))
                Row(verticalAlignment = Alignment.Vertical.CenterVertically) {
                    Text(
                        "${stats.overallPercent}% Complete",
                        style = TextStyle(color = ColorProvider(Color(0xFF818CF8)), fontWeight = FontWeight.Bold, fontSize = 11.sp),
                    )
                    Spacer(modifier = GlanceModifier.width(8.dp))
                    Text(
                        "🔥 ${BengaliFormat.toBengaliNum(stats.streak)}",
                        style = TextStyle(color = ColorProvider(Color(0xFFFBBF24)), fontWeight = FontWeight.Bold, fontSize = 11.sp),
                    )
                }
            }
        }
    }
}
