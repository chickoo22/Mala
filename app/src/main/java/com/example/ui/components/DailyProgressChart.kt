package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.DailySummary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class ChartDayData(
    val dateStr: String,
    val dayLabel: String,
    val isToday: Boolean,
    val jaapCount: Int,
    val malasCount: Int
)

@Composable
fun DailyProgressChart(
    dailySummaries: List<DailySummary>,
    dailyGoalJaaps: Int,
    daysCount: Int = 7,
    modifier: Modifier = Modifier
) {
    // Generate the last N days with continuous data
    val chartDays = remember(dailySummaries, daysCount) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val summaryMap = dailySummaries.associateBy { it.dateStr }

        val list = mutableListOf<ChartDayData>()
        val cal = Calendar.getInstance()
        val todayStr = sdf.format(cal.time)

        for (i in (daysCount - 1) downTo 0) {
            val dayCal = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -i)
            }
            val dateStr = sdf.format(dayCal.time)
            val dayLabel = if (i == 0) "Today" else dayFormat.format(dayCal.time)
            val summary = summaryMap[dateStr]

            list.add(
                ChartDayData(
                    dateStr = dateStr,
                    dayLabel = dayLabel,
                    isToday = (dateStr == todayStr),
                    jaapCount = summary?.totalJaaps ?: 0,
                    malasCount = summary?.totalMalas ?: 0
                )
            )
        }
        list
    }

    var selectedDayIndex by remember { mutableStateOf<Int?>(null) }
    val maxCount = remember(chartDays, dailyGoalJaaps) {
        val maxInDays = chartDays.maxOfOrNull { it.jaapCount } ?: 0
        maxOf(maxInDays, dailyGoalJaaps, 108) * 1.15f
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val outlineColor = MaterialTheme.colorScheme.outline
    val goalLineColor = Color(0xFFFFB300)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_progress_chart_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header with Goal and Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Daily Jaap Activity",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "Goal: $dailyGoalJaaps Jaaps / Day",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = goalLineColor.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(goalLineColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Goal Line",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = goalLineColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selected Day Info Banner
            val selectedDay = selectedDayIndex?.let { chartDays.getOrNull(it) }
            if (selectedDay != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = primaryColor.copy(alpha = 0.1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${selectedDay.dayLabel} (${selectedDay.dateStr})",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                        )
                        Text(
                            text = "${selectedDay.jaapCount} Jaaps • ${selectedDay.malasCount} Malas",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }

            // Canvas Chart Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .pointerInput(chartDays) {
                            detectTapGestures { offset ->
                                val availableWidth = size.width
                                val barSlotWidth = availableWidth / chartDays.size
                                val tappedIndex = (offset.x / barSlotWidth).toInt().coerceIn(0, chartDays.size - 1)
                                selectedDayIndex = if (selectedDayIndex == tappedIndex) null else tappedIndex
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height
                    val bottomPadding = 30.dp.toPx()
                    val topPadding = 16.dp.toPx()
                    val chartHeight = height - bottomPadding - topPadding

                    val barSlotWidth = width / chartDays.size
                    val barWidth = barSlotWidth * 0.48f

                    // Draw Horizontal Goal Line
                    if (dailyGoalJaaps > 0) {
                        val goalY = height - bottomPadding - (dailyGoalJaaps.toFloat() / maxCount) * chartHeight
                        drawLine(
                            color = goalLineColor.copy(alpha = 0.8f),
                            start = Offset(0f, goalY),
                            end = Offset(width, goalY),
                            strokeWidth = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )
                    }

                    // Draw Base Axis
                    drawLine(
                        color = outlineColor.copy(alpha = 0.3f),
                        start = Offset(0f, height - bottomPadding),
                        end = Offset(width, height - bottomPadding),
                        strokeWidth = 1.dp.toPx()
                    )

                    // Draw Bars
                    chartDays.forEachIndexed { index, day ->
                        val barCenterX = (index * barSlotWidth) + (barSlotWidth / 2f)
                        val barHeight = ((day.jaapCount.toFloat() / maxCount) * chartHeight).coerceAtLeast(4.dp.toPx())
                        val barTopY = height - bottomPadding - barHeight

                        val isGoalMet = day.jaapCount >= dailyGoalJaaps && dailyGoalJaaps > 0
                        val isSelected = selectedDayIndex == index

                        // Bar background track (subtle)
                        drawRoundRect(
                            color = surfaceVariant.copy(alpha = 0.4f),
                            topLeft = Offset(barCenterX - barWidth / 2f, topPadding),
                            size = Size(barWidth, chartHeight),
                            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                        )

                        // Active Bar Fill with Gradient
                        val barBrush = when {
                            isSelected -> Brush.verticalGradient(
                                colors = listOf(Color(0xFFFFB300), primaryColor)
                            )
                            isGoalMet -> Brush.verticalGradient(
                                colors = listOf(Color(0xFFFFC107), primaryColor)
                            )
                            day.jaapCount > 0 -> Brush.verticalGradient(
                                colors = listOf(primaryColor, primaryColor.copy(alpha = 0.7f))
                            )
                            else -> Brush.verticalGradient(
                                colors = listOf(surfaceVariant, surfaceVariant)
                            )
                        }

                        drawRoundRect(
                            brush = barBrush,
                            topLeft = Offset(barCenterX - barWidth / 2f, barTopY),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                        )

                        // If goal met, draw small golden check star dot on top
                        if (isGoalMet) {
                            drawCircle(
                                color = Color(0xFFFFB300),
                                radius = 3.5.dp.toPx(),
                                center = Offset(barCenterX, barTopY - 7.dp.toPx())
                            )
                        }
                    }
                }
            }

            // X-Axis Day Labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                chartDays.forEachIndexed { index, day ->
                    val isSelected = selectedDayIndex == index
                    Text(
                        text = day.dayLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (day.isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = when {
                                isSelected -> primaryColor
                                day.isToday -> primaryColor
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}
