package com.aj75.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aj75.app.ui.theme.GlassCardBorder
import com.aj75.app.ui.theme.GlassCardFill

/** The frosted, faintly glossy card used everywhere — soft shadow for lift, a subtle top-to-
 *  bottom sheen standing in for glass, and a light-catching gradient border. */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(24.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 14.dp, shape = shape, ambientColor = Color.Black.copy(alpha = 0.35f), spotColor = Color.Black.copy(alpha = 0.35f))
            .clip(shape)
            .background(Brush.verticalGradient(listOf(Color(0x21FFFFFF), GlassCardFill, GlassCardFill)))
            .border(1.dp, Brush.verticalGradient(listOf(Color(0x4DFFFFFF), GlassCardBorder)), shape)
            .padding(20.dp),
        content = content,
    )
}

/** Small rounded pill, e.g. a day badge or quick-stat chip ("glass-pill"). */
@Composable
fun GlassPill(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Brush.verticalGradient(listOf(Color(0x26FFFFFF), GlassCardFill)))
            .border(1.dp, Brush.verticalGradient(listOf(Color(0x40FFFFFF), GlassCardBorder)), RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        content = content,
    )
}

/** Circular countdown ring — animates smoothly on value change and strokes with a soft gradient
 *  for a glossier finish than a flat color. */
@Composable
fun ProgressRing(
    percent: Int,
    centerTop: String,
    centerBottom: String,
    centerSub: String,
    modifier: Modifier = Modifier,
    trackColor: Color = Color(0xFF1E293B),
    progressColor: Color = MaterialTheme.colorScheme.primary,
    ringSize: Dp = 176.dp,
) {
    val animatedFraction by animateFloatAsState(
        targetValue = percent.coerceIn(0, 100) / 100f,
        animationSpec = tween(durationMillis = 700),
        label = "ringProgress",
    )
    Box(modifier = modifier.size(ringSize), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(ringSize)) {
            val strokeWidth = 16.dp.toPx()
            val diameter = this.size.minDimension - strokeWidth
            val topLeft = Offset((this.size.width - diameter) / 2f, (this.size.height - diameter) / 2f)
            val arcSize = Size(diameter, diameter)
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
            drawArc(
                brush = Brush.sweepGradient(listOf(progressColor.copy(alpha = 0.55f), progressColor, progressColor.copy(alpha = 0.85f))),
                startAngle = -90f,
                sweepAngle = 360f * animatedFraction,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(centerTop, fontSize = 30.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onBackground)
            Text(centerBottom, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(centerSub, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

/** Small colored dot used as a subject-category swatch in Progress and Routine lists. */
@Composable
fun ColorDot(color: Color, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(10.dp).clip(CircleShape).background(color))
}

/** Thin horizontal progress bar used in the Progress screen's subject breakdown — animates and
 *  fills with a light-to-solid gradient for a glossier look than a flat fill. */
@Composable
fun ThinProgressBar(percent: Int, color: Color, modifier: Modifier = Modifier) {
    val animatedFraction by animateFloatAsState(
        targetValue = percent.coerceIn(0, 100) / 100f,
        animationSpec = tween(durationMillis = 700),
        label = "barProgress",
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .size(8.dp)
            .clip(RoundedCornerShape(50))
            .background(Color(0xFF1E293B)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedFraction)
                .size(8.dp)
                .background(Brush.horizontalGradient(listOf(color.copy(alpha = 0.7f), color)), RoundedCornerShape(50)),
        )
    }
}
