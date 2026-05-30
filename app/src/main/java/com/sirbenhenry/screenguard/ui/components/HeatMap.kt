package com.sirbenhenry.screenguard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sirbenhenry.screenguard.data.entity.StreakRecord
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HeatMap(
    records: List<StreakRecord>,
    modifier: Modifier = Modifier
) {
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    val recordMap = records.associateBy { it.dateKey }

    // Build 15 weeks x 7 days grid (105 days back)
    val calendar = Calendar.getInstance()
    // Align to Sunday start
    while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }

    val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val fmtDay = SimpleDateFormat("d", Locale.US)
    val fmtMonth = SimpleDateFormat("MMM", Locale.US)

    // Build weeks from 14 weeks ago to now
    calendar.add(Calendar.WEEK_OF_YEAR, -14)

    val weeks = mutableListOf<List<Pair<String, StreakRecord?>>>()
    repeat(15) {
        val week = mutableListOf<Pair<String, StreakRecord?>>()
        repeat(7) {
            val key = fmt.format(calendar.time)
            week.add(key to recordMap[key])
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        weeks.add(week)
    }

    val dayLabels = listOf("S", "M", "T", "W", "T", "F", "S")

    Column(modifier = modifier) {
        // Day-of-week labels
        Row {
            Spacer(Modifier.width(20.dp))
            dayLabels.forEach { label ->
                Text(
                    label,
                    modifier = Modifier.width(16.dp),
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.width(2.dp))
            }
        }
        Spacer(Modifier.height(4.dp))

        Row {
            // Month labels column
            Column {
                var prevMonth = ""
                weeks.forEach { week ->
                    val firstDay = week.first().first
                    val cal2 = Calendar.getInstance()
                    try {
                        cal2.time = fmt.parse(firstDay)!!
                    } catch (_: Exception) {}
                    val month = fmtMonth.format(cal2.time)
                    val label = if (month != prevMonth) { prevMonth = month; month.take(3) } else ""
                    Text(
                        label,
                        modifier = Modifier.size(width = 20.dp, height = 18.dp),
                        fontSize = 8.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Heatmap grid
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                weeks.forEach { week ->
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        week.forEach { (dateKey, record) ->
                            val isFuture = dateKey > today
                            val isToday = dateKey == today
                            val color = when {
                                isFuture -> Color.Transparent
                                record == null -> Color(0xFF1A2233)
                                record.allUnderLimit -> {
                                    val intensity = (record.totalLimitMinutes - record.totalMinutesUsed)
                                        .toFloat() / record.totalLimitMinutes.coerceAtLeast(1)
                                    Color(
                                        red = 0.1f,
                                        green = 0.3f + 0.5f * intensity,
                                        blue = 0.2f + 0.3f * intensity,
                                        alpha = 1f
                                    )
                                }
                                else -> {
                                    val overRatio = (record.totalMinutesUsed - record.totalLimitMinutes)
                                        .toFloat() / record.totalLimitMinutes.coerceAtLeast(1)
                                    Color(
                                        red = 0.5f + 0.4f * overRatio.coerceIn(0f, 1f),
                                        green = 0.1f,
                                        blue = 0.1f,
                                        alpha = 1f
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(color)
                                    .then(
                                        if (isToday) Modifier.background(
                                            Color(0xFF4A9EFF).copy(alpha = 0.3f),
                                            RoundedCornerShape(2.dp)
                                        ) else Modifier
                                    )
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Less", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(4.dp))
            listOf(Color(0xFF1A2233), Color(0xFF1A4433), Color(0xFF22885A), Color(0xFF44CC88)).forEach { c ->
                Box(Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(c))
                Spacer(Modifier.width(2.dp))
            }
            Text("More saved", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
