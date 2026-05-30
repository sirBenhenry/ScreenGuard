package com.sirbenhenry.screenguard.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sirbenhenry.screenguard.data.entity.Achievement
import com.sirbenhenry.screenguard.data.entity.MonitoredApp
import com.sirbenhenry.screenguard.data.entity.StreakRecord
import com.sirbenhenry.screenguard.data.entity.UsageRecord
import com.sirbenhenry.screenguard.ui.components.HeatMap
import com.sirbenhenry.screenguard.viewmodel.StatsUiState
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StatsScreen(state: StatsUiState) {
    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val scroll = rememberScrollState()
    val last7 = state.streakRecords.take(7)
    val goodDays = state.streakRecords.count { it.allUnderLimit }
    val totalDays = state.streakRecords.size

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Text("Statistics", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)

        Spacer(Modifier.height(20.dp))

        // Summary row
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatBox("Good days", "$goodDays", Color(0xFF44BB88), Modifier.weight(1f))
            StatBox("Total days", "$totalDays", Color(0xFF4A9EFF), Modifier.weight(1f))
            StatBox(
                "Success rate",
                if (totalDays > 0) "${goodDays * 100 / totalDays}%" else "—",
                Color(0xFFFFCC00),
                Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(20.dp))

        // Last 7 days bar chart
        if (last7.isNotEmpty()) {
            Text("LAST 7 DAYS", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, letterSpacing = 1.5.sp)
            Spacer(Modifier.height(10.dp))
            Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    last7.reversed().forEach { record ->
                        DayBar(record)
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Per-app breakdown
        if (state.monitoredApps.isNotEmpty()) {
            Text("PER APP (ALL TIME)", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, letterSpacing = 1.5.sp)
            Spacer(Modifier.height(10.dp))
            state.monitoredApps.forEach { app ->
                AppStatCard(app, state.usageRecords.filter { it.packageName == app.packageName })
                Spacer(Modifier.height(10.dp))
            }
        }

        Spacer(Modifier.height(20.dp))

        // Achievements
        if (state.achievements.isNotEmpty()) {
            Text("ACHIEVEMENTS", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, letterSpacing = 1.5.sp)
            Spacer(Modifier.height(10.dp))
            Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    val columns = 2
                    state.achievements.chunked(columns).forEach { row ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            row.forEach { achievement ->
                                AchievementBadge(achievement, Modifier.weight(1f))
                            }
                            repeat(columns - row.size) { Spacer(Modifier.weight(1f)) }
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        // Full heat map
        Text("105-DAY HEAT MAP", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, letterSpacing = 1.5.sp)
        Spacer(Modifier.height(10.dp))
        Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
            HeatMap(records = state.streakRecords, modifier = Modifier.padding(16.dp))
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun DayBar(record: StreakRecord) {
    val fmt = SimpleDateFormat("EEE MMM d", Locale.getDefault())
    val fmtKey = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val date = try { fmtKey.parse(record.dateKey)?.let { fmt.format(it) } ?: record.dateKey } catch (_: Exception) { record.dateKey }
    val pct = if (record.totalLimitMinutes > 0)
        (record.totalMinutesUsed.toFloat() / record.totalLimitMinutes).coerceIn(0f, 1.5f)
    else 0f
    val color = if (record.allUnderLimit) Color(0xFF44BB88) else Color(0xFFFF5555)

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(date, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(90.dp))
        Spacer(Modifier.width(8.dp))
        Box(
            Modifier
                .weight(1f)
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF1A2233))
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(pct.coerceAtMost(1f))
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            "${record.totalMinutesUsed}m",
            fontSize = 11.sp,
            color = color,
            modifier = Modifier.width(36.dp)
        )
    }
}

@Composable
private fun AppStatCard(app: MonitoredApp, records: List<UsageRecord>) {
    val totalDays = records.size
    val goodDays = records.count { it.underLimit }
    val avgMin = if (records.isNotEmpty()) records.sumOf { it.totalMinutes } / records.size else 0
    val successRate = if (totalDays > 0) goodDays * 100 / totalDays else 0

    Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(app.appName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.weight(1f))
                Text("${app.dailyLimitMinutes}m/day limit", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                MiniNum("$successRate%", "success rate")
                MiniNum("${avgMin}m", "avg/day")
                MiniNum("$goodDays/$totalDays", "good days")
            }
        }
    }
}

@Composable
private fun StatBox(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surface, modifier = modifier) {
        Column(Modifier.padding(14.dp)) {
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Spacer(Modifier.height(2.dp))
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 13.sp)
        }
    }
}

@Composable
private fun MiniNum(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun AchievementBadge(achievement: Achievement, modifier: Modifier = Modifier) {
    val borderColor = if (achievement.isRare) Color(0xFFFFD700) else Color(0xFF334455)
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (achievement.isRare) Color(0xFF1A1400) else MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(if (achievement.isRare) 1.5.dp else 1.dp, borderColor),
        modifier = modifier
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(achievement.emoji, fontSize = 28.sp)
            Spacer(Modifier.height(4.dp))
            Text(achievement.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, lineHeight = 15.sp)
            Spacer(Modifier.height(2.dp))
            Text(achievement.description, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 13.sp)
            if (achievement.isRare) {
                Spacer(Modifier.height(4.dp))
                Text("RARE", fontSize = 9.sp, color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        }
    }
}
